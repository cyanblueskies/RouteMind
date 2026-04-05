# RouteMind

An intelligent pedestrian route planner that scores walking routes based on environmental comfort — noise, air quality, lighting, and wheelchair accessibility — using real OpenStreetMap data across England.

Unlike traditional navigation apps that optimise purely for speed, RouteMind helps users find routes that match their personal comfort preferences.

## Features

- **Multi-criteria route scoring** — Routes scored on noise, pollution, lighting, and wheelchair accessibility using real OSM road properties
- **Personalised preferences** — Adjust priority sliders to weight what matters most to you
- **Multiple route comparison** — View and compare up to 5 alternative routes with detailed score breakdowns
- **Community hazard reporting** — Report and confirm obstacles (physical, environmental, infrastructure)
- **Route saving & sharing** — Save favourite routes and share via URL
- **Accessibility modes** — Dark mode, high-contrast map, adjustable text size
- **England-wide coverage** — Routing powered by GraphHopper with England OSM data

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, Vite, Leaflet, Material UI, Tailwind CSS |
| Backend | Java 21, Spring Boot 3.4, GraphHopper 11 |
| Database | PostgreSQL (prod) / H2 (dev) |
| Auth | JWT + bcrypt |
| Testing | JUnit 5, Mockito, Vitest, React Testing Library |
| CI/CD | GitHub Actions |

## Architecture

```
┌─────────────────────────────────────────────┐
│                  Frontend                    │
│  React + Leaflet Map + Material UI          │
│  Components: Map, Search, RouteCards,       │
│  Preferences, HazardReport, SavedRoutes     │
└──────────────────┬──────────────────────────┘
                   │ REST API (JSON)
┌──────────────────▼──────────────────────────┐
│                  Backend                     │
│  Spring Boot REST Controllers               │
│  ┌─────────┐ ┌──────────┐ ┌──────────────┐ │
│  │ Router  │ │ Scoring  │ │   Hazard     │ │
│  │ Service │ │ Service  │ │   Service    │ │
│  └────┬────┘ └─────┬────┘ └──────┬───────┘ │
│       │            │             │          │
│  ┌────▼────┐ ┌─────▼────┐ ┌─────▼───────┐ │
│  │GraphHop │ │ Environ  │ │ PostgreSQL  │ │
│  │per + OSM│ │ Scorer   │ │ / H2        │ │
│  └─────────┘ └──────────┘ └─────────────┘ │
└─────────────────────────────────────────────┘
```

## Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL 15+ (or use H2 for development)
- England OSM data file

## Quick Start

### 1. Download OSM data

```bash
cd backend
curl -L -o england-latest.osm.pbf https://download.geofabrik.de/europe/great-britain/england-latest.osm.pbf
```

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx6g"
```

First startup builds the routing graph (~15 min). Subsequent starts load from cache (~30 sec).

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |
| POST | `/api/routes/scored` | Generate scored routes |
| GET | `/api/routes/saved` | Get user's saved routes |
| POST | `/api/routes/saved` | Save a route |
| DELETE | `/api/routes/saved/{id}` | Delete saved route |
| POST | `/api/hazards` | Report a hazard |
| GET | `/api/hazards/nearby` | Get nearby hazards |
| PATCH | `/api/hazards/{id}/upvote` | Confirm a hazard |
| GET | `/api/preferences` | Get user preferences |
| PUT | `/api/preferences` | Update preferences |

## Testing

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npx vitest run
```

## Screenshots

| Route Comparison | Hazard Reporting |
|:---:|:---:|
| ![routes](docs/screenshots/routes.png) | ![hazards](docs/screenshots/hazards.png) |

| Dark Mode | Preferences |
|:---:|:---:|
| ![dark](docs/screenshots/dark-mode.png) | ![prefs](docs/screenshots/preferences.png) |

## License

MIT
