## Overview

This project implements a scalable e-commerce system with separate services with own database for authentication, product management, cart operations, order processing and more.


## Table of Contents
1. [Technologies Used](#technologies-used)
2. [Project Structure](#project-structure)
3. [Services Description](#services-description)
4. [Setup and Installation](#setup-and-installation)
5. [Monitoring and Observability](#monitoring-and-observability)

## Technologies Used

- **Java**: 21
- **Spring Boot**: 3.4.4
- **Database**: Separate PostgreSQL instances for each service
- **Spring Cloud**: 2024.0.1
- **Service Discovery**: Eureka Server
- **Communication between microservices**: OpenFeign
- **Message Broker**: Kafka
- **Monitoring**: Grafana, Prometheus
- **Logging**: Logstash, Kibana
- **Additional Libraries**:
  - Lombok
  - MapStruct
  - JWT
- **API Documentation**: Swagger UI
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven

## Project Structure

```
├── api-gateway/          # API Gateway for routing and authorizing requests
├── auth-service/         # Authentication
├── cart-service/         # Shopping cart management
├── eureka-server/        # Service discovery
├── exception-lib/        # Common exception handling
├── logging-starter/      # Custom logging configuration
├── logstash/             # Log aggregation setup
├── monitoring/           # Prometheus and Grafana configs
├── notification-service/ # Email notifications
├── order-service/        # Order processing and management
├── product-service/      # Product catalog and management
└── stock-service/        # Product stock management
```

## Services Description

### Core Services

1. **API Gateway**
   - Routes requests to appropriate microservices
   - Handles authorization by jwt cookie and passes user id and user roles in "X-User-Id" and "X-User-Authorities" headers to appropriate services

2. **Auth Service**
   - User authentication
   - JWT management

3. **Product Service**
   - Product searching and filtering by pageNumber, pageSize, categories, minPrice, maxPrice, name and description sorted in ascending or descending order
   - Product catalog management
   - Category management

4. **Cart Service**
   - Shopping cart operations for logged in and anonymous users

5. **Order Service**
   - Order processing
   - Order status management

6. **Stock Service**
   - Inventory management
   - Stock product tracing

7. **Notification Service**
   - Email notifications
   - Order status updates

### Infrastructure Services

- **Eureka Server**
  - Service registration and discovery

- **Exception Library**
  - Standardized error handling
  - Common exception types
  - Global error responses

- **Logging Starter**
  - Centralized logging configuration
  - Custom log formatting

## Setup and Installation

### Prerequisites
- JDK 21
- Docker and Docker Compose
- Maven

### Getting Started

1. Clone the repository

2. Configure environment variables:
   ```bash
   cp example.env .env
   ```
   Update the values according to your environment

3. Build and start the project using docker:
   ```bash
   docker compose up --buiild
   ```

### Service URLs

- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Grafana: http://localhost:3000
- Prometheus: http://localhost:9090
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

## Monitoring and Observability

### Metrics
- Application metrics via Spring Boot Actuator
- System metrics collected by Prometheus
- Custom Grafana dashboards

### Logging
- Centralized logging with Logstash
- Structured JSON log format

