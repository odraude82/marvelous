# marvelous
A project that clients manage yours suppliers

## Pré-requisitos

Antes de iniciar, certifique-se de ter as seguintes ferramentas instaladas:

- [Java 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/) (ou use o wrapper `mvnw` incluído no projeto)
- [Node.js](https://nodejs.org/) (versão recomendada: LTS)
- [npm](https://www.npmjs.com/) 11.9.0+
- [Angular CLI](https://angular.dev/tools/cli) 21+

---

## Backend (Spring Boot)

### Configuração do ambiente

O backend utiliza **Spring Boot 3.5**, **Java 17** e banco de dados em memória **H2** (não requer instalação de banco de dados externo).

### Executar localmente

```bash
cd backend
./mvnw spring-boot:run
```

> No Windows, use `mvnw.cmd spring-boot:run`

A API estará disponível em: `http://localhost:8080`

O console do H2 (banco de dados em memória) pode ser acessado em: `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:marvelousdb`
- **Usuário:** `sa`
- **Senha:** *(deixe em branco)*

### Executar os testes

```bash
cd backend
./mvnw test
```

---

## Frontend (Angular)

### Configuração do ambiente

```bash
cd frontend
npm install
```

### Executar localmente

```bash
cd frontend
npm start
```

A aplicação estará disponível em: `http://localhost:4200`

> Certifique-se de que o backend esteja em execução antes de iniciar o frontend.

### Build para produção

```bash
cd frontend
npm run build
```

Os artefatos serão gerados na pasta `dist/`.

### Executar os testes

```bash
cd frontend
npm test
```

---

## Executando o projeto completo

Para rodar o projeto completo localmente, abra dois terminais:

**Terminal 1 – Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Terminal 2 – Frontend:**
```bash
cd frontend
npm install
npm start
```

Acesse a aplicação em `http://localhost:4200`.
