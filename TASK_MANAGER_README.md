# Task Manager (Spring Boot) - Local Run & Features

This project includes a small Spring Boot Task Manager demonstrating:
- IPFS integration (upload JSON to IPFS and store CID)
- OAuth2 (GitHub) protection for mutating endpoints
- Redis caching for reads
- Thymeleaf UI

Quick run (local dev using Docker):

1. Start services with Docker Compose (Postgres, Redis, IPFS):

```powershell
cd 'c:\Users\Admin\New folder\internship1'
docker-compose up -d
```

2. Build the application:

```powershell
mvn -DskipTests package
```

3. Run the app (example):

```powershell
java -jar target\internship1-0.0.1-SNAPSHOT.jar
# or pass a profile / env vars to point to Postgres
# e.g. set environment variables or use --spring.profiles.active=postgres
```

Configuration notes:
- `src/main/resources/application.yml` contains default H2 settings and placeholders for Redis and GitHub OAuth credentials.
- For GitHub OAuth: create an OAuth App at https://github.com/settings/developers and set `spring.security.oauth2.client.registration.github.client-id` and `client-secret` in `application.yml` or as env vars.
- IPFS daemon is expected at `http://localhost:5001` by default (Docker Compose exposes port 5001).

API endpoints:
- `GET /tasks` — list tasks
- `GET /tasks/{id}` — get task by id
- `POST /tasks` — create task (requires login when OAuth enabled)
- `PUT /tasks/{id}` — update task (requires login)
- `DELETE /tasks/{id}` — delete task (requires login)
- `GET /tasks/ipfs/{cid}` — fetch raw task JSON from IPFS by CID

Notes & next steps:
- Mutating endpoints are protected by OAuth2; supply GitHub client credentials to enable login.
- Redis must be running (see `docker-compose.yml`) to enable caching benefits.
- IPFS must be running for CID storage to work; when IPFS is unavailable the service will still persist tasks, but without a CID.
