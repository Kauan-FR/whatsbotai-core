# WhatsBotAI Core 📱🤖
## Version in English 🇺🇸

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](www.linkedin.com/in/kauan-santos-ferreira)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Kauan-FR)
[![Portfolio](https://img.shields.io/badge/Portfolio-000000?style=for-the-badge&logo=about.me&logoColor=white)](https://portifolio-kappa-rose.vercel.app)
[![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](kauanferreira3011@gmail.com)

> Production-grade backend for a multi-tenant WhatsApp AI bot SaaS, built with Spring Boot 4, Clean Architecture, and Domain-Driven Design principles.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](#license)

---

## Overview

WhatsBotAI Core is the backend service powering a SaaS platform that lets businesses deploy AI-powered WhatsApp bots without managing infrastructure. It handles tenant lifecycle, conversation routing, AI integration, and message delivery via the Baileys WhatsApp library.

This repository contains the **core domain and API layer**. Companion services (Baileys workers, AI inference) live in separate repositories.

### Key features

- **Multi-tenant architecture**: complete isolation between customer workspaces
- **Clean Architecture + DDD**: domain logic independent of frameworks and infrastructure
- **JWT-based authentication** with role-based access control
- **Production-ready observability**: Actuator endpoints, structured logging, build metadata exposure
- **Database migrations** managed by Flyway
- **Vector search** for RAG (Retrieval-Augmented Generation) via pgvector

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle 9 (Kotlin DSL) |
| Database | PostgreSQL 16 + pgvector |
| Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JJWT |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| DTO mapping | MapStruct |
| Resilience | Resilience4j + Bucket4j |
| Caching | Caffeine |
| Containerization | Docker (development) |
| Deployment target | Fly.io + Neon PostgreSQL |

---

## Architecture

The codebase follows **Clean Architecture** with four primary layers:

```
┌─────────────────────────────────────────────────┐
│  Presentation  (REST controllers, DTOs)         │
├─────────────────────────────────────────────────┤
│  Application   (Use cases, application services)│
├─────────────────────────────────────────────────┤
│  Domain        (Entities, value objects, rules) │  ← Framework-free
├─────────────────────────────────────────────────┤
│  Infrastructure (JPA, external APIs, adapters)  │
└─────────────────────────────────────────────────┘
```

**Dependency rule**: outer layers depend on inner layers. The Domain layer has no dependency on Spring, JPA, or any external library.

---

## Environment profiles

The application supports three Spring profiles, each targeting a distinct environment. **Profile activation is always explicit** — running without `--spring.profiles.active` fails fast to prevent accidental misconfiguration.

| Profile | Purpose | Database | Use case |
|---|---|---|---|
| `dev-docker` | Local development at home | Postgres via Docker | Default daily development |
| `dev-local` | Local development behind restricted networks | Native Postgres install | Used when Docker registry is blocked |
| `prod` | Production deployment | Neon PostgreSQL | Fly.io + local validation |

### Running each profile

```bash
# Development with Docker (home)
./gradlew bootRun --args='--spring.profiles.active=dev-docker'

# Development with native Postgres (restricted networks)
./gradlew bootRun --args='--spring.profiles.active=dev-local'

# Production validation locally (points to Neon)
./gradlew bootRun --args='--spring.profiles.active=prod'
```

In production (Fly.io), the profile is injected via the `SPRING_PROFILES_ACTIVE=prod` environment variable.

---

## Getting started

### Prerequisites

- Java 21 (OpenJDK or Eclipse Temurin recommended)
- Docker + Docker Compose (for `dev-docker` profile)
- Git
- `psql` CLI (optional, for direct database access)

### 1. Clone the repository

```bash
git clone https://github.com/(inserir link)/whatsbotai-core.git
cd whatsbotai-core
```

### 2. Create your local `.env`

Copy the template and fill in your environment credentials:

```bash
cp .env.example .env
```

Choose **one** of the variants documented inside `.env.example` based on the profile you'll run. The strategy is one `.env` per machine, with the same variable names across environments.

### 3. Start the database

**For `dev-docker`:**

```bash
docker compose up -d
```

This starts PostgreSQL 16 (with pgvector) on port `5432` and pgAdmin on port `5050`.

**For `dev-local`:** make sure your native Postgres is running and create the database:

```sql
CREATE DATABASE whatsbotai_dev;
```

### 4. Run the application

```bash
./gradlew bootRun --args='--spring.profiles.active=dev-docker'
```

On first run, Flyway will apply all migrations automatically.

### 5. Verify it's running

```bash
# Health check
curl http://localhost:8080/actuator/health

# Build info (version, git commit, build timestamp)
curl http://localhost:8080/actuator/info
```

---

## Observability

### Startup banner

The application logs a comprehensive startup banner showing the active profile, JVM details, memory limits, timezone, process ID, startup duration, and exposed Actuator endpoints. This makes environment misconfiguration immediately visible in logs.

### Actuator endpoints

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Liveness/readiness probes (used by orchestrators) |
| `/actuator/info` | Build metadata: version, git branch, commit hash, build timestamp |
| `/actuator/metrics` | JVM metrics, HTTP request stats, database pool metrics |

In production, only `/health` and `/info` should be publicly exposed. The `/metrics` endpoint requires authentication.

---

## Project structure

```
whatsbotai-core/
├── build.gradle.kts                       # Gradle build (Kotlin DSL)
├── docker-compose.yml                     # Local dev infrastructure
├── .env.example                           # Environment variables template
├── src/
│   ├── main/
│   │   ├── java/com/whatsbotai/
│   │   │   ├── WhatsbotaiCoreApplication.java
│   │   │   ├── config/                    # Cross-cutting Spring configuration
│   │   │   ├── domain/                    # Entities, value objects, domain services
│   │   │   ├── application/               # Use cases, application services
│   │   │   ├── infrastructure/            # JPA, external APIs, adapters
│   │   │   └── presentation/              # REST controllers, DTOs
│   │   └── resources/
│   │       ├── application.yml            # Base configuration
│   │       ├── application-dev-docker.yml
│   │       ├── application-dev-local.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/              # Flyway SQL migrations
│   └── test/
│       └── java/com/whatsbotai/           # Unit and integration tests
└── README.md
```

---

## Development workflow

This project follows **Conventional Commits** with English commit messages for international portfolio visibility. Examples:

```bash
feat(domain): add Tenant aggregate with email validation
fix(config): correctly read Actuator endpoints from YAML list format
refactor(config): split application config into profile-based structure
docs(readme): document three-profile environment strategy
```

### Code quality standards

- **Clean Code** and **SOLID** principles throughout
- **Domain-Driven Design** where complexity warrants it (rich domain models, value objects, domain events)
- **Test coverage** for domain and application layers (TDD encouraged)
- **Javadoc in English** for all public APIs
- **No `application.yml` secrets** — everything flows through `.env` and environment variables

---

## Database migrations

Migrations live in `src/main/resources/db/migration/` and follow the Flyway naming convention:

```
V<version>__<description>.sql
```

Example: `V1__create_tenant_table.sql`

Migrations are applied automatically on application startup. Never modify a migration that has already been applied to any environment.

---

## About the author

**Kauan Santos Ferreira**

Backend developer specializing in Java and Spring Boot, with production experience in enterprise systems at IFS Reitoria. Currently pursuing a degree in Systems Analysis and Development at Estácio, building portfolio projects with enterprise-grade standards.


---

## License

This project is proprietary and not licensed for public use, modification, or distribution. All rights reserved.

---

*This README is a living document and will be updated as the project evolves.*

---

## Versão em Português 🇧🇷

Back-end de nível de produção para um SaaS de bot de WhatsApp com IA multi-tenant, construído com Spring Boot 4, Clean Architecture e princípios de Domain-Driven Design.

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![License](https://img.shields.io/badge/Licença-Proprietária-red.svg)

## Visão Geral
O **WhatsBotAI Core** é o serviço de back-end que alimenta uma plataforma SaaS que permite que empresas implantem bots de WhatsApp com IA sem precisar gerenciar infraestrutura. Ele gerencia o ciclo de vida dos *tenants*, roteamento de conversas, integração com IA e entrega de mensagens por meio da biblioteca Baileys do WhatsApp.

Este repositório contém a camada de domínio e API principal. Os serviços complementares (workers Baileys, inferência de IA) residem em repositórios separados.

## Principais Recursos
- **Arquitetura multi-tenant:** isolamento completo entre os workspaces dos clientes
- **Clean Architecture + DDD:** lógica de domínio independente de frameworks e infraestrutura
- **Autenticação baseada em JWT** com controle de acesso baseado em funções (RBAC)
- **Observabilidade pronta para produção:** endpoints do Actuator, logs estruturados, exposição de metadados de build
- **Migrações de banco de dados** gerenciadas pelo Flyway
- **Busca vetorial para RAG** (Retrieval-Augmented Generation) via `pgvector`

## Stack Tecnológica
| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle 9 (Kotlin DSL) |
| Banco de Dados | PostgreSQL 16 + pgvector |
| Migrações | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Segurança | Spring Security + JJWT |
| Documentação da API | SpringDoc OpenAPI (Swagger UI) |
| Mapeamento de DTOs | MapStruct |
| Resiliência | Resilience4j + Bucket4j |
| Cache | Caffeine |
| Containerização | Docker (desenvolvimento) |
| Alvo de Deploy | Fly.io + Neon PostgreSQL |

## Arquitetura
O código segue a Clean Architecture com quatro camadas principais:

```
┌─────────────────────────────────────────────────┐
│  Apresentação  (REST controllers, DTOs)         │
├─────────────────────────────────────────────────┤
│  Aplicação     (Use cases, application services)│
├─────────────────────────────────────────────────┤
│  DomDomínio    (Entities, value objects, rules) │  ← Framework-free
├─────────────────────────────────────────────────┤
│  Infraestrutura (JPA, external APIs, adapters)  │
└─────────────────────────────────────────────────┘
```

**Regra de dependência:** camadas externas dependem das internas. A camada de Domínio não possui dependência do Spring, JPA ou qualquer biblioteca externa.

## Perfis de Ambiente
A aplicação suporta três perfis Spring, cada um voltado para um ambiente distinto. A ativação do perfil é sempre explícita — executar sem `--spring.profiles.active` falha rapidamente para evitar configurações acidentais incorretas.

| Perfil | Propósito | Banco de Dados | Caso de Uso |
| --- | --- | --- | --- |
| `dev-docker` | Desenvolvimento local em casa | Postgres via Docker | Desenvolvimento diário padrão |
| `dev-local` | Desenvolvimento local em redes restritas | Instalação nativa do Postgres | Usado quando o registry do Docker está bloqueado |
| `prod` | Validação de produção | Neon PostgreSQL | Fly.io + validação local |

### Executando cada perfil
```bash
# Desenvolvimento com Docker (casa)
./gradlew bootRun --args='--spring.profiles.active=dev-docker'

# Desenvolvimento com Postgres nativo (redes restritas)
./gradlew bootRun --args='--spring.profiles.active=dev-local'

# Validação de produção localmente (aponta para Neon)
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Em produção (Fly.io), o perfil é injetado via variável de ambiente SPRING_PROFILES_ACTIVE=prod.

---

## Primeiros Passos

### Pré-requisitos

- Java 21 (OpenJDK ou Eclipse Temurin recomendado)
- Docker + Docker Compose (para o perfil dev-docker)
- Git
- psql CLI (opcional, para acesso direto ao banco)

### 1. Clone o repositório

```bash
git clone https://github.com/(inserir link)/whatsbotai-core.git
cd whatsbotai-core
```

### 2. Crie seu `.env` local

Copie o template e preencha suas credenciais de ambiente:

```bash
cp .env.example .env
```
Escolha **uma** das variantes documentadas dentro de `.env.example` com base no perfil que você irá executar. A estratégia é um `.env` por máquina, com os mesmos nomes de variáveis entre ambientes.

### 3. Inicie o banco de dados

**Para `dev-docker`:**

```bash
docker compose up -d
```

Isso inicia o PostgreSQL 16 (com pgvector) na porta `5432` e o pgAdmin na porta `5050`.

**Para `dev-local`:** certifique-se de que seu Postgres nativo está rodando e crie o banco:

```sql
CREATE DATABASE whatsbotai_dev;
```

### 4. Execute a aplicação

```bash
./gradlew bootRun --args='--spring.profiles.active=dev-docker'
```

Na primeira execução, o Flyway aplicará todas as migrações automaticamente.

### 5. Verifique se está rodando

```bash
# Health check
curl http://localhost:8080/actuator/health

# Build info (version, git commit, build timestamp)
curl http://localhost:8080/actuator/info
```

---

## Observabilidade

### Banner de Inicialização

A aplicação registra um banner de inicialização abrangente mostrando o perfil ativo, detalhes da JVM, limites de memória, fuso horário, PID do processo, duração da inicialização e endpoints do Actuator expostos. Isso torna a má configuração do ambiente imediatamente visível nos logs.

### Endpoints do Actuator

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Probes de liveness/readiness (usados por orquestradores) |
| `/actuator/info` | Metadados do build: versão, branch do git, hash do commit, timestamp do build |
| `/actuator/metrics` | Métricas da JVM, estatísticas de requisições HTTP, métricas do pool do banco |

Em produção, apenas `/health` e `/info` devem ser expostos publicamente. O endpoint `/metrics` requer autenticação.

---

## Estrutura do Projeto

```
whatsbotai-core/
├── build.gradle.kts                       # Build do Gradle (Kotlin DSL)
├── docker-compose.yml                     # Infraestrutura local de dev
├── .env.example                           # Template de variáveis de ambiente
├── src/
│   ├── main/
│   │   ├── java/com/whatsbotai/
│   │   │   ├── WhatsbotaiCoreApplication.java
│   │   │   ├── config/                    # Configurações transversais do Spring
│   │   │   ├── domain/                    # Entidades, value objects, serviços de domínio
│   │   │   ├── application/               # Casos de uso, serviços de aplicação
│   │   │   ├── infrastructure/            # JPA, APIs externas, adaptadores
│   │   │   └── presentation/              # Controllers REST, DTOs
│   │   └── resources/
│   │       ├── application.yml            # Configuração base
│   │       ├── application-dev-docker.yml
│   │       ├── application-dev-local.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/              # Migrações SQL do Flyway
│   └── test/
│       └── java/com/whatsbotai/           # Testes unitários e de integração
└── README.md
```

---

## Fluxo de Desenvolvimento

Este projeto segue **Conventional Commits** com mensagens de commit em inglês para visibilidade em portfólios internacionais. Exemplos:

```bash
feat(domain): add Tenant aggregate with email validation
fix(config): correctly read Actuator endpoints from YAML list format
refactor(config): split application config into profile-based structure
docs(readme): document three-profile environment strategy
```

### Padrões de Qualidade de Código

- **Clean Code** e princípios **SOLID** em todo o projeto
- **Domain-Driven Design** onde a complexidade exige (modelos de domínio ricos, value objects, eventos de domínio)
- **Cobertura de testes** para as camadas de domínio e aplicação (TDD incentivado)
- **Javadoc em inglês** para todas as APIs públicas
- **Nenhum segredo no `application.yml`** — tudo flui através do `.env` e variáveis de ambiente

---

## Migrações de Banco de Dados

As migrações ficam em `src/main/resources/db/migration/` e seguem a convenção de nomenclatura do Flyway:
```
V<version>__<description>.sql
```

Exemplo: `V1__create_tenant_table.sql`

As migrações são aplicadas automaticamente na inicialização da aplicação. Nunca modifique uma migração que já tenha sido aplicada a qualquer ambiente.

---

## Sobre o Autor

**Kauan Santos Ferreira**

Desenvolvedor Back-end especializado em Java e Spring Boot, com experiência em produção em sistemas corporativos no IFS Reitoria. Atualmente cursando Análise e Desenvolvimento de Sistemas na Estácio, construindo projetos de portfólio com padrões de nível empresarial.

---


## License

Este projeto é proprietário e não está licenciado para uso, modificação ou distribuição pública. Todos os direitos reservados.

---



*This README is a living document and will be updated as the project evolves.*

---
