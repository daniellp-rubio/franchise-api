# 🏗 Architecture Documentation - Franchise API

Documentación técnica de la arquitectura, decisiones de diseño y patrones utilizados.

---

## 📑 Tabla de Contenidos

- [Visión General](#visión-general)
- [Arquitectura de Alto Nivel](#arquitectura-de-alto-nivel)
- [Patrones de Diseño](#patrones-de-diseño)
- [Decisiones Técnicas](#decisiones-técnicas)
- [Modelo de Datos](#modelo-de-datos)
- [Programación Reactiva](#programación-reactiva)
- [Seguridad](#seguridad)
- [Escalabilidad](#escalabilidad)
- [Monitoreo](#monitoreo)

---

## 🎯 Visión General

Franchise API es una aplicación construida siguiendo principios de:
- **Clean Architecture**
- **Domain-Driven Design (DDD)**
- **Reactive Programming**
- **12-Factor App**

### Características Técnicas Clave

- ⚡ **No-Blocking I/O** - WebFlux para alta concurrencia
- 📦 **Document-Based Storage** - MongoDB para flexibilidad
- 🐳 **Containerized** - Docker para portabilidad
- ☁️ **Cloud-Native** - Diseñada para la nube
- 📈 **Horizontally Scalable** - Arquitectura stateless

---

## 🏛 Arquitectura de Alto Nivel

### Diagrama de Capas

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────────────────────────────────────────┐  │
│  │  REST Controllers                                │  │
│  │  - FranchiseController                           │  │
│  │  - Request Validation                            │  │
│  │  - Response Mapping                              │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                   Application Layer                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Services                                        │  │
│  │  - FranchiseService                             │  │
│  │  - Business Logic                                │  │
│  │  - Transaction Orchestration                     │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                   Domain Layer                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Domain Models                                   │  │
│  │  - Franchise, Branch, Product                    │  │
│  │  - Business Rules                                │  │
│  │  - Domain Events (future)                        │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                 Infrastructure Layer                     │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Data Access                                     │  │
│  │  - FranchiseRepository                          │  │
│  │  - MongoDB Driver                                │  │
│  │  - External Services (future)                    │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Flujo de Datos (Request/Response)

```
Cliente HTTP
    │
    ▼
[1] FranchiseController
    │ - Recibe Request
    │ - Valida con @Valid
    │ - Llama al Service
    ▼
[2] FranchiseService
    │ - Ejecuta lógica de negocio
    │ - Valida reglas de dominio
    │ - Orquesta operaciones
    ▼
[3] FranchiseRepository (Reactive)
    │ - Ejecuta operaciones DB
    │ - Retorna Mono/Flux
    ▼
[4] MongoDB (Reactive Driver)
    │ - Persistencia
    │ - Consultas
    ▼
[Response] Cliente recibe JSON
```

---

## 🎨 Patrones de Diseño

### 1. Repository Pattern

**Propósito:** Abstracción del acceso a datos

```java
public interface FranchiseRepository 
    extends ReactiveMongoRepository<Franchise, String> {
    // Spring Data genera implementación automáticamente
}
```

**Beneficios:**
- ✅ Desacoplamiento de la lógica de negocio y persistencia
- ✅ Facilita testing (mock del repositorio)
- ✅ Cambio de BD sin afectar lógica de negocio

---

### 2. DTO Pattern (Data Transfer Object)

**Propósito:** Transferir datos entre capas

```java
// Request DTO
public class CreateFranchiseRequest {
    @NotBlank
    private String name;
}

// Response DTO
public class FranchiseResponse {
    private String id;
    private String name;
    private List<BranchResponse> branches;
    
    public static FranchiseResponse fromEntity(Franchise franchise) {
        // Mapeo de entidad a DTO
    }
}
```

**Beneficios:**
- ✅ Separación entre modelo de dominio y API
- ✅ Validación en capa de presentación
- ✅ Versionado de API independiente del dominio

---

### 3. Service Layer Pattern

**Propósito:** Encapsular lógica de negocio

```java
@Service
@RequiredArgsConstructor
public class FranchiseService {
    private final FranchiseRepository repository;
    
    public Mono<FranchiseResponse> createFranchise(CreateFranchiseRequest request) {
        // Lógica de negocio aquí
    }
}
```

**Beneficios:**
- ✅ Transacciones manejadas en un solo lugar
- ✅ Reutilización de lógica
- ✅ Testing independiente de controllers

---

### 4. Builder Pattern

**Propósito:** Construcción fluida de objetos

```java
Franchise franchise = Franchise.builder()
    .name("Starbucks")
    .branches(new ArrayList<>())
    .build();
```

**Beneficios:**
- ✅ Código más legible
- ✅ Inmutabilidad opcional
- ✅ Validación en construcción

---

### 5. Exception Handler Pattern

**Propósito:** Manejo centralizado de errores

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(FranchiseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        FranchiseNotFoundException ex) {
        // Manejo del error
    }
}
```

**Beneficios:**
- ✅ Consistencia en respuestas de error
- ✅ Separación de concerns
- ✅ Logging centralizado

---

## 🔧 Decisiones Técnicas

### Por Qué Spring WebFlux (Reactive)

**Decisión:** Usar WebFlux en lugar de Spring MVC tradicional

**Razones:**
1. **Alta Concurrencia**
    - Maneja miles de conexiones simultáneas
    - Uso eficiente de threads (Event Loop)

2. **Non-Blocking I/O**
    - No bloquea threads esperando DB
    - Mejor utilización de recursos

3. **Backpressure**
    - Control de flujo de datos
    - Evita saturación de memoria

**Trade-offs:**
- ❌ Curva de aprendizaje más alta
- ❌ Debugging más complejo
- ✅ Mejor rendimiento bajo carga
- ✅ Escalabilidad superior

---

### Por Qué MongoDB (NoSQL)

**Decisión:** MongoDB en lugar de PostgreSQL/MySQL

**Razones:**
1. **Esquema Flexible**
    - Franquicias pueden tener diferentes estructuras
    - Fácil evolución del modelo

2. **Document-Based**
    - Agregaciones naturales (franquicia → sucursales → productos)
    - Menos JOINs = mejor rendimiento

3. **Escalabilidad Horizontal**
    - Sharding nativo
    - Replicación simple

**Trade-offs:**
- ❌ No hay transacciones ACID complejas (no necesarias aquí)
- ❌ Joins complejos son difíciles
- ✅ Lectura/escritura rápida
- ✅ Escalabilidad

---

### Por Qué Docker

**Decisión:** Containerizar la aplicación

**Razones:**
1. **Portabilidad**
    - Funciona igual en dev/staging/prod

2. **Aislamiento**
    - Dependencias empaquetadas

3. **CI/CD Friendly**
    - Build → Test → Deploy automatizado

---

### Por Qué Terraform

**Decisión:** IaC con Terraform en lugar de CloudFormation

**Razones:**
1. **Multi-Cloud**
    - Portabilidad entre AWS/GCP/Azure

2. **Estado Declarativo**
    - Define el estado deseado

3. **Versionado**
    - Infraestructura en Git

---

## 💾 Modelo de Datos

### Diseño de Schema (MongoDB)

```javascript
// Collection: franchises
{
  _id: ObjectId("6979009bb8fd782cb1964a5d"),  // MongoDB ID
  name: "Starbucks",                           // Nombre franquicia
  branches: [                                   // Embedded documents
    {
      id: "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",  // UUID
      name: "Sucursal Centro",
      products: [                                   // Nested array
        {
          id: "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
          name: "Café Latte",
          stock: 50
        }
      ]
    }
  ]
}
```

### Estrategia: Embedding vs Referencing

**Decisión:** Usar **Embedding** (documentos anidados)

**Por Qué:**
- ✅ Lectura atómica (1 query trae todo)
- ✅ Consistencia natural
- ✅ Mejor rendimiento de lectura
- ✅ Modelo de acceso: "siempre leo franquicia completa"

**Cuándo Usar Referencing:**
- Documentos muy grandes (> 16 MB)
- Relaciones many-to-many
- Datos compartidos entre entidades

---

### Índices

```javascript
// Índice en nombre de franquicia (único)
db.franchises.createIndex({ name: 1 }, { unique: true })

// Índice compuesto (futuro)
db.franchises.createIndex({ "branches.name": 1 })
```

---

## ⚡ Programación Reactiva

### Conceptos Core

#### Mono<T> - 0 o 1 elemento

```java
Mono<Franchise> franchise = repository.findById("123");
```

**Uso:** Operaciones que retornan un solo resultado

#### Flux<T> - 0 a N elementos

```java
Flux<Franchise> franchises = repository.findAll();
```

**Uso:** Operaciones que retornan múltiples resultados

---

### Operadores Reactivos Utilizados

#### map() - Transformación

```java
repository.findById(id)
    .map(FranchiseResponse::fromEntity)  // Entity → DTO
```

#### flatMap() - Transformación Asíncrona

```java
repository.findById(id)
    .flatMap(franchise -> {
        // Operación asíncrona
        return repository.save(franchise);
    })
```

#### switchIfEmpty() - Valor por Defecto

```java
repository.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException()))
```

#### doOnSuccess() - Side Effect

```java
repository.save(franchise)
    .doOnSuccess(f -> log.info("Guardado: {}", f.getId()))
```

---

### Manejo de Errores Reactivo

```java
repository.findById(id)
    .switchIfEmpty(Mono.error(new NotFoundException()))
    .onErrorMap(MongoException.class, ex -> 
        new DataAccessException("DB Error", ex))
```

---

### Backpressure

```java
// El subscriber controla cuántos elementos procesar
Flux.range(1, 1000)
    .limitRate(10)  // Procesa máximo 10 a la vez
    .subscribe();
```

---

## 🔒 Seguridad

### Estado Actual

**Autenticación:** No implementada (v1.0)

**Autorización:** No implementada (v1.0)

### Roadmap de Seguridad

#### v1.1 - JWT Authentication

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange()
            .pathMatchers("/api/**").authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .build();
    }
}
```

#### v1.2 - Role-Based Access Control (RBAC)

```java
@PreAuthorize("hasRole('ADMIN')")
public Mono<Void> deleteFranchise(String id) {
    // Solo admins pueden eliminar
}
```

#### v2.0 - API Keys

```http
X-API-Key: sk_live_123456789
```

---

### Mejores Prácticas de Seguridad

1. **Input Validation**
   ```java
   @Valid @RequestBody CreateFranchiseRequest request
   ```

2. **SQL Injection** (N/A - NoSQL)
    - MongoDB driver maneja escapado automático

3. **Rate Limiting** (futuro)
   ```java
   @RateLimiter(name = "api", fallbackMethod = "fallback")
   ```

4. **HTTPS Only** (producción)
   ```yaml
   server:
     ssl:
       enabled: true
   ```

---

## 📈 Escalabilidad

### Arquitectura Stateless

```
┌──────────┐  ┌──────────┐  ┌──────────┐
│ Instance │  │ Instance │  │ Instance │
│    1     │  │    2     │  │    3     │
└─────┬────┘  └─────┬────┘  └─────┬────┘
      │             │             │
      └──────────┬──┴─────────────┘
                 │
           ┌─────▼─────┐
           │  MongoDB  │
           │  Cluster  │
           └───────────┘
```

**Características:**
- ✅ No mantiene estado en memoria
- ✅ Cualquier instancia puede servir cualquier request
- ✅ Auto-scaling horizontal

---

### Estrategias de Scaling

#### Horizontal Scaling (Recomendado)

```bash
# ECS Fargate
aws ecs update-service \
  --cluster franchise-api-cluster \
  --service franchise-api-service \
  --desired-count 5  # Aumentar instancias
```

**Beneficios:**
- ✅ Sin límite teórico
- ✅ Tolerancia a fallos
- ✅ Costo incremental

#### Vertical Scaling

```hcl
# terraform/main.tf
resource "aws_ecs_task_definition" "main" {
  cpu    = "512"   # Aumentar CPU
  memory = "1024"  # Aumentar memoria
}
```

**Limitaciones:**
- ❌ Límite físico del hardware
- ❌ Downtime al escalar

---

### Caching (Futuro)

```java
@Cacheable("franchises")
public Mono<Franchise> findById(String id) {
    return repository.findById(id);
}
```

**Tecnologías:**
- Redis para cache distribuido
- Spring Cache abstraction

---

### Database Sharding (Futuro)

```javascript
// MongoDB Sharding Strategy
sh.shardCollection("franchisedb.franchises", { _id: "hashed" })
```

---

## 📊 Monitoreo

### Métricas Expuestas (Actuator)

```
GET /actuator/health
GET /actuator/metrics
GET /actuator/prometheus  # Futuro
```

### Métricas Clave

| Métrica | Descripción | Umbral Crítico |
|---------|-------------|----------------|
| `http.server.requests` | Requests totales | > 1000 req/s |
| `jvm.memory.used` | Memoria usada | > 80% |
| `mongodb.driver.pool.size` | Conexiones DB | > 90 |

---

### Logging

```java
@Slf4j
public class FranchiseService {
    
    public Mono<Franchise> createFranchise(CreateFranchiseRequest request) {
        log.info("Creating franchise: {}", request.getName());
        
        return repository.save(franchise)
            .doOnSuccess(f -> log.info("Created franchise with ID: {}", f.getId()))
            .doOnError(ex -> log.error("Error creating franchise", ex));
    }
}
```

**Niveles:**
- `ERROR`: Errores que requieren atención
- `WARN`: Situaciones inusuales pero manejables
- `INFO`: Eventos importantes del negocio
- `DEBUG`: Información detallada para debugging

---

### Tracing (Futuro)

**Tecnología:** Spring Cloud Sleuth + Zipkin

```java
// Headers automáticos
X-B3-TraceId: 463ac35c9f6413ad
X-B3-SpanId: a2fb4a1d1a96d312
```

---

## 🧪 Testing

### Pirámide de Testing

```
       /\
      /  \     E2E Tests (10%)
     /────\    
    /      \   Integration Tests (30%)
   /────────\  
  /          \ Unit Tests (60%)
 /────────────\
```

### Estrategias por Capa

#### Unit Tests - Service Layer

```java
@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {
    
    @Mock
    private FranchiseRepository repository;
    
    @InjectMocks
    private FranchiseService service;
    
    @Test
    void createFranchise_Success() {
        // Given
        CreateFranchiseRequest request = new CreateFranchiseRequest("Starbucks");
        Franchise franchise = Franchise.builder().name("Starbucks").build();
        
        when(repository.save(any())).thenReturn(Mono.just(franchise));
        
        // When
        StepVerifier.create(service.createFranchise(request))
            // Then
            .assertNext(response -> {
                assertThat(response.getName()).isEqualTo("Starbucks");
            })
            .verifyComplete();
    }
}
```

#### Integration Tests - Repository Layer

```java
@DataMongoTest
class FranchiseRepositoryTest {
    
    @Autowired
    private FranchiseRepository repository;
    
    @Test
    void save_ShouldPersistFranchise() {
        // Given
        Franchise franchise = Franchise.builder().name("Test").build();
        
        // When
        StepVerifier.create(repository.save(franchise))
            // Then
            .assertNext(saved -> {
                assertThat(saved.getId()).isNotNull();
            })
            .verifyComplete();
    }
}
```

#### E2E Tests - API Layer

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class FranchiseApiE2ETest {
    
    @Autowired
    private WebTestClient webClient;
    
    @Test
    void createFranchise_EndToEnd() {
        webClient.post().uri("/api/franchises")
            .bodyValue(new CreateFranchiseRequest("Starbucks"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(FranchiseResponse.class)
            .value(response -> {
                assertThat(response.getName()).isEqualTo("Starbucks");
            });
    }
}
```

---

## 🔄 CI/CD Pipeline

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run tests
        run: mvn test
      
  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker image
        run: docker build -t franchise-api .
      
  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to AWS
        run: |
          # Push to ECR
          # Update ECS service
```

---

## 📚 Recursos Adicionales

### Documentación de Referencia

- [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor](https://projectreactor.io/docs)
- [MongoDB Reactive](https://mongodb.github.io/mongo-java-driver-reactivestreams/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)

### Libros Recomendados

- "Reactive Spring" - Josh Long
- "Clean Architecture" - Robert C. Martin
- "Domain-Driven Design" - Eric Evans

---

## 🎓 Glosario

| Término | Definición |
|---------|------------|
| **Reactive** | Paradigma de programación no-bloqueante |
| **Mono** | Publisher de 0-1 elemento |
| **Flux** | Publisher de 0-N elementos |
| **Backpressure** | Control de flujo de datos |
| **Event Loop** | Modelo de concurrencia reactivo |
| **Embedding** | Documentos anidados en MongoDB |
| **Sharding** | Particionamiento horizontal de datos |

---

**Arquitectura diseñada para crecer con el negocio** 🚀

Última actualización: 2026-01-27