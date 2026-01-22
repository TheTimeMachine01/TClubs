# Clubs

Clubs is a microservices-based social media platform for creating and managing communities. It allows users to create clubs, join clubs, and interact with other members through posts and media sharing.

## Features

- User registration and authentication
- Club creation and management
- Membership requests and approvals
- Posting and commenting on feeds
- Media upload and storage
- Event-driven architecture with Kafka
- Service discovery with Eureka
- API Gateway for routing and security

## Architecture

The project follows a microservices architecture, with each service responsible for a specific domain. The services communicate with each other through a combination of REST APIs and a Kafka-based messaging system.

The main components of the architecture are:

*   **API Gateway:** The single entry point for all client requests. It routes requests to the appropriate service and handles authentication and authorization.
*   **Service Discovery:** Eureka is used for service registration and discovery, allowing services to find and communicate with each other dynamically.
*   **Messaging System:** Kafka is used for asynchronous communication between services, enabling event-driven workflows and improving fault tolerance.
*   **Database:** The project uses a combination of PostgreSQL and MongoDB for data storage.

## Services

The project is composed of the following microservices:

*   **`auth-service`:** Handles user authentication and authorization using JWT tokens.
*   **`user-service`:** Manages user profiles and data, stored in PostgreSQL.
*   **`club-service`:** Manages clubs and memberships, stored in PostgreSQL.
*   **`feed-service`:** Manages the user's feed, including posts and comments, stored in MongoDB.
*   **`media-service`:** Handles media uploads and storage using MinIO.
*   **`api-gateway`:** The single entry point for all client requests, built with Spring Cloud Gateway.
*   **`eureka-server`:** The service discovery server.

## Technologies Used

*   **Backend:** Java 21, Spring Boot 3, Spring Cloud, Spring Security, Project Reactor (WebFlux)
*   **Database:** PostgreSQL, MongoDB
*   **Messaging:** Kafka
*   **Service Discovery:** Eureka
*   **Containerization:** Docker, Docker Compose
*   **Build Tool:** Maven
*   **Object Storage:** MinIO

## Prerequisites

Before setting up the project, ensure you have the following installed:

*   **Java 21:** Download from [Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) or use OpenJDK.
*   **Maven 3.8+:** Download from [Apache Maven](https://maven.apache.org/download.cgi).
*   **Docker:** Download from [Docker](https://www.docker.com/get-started).
*   **Docker Compose:** Usually included with Docker Desktop.

Verify installations:

```bash
java -version
mvn -version
docker --version
docker-compose --version
```

## Installation and Setup

1.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    cd clubs
    ```

2.  **Create Secrets:**
    The application uses secrets for database connections and service communication. Run the provided script to generate them:
    ```bash
    ./scripts/create-secrets.sh
    ```

3.  **Build the Project:**
    Compile and package all services:
    ```bash
    mvn clean install -DskipTests
    ```

4.  **Start Infrastructure Services:**
    Use Docker Compose to start databases and messaging services:
    ```bash
    docker-compose up -d postgres kafka zookeeper minio
    ```

5.  **Run the Application:**
    Start all microservices:
    ```bash
    docker-compose up -d --build
    ```

6.  **Verify Setup:**
    Check that all services are running:
    ```bash
    docker-compose ps
    ```

## Running Locally (Development)

For development, you can run services individually:

1.  Start infrastructure (PostgreSQL, Kafka, etc.) with Docker Compose.

2.  Run each service from your IDE or command line:
    ```bash
    mvn spring-boot:run -pl <service-name>
    ```

3.  Access Eureka dashboard at `http://localhost:8761`.

4.  API Gateway at `http://localhost:8080`.

## Configuration

Configuration is managed through `application.yml` files in each service and environment variables set in Docker Compose.

Key configurations:

*   Database URLs, usernames, passwords (set via secrets)
*   Kafka brokers
*   MinIO endpoint and credentials
*   Eureka server URL

## Testing

Run tests for all services:

```bash
mvn test
```

For integration tests, ensure infrastructure services are running.

## API Documentation

For detailed information about the APIs, please refer to the [APIs.md](APIs.md) file.

## Troubleshooting

*   **Port Conflicts:** Ensure ports 8080, 8761, etc., are free.
*   **Database Connection Issues:** Check secrets and database logs.
*   **Service Discovery:** Verify Eureka is running and services are registered.
*   **Kafka Issues:** Check Kafka logs and topic creation.
*   **Kubernetes Issues:**
    - Check pod status: `kubectl get pods`
    - View pod logs: `kubectl logs <pod-name>`
    - Describe pod for events: `kubectl describe pod <pod-name>`
    - If pods are in CrashLoopBackOff, logs will show startup errors.
    - Ensure images are pulled: `kubectl describe pod <pod-name>` may show image pull errors.

## Contributing

1.  Fork the repository.
2.  Create a feature branch.
3.  Make changes and add tests.
4.  Submit a pull request.

## License

This project is licensed under the MIT License.

## Deploying to Kubernetes

The project includes Kubernetes manifests for deploying all services and infrastructure to a Kubernetes cluster.

### Prerequisites

*   A running Kubernetes cluster (e.g., Minikube, EKS, GKE)
*   `kubectl` configured to access the cluster
*   Helm (optional, for advanced deployments)

### Deployment Steps

1.  **Create Secrets:**
    Apply the secrets manifest:
    ```bash
    kubectl apply -f kubernetes/secrets.yaml
    ```

2.  **Pull Required Images (if using local cluster):**
    If your Kubernetes cluster uses local Docker images (e.g., Minikube), pull the required infrastructure images:
    ```bash
    docker pull postgres:latest
    docker pull redis:latest
    docker pull confluentinc/cp-kafka:latest
    docker pull zookeeper:latest
    docker pull minio/minio:latest
    docker pull mongo:latest
    ```

3.  **Deploy Infrastructure:**
    Deploy databases, messaging, and storage:
    ```bash
    kubectl apply -f kubernetes/infrastructure/
    ```

4.  **Deploy Services:**
    Deploy all microservices:
    ```bash
    kubectl apply -f kubernetes/services/
    ```

5.  **Apply ConfigMaps:**
    ```bash
    kubectl apply -f kubernetes/configmap.yaml
    ```

6.  **Verify Deployment:**
    Check pod status:
    ```bash
    kubectl get pods
    ```

7.  **Access the Application:**
    Port-forward the API Gateway:
    ```bash
    kubectl port-forward svc/api-gateway 8080:8080
    ```
    Access at `http://localhost:8080`.

### Dashboard

For monitoring, deploy the Kubernetes dashboard:
```bash
kubectl apply -f kubernetes/dashboard/dashboard-admin.yaml
```

Access the dashboard using the provided token or service account.

### Scaling and Management

*   Scale services: `kubectl scale deployment <service-name> --replicas=<number>`
*   View logs: `kubectl logs -f deployment/<service-name>`
*   Update deployments: `kubectl apply -f kubernetes/services/<service>.yaml`

## Scripts

The project includes several utility scripts to simplify development and deployment tasks. These are located in the `scripts/` directory.

### `create-secrets.sh`

Generates necessary secrets for database connections, JWT keys, and service communication. Run this before starting the application.

```bash
./scripts/create-secrets.sh
```

This script creates encrypted secrets that are used by Docker Compose and Kubernetes deployments.

### `build-all.sh`

Builds all services and creates Docker images.

```bash
./scripts/build-all.sh
```

### `run-local.sh`

Starts the application locally using Docker Compose.

```bash
./scripts/run-local.sh
```

### `clean-all.sh`

Cleans up Docker containers, images, and volumes.

```bash
./scripts/clean-all.sh
```

### `stop-k8s.sh`

Stops all Kubernetes deployments and services.

```bash
./scripts/stop-k8s.sh
```

### `phase2-check.sh`

Checks the status of Phase 2 implementation.

```bash
./scripts/phase2-check.sh
```
