# OnlineOrder

OnlineOrder is a full-stack food ordering application built to practice production-style backend layering, relational data modeling, authentication, transaction handling, React integration, containerized local development, and cloud-ready configuration.

The application allows a customer to register, sign in, browse restaurant menus, manage a shopping cart, save a demo payment method, check out, and view order history. The seeded administrator account can also manage restaurants and menu items through a protected admin interface.

## Features

- Session-based authentication with Spring Security and BCrypt password hashing
- Role-based authorization: regular users browse and order; `ROLE_ADMIN` manages the catalog
- Restaurant and menu browsing with a searchable React interface
- Cart creation, quantity update, removal, recalculated totals, and Caffeine caching
- Transactional checkout that creates immutable order line-item snapshots before clearing the cart
- Demo payment methods that store only card brand and last four digits
- Order history with payment summary and purchased-item details
- Local recommendation assistant that searches real menu names/descriptions and respects an optional budget
- Global JSON error responses for invalid input and missing resources
- Docker Compose environment for PostgreSQL and the Spring Boot application
- Unit and Spring context tests

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 4, Spring MVC, Spring Security |
| Data | Spring Data JDBC, PostgreSQL 15 |
| Frontend | React 18, Ant Design |
| Build | Gradle, npm |
| Infrastructure | Docker, Docker Compose |
| Testing | JUnit 5, Mockito |

## Architecture

```text
React UI
   |
   v
Controller -> Service -> Repository -> PostgreSQL
                 |
                 +-> Cache / Transactions / Authorization checks
```

- `controller`: HTTP request/response handling only.
- `service`: business rules, ownership checks, transactions, and cache behavior.
- `repository`: Spring Data JDBC queries and persistence.
- `entity`: records mapped to database tables.
- `model`: request and response DTOs; database entities are not exposed blindly.

## Project Structure

```text
OnlineOrder/
+-- frontend/                       # Editable React source
|   +-- src/App.js                  # Auth, menu, cart, admin, and history UI
|   +-- src/api.js                  # Browser-to-backend API client
+-- src/main/java/.../
|   +-- controller/                 # REST endpoints
|   +-- service/                    # Business logic
|   +-- repository/                 # JDBC persistence
|   +-- entity/                     # Table mappings
|   +-- model/                      # DTOs
|   +-- exception/                  # Consistent error responses
+-- src/main/resources/
|   +-- application.yaml
|   +-- database-init.sql
|   +-- public/                     # React production build served by Spring Boot
+-- Dockerfile
+-- docker-compose.yml
```

## Run Locally

### Prerequisites

- Java 21
- Docker Desktop
- Node.js 24 only when editing the React source

### Option A: Run PostgreSQL in Docker and Spring Boot locally

```powershell
docker compose up -d db
.\gradlew.bat bootRun
```

Open `http://localhost:8080`.

### Option B: Run the entire application with Docker Compose

```powershell
docker compose up -d --build
```

Check containers:

```powershell
docker compose ps
```

Stop the environment:

```powershell
docker compose down
```

## Demo Accounts and Data

The startup runner creates this local demonstration administrator:

```text
Email:    foo@mail.com
Password: 123456
```

Use a new email to test a normal customer account. For the demo payment form, use a fake number such as `4242 4242 4242 4242`; do not enter a real card number.

## Frontend Development

The source code is in `frontend/`. The development server proxies API calls to the Spring Boot server on port 8080.

```powershell
cd frontend
npm.cmd install
npm.cmd start
```

Create a production build and embed it into Spring Boot:

```powershell
cd frontend
npm.cmd run build
Copy-Item -Path build\* -Destination ..\src\main\resources\public -Recurse -Force
```

## API Overview

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/signup` | Create an account |
| POST | `/login` | Start an authenticated session |
| POST | `/logout` | End the session |
| GET | `/me` | Get current user and admin status |
| GET | `/restaurants/menu` | Get restaurants with menu items |
| GET / POST | `/cart` | Read cart / add an item |
| PATCH / DELETE | `/cart/items/{orderItemId}` | Change quantity / remove item |
| GET / POST | `/payment-methods` | Read / save demo payment methods |
| POST | `/cart/checkout` | Create an order from the current cart |
| GET | `/orders` | Get current user's order history |
| POST | `/recommendations` | Find suitable catalog items from a text request |
| POST / PUT / DELETE | `/restaurants`, `/restaurant/{id}` | Admin restaurant management |
| POST / PUT / DELETE | `/restaurant/{id}/menu`, `/menu/{id}` | Admin menu management |

## Tests

```powershell
.\gradlew.bat test
```

The test suite includes Spring context startup checks and mocked cart-service unit tests for create/update/cart retrieval/clear behavior.

## Development Notes

- `database-init.sql` is intentionally configured for course development and resets/loads sample data when SQL initialization is enabled.
- Before persistent or cloud deployment, set `SPRING_SQL_INIT_MODE=never` after migrating the schema and seed data appropriately.
- The recommendation assistant is deterministic and self-contained so the repository works without a paid AI key. It is designed as a clean candidate-retrieval layer that can later be enhanced with an LLM.
- The payment module is a demo workflow, not a PCI-compliant payment integration. A production application should use a payment processor such as Stripe and never process raw card data directly.
