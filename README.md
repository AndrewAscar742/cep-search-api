# CEP Search API

API REST desenvolvida em Java com Spring Boot para consulta de endereços a partir de um CEP.

O projeto foi criado como parte de um desafio técnico e possui suporte a múltiplos ambientes por meio de Spring Profiles:

- `DSV`: consulta uma API externa mockada via WireMock.
- `PRD`: consulta a API pública ViaCEP.

A aplicação também persiste os endereços consultados em uma base PostgreSQL e expõe documentação interativa via Swagger/OpenAPI.

---

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Docker / Docker Compose
- WireMock
- SpringDoc OpenAPI / Swagger UI
- Actuator
- Maven 3.9.11

---

## Objetivo da API

A API permite consultar um endereço a partir de um CEP informado pelo cliente.

Fluxo principal:

```text
Cliente
  ↓
CEP Search API
  ↓
EnderecoService
  ↓
Provider ativo por profile
      ├── DSV -> WireMock
      └── PRD -> ViaCEP
  ↓
Mapeamento da resposta
  ↓
Persistência no PostgreSQL
  ↓
Resposta JSON ao cliente
```

---

## Profiles da aplicação

A aplicação utiliza Spring Profiles para separar os comportamentos por ambiente.

### Profile `dsv`

Utilizado para desenvolvimento local e validação com dependências containerizadas.

Neste profile, a API pode ser executada junto com:

- PostgreSQL 16 containerizado.
- WireMock simulando a API do ViaCEP.

### Profile `prd`

Utilizado para execução apontando para a API pública do ViaCEP.

Neste profile, a aplicação deve receber as configurações reais de banco de dados por variáveis de ambiente.

---

## Variáveis de ambiente

A aplicação espera as seguintes variáveis de ambiente:

| Variável | Descrição | Exemplo |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Profile ativo da aplicação | `dsv` ou `prd` |
| `DATABASE_URL` | String de conexão JDBC do PostgreSQL | `jdbc:postgresql://postgres-16:5432/cep_service_db` |
| `DATABASE_USER` | Usuário do banco de dados | `admin` |
| `DATABASE_PASSWORD` | Senha do banco de dados | `admin` |
| `VIACEP_BASE_URL` | URL base do provider de CEP | `http://wiremock:8080` ou `https://viacep.com.br` |
| `VIACEP_CONNECT_TIMEOUT_MS` | Timeout de conexão com o provider | `5000` |
| `VIACEP_READ_TIMEOUT_MS` | Timeout de leitura com o provider | `30000` |

Exemplo esperado nos arquivos `application-*.properties`:

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}

viacep.base-url=${VIACEP_BASE_URL}
viacep.connect-timeout-ms=${VIACEP_CONNECT_TIMEOUT_MS:5000}
viacep.read-timeout-ms=${VIACEP_READ_TIMEOUT_MS:30000}
```

---

## Estrutura recomendada para Docker e WireMock

```text
cep-search-api/
├── src/
├── wiremock/
│   ├── mappings/
│   └── __files/
├── Dockerfile
├── docker-compose.dsv.yml
├── docker-compose.prd.yml
├── pom.xml
└── README.md
```

A pasta `wiremock` deve ficar fora de `src/main/resources`, pois ela representa infraestrutura de desenvolvimento/teste, não recurso interno da aplicação Java.

---

## Docker Compose - Ambiente DSV

O arquivo `docker-compose.dsv.yml` deve subir os seguintes serviços:

- `postgres-16`: banco PostgreSQL 16.
- `wiremock`: servidor de mock para simular o ViaCEP.
- `cep-search-api`: aplicação Spring Boot.

Exemplo de configuração:

```yaml
services:
  postgres-16:
    image: postgres:16
    container_name: postgres-16
    restart: unless-stopped
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
      POSTGRES_DB: cep_service_db
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - cep-service-network

  wiremock:
    image: wiremock/wiremock:latest
    container_name: cep-service-wiremock
    restart: unless-stopped
    ports:
      - "8081:8080"
    volumes:
      - ./wiremock/mappings:/home/wiremock/mappings
      - ./wiremock/__files:/home/wiremock/__files
    command:
      - --verbose
    networks:
      - cep-service-network

  cep-search-api:
    build:
      context: .
      dockerfile: Dockerfile
    image: cep-search-api:latest
    container_name: cep-search-api
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dsv
      DATABASE_URL: jdbc:postgresql://postgres-16:5432/cep_service_db
      DATABASE_USER: admin
      DATABASE_PASSWORD: admin
      VIACEP_BASE_URL: http://wiremock:8080
      VIACEP_CONNECT_TIMEOUT_MS: 5000
      VIACEP_READ_TIMEOUT_MS: 30000
    depends_on:
      - postgres-16
      - wiremock
    networks:
      - cep-service-network

volumes:
  postgres_data:
    name: postgres_data

networks:
  cep-service-network:
    driver: bridge
```

### Subir o ambiente DSV

```bash
docker compose -f docker-compose.dsv.yml up -d --build
```

### Subir sem rebuildar a imagem

```bash
docker compose -f docker-compose.dsv.yml up -d
```

### Subir somente a API

```bash
docker compose -f docker-compose.dsv.yml up -d --build cep-search-api
```

### Subir somente o PostgreSQL

```bash
docker compose -f docker-compose.dsv.yml up -d postgres-16
```

### Subir somente o WireMock

```bash
docker compose -f docker-compose.dsv.yml up -d wiremock
```

### Parar e remover containers sem apagar volumes

```bash
docker compose -f docker-compose.dsv.yml down
```

### Parar e remover containers apagando volumes

> Use apenas quando quiser resetar o banco local.

```bash
docker compose -f docker-compose.dsv.yml down -v
```

---

## Docker Compose - Ambiente PRD

O arquivo `docker-compose.prd.yml` deve subir os seguintes serviços:

- `postgres-16`: banco PostgreSQL 16.
- `cep-search-api`: aplicação Spring Boot.

Exemplo:

```yaml
services:
  postgres-16:
    image: postgres:16
    container_name: postgres-16
    restart: unless-stopped
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
      POSTGRES_DB: cep_service_db
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - cep-service-network

  cep-search-api:
    build:
      context: .
      dockerfile: Dockerfile
    image: cep-search-api:latest
    container_name: cep-search-api
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prd
      DATABASE_URL: jdbc:postgresql://host-do-banco-prd:5432/cep_service_db
      DATABASE_USER: usuario_prd
      DATABASE_PASSWORD: senha_prd
      VIACEP_BASE_URL: https://viacep.com.br
      VIACEP_CONNECT_TIMEOUT_MS: 5000
      VIACEP_READ_TIMEOUT_MS: 30000
    networks:
      - cep-service-network

volumes:
  postgres_data:
    name: postgres_data

networks:
  cep-service-network:
    driver: bridge
```

### Subir o ambiente PRD

```bash
docker compose -f docker-compose.prd.yml up -d --build
```

### Parar o ambiente PRD

```bash
docker compose -f docker-compose.prd.yml down
```

---

## URLs úteis em DSV

Após subir o `docker-compose.dsv.yml`, os serviços ficam disponíveis no host nas seguintes URLs:

| Serviço | URL |
|---|---|
| API | `http://localhost:8080/cep-search-api` |
| Swagger UI | `http://localhost:8080/cep-search-api/swagger-ui/index.html` |
| Actuator Health | `http://localhost:8080/cep-search-api/actuator/health` |
| WireMock Admin | `http://localhost:8081/__admin/mappings` |
| PostgreSQL | `localhost:5433` |

Dentro da rede Docker, a API deve acessar os serviços pelos nomes dos services:

| Origem | Destino | URL interna |
|---|---|---|
| API | PostgreSQL | `jdbc:postgresql://postgres-16:5432/cep_service_db` |
| API | WireMock | `http://wiremock:8080` |

---

## Comandos úteis Docker

### Listar containers em execução

```bash
docker ps
```

### Listar todos os containers, inclusive parados

```bash
docker ps -a
```

### Ver logs da API

```bash
docker compose -f docker-compose.dsv.yml logs -f cep-search-api
```

### Ver logs do PostgreSQL

```bash
docker compose -f docker-compose.dsv.yml logs -f postgres-16
```

### Ver logs do WireMock

```bash
docker compose -f docker-compose.dsv.yml logs -f wiremock
```

### Reiniciar somente a API

```bash
docker compose -f docker-compose.dsv.yml restart cep-search-api
```

### Remover somente um service parado

```bash
docker compose -f docker-compose.dsv.yml rm cep-search-api
```

### Listar imagens e tamanhos

```bash
docker image ls
```

### Ver uso geral de espaço do Docker

```bash
docker system df
```

---

## Observações importantes

### Diferença entre acesso pelo host e acesso entre containers

No host, o WireMock é acessado por:

```text
http://localhost:8081
```

Dentro do container da API, o WireMock é acessado por:

```text
http://wiremock:8080
```

Isso acontece porque cada container possui seu próprio `localhost`. Dentro do container da API, `localhost` aponta para a própria API, não para o WireMock.

### Volume do PostgreSQL

O volume `postgres_data` persiste os dados do PostgreSQL mesmo que os containers sejam removidos com:

```bash
docker compose -f docker-compose.dsv.yml down
```

Os dados só são apagados se o volume for removido, por exemplo com:

```bash
docker compose -f docker-compose.dsv.yml down -v
```

### WireMock

Os mappings e responses do WireMock ficam versionados nas pastas:

```text
wiremock/mappings
wiremock/__files
```

Essas pastas são montadas no container por bind mount, permitindo alterar os mocks sem perder os arquivos ao recriar o container.

---

## Build manual da imagem

Caso deseje gerar a imagem manualmente, execute:

```bash
docker build -t cep-search-api:latest .
```

Para rodar manualmente em DSV:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dsv \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5433/cep_service_db \
  -e DATABASE_USER=admin \
  -e DATABASE_PASSWORD=admin \
  -e VIACEP_BASE_URL=http://host.docker.internal:8081 \
  cep-search-api:latest
```

Para rodar manualmente em PRD:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prd \
  -e DATABASE_URL=jdbc:postgresql://host-do-banco-prd:5432/cep_service_db \
  -e DATABASE_USER=usuario_prd \
  -e DATABASE_PASSWORD=senha_prd \
  -e VIACEP_BASE_URL=https://viacep.com.br \
  cep-search-api:latest
```
