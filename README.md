# marvelous
A project that clients manage yours suppliers

## Pré-requisitos

Antes de iniciar, certifique-se de ter as seguintes ferramentas instaladas:

- [Java 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/) (ou use o wrapper `mvnw` incluído no projeto)
- [Node.js](https://nodejs.org/) (versão recomendada: LTS)
- [npm](https://www.npmjs.com/) 11.9.0+
- [Angular CLI](https://angular.dev/tools/cli) 21+
- [PostgreSQL](https://www.postgresql.org/) 14+

---

## Backend (Spring Boot)

### Configuração do ambiente

O backend utiliza **Spring Boot 3.5**, **Java 21** e banco de dados **PostgreSQL**.

#### Configurar o PostgreSQL

Crie um banco de dados no PostgreSQL:

```sql
CREATE DATABASE marvelousdb;
```

Por padrão, o backend conecta com as seguintes configurações:

| Parâmetro | Valor padrão |
|-----------|-------------|
| URL       | `jdbc:postgresql://localhost:5432/marvelousdb` |
| Usuário   | `postgres` |
| Senha     | `postgres` |

Essas configurações podem ser sobrescritas via variáveis de ambiente:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/marvelousdb
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha
```

### Executar localmente

```bash
cd backend
./mvnw spring-boot:run
```

> No Windows, use `mvnw.cmd spring-boot:run`

A API estará disponível em: `http://localhost:8080`

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

## Funcionalidades

### Fornecedores
Cadastro e gerenciamento de fornecedores com informações como CNPJ/CPF, contato e categoria de produtos.

### Produtos
Controle de estoque com alertas de estoque mínimo e acompanhamento de preços de custo.

### Compras
Registro de histórico de compras com atualização automática de estoque e preço de custo.

### Comparação de Orçamentos
Permite cadastrar orçamentos de diferentes fornecedores para o mesmo item e compará-los em uma tela dedicada. O sistema **sugere automaticamente a melhor opção** com base no menor preço unitário, destacando visualmente o fornecedor mais vantajoso.

### Dashboard
Painel com métricas gerais: valor total em estoque, alertas de estoque baixo, maiores fornecedores por gasto e variação de preços nas últimas compras.

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
