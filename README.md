# Medix API - Plataforma de Gestão de Saúde

API desenvolvida em Java Spring Boot para dar suporte à plataforma Medix, centralizando autenticação, regras de negócio, persistência de dados, gestão de saúde e integração com chatbot de IA.

A aplicação permite o gerenciamento de pacientes, colaboradores, unidades de saúde, salas, agendamentos e recursos de suporte inteligente com RAG simplificado e fluxo conversacional para criação, listagem e cancelamento de consultas.

## Deploy

A API está disponível em: 
```text
    https://sprint-04-java.onrender.com
```

## Vídeo demonstrativo

## Tecnologias

- Java
- Spring Boot
- Oracle Database
- Spring Security
- Spring AI
- Flyway
- Docker

## Funcionalidades

- Cadastro e autenticação de usuários
- Segurança com token JWT
- Gestão de pacientes
- Gestão de colaboradores
- Gestão de unidades de saúde
- Gestão de salas
- Criação de agendamentos
- Listagem de agendamentos futuros
- Cancelamento de agendamentos
- Validação de disponibilidade de médicos e horários
- Integração com chatbot Medix AI
- Consulta de regras institucionais com RAG simplificado
- Persistência em banco Oracle
- Migrations com Flyway

## Integração com IA

A API possui integração com modelo de linguagem para suporte ao chatbot Medix AI.

O chatbot pode:

- responder dúvidas gerais sobre a clínica;
- orientar pacientes sobre o processo de agendamento;
- consultar contexto institucional;
- conduzir fluxo de agendamento;
- listar agendamentos futuros;
- cancelar agendamentos mediante confirmação.

Os dados operacionais, como médicos, unidades, horários e agendamentos, são consultados diretamente no banco de dados, evitando respostas fixas ou inventadas.

## Variáveis de ambiente

Configure a chave da IA através de variável de ambiente:

```env
GROQ_API_KEY=sua_chave_aqui
```

No `application.yaml`, a chave deve ser referenciada assim:

```yaml
spring:
    ai:
        openai:
          api-key: ${GROQ_API_KEY}
```

## Como executar o projeto

Clone o repositório:

```bash
git clone https://github.com/challengeoracle/sprint-04-java.git
```

Acesse a pasta do projeto:

```bash
cd sprint-04-java
```

Execute a aplicação:

```bash
mvn spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Banco de dados

A aplicação utiliza Oracle Database para persistência dos dados.

O Flyway é responsável pela criação e versionamento das tabelas, procedures, funções e demais estruturas necessárias para o funcionamento da API.

## Endpoints principais

- Autenticação
- Usuários
- Pacientes
- Colaboradores
- Unidades de saúde
- Salas
- Agendamentos
- Chatbot Medix AI

## Integração com o frontend

Esta API é consumida pelo frontend Angular do projeto Medix.

Repositório do frontend:

```text
https://github.com/challengeoracle/angular-sprint-04
```

## Observações

Antes de executar a aplicação, verifique se:

- o banco Oracle está acessível;
- as credenciais do banco estão configuradas;
- a variável `GROQ_API_KEY` foi definida;
- as migrations do Flyway foram executadas corretamente;
- o frontend está apontando para a URL correta da API.

## Integrantes

- RM561061 - Arthur Thomas Mariano de Souza
- RM559873 - Davi Cavalcanti Jorge
- RM559728 - Mateus da Silveira Lima

