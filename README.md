# Franchise API

🏢 **Sistema de Gestión de Franquicias** - API REST reactiva construida con Spring Boot WebFlux y MongoDB

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-ECS-orange.svg)](https://aws.amazon.com/ecs/)

---

## 📑 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#-configuración)
- [Ejecución](#-ejecución)
- [API Endpoints](#-api-endpoints)
- [Documentación](#-documentación)
- [Testing](#-testing)
- [Despliegue](#-despliegue)
- [Arquitectura](#-arquitectura)
- [Contribuir](#-contribuir)
- [Licencia](#-licencia)

---

## 🎯 Descripción

**Franchise API** es un sistema robusto y escalable para la gestión de franquicias, sucursales y productos. Implementa una arquitectura reactiva utilizando Spring WebFlux y MongoDB para proporcionar operaciones de alta concurrencia y baja latencia.

### Funcionalidades Principales

- ✅ Gestión completa de franquicias
- ✅ Administración de sucursales por franquicia
- ✅ Control de inventario de productos
- ✅ Actualización de stock en tiempo real
- ✅ Consultas de productos con mayor stock por sucursal
- ✅ Actualización de nombres (franquicias, sucursales, productos)

---

## ✨ Características

### Técnicas

- 🚀 **Programación Reactiva** - WebFlux con Reactor
- 📦 **Base de Datos NoSQL** - MongoDB con soporte reactivo
- 🐳 **Containerización** - Docker y Docker Compose
- ☁️ **Cloud-Ready** - Infraestructura como código con Terraform
- 📝 **Documentación Automática** - Swagger/OpenAPI
- ✔️ **Validación** - Bean Validation (JSR-380)
- 🔍 **Logging** - SLF4J con Logback
- 📊 **Monitoreo** - Spring Boot Actuator

### Funcionales

- **CRUD Completo** de entidades
- **Validaciones** de datos de entrada
- **Manejo de errores** estructurado
- **Consultas optimizadas** para reportes
- **Transaccionalidad** implícita en operaciones

---

## 🛠 Tecnologías

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.2.1** - Framework principal
- **Spring WebFlux** - Programación reactiva
- **Spring Data MongoDB Reactive** - Acceso a datos reactivo
- **Lombok** - Reducción de boilerplate
- **Bean Validation** - Validación de datos

### Base de Datos
- **MongoDB 7.0** - Base de datos NoSQL
- **MongoDB Atlas** - Servicio cloud (opcional)

### DevOps
- **Docker** - Containerización
- **Docker Compose** - Orquestación local
- **Terraform** - Infrastructure as Code
- **AWS ECS Fargate** - Deployment en cloud
- **AWS ECR** - Registro de imágenes Docker

### Documentación
- **SpringDoc OpenAPI 3** - Especificación OpenAPI
- **Swagger UI** - Interfaz interactiva de API

---

## 📋 Requisitos Previos

### Para Desarrollo Local

```bash
# Verificar versiones instaladas
java -version    # Java 17 requerido
mvn -version     # Maven 3.6+ requerido
docker --version # Docker 20+ requerido
```

### Para Despliegue en AWS

```bash
terraform --version  # Terraform 1.0+ requerido
aws --version        # AWS CLI 2.0+ requerido
```

---

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/franchise-api.git
cd franchise-api
```

### 2. Instalar Dependencias

```bash
mvn clean install
```

### 3. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
# MongoDB Configuration
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/franchisedb

# Server Configuration
PORT=8081

# Logging Level
LOGGING_LEVEL_COM_COMPANY_FRANCHISE=INFO
```

---

## ⚙️ Configuración

### application.yml

El archivo principal de configuración está en `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: franchise-api
  
  data:
    mongodb:
      uri: ${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/franchisedb}
      auto-index-creation: true

server:
  port: ${PORT:8081}

logging:
  level:
    com.company.franchise: ${LOGGING_LEVEL_COM_COMPANY_FRANCHISE:INFO}
    org.springframework.data.mongodb: INFO

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---

## 🏃 Ejecución

### Opción 1: Desarrollo Local con Maven

```bash
# 1. Iniciar MongoDB
docker run -d -p 27017:27017 --name franchise-mongodb mongo:7.0

# 2. Ejecutar la aplicación
mvn spring-boot:run

# 3. Verificar que esté corriendo
curl http://localhost:8081/actuator/health
```

### Opción 2: Docker Compose (Recomendado)

```bash
# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f franchise-api

# Detener servicios
docker-compose down
```

### Opción 3: Docker Manual

```bash
# 1. Construir imagen
docker build -t franchise-api:latest .

# 2. Ejecutar contenedor
docker run -d \
  -p 8081:8081 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/franchisedb \
  --name franchise-api \
  franchise-api:latest
```

---

## 📡 API Endpoints

### Franquicias

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/franchises` | Crear nueva franquicia |
| `GET` | `/api/franchises` | Listar todas las franquicias |
| `GET` | `/api/franchises/{id}` | Obtener franquicia por ID |
| `PATCH` | `/api/franchises/{id}/name` | Actualizar nombre de franquicia |

### Sucursales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/franchises/{id}/branches` | Agregar sucursal |
| `PATCH` | `/api/franchises/{id}/branches/{bid}/name` | Actualizar nombre de sucursal |

### Productos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/franchises/{id}/branches/{bid}/products` | Agregar producto |
| `DELETE` | `/api/franchises/{id}/branches/{bid}/products/{pid}` | Eliminar producto |
| `PATCH` | `/api/franchises/{id}/branches/{bid}/products/{pid}/stock` | Actualizar stock |
| `PATCH` | `/api/franchises/{id}/branches/{bid}/products/{pid}/name` | Actualizar nombre |

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/franchises/{id}/top-stock-products` | Productos con mayor stock por sucursal |

### Ejemplos de Uso

#### Crear Franquicia

```bash
curl -X POST http://localhost:8081/api/franchises \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Starbucks"
  }'
```

**Respuesta:**
```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": []
}
```

#### Agregar Sucursal

```bash
curl -X POST http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sucursal Centro"
  }'
```

#### Agregar Producto

```bash
curl -X POST http://localhost:8081/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Café Latte",
    "stock": 50
  }'
```

#### Consultar Productos con Mayor Stock

```bash
curl http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/top-stock-products
```

**Respuesta:**
```json
[
  {
    "branchId": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
    "branchName": "Sucursal Centro",
    "productId": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
    "productName": "Café Latte",
    "stock": 50
  }
]
```

---

## 📚 Documentación

### Swagger UI

Una vez que la aplicación esté corriendo, accede a la documentación interactiva:

```
http://localhost:8081/swagger-ui.html
```

### OpenAPI Specification

El documento OpenAPI está disponible en:

```
http://localhost:8081/api-docs
```

### Documentación Adicional

- [Guía de Despliegue](DEPLOYMENT.md) - Instrucciones detalladas para AWS
- [Documentación de API](API.md) - Especificación completa de endpoints
- [Arquitectura](ARCHITECTURE.md) - Diseño y decisiones técnicas
- [Guía de Contribución](CONTRIBUTING.md) - Cómo contribuir al proyecto

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=FranchiseServiceTest

# Con coverage
mvn clean test jacoco:report
```

### Tests Incluidos

- ✅ Unit Tests - Service layer
- ✅ Integration Tests - Repository layer
- ✅ API Tests - Controller endpoints

---

## 🚀 Despliegue

### Local con Docker Compose

```bash
docker-compose up -d
```

### AWS con Terraform

Ver [DEPLOYMENT.md](DEPLOYMENT.md) para instrucciones completas.

**Resumen:**

```bash
# 1. Configurar AWS CLI
aws configure

# 2. Inicializar Terraform
cd terraform
terraform init

# 3. Aplicar infraestructura
terraform apply -var="mongodb_uri=mongodb+srv://..."

# 4. Construir y subir imagen
docker build -t franchise-api .
docker tag franchise-api:latest <ECR_URL>:latest
docker push <ECR_URL>:latest

# 5. Deployar servicio
aws ecs update-service --cluster franchise-api-cluster \
  --service franchise-api-service --force-new-deployment
```

---

## 🏗 Arquitectura

```
┌─────────────────────────────────────────────────────┐
│                   Cliente (Web/Mobile)               │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│              Load Balancer (AWS ALB)                 │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│         Controller Layer (REST Endpoints)            │
│  - FranchiseController                              │
│  - Validation & Error Handling                       │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│          Service Layer (Business Logic)              │
│  - FranchiseService                                 │
│  - Transaction Management                            │
│  - Business Rules                                    │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│        Repository Layer (Data Access)                │
│  - FranchiseRepository (Reactive)                   │
│  - Spring Data MongoDB                              │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│              MongoDB (Database)                      │
│  - Collection: franchises                           │
│  - Document-based storage                            │
└─────────────────────────────────────────────────────┘
```

### Modelo de Datos

```json
{
  "_id": "ObjectId",
  "name": "String",
  "branches": [
    {
      "id": "UUID",
      "name": "String",
      "products": [
        {
          "id": "UUID",
          "name": "String",
          "stock": "Integer"
        }
      ]
    }
  ]
}
```
---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 👤 Autor

**Tu Nombre**
- GitHub: [@Daintz](https://github.com/Daintz)
- LinkedIn: [Daintz](https://www.linkedin.com/in/daniel-lopez-rubio/)

---
