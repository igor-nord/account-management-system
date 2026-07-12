# Account Management System

A self-contained account-management service: a **Spring Boot 4** REST backend and an
**Angular 21** single-page app in a two-folder monorepo. Customers hold multi-currency
accounts; the system records money movements as a strict **double-entry ledger**,
serves paginated history and a balance chart, and exports transaction PDFs.

**What it does**

- Lists a customer's accounts with balances across currencies (EUR, USD, SEK, GBP, VND).
- Adds funds (**credit**) and withdraws funds (**debit**); every debit is preceded by a
  fail-closed external-logging call.
- **Exchanges** money between two of the customer's accounts at fixed rates.
- Serves **paginated transaction history** (infinite scroll) and a **balance-over-time
  line chart**.
- Exports a transaction summary as a downloadable **PDF**.

---

## Table of contents

1. [Functional requirements](#functional-requirements)
2. [Tech stack](#tech-stack)
3. [High-level architecture](#high-level-architecture)
4. [Design decisions](#design-decisions)
5. [Business rules & domain model](#business-rules--domain-model)
6. [API](#api)
7. [Frontend](#frontend)
8. [Conventions](#conventions)
9. [How to run](#how-to-run)
10. [Project layout](#project-layout)
11. [Roadmap](#roadmap)

---

## Functional requirements

Reproduced from the assignment. Each **assignment requirement is stated as written**;
our own implementation decisions are marked with *→ …* so the two are never conflated.

### Part 1 — REST API (Java, Spring Boot)

The five operations the assignment asks for, and where they live:

| Operation (assignment) | Endpoint |
|------------------------|----------|
| Add money to account | `POST /api/account/credit` |
| Debit money from account | `POST /api/account/debit` (external-log call first) |
| Get account balance | `GET /api/account` / `GET /api/accounts` |
| Perform currency exchange (fixed rates) | `POST /api/exchange` |
| Retrieve transaction history for a given account | `GET /api/account/transactions` |

The assignment's requirements:

- **A user can have multiple accounts with separate balances.**
- **Every account has exactly one currency** (options: `EUR`, `USD`, `SEK`, `GBP`, `VND`).
- **Debiting orders can only use one currency (no automatic currency exchange).**
  *→ In our implementation that one currency is **EUR**: debit is allowed only on EUR
  accounts, and a debit on a non-EUR account is rejected with `422`. The assignment does
  not require EUR specifically — this is our simplification.*
- **Before debiting, simulate a call to an external system** (external logging) by
  calling a web page (the assignment suggests `https://httpstat.us/`).
  *→ We call `https://httpbun.com/status/200` and treat it **fail-closed**: if the call
  does not succeed, the debit is rejected (`502`) and nothing is committed. Timeouts and
  URL are configurable via `external-logging.*`.*
- **Microservice (self-contained).**
- **Saved in an SQL DB** (the assignment allows H2). *→ H2: file-backed at runtime,
  in-memory for tests; schema versioned with Liquibase.*

### Part 2 — Angular SPA

The assignment's three pages and their sub-requirements:

| Requirement (assignment) | Where |
|--------------------------|-------|
| **Home** — shows all accounts for the current user; balance & currency each; click an account → Account Overview | Home page |
| **Account Overview** — info about one account; balance & currency | Account Overview page |
| — transaction history, limited by default + loaded dynamically on scroll (**infinite scroll**, no page reload) | keyset cursor + `IntersectionObserver` |
| — click a transaction → Transaction Overview | history rows link out |
| — **line chart** of the historic balance time-series (time × balance) | Chart.js on `/balance-series` |
| **Transaction Overview** — info about one transaction; **export & download** the summary as a `*.pdf` | Transaction Overview page |
| *Nice to have* — **NgRx** state management | one feature-store per domain |

Notes on our interpretation:

- The assignment says the Home page shows accounts for *the current user* but does not
  specify how that user is chosen. *→ With no authentication, we select the current user
  by **username**: the Home page is a username search, and the chosen username travels in
  the `X-Username` header (see [API](#api)).*
- Beyond the assignment's read-only pages, the Account Overview also offers **credit /
  debit / exchange** action forms, so money movements can be performed from the UI —
  convenient for the live demo (see [Roadmap](#roadmap)).
- Per the assignment's note, the UI is **functional, not styled** for polish.

## Tech stack

| Area | Choice |
|------|--------|
| Language / runtime | Java 25 (LTS) |
| Backend | Spring Boot 4.1.x, blocking Spring MVC (+ virtual threads) |
| Persistence | Spring Data JDBC, H2 (file at runtime, in-memory for tests), Liquibase 5.0.3 |
| IDs | TSID (`com.github.f4b6a3:tsid-creator`) for transaction ids |
| API docs | springdoc-openapi 3.0.3 — Swagger UI at `/swagger-ui/index.html` |
| HTTP client | RestClient (outbound external-logging call) |
| PDF | OpenPDF 3.0.5 (`org.openpdf.text`) |
| Build | Gradle Wrapper, version catalog (`backend/gradle/libs.versions.toml`) |
| Frontend | Angular 21 (standalone, zoneless), NgRx 21, Chart.js, Vitest |

---

## High-level architecture

A **modular monolith** designed so any feature could later be extracted into its own
service. Two structuring choices:

- **Package-by-feature** at the top level — code is grouped by business domain, not by
  technical layer: `com.homework.{account, customer, transaction, exchange, reporting, history}`.
  Each feature is self-contained.
- **Classic layered architecture** *inside* each feature — a **controller → service →
  repository** call chain over a shared **domain**, with a separate **integration** layer
  for outbound calls to external systems.

### The layers inside a feature

Dependencies point inward toward the domain
(`controller` → `service` → `repository` / `integration` → `domain`):

- **`domain`** — the aggregates/entities and enums (`Account`, `AccountTransaction`,
  `Currency`, `AccountType`, `LedgerCode`, `TransactionType`). Plain objects that double
  as the Spring Data JDBC entities; no framework logic.
- **`controller`** — Spring MVC REST controllers + request/response DTOs (the
  presentation layer). Controllers convert DTOs ↔ domain and call services.
- **`service`** — the application/business logic, each exposed as an **interface with a
  single `…Default` implementation**: `CreditAccountService`, `DebitAccountService`,
  `ExchangeService`, and `TransactionService` (credit, debit, exchange, read), plus
  `AccountService` and `CustomerService` (account access and username lookup),
  `TransactionHistoryService` / `BalanceSeriesService` (history), and `PdfService` (PDF
  export). Supporting/technical collaborators: `LedgerHandlerService`,
  `TsidTransactionIdGenerator`, `PdrRender` (the OpenPDF renderer), and
  `CurrencyExchangeService` / `CurrencyExchangeServiceMock`. No web or persistence
  concerns leak in.
- **`repository`** — Spring Data JDBC persistence: a Spring Data `CrudRepository` DAO
  (`AccountDao`) wrapped by a `@Repository` class (`AccountRepository`) that holds the
  real persistence logic (business-id sequences, timestamps, and the running-balance
  window query). Most features expose this as one concrete class; `history` splits it
  into an `AccountHistoryRepository` interface + `AccountHistoryRepositoryDefault`, since
  its hand-written window query is worth mocking in isolation.
- **`integration`** — adapters for outbound calls to **external systems**: the
  `ExternalLoggingClient` (RestClient call to the status endpoint) and its config. Only
  features that talk to the outside world carry this package.
- **`exception`** — the feature's own exception types (`AccountNotFoundException`,
  `InsufficientFundsException`, `ExternalLoggingException`, …), collected in one place and
  mapped to HTTP responses by the shared `common/web/GlobalExceptionHandler`.

Each service is an **interface with a single `…Default` implementation**, so callers
depend on the abstraction and every feature stays mockable in isolation — the assignment
explicitly calls for this on exchange (`CurrencyExchangeService` with a
`CurrencyExchangeServiceMock`). Repositories are the exception: outside `history` they
are plain concrete `@Repository` classes, since a single persistence implementation gains
nothing from the indirection. Not every feature has every layer — a feature carries only
the packages it needs (e.g. `exchange` is just `service`; `reporting` is `controller` +
`service`). This keeps a feature testable in isolation and later extractable into its own
service.

---

## Design decisions

### Persistence & model strategy — two models, not three

With **Spring Data JDBC**, the domain aggregate doubles as the persistence entity
(`@Table`/`@Id` on the aggregate). There is **no** separate entity twin and **no**
domain↔entity mapper. Unlike JPA, Spring Data JDBC loads plain immutable objects (no
proxies, lazy loading, or dirty tracking), so the aggregate serves as the entity with
negligible framework leakage. The fully separate "domain + entity + mapper" variant was
rejected as boilerplate that buys little on this stack.

The separations we *do* keep are the ones that pay off:

- **Persistence lives in the `repository` layer**: a thin `@Repository` class wraps the
  Spring Data `CrudRepository` and holds the only real persistence logic (business-id
  sequence assignment, timestamps). Services depend on that repository directly; only
  `history` puts an interface in front of it (its window query is worth mocking), while a
  single-implementation repository elsewhere gains nothing from the indirection.
- **Request/response DTOs** live at the **web boundary** (the `controller` layer) and are
  converted there — never in the service, which speaks only domain. This decouples the
  API contract from the internal model.

### Why Spring Data JDBC (not JPA)

The running-balance time-series is a hand-written SQL **window function**
(`SUM(...) OVER (ORDER BY created_at)`). Spring Data JDBC keeps the domain pure and
makes such SQL natural, which JPA's abstraction would fight.

### Blocking MVC, not reactive

The data layer (Spring Data JDBC / H2) is blocking and infinite scroll is
request/response pagination, not streaming — so RestClient (not WebClient) is used for
the outbound call, and **virtual threads** (`spring.threads.virtual.enabled`) are the
concurrency answer rather than going reactive.

---

## Business rules & domain model

### Rules

- A **customer** owns multiple **accounts**; each account holds **exactly one currency**
  — `EUR`, `USD`, `SEK`, `GBP`, or `VND`.
- **Credit** adds funds to an account (any currency). **Debit** withdraws funds — with
  **no** automatic currency exchange, and is **only allowed on `EUR` accounts** (a debit
  on a non-EUR account is rejected with `422`; the SPA hides the debit action for those).
- Every **debit** is preceded by a successful outbound **external-logging call** (an
  HTTP call to a status endpoint — `https://httpbun.com/status/200` by default;
  connect/read timeouts `2s`/`3s`; all configurable via `external-logging.*`). The
  operation is **fail-closed** — if that call does not succeed, the debit is rejected
  and nothing is committed (so debit and exchange require that endpoint to be
  reachable).
- **Currency exchange** moves money between two of the customer's accounts using
  **fixed rates** (`CurrencyExchangeService` / `CurrencyExchangeServiceMock`; see below).
- **Ownership** is enforced: an account or transaction not owned by the current customer
  returns **`404`** (never leaks existence).
- Amounts are positive magnitudes in `DECIMAL(19,2)` / `BigDecimal` — never floating
  point.

### The double-entry ledger

Every financial operation writes a set of **balanced rows** to `account_transaction`,
grouped by one `transaction_id`, such that **each currency independently sums to zero**.

- **Accounts** are of two kinds (`account_type`), and the two identities are mutually
  exclusive (enforced by a DB `CHECK`, see [Data integrity](#data-integrity)):
  - `CUSTOMER` — a customer wallet, exactly one currency: `customer_id` set, `ledger_code`
    null.
  - `LEDGER` — a system account addressed by a role `ledger_code` (`LedgerCode`:
    `FX_CLEARING`, `EXTERNAL`), one per currency, with no owning customer: `ledger_code`
    set, `customer_id` null.
- **Credit / debit** = **2 rows**, routed through the `EXTERNAL` ledger account of that
  currency.
- **Exchange** = **4 rows**, routed through the two `FX_CLEARING` accounts (source and
  target currency), so each currency group balances on its own and the FX-clearing
  accounts carry the resulting currency position. A customer's per-account history stays
  strictly single-currency.
- A row's `type` is `CREDIT`/`DEBIT`; `amount` is a positive magnitude; `balance` on
  `account` is a cached aggregate.

### Fixed exchange rates

Rates are anchored to EUR (`CurrencyExchangeServiceMock`). A cross-rate is `to ÷ from`,
applied to the source amount and rounded HALF_UP to 2 decimals:

| Currency | Units per 1 EUR |
|----------|-----------------|
| EUR | 1.00 |
| USD | 1.08 |
| SEK | 11.30 |
| GBP | 0.85 |
| VND | 27000.00 |

### Identifiers

- **`account`** has a technical primary key `id` (identity) and a separate **business
  `account_code`** (unique, from a DB sequence in the range **1,000,000–9,999,999**). The
  ledger references accounts by the business `account_code`, never the technical `id`.
- **`transaction_id`** is a **TSID** (time-sortable id), generated in the application and
  stamped across every leg of one operation, stored as a 13-char string.

### Data integrity

Closed value sets are enforced both in code (enums `Currency`, `AccountType`,
`LedgerCode`) and in the database (`CHECK` constraints on `currency`, `account_type`,
`ledger_code`). A further `CHECK` ties `account_type` to its identity columns — a
`CUSTOMER` row must have a `customer_id` and no `ledger_code`, a `LEDGER` row the reverse
— so the two account kinds can never be mixed. Money is encoded as **strings** in JSON to
protect `DECIMAL(19,2)` precision from JavaScript floats.

### Seeded demo data

Five customers — `demo`, `alice`, `bob`, `johndoe`, `janedoe` — are seeded (identified by
**username**), each with accounts in several currencies, plus the system `LEDGER`
accounts, so the app is usable immediately.

`johndoe` and `janedoe` also have accounts wired for a **populated EUR account** demo of
the history, infinite scroll, and balance chart. Because a transaction is a balanced
ledger entry (not a single row), the history isn't static seed SQL — it's generated by
driving the real API with `scripts/generate-demo-transactions.sh` (see
[Populate demo history](#populate-demo-history)).

---

## API

### Identifiers in headers (MVP, no auth)

There is no authentication (not required). The "current user" and every acted-on
identifier travel in **request headers**, not the URL: `X-Username` (the customer),
`X-Account-Code`, `X-Source-Account-Code` / `X-Target-Account-Code`, `X-Transaction-Id`. The
customer is identified by **username end-to-end** — the technical primary key is never
the application-level identity: a `HandlerMethodArgumentResolver` validates `X-Username`
(`404` if the username is unknown, `400` if the header is missing) and injects the
**username** into controllers, which thread it through the services. The customer PK
exists only as the `account.customer_id` foreign key inside the database (the account
lookup joins `customer` on `username`); it never appears in the app layer or any DTO.
Request bodies carry
only the payload (`amount`, `description`). Methods follow HTTP semantics — `GET` for
reads, `POST` for writes. Taking identity from a header with no auth is an **IDOR by
design**; with authentication, `X-Username` would be replaced by the token and the URLs
would not change. Errors are RFC-9457 `application/problem+json`.

### Endpoints

| Method & path | Purpose |
|---|---|
| `GET /api/customer` | resolve the current customer from the `X-Username` header (returns `{username}`, `404` if unknown) |
| `GET /api/accounts` | list the customer's accounts (currency + balance) |
| `GET /api/account` | one account (`X-Account-Code`) |
| `POST /api/account/credit` | add funds (`X-Account-Code`, body `amount`) |
| `POST /api/account/debit` | withdraw funds (external log first, then debit) |
| `POST /api/exchange` | fixed-rate transfer (`X-Source-Account-Code`, `X-Target-Account-Code`) |
| `GET /api/transaction` | one transaction's legs (`X-Transaction-Id`) |
| `GET /api/transaction/pdf` | downloadable transaction-summary PDF (OpenPDF) |
| `GET /api/account/transactions?limit=&cursor=` | history, newest-first, **keyset pagination** for infinite scroll (`nextCursor` `null` on the last page) |
| `GET /api/account/balance-series` | running-balance time-series for the line chart (SQL window function) |

Explore every endpoint via **Swagger UI** at
`http://localhost:8080/swagger-ui/index.html`.

---

## Frontend

Angular 21 (standalone, **zoneless** → signals) with **NgRx** one feature-store per
domain and **Chart.js**.

- **Home** — a **username search**: enter a customer's username to see that customer and
  their accounts (currency + balance); each account links to its overview.
- **Account Overview** (`/accounts/:accountCode`) — account header, a **balance line
  chart** (Chart.js on `/balance-series`), and **infinite-scroll history** (keyset
  cursor, driven by an `IntersectionObserver` on a bottom sentinel). It also exposes
  **credit / debit / exchange** action forms; on success the balance, history, and
  chart refresh.
- **Transaction Overview** (`/transactions/:transactionId`) — the transaction's legs and
  an **Export PDF** button that fetches the PDF as a blob (so the `X-*` headers are sent)
  and triggers a download.

A `CurrentCustomer` service holds the selected username (persisted to `localStorage`); a
functional `HttpInterceptor` adds it as `X-Username` on every `/api` request, and the
services add the per-request `X-Account-Code` / `X-Transaction-Id`. In development, a proxy
forwards `/api` → `:8080` (no CORS).

---

## Conventions

- **Liquibase** — changelogs are **XML** (never YAML), split into
  `db/changelog/changes/ddl` and `.../dml`; **one file per table**, numbered with a
  3-digit prefix (a table's own sequence lives in that table's file). Changeset `author`
  is `igor`; the XSD is pinned to a concrete version (`dbchangelog-5.0.xsd`), not
  `-latest`.
- **No code comments** — code is self-explanatory through naming and structure; domain
  vocabulary, business flow, and requirements are documented in this README, not
  scattered in source.
- **H2 identifiers** — datasource URLs set `CASE_INSENSITIVE_IDENTIFIERS=TRUE` so
  Liquibase-created (uppercase) tables match Spring Data JDBC's quoted identifiers.
- **Web DTO naming** — DTOs live in each feature's `controller` layer:
  - a **full response body** a controller returns → **`...Response`**
    (`TransactionResponse`, `HistoryPageResponse`, `BalanceSeriesResponse`);
  - a **full request body** (`@RequestBody`) → **`...Request`** (`AmountRequest`);
  - a **nested component** — a collection element, sub-object, or a bare entity returned
    directly — gets a **descriptive name**, not a generic suffix (`AccountSummary`,
    `TransactionLeg`, `TransactionSummary`, `BalanceSnapshot`).

    No `Dto`/`View` suffixes; a name should not encode its consumer. A bare entity reuses
    its component name for both single and list endpoints (`GET /account` →
    `AccountSummary`, `GET /accounts` → `List<AccountSummary>`). Presentation component
    names stay distinct from domain value names in the same feature (domain has
    `HistoryItem`/`BalancePoint`; the DTOs are `TransactionSummary`/`BalanceSnapshot`).
    Technical persistence ids never leak into domain values or DTOs.

---

## How to run

### Prerequisites

- **JDK 25**
- **Node.js** (v20+); an even-numbered LTS is recommended for production builds

### Development (two processes)

Backend — http://localhost:8080

```
cd backend
.\gradlew.bat bootRun
```

Frontend — http://localhost:4200 (proxies `/api` → `:8080`, so no CORS)

```
cd frontend
npm install
npm start
```

Then open **http://localhost:4200**. Quick checks:

- Backend health: http://localhost:8080/actuator/health → `{"status":"UP"}`
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- H2 console: http://localhost:8080/h2-console

### Populate demo history

The seed migrations create the customers and accounts but leave the ledger empty. To get
a **populated EUR account** for `johndoe` and `janedoe` (so the history, infinite scroll,
and balance chart have data), drive the real API with:

```
bash scripts/generate-demo-transactions.sh [BASE_URL] [COUNT_PER_CUSTOMER]
```

It generates transactions against the running backend — every entry is a valid
double-entry ledger operation, since the app (not raw SQL) enforces the balances, TSIDs,
the external-logging prerequisite, and EUR-only debit. Both arguments are optional:

| Argument | Default | Meaning |
|---|---|---|
| `BASE_URL` | `http://localhost:8080` | where the backend is reachable |
| `COUNT_PER_CUSTOMER` | `100` | transactions per customer (a mix of deposits ≈45%, withdrawals ≈28%, exchanges ≈27%) |

```
bash scripts/generate-demo-transactions.sh                       # 100 txns each, localhost
bash scripts/generate-demo-transactions.sh http://localhost:8080 250
```

**Prerequisites:** the backend is running and the `johndoe` / `janedoe` accounts are
seeded (Liquibase, on startup); the script needs `bash` and `curl`. It drives each
customer's seeded EUR account (`1000041` / `1000051`), exchanging into their USD/GBP/SEK
accounts. On an external-logging hiccup (`502`) it pauses and falls back to a deposit so
progress isn't blocked. It is **idempotent-friendly, not idempotent** — re-running simply
**appends** more history; to start clean, stop the app and delete the H2 file
(`backend/data/`), then restart to re-seed.

### Single-deployable demo jar

Bundles the built SPA into the backend jar so one process serves both the API and the
app:

```
cd backend
.\gradlew.bat clean bootJar -PbundleFrontend
java -jar build/libs/accounts-0.0.1-SNAPSHOT.jar
```

Then open http://localhost:8080 — the SPA is served from `/`, the API from `/api/**`.
(A plain `.\gradlew.bat build` does **not** build the frontend, keeping dev builds fast.)

### Tests

```
cd backend
.\gradlew.bat test        # JUnit 5 (unit + @SpringBootTest MockMvc)

cd ../frontend
npx ng test --watch=false # Vitest
```

---

## Project layout

```
homework_1/
├── backend/     Spring Boot 4 app (Gradle) — package-by-feature + layered
├── frontend/    Angular 21 SPA (standalone, NgRx, Chart.js)
├── scripts/     generate-demo-transactions.sh — drives the API to populate demo history
└── README.md    this file
```

## Roadmap

Enhancements beyond the assignment's scope:

| Item | Notes |
|------|-------|
| Retry & circuit breaker for external logging | Wrap the pre-debit call in `ExternalLoggingClient` with a **retry (exponential backoff)** and a **circuit breaker** that trips after N unsuccessful attempts (e.g. Spring Retry / Resilience4j). Today it is a single fail-closed attempt, so a transient outage of the status endpoint blocks every debit and exchange. |
| Move the external call out of the DB transaction | In `ExchangeServiceDefault` (and the debit path) the RestClient call runs **inside** `@Transactional`, so a slow status endpoint holds a DB connection and row locks open for the whole HTTP round-trip. Validate and log **before** opening the transaction, keeping the transactional scope to the ledger writes only. |
| Concurrency safety on balance updates | `LedgerHandlerServiceDefault.applyToBalance` does a read-modify-write on `account.balance`, so two concurrent debits/exchanges can lost-update each other. Guard it with **optimistic locking** (`@Version` on `Account`, callers retry on failure) or an **atomic `UPDATE account SET balance = balance + :delta WHERE account_code = :code`** (which can also enforce a non-negative balance in SQL). |
| Request validation at the controller layer | Validate incoming request bodies/params in the web layer (Bean Validation — `@Valid`, `@Positive`, `@NotNull`, `@DecimalMin` on the request DTOs; `MethodArgumentNotValidException` → `400` in `GlobalExceptionHandler`), **not** in the services. Amounts must be positive and non-null, currencies within the allowed set, etc. Keeps services free of input-shape checks and rejects malformed requests before any business logic runs. |
| Money-operation UI | The credit / debit / exchange forms on the Account Overview page go beyond the assignment's read-only pages; kept for demo convenience — revisit whether to retain for the final submission. |
| Custom chart date ranges | Filter the balance line chart by a selectable time range. Pairs with the balance-series item below so the window function runs over a bounded input. |
| Bounded balance-series query | `getBalanceSeries` recomputes the running balance over **all** of an account's transactions on every chart load (no bound, no filter). Add a date/row window so the query stays cheap as history grows. |
| Production database | Swap H2 for a production SQL database (e.g. PostgreSQL) via a profile. |
| Authentication | Replace the `X-Username` header with an auth token (URLs stay the same). |
| Index `account.customer_id` | The account-by-customer lookup joins on this FK, but H2 does not auto-index FK columns. The transaction-history indexes (`(account_code, created_at)`, `transaction_id`) and the `username` / `account_code` unique indexes already exist — this FK is the remaining gap. |
| Cursor-only history paging | Because `transaction_id` is a TSID (time-sorted id), a history page can be fetched with `transaction_id` + `limit` alone, dropping `created_at` from the cursor, `WHERE`, and `ORDER BY`; the supporting index then simplifies to `(account_code, transaction_id)`. |
| Unit-test the money core | Add direct tests for the riskiest units — `ExchangeServiceDefault` (four-leg construction) and the balance math in `LedgerHandlerServiceDefault` — plus a parallel-debit test that exercises the concurrency guard above. |

### Frontend

| Item | Notes |
|------|-------|
| Input validation & submit guarding | The credit / debit / exchange amount fields are plain text inputs guarded only by a truthiness check, and the action effects use `mergeMap`, so malformed values (`"abc"`) or a double-click fire bad/duplicate requests. Add a positive-number check, `inputmode="decimal"`, disable the submit button while an action is in flight (or `exhaustMap` to drop duplicate submits). Complements the controller-layer validation above — the backend stays authoritative. |
| Time-scale chart axis | `BalanceChart` maps each point to a `toLocaleString()` string label, so points are evenly spaced regardless of the real time gaps. Use a Chart.js **time scale** (`type: 'time'` + a date adapter) so transactions sit at their true temporal position, as the requirement ("time on the horizontal axis") intends. |
| Money formatting on display | Balances render raw (`1234.5 EUR`). Use `Intl.NumberFormat` / Angular's `currency` pipe for locale-aware output (`€1,234.50`). |
