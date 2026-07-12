# ADIMS — Anti-Doping Intelligence & Investigation Management System

A full-stack project for **intelligence gathering and investigation management in support of anti-doping efforts**:
athlete registry & testing pools, anonymous/informant intelligence tips, formal investigation case files
(with evidence & timeline notes), automated risk scoring, doping-control testing records, role-based
access control, and a full audit trail.

```
adims/
├── backend/     Spring Boot 3.3 (Java 21) REST API, JWT auth, Flyway migrations
├── frontend/    HTML + CSS + vanilla JS single-page-style console
└── docker-compose.yml   PostgreSQL for local development
```

---

## 1. Architecture overview

**Backend** — layered architecture:
```
controller  → REST endpoints, request validation, @PreAuthorize role checks
service     → interfaces + impl, business rules (risk scoring, case numbering, escalation)
repository  → Spring Data JPA repositories
entity      → JPA entities mapped 1:1 to Flyway-versioned tables
dto         → request/response DTOs (never expose entities directly)
security    → JWT filter, UserDetails, password hashing (BCrypt)
exception   → centralized @RestControllerAdvice error handling
```

**Database** — PostgreSQL, versioned exclusively through Flyway migrations
(`backend/src/main/resources/db/migration`). Tables: `users`, `athletes`, `intelligence_tips`,
`investigation_cases`, `evidence`, `case_notes`, `risk_assessments`, `testing_records`, `audit_logs`.

**Frontend** — plain HTML/CSS/JS (no build step). One page per module, a shared `api.js`
(fetch wrapper + JWT session handling) and `nav.js` (sidebar shell, role-based menu).

**Roles**
| Role | Can do |
|---|---|
| `ADMIN` | Everything, incl. user management & audit log |
| `INVESTIGATOR` | Manage athletes, tips, cases, evidence, notes, testing records |
| `ANALYST` | Review tips, recalculate risk, log testing records (read-mostly) |

---

## 2. Prerequisites

- JDK 21
- Maven 3.9+ (or use the included `mvnw` if you add one — not bundled here, use your local Maven)
- PostgreSQL 14+ (or Docker, see below)
- Any static file server for the frontend (do **not** open the HTML files with `file://` — see step 5)

---

## 3. Start the database

**Option A — Docker (recommended, zero manual steps)**
```bash
cd adims
docker compose up -d
```
This starts PostgreSQL on `localhost:5432` with database `antidoping_investigation`, user `adims_user`, password `adims_pass` — already matching the backend's defaults. Skip straight to step 4.

**Option B — you already have PostgreSQL installed locally**

Run the included script **once** to create the database, the app user, and grant privileges — this
must be done before the very first launch, and again any time you want to wipe the app back to a
clean slate:

- **pgAdmin (GUI):** open the Query Tool against the default `postgres` database, paste the
  contents of `backend/setup-database.sql`, and run it (F5).
- **Command line (Windows, from your PostgreSQL `bin` folder, e.g.
  `C:\Program Files\PostgreSQL\16\bin`):**
  ```
  psql -U postgres -f "C:\Users\User\Downloads\anti-doping-intelligence-system\adims\backend\setup-database.sql"
  ```
  It will ask for your `postgres` superuser password (the one you set when installing PostgreSQL).
- **Command line (macOS/Linux):**
  ```bash
  psql -U postgres -f adims/backend/setup-database.sql
  ```

This creates database `antidoping_investigation` owned by user `adims_user` (password `adims_pass`)
— exactly matching the defaults already in `application.yml`, so **you do not need to edit any
config file or set any environment variable.**

> Getting `FATAL: database "antidoping_investigation" does not exist`? It means this step was
> skipped, or the database was dropped afterward without being recreated. Just re-run the script
> above — it's safe to run repeatedly (it drops and recreates cleanly each time).

---

## 4. Run the backend

```bash
cd adims/backend
mvn spring-boot:run
```

On startup, **Flyway automatically runs the migrations** in `src/main/resources/db/migration`:
- `V1__init_schema.sql` — creates all tables, constraints and indexes
- `V2__seed_data.sql` — seeds 3 demo users, sample athletes, a tip, a case, evidence, notes,
  testing records and risk assessments so the app is immediately explorable

The API starts on **http://localhost:8080**.

Demo login accounts (change these before any real deployment):

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | ADMIN |
| `investigator` | `Investigator@123` | INVESTIGATOR |
| `analyst` | `Analyst@123` | ANALYST |

To build a runnable jar instead:
```bash
mvn clean package
java -jar target/anti-doping-intelligence-system.jar
```

### Configuration (environment variables)

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` | `localhost` | Postgres host |
| `DB_PORT` | `5432` | Postgres port |
| `DB_NAME` | `antidoping_investigation` | Database name |
| `DB_USERNAME` | `adims_user` | Database user |
| `DB_PASSWORD` | `adims_pass` | Database password |
| `JWT_SECRET` | (dev default, base64) | **Change in production** — HMAC signing key |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5500,...` | Comma-separated allowed frontend origins |

---

## 5. Run the frontend

Because the backend uses CORS (browsers block `file://` origins), serve the `frontend` folder
with any static server. Two easy options:

**Python (built-in almost everywhere):**
```bash
cd adims/frontend
python3 -m http.server 5500
```
Then open **http://localhost:5500**

**VS Code:** install the "Live Server" extension, right-click `frontend/index.html` → "Open with Live Server".

The frontend calls the API at `http://localhost:8080/api` by default (set in each page via
`window.ADIMS_API_BASE_URL`). If you serve the frontend from a different origin/port, add that
origin to `CORS_ALLOWED_ORIGINS` on the backend.

Log in with any of the demo accounts above.

---

## 6. Feature tour

- **Dashboard** — live counts (athletes, open cases, new tips), breakdowns by status/risk.
- **Athletes** — searchable registry, testing-pool classification, create/edit (Admin/Investigator).
  Athlete detail page shows full risk history, testing records, related tips and cases.
- **Intelligence Tips** — anyone signed in can log a tip (there's also an unauthenticated
  `POST /api/public/tips` endpoint for a true anonymous tip line); Investigators/Admins can
  escalate a credible tip directly into a formal investigation case (auto-generates a case
  number like `INV-2026-0007` and copies over context).
- **Investigations** — case list with filters, case file view with an editable status/priority,
  investigator assignment, a running notes timeline, and an evidence log.
- **Testing Records** — in/out-of-competition sample results feed directly into risk scoring.
- **Risk scoring** — a transparent, explainable weighted formula (open tips, open cases, adverse
  test results, prior violations) recalculated on demand from the athlete profile page.
- **Users** (Admin only) — create/deactivate investigator & analyst accounts.
- **Audit Log** (Admin only) — every state-changing action is recorded with actor, action, entity, timestamp.

---

## 7. Notes on the risk-scoring model

`RiskAssessmentServiceImpl.recalculate()` implements a simple, explainable rule-based score
(0–100, weighted: open tips ×10, open cases ×20, adverse test results ×25, prior violations ×15,
capped at 100) mapped to LOW / MEDIUM / HIGH / CRITICAL bands. This is intentionally transparent
and easy to defend/explain in a report or viva — a production system would instead calibrate these
weights against historical case outcomes.

---

## 8. Security notes for a real deployment

This project is built to demonstrate solid architecture for an academic/demo setting. Before any
real-world use you should additionally: rotate `JWT_SECRET` to a strong random value and keep it
out of version control, move tokens out of `localStorage` (e.g. httpOnly cookies) to reduce XSS
token-theft risk, add rate limiting on the public tip endpoint, add HTTPS/TLS termination, and add
field-level encryption for informant contact details.
