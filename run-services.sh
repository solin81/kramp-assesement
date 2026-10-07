#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

services=(
  catalog-service
  pricing-service
  availability-service
  customer-service
  aggregation-service
)

echo "Building all services..."
./mvnw clean verify

pids=()

stop_services() {
  echo
  echo "Stopping services..."
  if ((${#pids[@]})); then
    kill "${pids[@]}" 2>/dev/null || true
    wait "${pids[@]}" 2>/dev/null || true
  fi
}

trap stop_services EXIT INT TERM

for service in "${services[@]}"; do
  echo "Starting ${service}..."
  ./mvnw -pl "${service}" spring-boot:run &
  pids+=("$!")
done

echo
echo "All services are starting. Press Ctrl+C to stop them."
wait "${pids[@]}"
