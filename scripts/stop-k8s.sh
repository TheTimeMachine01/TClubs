#!/usr/bin/env bash
# Script to delete Kubernetes deployments and services
set -euo pipefail

# Define paths relative to the project root
SERVICES_DIR="./kubernetes/services"
INFRA_DIR="./kubernetes/infrastructure"

usage() {
    echo "Usage: $0 [all|services|infra]"
    echo "Examples:"
    echo "  $0 all       # Delete everything (Services + Infra)"
    echo "  $0 services  # Delete only microservices"
    echo "  $0 infra     # Delete only database, redis, eureka"
    exit 1
}

if [ $# -eq 0 ]; then
    usage
fi

case "$1" in
    all)
        echo "Deleting all resources..."
        kubectl delete -f "$SERVICES_DIR" --ignore-not-found
        kubectl delete -f "$INFRA_DIR" --ignore-not-found
        # Optional: Uncomment below if you want to wipe the DB data too
        # kubectl delete pvc postgres-pvc --ignore-not-found
        ;;
    services)
        echo "Deleting microservices..."
        kubectl delete -f "$SERVICES_DIR" --ignore-not-found
        ;;
    infra)
        echo "Deleting infrastructure..."
        kubectl delete -f "$INFRA_DIR" --ignore-not-found
        ;;
    *)
        usage
        ;;
esac

echo "Done. Resources removed."