# The Clubs

A microservices-based application built with Spring Boot, featuring user authentication, management, and API gateway with Eureka service discovery. Deployed on Kubernetes for scalable, containerized environments.

## Features

- **Eureka Server**: Service discovery and registration.
- **API Gateway**: Centralized routing, rate limiting, circuit breakers, and JWT authentication.
- **Auth Service**: User authentication with JWT and Redis caching.
- **User Service**: User management with PostgreSQL database.
- **Dummy Service**: Testing service for development.

## Prerequisites

- **Java 21**: JDK for building the project.
- **Maven**: For dependency management and building.
- **Docker**: For containerization.
- **Kubernetes Cluster**: Local setup with Minikube, Kind, or Docker Desktop Kubernetes.
- **kubectl**: Kubernetes CLI tool.
- **Git**: For cloning the repository.

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Clubs
```

### 2. Build the Project

Build all modules and create JAR files:

```bash
mvn clean package -DskipTests
```

### 3. Build Docker Images

Use the provided script to build lightweight runtime images:

```bash
./scripts/build-all.sh
```

This creates images like `tclubs/auth-service:latest`, etc.

## Kubernetes Deployment

### 1. Prerequisites

Ensure your Kubernetes cluster is running and `kubectl` is configured:

```bash
kubectl cluster-info
```

### 2. Deploy Infrastructure

Deploy PostgreSQL, Redis, and Eureka Server:

```bash
kubectl apply -f kubernetes/infrastructure/
```

### 3. Configure Secrets

Before deploying, set up your secrets using environment variables:

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with actual secure values (generate strong secrets):
   ```bash
   # Example: Generate JWT secret
   openssl rand -hex 32
   ```

3. Create Kubernetes secrets from the `.env` file:
   ```bash
   ./scripts/create-secrets.sh
   ```

This creates the secrets dynamically without committing sensitive data to the repository.

### 4. Deploy Configuration

Apply secrets and config maps:

```bash
kubectl apply -f kubernetes/secrets.yaml
kubectl apply -f kubernetes/configmap.yaml
```

### 5. Deploy Services

Deploy the microservices:

```bash
kubectl apply -f kubernetes/services/
```

### 6. Verify Deployment

Check pod statuses:

```bash
kubectl get pods -o wide
```

All pods should be `Running` (1/1 Ready).

## Accessing Services

### API Gateway

Port-forward the API Gateway:

```bash
kubectl port-forward svc/api-gateway 8080:8080
```

Access endpoints:
- Health: `http://localhost:8080/actuator/health`
- Auth: `http://localhost:8080/api/v1/auth/...`
- Users: `http://localhost:8080/api/v1/users/...`

### Eureka Dashboard

View registered services:

```bash
kubectl port-forward svc/eureka-server 8761:8761
```

Open: `http://localhost:8761`

### Kubernetes Dashboard

For cluster monitoring:

1. Start proxy:
   ```bash
   kubectl proxy
   ```

2. Open: `http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/`

3. Login with token:
   ```bash
   kubectl -n kubernetes-dashboard create token dashboard-admin-sa
   ```

## Monitoring and Logging

- **Actuator Endpoints**: Exposed on each service for health checks and metrics.
- **Logs**: View pod logs with `kubectl logs <pod-name>`.
- **Prometheus/Grafana**: Configure from `monitoring/` directory for advanced monitoring.

## Development

### Running Locally (Non-K8s)

Use Docker Compose (if available) or run services individually with profiles.

### Adding New Services

1. Create module in `services/`.
2. Add to `pom.xml` and `scripts/build-all.sh`.
3. Create Kubernetes manifests in `kubernetes/services/`.

### Testing

Run tests:

```bash
mvn test
```

## Troubleshooting

- **Pods not starting**: Check logs with `kubectl logs <pod-name>`.
- **Image pull issues**: Ensure images are built and tagged correctly.
- **Service discovery**: Verify Eureka registration.
- **Database connection**: Check PostgreSQL pod and secrets.
- **Stop services**: Use `./scripts/stop-k8s.sh all` to scale down all deployments.

## Contributing

1. Fork the repository.
2. Create a feature branch.
3. Commit changes and push.
4. Create a pull request.

## License

[Specify license if applicable]
