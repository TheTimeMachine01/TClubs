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
  --dry-run=client -o yaml | kubectl apply -f -

# Update postgres-secret with password from .env
POSTGRES_PASSWORD="$(grep '^POSTGRES_PASSWORD=' "$ENV_FILE" | cut -d'=' -f2-)"
kubectl patch secret postgres-secret \
  --type='merge' \
  -p "{\"stringData\":{\"POSTGRES_PASSWORD\":\"$POSTGRES_PASSWORD\"}}" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "Secrets created/updated successfully."
