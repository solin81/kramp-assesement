# Kramp microservices assessment

Five self-contained Spring Boot 4.1.1 services, using Java 25 and in-memory sample data. The aggregation API composes localized product, price, inventory, and optional customer details.

## Run locally

Java 25 is required; Maven is downloaded automatically by the wrapper.

```bash
./mvnw clean verify
./mvnw -pl aggregation-service spring-boot:run
```

Service ports: aggregation `8080`, catalog `8081`, pricing `8082`, availability `8083`, customer `8084`. Start all services with:

```bash
docker compose up --build
```

Examples:

```bash
curl 'http://localhost:8081/api/products/P-100?marketCode=en-EN'
curl 'http://localhost:8080/api/aggregated/products/P-100?marketCode=nl-NL&customerId=C-100'
curl http://localhost:8080/actuator/health
```

The aggregation service reads `CATALOG_SERVICE_URL`, `PRICING_SERVICE_URL`, `AVAILABILITY_SERVICE_URL`, and `CUSTOMER_SERVICE_URL`; all default to local ports. Supported market codes are `en-EN`, `nl-NL`, and `de-DE`.

## Simulated downstream behavior

Each downstream service delays every request and randomly responds with HTTP 503 according to its default reliability: catalog 50 ms / 99.9%, pricing 80 ms / 99.5%, availability 100 ms / 98%, and customer 60 ms / 99%. Override a service with its `*_LATENCY_MS` and `*_RELIABILITY_PERCENT` environment variables (for example, `AVAILABILITY_RELIABILITY_PERCENT=100`). The aggregation endpoint returns whatever data is available and records failed or missing sections in `warnings`; customer data is omitted when no `customerId` is provided.

`price.customerDiscount` is a monetary amount, so `basePrice - customerDiscount = finalPrice`.
