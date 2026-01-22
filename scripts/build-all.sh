#!/usr/bin/env bash
# Script to build all services by first packaging with Maven on the host, then building lightweight runtime images.
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SERVICES=(eureka-server api-gateway auth-service user-service club-service feed-service media-service dummy-service)

# Determine image repository name.
# Priority: env IMAGE_REPO -> git remote repo name (lowercased) -> default 'tclubs'
IMAGE_REPO="${IMAGE_REPO:-}"
if [ -z "$IMAGE_REPO" ]; then
  if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    origin_url=$(git config --get remote.origin.url || true)
    if [ -n "$origin_url" ]; then
      # extract basename and lowercase it
      repo_name=$(basename -s .git "$origin_url" | tr 'A-Z' 'a-z')
      IMAGE_REPO="$repo_name"
    fi
  fi
fi
IMAGE_REPO="${IMAGE_REPO:-tclubs}"

echo "Using image repository: $IMAGE_REPO"

cd "$ROOT_DIR"
# Build artifacts on the host to avoid network/DNS issues inside docker build
echo "Running Maven build (host)..."
if ! command -v mvn >/dev/null 2>&1; then
  echo "mvn not found in PATH. Install Maven or adjust PATH." >&2
  exit 1
fi
mvn -DskipTests package -T1C

# If MINIKUBE=true, use minikube docker daemon
if [ "${MINIKUBE:-}" = "true" ]; then
  echo "Using Minikube docker-env"
  if command -v minikube >/dev/null 2>&1; then
    eval "$(minikube docker-env)"
  else
    echo "minikube not found in PATH" >&2
    exit 1
  fi
fi

# Check docker
if ! command -v docker >/dev/null 2>&1; then
  echo "docker CLI not found. Install Docker and try again." >&2
  exit 1
fi
if ! docker info >/dev/null 2>&1; then
  echo "Docker does not seem to be running or you don't have permission to access it." >&2
  echo "On systemd systems: sudo systemctl start docker" >&2
  echo "Or add your user to the docker group and re-login: sudo usermod -aG docker $USER" >&2
  exit 1
fi

for svc in "${SERVICES[@]}"; do
  JAR_PATH=$(ls -1 services/${svc}/target/*-SNAPSHOT.jar 2>/dev/null || true)
  if [ -z "$JAR_PATH" ]; then
    echo "No built JAR found for $svc (skipping). Expected services/$svc/target/*-SNAPSHOT.jar"
    continue
  fi
  JAR_FILE=$(basename "$JAR_PATH")
  echo "Building runtime image for $svc using $JAR_FILE"
  # Dynamically create a tiny Dockerfile that copies the JAR and sets entrypoint
  docker build -t "${IMAGE_REPO}/${svc}:latest" -f - . <<EOF
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY services/${svc}/target/${JAR_FILE} /app/app.jar
ENV JAVA_OPTS="-Xms128m -Xmx512m -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default}"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java \$JAVA_OPTS -jar /app/app.jar"]
EOF

done

echo "Done. Built images for services with existing artifacts (repo: $IMAGE_REPO)."
