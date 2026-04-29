# Clean Pro Solutions - Scheduling Service 🗓️

## 🎯 Papel no Ecossistema
O **Scheduling Service** é o coração operacional da plataforma. Ele orquestra o fluxo de reserva:
- Criação e gestão de agendamentos de limpeza.
- Validação de regras de negócio para novas reservas.
- Emissão de eventos para o `contract-service` e `payment-service`.
- Controle de status do agendamento (PENDING, CONFIRMED, COMPLETED, CANCELLED).

## 🚀 Tecnologias
- **Java 21** & **Spring Boot 3.3.4**
- **MongoDB** (Persistência de agendamentos)
- **RabbitMQ** (Mensageria para orquestração de pedidos)
- **Netflix Eureka** (Service Discovery)

## 🛠️ Como Executar

### 1. Execução Isolada (Individual)
Para rodar este serviço e suas dependências:
```bash
docker-compose up -d --build
```
O serviço estará disponível em `http://localhost:8084`.

### 2. Execução Integrada
Este serviço é orquestrado pelo projeto principal [Clean Pro Platform](../README.md).

---
© 2026 Clean Pro Solutions - Desenvolvido por Emerson Lima.
