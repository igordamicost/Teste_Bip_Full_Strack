# Benefícios - API e Frontend

Solução fullstack: backend Spring Boot (CRUD + transferência com validação e locking), autenticação JWT, Swagger, observabilidade (Actuator/Prometheus), frontend Angular e Docker.

## Pré-requisitos

- **Local:** JDK 17, Node 20+, Maven 3.8+
- **Docker:** Docker e Docker Compose (para rodar tudo em containers)

## Como rodar

### 1. Com Docker (recomendado)

Na raiz do projeto:

```bash
# Derrubar tudo
docker compose down

# Build e subir todos os serviços (DB, backend, frontend, Prometheus, Grafana)
docker compose up -d --build
```

Com tudo de pé:

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Actuator (lista): `http://localhost:8080/actuator`
- Health: `http://localhost:8080/actuator/health`
- Métricas Prometheus: `http://localhost:8080/actuator/prometheus`
- Prometheus UI: `http://localhost:9090`
- Grafana UI: `http://localhost:3000`
  - Usuário: `admin`
  - Senha: `admin`
  - Dashboard principal: `Dashboards → Browse → "Beneficio API Overview"`

O frontend em `http://localhost` usa o proxy nginx para `/api` apontando ao backend.

### 2. Backend local

```bash
cd backend-module

# Banco: executar db/schema.sql e db/seed.sql num PostgreSQL (ou use perfil dev com H2)
# Depois:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou com PostgreSQL:
export DB_HOST=localhost DB_PORT=5432 DB_NAME=beneficios DB_USER=postgres DB_PASSWORD=postgres
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- Actuator: http://localhost:8080/actuator (health, metrics, prometheus)

### 3. Frontend local

```bash
cd frontend
npm install
npm start
```

Acesse http://localhost:4200. Configure `src/environments/environment.ts` com `apiUrl: 'http://localhost:8080'` para apontar ao backend.

### 4. Testes

**Backend (JUnit):**

```bash
cd backend-module
mvn test
```

**Teste de carga (k6):**

```bash
# Instale k6 (https://k6.io) e, com o backend rodando:
k6 run -e BASE_URL=http://localhost:8080 backend-module/src/test/resources/load/k6-load.js
```

## Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/v1/auth/login | Login (retorna JWT) |
| GET | /api/v1/beneficios | Lista benefícios ativos |
| GET | /api/v1/beneficios/all | Lista todos |
| GET | /api/v1/beneficios/{id} | Busca por ID |
| POST | /api/v1/beneficios | Criar (body JSON) |
| PUT | /api/v1/beneficios/{id} | Atualizar |
| DELETE | /api/v1/beneficios/{id} | Excluir |
| POST | /api/v1/beneficios/transfer | Transferir (body: fromId, toId, amount) |

Todas as rotas de benefícios exigem header: `Authorization: Bearer <token>`.

## Observabilidade

### Backend (Spring Boot / Actuator)

- Health: `GET http://localhost:8080/actuator/health`
- Lista de métricas: `GET http://localhost:8080/actuator/metrics`
- Endpoint de métricas para Prometheus: `GET http://localhost:8080/actuator/prometheus`

### Prometheus

- URL: `http://localhost:9090`
- Target configurado: `backend:8080` (job `backend`, caminho `/actuator/prometheus`)
- Exemplos de queries:
  - `sum(rate(http_server_requests_seconds_count{job="backend"}[1m]))` – requisições/s
  - `histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job="backend"}[5m])) by (le))` – p95 de latência

### Grafana

- URL: `http://localhost:3000`
- Login: `admin` / `admin`
- Datasource default: **Prometheus** (`http://prometheus:9090`)
- Dashboard provisionado: **Beneficio API Overview**
  - Mostra RQPS, latência p50/p95/p99, taxa de erro, status HTTP, heap, threads e CPU

## Segurança

- Autenticação JWT; usuário padrão: `admin` / `admin123`.
- Em produção, defina `JWT_SECRET` e troque a senha do usuário (ou use outro mecanismo de usuários).
