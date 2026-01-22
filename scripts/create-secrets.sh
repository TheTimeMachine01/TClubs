#!/usr/bin/env bash
# Script to create Kubernetes secrets from .env file
set -euo pipefail

ENV_FILE=".env"
if [ ! -f "$ENV_FILE" ]; then
  echo "Error: $ENV_FILE file not found. Please copy .env.example to .env and fill with actual values."
  exit 1
fi

echo "Creating secrets from $ENV_FILE..."

# Create tclubs-secrets from JWT variables
kubectl create secret generic tclubs-secrets \
  --from-literal=JWT_SECRET="$(grep '^JWT_SECRET=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=JWT_EXPIRATION_TIME="$(grep '^JWT_EXPIRATION_TIME=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=JWT_REFRESH_EXPIRATION_TIME="$(grep '^JWT_REFRESH_EXPIRATION_TIME=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=KAFKA_BOOTSTRAP_SERVERS="$(grep '^KAFKA_BOOTSTRAP_SERVERS=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MINIO_URL="$(grep '^MINIO_URL=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MINIO_ACCESS_KEY="$(grep '^MINIO_ACCESS_KEY=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MINIO_SECRET_KEY="$(grep '^MINIO_SECRET_KEY=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MINIO_BUCKET_NAME="$(grep '^MINIO_BUCKET_NAME=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MONGO_USER="$(grep '^MONGO_USER=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MONGO_PASSWORD="$(grep '^MONGO_PASSWORD=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MONGO_HOST="$(grep '^MONGO_HOST=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MONGO_PORT="$(grep '^MONGO_PORT=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --from-literal=MONGO_DB="$(grep '^MONGO_DB=' "$ENV_FILE" | cut -d'=' -f2-)" \
  --dry-run=client -o yaml | kubectl apply -f -

# Update postgres-secret with password from .env
POSTGRES_PASSWORD="$(grep '^POSTGRES_PASSWORD=' "$ENV_FILE" | cut -d'=' -f2-)"

kubectl create secret generic postgres-secret \
  --from-literal=POSTGRES_USER=tclubs \
  --from-literal=POSTGRES_DB=tclubsdb \
  --from-literal=POSTGRES_PASSWORD="$POSTGRES_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "Secrets created/updated successfully."
