#!/usr/bin/env bash
# Script to stop Kubernetes deployments and services
set -euo pipefail

# List of services to manage
SERVICES=(eureka-server api-gateway auth-service user-service dummy-service)

usage() {
    echo "Usage: $0 [all|service-name]"
    echo "Examples:"
    echo "  $0 all          # Stop all services"
    echo "  $0 api-gateway  # Stop only api-gateway"
    echo "  $0              # Show this help"
    exit 1
}

stop_service() {
    local service="$1"
    echo "Stopping $service..."
    kubectl scale deployment "$service" --replicas=0 || echo "Failed to scale $service"
}

if [ $# -eq 0 ]; then
    usage
fi

case "$1" in
    all)
        echo "Stopping all services..."
        for svc in "${SERVICES[@]}"; do
            stop_service "$svc"
        done
        ;;
    *)
        if [[ " ${SERVICES[*]} " =~ " $1 " ]]; then
            stop_service "$1"
        else
            echo "Error: Service '$1' not found. Available services: ${SERVICES[*]}"
            exit 1
        fi
        ;;
esac

echo "Done. Services stopped. Use 'kubectl scale deployment <name> --replicas=1' to restart."
