# Benefícios - API e Frontend

Solução fullstack: backend Spring Boot (CRUD + transferência com validação e locking), autenticação JWT, Swagger, observabilidade (Actuator/Prometheus), frontend Angular e Docker.

## Pré-requisitos

- **Local:** JDK 17, Node 20+, Maven 3.8+
- **Docker:** Docker e Docker Compose (para rodar tudo em containers)

## Como rodar

### 1. Com Docker (recomendado)

Na raiz do projeto:

```bash
# Build e subir todos os serviços (DB, backend, frontend)
docker compose up -d --build

# Frontend: http://localhost
# Backend API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# Login: admin / admin123
```

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

- **Health:** GET /actuator/health  
- **Métricas:** GET /actuator/metrics  
- **Prometheus:** GET /actuator/prometheus  

## Segurança

- Autenticação JWT; usuário padrão: `admin` / `admin123`.
- Em produção, defina `JWT_SECRET` e troque a senha do usuário (ou use outro mecanismo de usuários).
