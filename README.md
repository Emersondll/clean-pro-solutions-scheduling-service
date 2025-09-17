
# 📅 Clean Pro Solutions – Scheduling Service

O **Scheduling Service** é responsável pelo gerenciamento de **agendamentos** entre clientes e prestadores de serviço.
Ele permite criar, listar, atualizar e cancelar agendamentos, garantindo o fluxo de reservas entre os usuários (`user-service`) e os serviços (`catalog-service`).

---

## 📂 Estrutura do Projeto

```
clean-pro-solutions-scheduling-service
 └── src/main/java/io/github/emersondll/clean_pro_solutions_scheduling
     ├── controller      # Controllers REST
     ├── dto             # DTOs (Request e Response)
     ├── model           # Entidades (Scheduling, SchedulingStatus)
     ├── repository      # Interfaces do MongoDB
     ├── service         # Regras de negócio (interfaces)
     ├── service/impl    # Implementação dos serviços
     └── CleanProSolutionsSchedulingServiceApplication.java
```

---

## ⚙️ Configuração

O serviço utiliza **Spring Boot + Spring Data MongoDB**.

### application.yml

```yaml
spring:
  application:
    name: clean-pro-solutions-scheduling-service
  data:
    mongodb:
      database: scheduling-db
      host: localhost
      port: 27017
server:
  port: 8083
```

---

## 🚀 Endpoints da API

### 🔹 Criar um agendamento

```http
POST /api/v1/schedulings
```

**Exemplo cURL**

```bash
curl -X POST http://localhost:8083/api/v1/schedulings \
-H "Content-Type: application/json" \
-d '{
  "clientId": "123",
  "contractorId": "456",
  "serviceId": "789",
  "startTime": "2025-09-20T10:00:00",
  "endTime": "2025-09-20T11:00:00"
}'
```

---

### 🔹 Listar todos os agendamentos

```http
GET /api/v1/schedulings
```

**Exemplo cURL**

```bash
curl -X GET http://localhost:8083/api/v1/schedulings
```

---

### 🔹 Buscar agendamento por ID

```http
GET /api/v1/schedulings/{id}
```

**Exemplo cURL**

```bash
curl -X GET http://localhost:8083/api/v1/schedulings/66f2bc1a87a
```

---

### 🔹 Atualizar agendamento

```http
PUT /api/v1/schedulings/{id}
```

**Exemplo cURL**

```bash
curl -X PUT http://localhost:8083/api/v1/schedulings/66f2bc1a87a \
-H "Content-Type: application/json" \
-d '{
  "startTime": "2025-09-20T14:00:00",
  "endTime": "2025-09-20T15:00:00",
  "status": "CONFIRMED"
}'
```

---

### 🔹 Cancelar agendamento

```http
DELETE /api/v1/schedulings/{id}
```

**Exemplo cURL**

```bash
curl -X DELETE http://localhost:8083/api/v1/schedulings/66f2bc1a87a
```

---

## ▶️ Como rodar

1. Inicie o MongoDB:

```bash
docker run -d -p 27017:27017 mongo
```

2. Suba a aplicação:

```bash
mvn spring-boot:run
```

3. Acesse:

```
http://localhost:8083/api/v1/schedulings
```

---

