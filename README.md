<div align="center">

```
███╗   ███╗██╗   ██╗    ██╗     ██╗██████╗ ██████╗  █████╗ ██████╗ ██╗   ██╗
████╗ ████║╚██╗ ██╔╝    ██║     ██║██╔══██╗██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
██╔████╔██║ ╚████╔╝     ██║     ██║██████╔╝██████╔╝███████║██████╔╝ ╚████╔╝
██║╚██╔╝██║  ╚██╔╝      ██║     ██║██╔══██╗██╔══██╗██╔══██║██╔══██╗  ╚██╔╝
██║ ╚═╝ ██║   ██║       ███████╗██║██████╔╝██║  ██║██║  ██║██║  ██║   ██║
╚═╝     ╚═╝   ╚═╝       ╚══════╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝
```

**Gerencie sua biblioteca pessoal com qualidade e elegância.**

[![CI](https://github.com/Erick-Badaro/MyLibrary/actions/workflows/ci.yml/badge.svg)](https://github.com/Erick-Badaro/MyLibrary/actions/workflows/ci.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=Erick-Badaro_MyLibrary&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Erick-Badaro_MyLibrary)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Erick-Badaro_MyLibrary&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Erick-Badaro_MyLibrary)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-NoSQL-47A248?logo=mongodb)](https://www.mongodb.com/)

</div>

---

## 📖 Sobre o Projeto

**MyLibrary** é uma aplicação web completa para cadastro e gerenciamento de livros de uma biblioteca pessoal. Desenvolvida como projeto semestral com foco em **qualidade de software**, **testabilidade** e **boas práticas de engenharia**.

O sistema permite que cada usuário gerencie sua própria coleção de livros, controlando o status de leitura de cada título — tudo isso com autenticação segura via JWT e persistência em MongoDB.

---

## ✨ Funcionalidades

| Funcionalidade | Descrição |
|---|---|
| 🔐 **Autenticação** | Cadastro e login de usuários com JWT |
| 📚 **CRUD de Livros** | Criar, listar, editar e deletar livros |
| 🔍 **Filtragem** | Filtrar livros por título, categoria e status de leitura |
| 📍 **Endereço via CEP** | Consulta automática de endereço pela API ViaCEP |
| 📊 **Cobertura** | Mínimo de 80% de cobertura de testes (JaCoCo) |

---

## 🏗️ Arquitetura

```
mylibrary/
├── src/
│   ├── main/java/com/projetogs/mylibrary/
│   │   ├── controller/        # Camada de entrada HTTP (REST)
│   │   ├── service/           # Regras de negócio
│   │   ├── repository/        # Acesso ao MongoDB
│   │   ├── entities/          # Modelos de domínio
│   │   ├── dto/               # Objetos de transferência
│   │   ├── enums/             # Enumerações (ReadingStatus)
│   │   ├── jwt/               # Geração e validação de tokens
│   │   └── security/          # Configuração Spring Security
│   └── test/java/com/projetogs/mylibrary/
│       ├── repository/        # Testes de repositório (Testcontainers)
│       ├── service/           # Testes de serviço (Testcontainers)
│       ├── controller/        # Testes E2E (MockMvc)
|       ├── dto/               # Testes Data Transfer Object
│       └── vcr/               # Testes VCR (MockWebServer)
└── frontend/                  # Interface Web
```

---

## 🛠️ Stack Tecnológica

### Backend
- **Java 21** + **Spring Boot 3.5**
- **Spring Security** + **JWT** (OAuth2 Resource Server)
- **MongoDB** (NoSQL) via Spring Data
- **Swagger / OpenAPI** (springdoc)

### Frontend
- Interface Web responsiva com gerenciamento de sessão
- React + Tailwind

### Qualidade & Testes
- **JUnit 5** — testes unitários e parametrizados
- **Testcontainers** — MongoDB real em Docker (sem mocks)
- **MockWebServer (OkHttp)** — padrão VCR para APIs externas
- **JaCoCo** — relatório de cobertura de código
- **SonarCloud** — análise estática de qualidade

### DevOps
- **GitHub Actions** — Pipeline CI completo
- **Docker** — via Testcontainers nos testes

---

## 🧪 Estratégia de Testes

> ⚠️ **O uso de Mocks está proibido neste projeto.** Todos os testes utilizam infraestrutura real via Testcontainers ou VCR para chamadas externas.

### Tipos de Teste

```
┌─────────────────────────────────────────────────────────┐
│                    PIRÂMIDE DE TESTES                   │
│                                                         │
│              ▲  Controller (Caixa Preta / E2E)          │
│             ▲▲▲  Service (Caixa Branca / Integração)    │
│            ▲▲▲▲▲  Repository (Unitário / Persistência)  │
│           ▲▲▲▲▲▲▲  VCR (APIs Externas)                  │
└─────────────────────────────────────────────────────────┘
```

| Tipo | Arquivo | Infraestrutura |
|---|---|---|
| **Repositório** | `RepositoryTest.java` | Testcontainers (MongoDB) |
| **Serviço — Usuário** | `UserServiceTest.java` | Testcontainers (MongoDB) |
| **Serviço — Livro** | `BookServiceTest.java` | Testcontainers (MongoDB) |
| **VCR — ViaCEP** | `ZipCodeServiceTest.java` | MockWebServer (OkHttp) |

### Cobertura Mínima Exigida

```
✅ Cobertura global: ≥ 80%   (verificada pelo JaCoCo + SonarCloud)
✅ Todos os requisitos funcionais cobertos (ver RTM.md)
```

---

## 🚀 Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker Desktop (para os testes com Testcontainers)
- MongoDB local ou via `.env`

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto `mylibrary/`:

```env
MONGO_URI=mongodb://localhost:27017
DB_NAME=mylibrary
JWT_SECRET_RAW=sua-chave-secreta-aqui-minimo-32-chars
```

### Rodando a Aplicação

```bash
# Clone o repositório
git clone https://github.com/Erick-Badaro/MyLibrary.git
cd MyLibrary/mylibrary

# Execute a aplicação
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.
A documentação Swagger em `http://localhost:8080/swagger-ui.html`.

### Rodando os Testes

```bash
# Todos os testes (requer Docker rodando)
./mvnw clean verify

# Apenas uma classe de teste
./mvnw -Dtest=BookServiceTest test

# Relatório de cobertura (gerado em target/site/jacoco/index.html)
./mvnw clean verify
open target/site/jacoco/index.html
```

---

## 📡 Endpoints da API

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/user/signup` | Cadastrar novo usuário |
| `POST` | `/user/login` | Autenticar e obter token JWT |
| `GET` | `/user/zipcode/{zipcode}` | Consultar endereço por CEP |

### Livros *(requer token JWT)*

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/books` | Listar todos os livros do usuário |
| `GET` | `/books?status=READ` | Filtrar por status de leitura |
| `POST` | `/books` | Cadastrar novo livro |
| `PUT` | `/books/{bookid}` | Atualizar livro |
| `DELETE` | `/books/{bookid}` | Remover livro |

### Status de Leitura

```
WANNA_READ   →  Quero ler
READING      →  Lendo
READ         →  Lido
```

---

## ⚙️ Pipeline CI/CD

O projeto possui um pipeline completo no GitHub Actions que executa automaticamente a cada push ou PR nas branches `main` e `dev`:

```
push / pull_request
        │
        ▼
┌───────────────────┐
│  1. Testes        │  JUnit 5 + Testcontainers + MockWebServer
│  2. Cobertura     │  JaCoCo (mínimo 80%)
│  3. SonarCloud    │  Análise estática de qualidade
└────────┬──────────┘
         │ (se passou)
         ▼
┌───────────────────┐
│  4. Build         │  mvnw package
└───────────────────┘
```

---

## 📋 Requisitos Funcionais

| ID | Requisito | Status |
|---|---|---|
| RF01 | O sistema deve permitir o cadastro e login de usuários | ✅ |
| RF02 | O sistema deve permitir o gerenciamento de livros (CRUD) | ✅ |
| RF03 | O sistema deve permitir a filtragem por título, categoria e status | ✅ |

> Veja o mapeamento completo de requisitos → testes no arquivo **[RTM.md](./RTM.md)**

---

## 📁 Documentação

| Arquivo | Descrição |
|---|---|
| [`RTM.md`](./RTM.md) | Matriz de Rastreabilidade de Requisitos com diagramas UML |
| [`swagger-ui`](http://localhost:8080/swagger-ui.html) | Documentação interativa da API |
| `target/site/jacoco/` | Relatório de cobertura de testes (gerado localmente) |

---

## 👨‍💻 Autor

Desenvolvido por **Erick Badaro** e **Gabriel Antonio** como projeto semestral da disciplina de Qualidade de Software.

---

<div align="center">

*Feito com ☕ Java e muito teste.*

</div>