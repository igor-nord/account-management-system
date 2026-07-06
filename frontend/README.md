# Frontend — Account Management SPA

Angular 21 single-page app for the Account Management System. It consumes the Spring Boot
REST API (see the [root README](../README.md) for the full picture) and covers the
assignment's three pages: Home, Account Overview, and Transaction Overview.

Standalone components, **zoneless** change detection (signals), **NgRx** state management
(one feature-store per domain), and **Chart.js** for the balance line chart.

## Prerequisites

- **Node.js** v20+ (an even-numbered LTS is recommended for production builds)
- The backend running on `http://localhost:8080` (the dev server proxies `/api` to it)

## Development server

```bash
npm install
npm start          # ng serve → http://localhost:4200
```

`proxy.conf.json` forwards `/api` → `http://localhost:8080`, so there is no CORS in
development. Open http://localhost:4200; the SPA reloads on source changes.

The current customer is selected on the Home page by **username** and persisted to
`localStorage`; an HTTP interceptor adds it as the `X-Username` header on every `/api`
request.

## Building

```bash
npm run build      # ng build → dist/ (production-optimized)
```

For the single-deployable demo jar, the backend's `bootJar -PbundleFrontend` task builds
this app and bundles it into the Spring Boot jar — see the [root README](../README.md).

## Running unit tests

```bash
npm test           # ng test (Vitest)
# or non-interactive:
npx ng test --watch=false
```

## Project structure

```
src/app/
├── core/               HTTP interceptor (X-Username), current-customer service
├── customer/           customer lookup by username (NgRx feature store)
├── accounts/           account list + account state (NgRx feature store)
├── home/               Home page — username search + account list
├── account-overview/   Account Overview — balance chart, infinite-scroll history, actions
└── transaction/        Transaction Overview — details + PDF export
```

Each domain feature owns its NgRx actions, reducer/feature, and effects. See the
[root README](../README.md) for architecture, API contract, and conventions.
