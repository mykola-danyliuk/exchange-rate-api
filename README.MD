# Exchange Rate API

## Overview
This project implements an API to fetch and calculate currency exchange rates using Java and Spring Boot.

## Features
- Fetch exchange rates from [exchangerate.host](https://exchangerate.host).
- API Operations:
   1. Get exchange rate between two currencies.
   2. Get all exchange rates for a currency.
   3. Convert a value between two currencies.
   4. Convert a value to multiple currencies.
- Swagger documentation available at `/swagger-ui/index.html`.

## Setup
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd exchange-rate-api
   ```
2. Change API access key in `demo.env`:
   ```properties
   ERH_ACCESS_KEY=<your_access_key>
3. Build and run:
   ```bash
   mvn clean package
   docker-compose -f docker-compose.demo.yml up
   ```
3. Access API documentation: `http://localhost:8080/swagger-ui/index.html`

## Implementation Details
- Scheduled task to fetch API rates every 1 minute.
- All rates are stored locally in HashMap.
- Failback to DB, when API is not available.
- If DB does not have data, API will be unavailable.
- Code structure allows add more data sources.
- Unit test coverage for reliability.

## Testing
Run tests with:
```bash
mvn test
```
