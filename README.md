# Kramp microservices assessment

## Description

This project models a product-details API with five Spring Boot services. The aggregation API combines localized product, price, inventory, and optional customer data. All APIs return JSON and use in-memory sample data, so no database or external infrastructure is required.

### Libraries and platform

- Java 25 (latest Long-Term Support (LTS))
- Spring Boot 4.1.1 (latest stable version)
- Maven Wrapper for reproducible builds without a pre-installed Maven distribution

### Services

| Service | Purpose | Input | Output |
| --- | --- | --- | --- |
| Aggregation (`8080`) | Builds a product view by calling the upstream services. | Product ID, `marketCode`, and optional `customerId`. | An aggregated product containing product, price, availability, optional customer data, and any warnings. |
| Catalog (`8081`) | Provides localized product details. | Optional `marketCode`; product ID for an individual product. | Product ID, name, description, specifications, and image URLs. |
| Pricing (`8082`) | Provides market- and optionally customer-specific prices. | Optional `marketCode` and `customerId`; product ID for an individual price. | Product ID, base price, customer discount, and final price. |
| Availability (`8083`) | Provides product stock and delivery information. | Optional `marketCode`; product ID for an individual availability record. | Product ID, stock level, warehouse location, and expected delivery. |
| Customer (`8084`) | Provides customer context for personalized pricing and aggregation. | Optional `marketCode`; customer ID for an individual customer. | Customer ID, segment, and preferences. |

The parameter `marketCode` supports `en-EN`, `nl-NL`, and `de-DE`, and defaults to `en-EN`. The aggregation service calls catalog, pricing, and availability for every request, and calls customer only when a `customerId` is supplied. Catalog data is required: if Catalog Service cannot provide it, aggregation returns `503 Service Unavailable`. Pricing and Availability failures produce a partial product response with the respective section omitted and a warning that the price is unavailable or stock is unknown. A Customer Service failure, or the absence of a customer ID, produces the standard non-personalized response.

The four upstream services simulate a remote dependency by adding latency and occasionally returning HTTP 503. Defaults are catalog 50 ms / 99.9%, pricing 80 ms / 99.5%, availability 100 ms / 98%, and customer 60 ms / 99%. Set the applicable `*_LATENCY_MS` or `*_RELIABILITY_PERCENT` environment variable to change this behavior.

## How to run the service

Java 25 is required; Maven is downloaded automatically by the wrapper.
Use one of the repository scripts to build and then start every service:

```bash
./run-services.sh       # Linux/macOS
run-services.bat        # Windows Command Prompt
```

The Bash script keeps all service processes in the current terminal; press `Ctrl+C` to stop them. The Windows script opens one Command Prompt window per service; close those windows to stop them.

Service ports: aggregation `8080`, catalog `8081`, pricing `8082`, availability `8083`, customer `8084`.

The aggregation service reads `CATALOG_SERVICE_URL`, `PRICING_SERVICE_URL`, `AVAILABILITY_SERVICE_URL`, and `CUSTOMER_SERVICE_URL`; all default to local ports.

Example URLs:

```bash
# Aggregation service (8080)
curl 'http://localhost:8080/api/aggregated/products/P-100?marketCode=en-EN'
curl 'http://localhost:8080/api/aggregated/products/P-100?marketCode=en-EN&customerId=C-100'

# Catalog service (8081)
curl 'http://localhost:8081/api/products?marketCode=en-EN'
curl 'http://localhost:8081/api/products/P-100?marketCode=en-EN'

# Pricing service (8082)
curl 'http://localhost:8082/api/prices?marketCode=en-EN'
curl 'http://localhost:8082/api/prices?marketCode=en-EN&customerId=C-100'
curl 'http://localhost:8082/api/prices/P-100?marketCode=en-EN'
curl 'http://localhost:8082/api/prices/P-100?marketCode=en-EN&customerId=C-100'

# Availability service (8083)
curl 'http://localhost:8083/api/availability?marketCode=en-EN'
curl 'http://localhost:8083/api/availability/P-100?marketCode=en-EN'

# Customer service (8084)
curl 'http://localhost:8084/api/customers?marketCode=en-EN'
curl 'http://localhost:8084/api/customers/C-100?marketCode=en-EN'
```

## What to improve

- Replace the in-memory sample data with databases for product, price, stock, and customer data.
- Run independent calls asynchronously and in parallel, with per-service and whole-request timeouts, so one slow or unavailable service does not delay the others.
- Make the system handle temporary failures better: retry failed calls after short increasing delays, stop calling a service that is repeatedly failing, and use cached data or a simple fallback for non-critical information.
- Return structured dependency statuses in partial responses (for example, `TIMEOUT` or `UNAVAILABLE`) instead of text-only warnings, so clients can handle failures reliably.

## Design question answer
Option A: "The Assortment team wants to add a 'Related Products' service (200ms latency, 90% reliability). How would your design accommodate this? Should it be required or optional?"

I would add Related Products as an optional downstream service in the aggregation layer. The aggregator would call it in parallel with the existing services, using a timeout below its 200ms latency target and a circuit breaker. Its response would be added as a separate `relatedProducts` section in the aggregate response.

It should be optional: recommendations improve product discovery but are not needed to show a product page or complete a purchase. If the service times out or fails, the aggregator should return the normal product response without `relatedProducts`, record the failure for monitoring, and avoid delaying the critical request path. 
