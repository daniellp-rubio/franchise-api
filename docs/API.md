# 📡 API Documentation - Franchise API

Documentación completa de todos los endpoints REST de Franchise API.

---

## 📑 Tabla de Contenidos

- [Información General](#información-general)
- [Autenticación](#autenticación)
- [Endpoints](#endpoints)
    - [Franquicias](#franquicias)
    - [Sucursales](#sucursales)
    - [Productos](#productos)
    - [Reportes](#reportes)
- [Modelos de Datos](#modelos-de-datos)
- [Códigos de Error](#códigos-de-error)
- [Rate Limiting](#rate-limiting)
- [Ejemplos de Uso](#ejemplos-de-uso)

---

## ℹ️ Información General

### Base URL

```
http://localhost:8081/api
```

### Formato de Datos

- **Request:** JSON (`application/json`)
- **Response:** JSON (`application/json`)
- **Encoding:** UTF-8

### Versiones

- **Versión Actual:** v1.0
- **API Estable:** ✅ Sí
- **Breaking Changes:** Notificados con 30 días de anticipación

---

## 🔐 Autenticación

**Estado Actual:** No requiere autenticación

**Próximas Versiones:** Se implementará autenticación JWT

```http
Authorization: Bearer <token>
```

---

## 📍 Endpoints

### Franquicias

#### 1. Crear Franquicia

Crea una nueva franquicia en el sistema.

```http
POST /api/franchises
```

**Request Body:**

```json
{
  "name": "string"
}
```

**Validaciones:**
- `name`: Requerido, no vacío, longitud máxima 100 caracteres

**Response: 201 Created**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": []
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos de entrada inválidos
- `409 Conflict` - Franquicia con ese nombre ya existe (futuro)

**Ejemplo curl:**

```bash
curl -X POST http://localhost:8081/api/franchises \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Starbucks"
  }'
```

---

#### 2. Listar Todas las Franquicias

Obtiene la lista completa de franquicias con sus sucursales y productos.

```http
GET /api/franchises
```

**Query Parameters:** Ninguno

**Response: 200 OK**

```json
[
  {
    "id": "6979009bb8fd782cb1964a5d",
    "name": "Starbucks",
    "branches": [
      {
        "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
        "name": "Sucursal Centro",
        "products": [
          {
            "id": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
            "name": "Café Latte",
            "stock": 50
          }
        ]
      }
    ]
  }
]
```

**Respuesta Vacía:**

```json
[]
```

**Ejemplo curl:**

```bash
curl http://localhost:8081/api/franchises
```

---

#### 3. Obtener Franquicia por ID

Obtiene una franquicia específica con todos sus datos.

```http
GET /api/franchises/{franchiseId}
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia

**Response: 200 OK**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Centro",
      "products": [
        {
          "id": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
          "name": "Café Latte",
          "stock": 50
        }
      ]
    }
  ]
}
```

**Errores Posibles:**
- `404 Not Found` - Franquicia no encontrada

**Ejemplo de Error: 404 Not Found**

```json
{
  "message": "Franquicia con ID 6979009bb8fd782cb1964a5d no encontrada",
  "error": "NOT_FOUND",
  "status": 404
}
```

**Ejemplo curl:**

```bash
curl http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d
```

---

#### 4. Actualizar Nombre de Franquicia

Actualiza el nombre de una franquicia existente.

```http
PATCH /api/franchises/{franchiseId}/name
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia

**Request Body:**

```json
{
  "name": "string"
}
```

**Validaciones:**
- `name`: Requerido, no vacío

**Response: 200 OK**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks Premium",
  "branches": [...]
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - Franquicia no encontrada

**Ejemplo curl:**

```bash
curl -X PATCH http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/name \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Starbucks Premium"
  }'
```

---

### Sucursales

#### 5. Agregar Sucursal a Franquicia

Agrega una nueva sucursal a una franquicia existente.

```http
POST /api/franchises/{franchiseId}/branches
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia

**Request Body:**

```json
{
  "name": "string"
}
```

**Validaciones:**
- `name`: Requerido, no vacío

**Response: 201 Created**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Centro",
      "products": []
    }
  ]
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - Franquicia no encontrada
- `409 Conflict` - Sucursal con ese nombre ya existe en la franquicia

**Ejemplo de Error: 409 Conflict**

```json
{
  "message": "Sucursal con nombre 'Sucursal Centro' ya existe",
  "error": "CONFLICT",
  "status": 409
}
```

**Ejemplo curl:**

```bash
curl -X POST http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sucursal Centro"
  }'
```

---

#### 6. Actualizar Nombre de Sucursal

Actualiza el nombre de una sucursal existente.

```http
PATCH /api/franchises/{franchiseId}/branches/{branchId}/name
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia
- `branchId` (string): ID de la sucursal

**Request Body:**

```json
{
  "name": "string"
}
```

**Response: 200 OK**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Norte",
      "products": [...]
    }
  ]
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - Franquicia o sucursal no encontrada

**Ejemplo curl:**

```bash
curl -X PATCH http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches/fe5dadc0-d393-4e1c-9270-27c99e6f4a28/name \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sucursal Norte"
  }'
```

---

### Productos

#### 7. Agregar Producto a Sucursal

Agrega un nuevo producto a una sucursal.

```http
POST /api/franchises/{franchiseId}/branches/{branchId}/products
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia
- `branchId` (string): ID de la sucursal

**Request Body:**

```json
{
  "name": "string",
  "stock": 0
}
```

**Validaciones:**
- `name`: Requerido, no vacío
- `stock`: Requerido, entero positivo (> 0)

**Response: 201 Created**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Centro",
      "products": [
        {
          "id": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
          "name": "Café Latte",
          "stock": 50
        }
      ]
    }
  ]
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - Franquicia o sucursal no encontrada
- `409 Conflict` - Producto con ese nombre ya existe en la sucursal

**Ejemplo de Error: 400 Bad Request**

```json
{
  "message": "El stock debe ser mayor a 0",
  "error": "VALIDATION_ERROR",
  "status": 400
}
```

**Ejemplo curl:**

```bash
curl -X POST http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches/fe5dadc0-d393-4e1c-9270-27c99e6f4a28/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Café Latte",
    "stock": 50
  }'
```

---

#### 8. Eliminar Producto de Sucursal

Elimina un producto de una sucursal.

```http
DELETE /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia
- `branchId` (string): ID de la sucursal
- `productId` (string): ID del producto

**Response: 200 OK**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Centro",
      "products": []
    }
  ]
}
```

**Errores Posibles:**
- `404 Not Found` - Franquicia, sucursal o producto no encontrado

**Ejemplo curl:**

```bash
curl -X DELETE http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches/fe5dadc0-d393-4e1c-9270-27c99e6f4a28/products/dc1ed759-e5b1-4b22-9e5a-53a4cc48d104
```

---

#### 9. Actualizar Stock de Producto

Actualiza la cantidad de stock de un producto.

```http
PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia
- `branchId` (string): ID de la sucursal
- `productId` (string): ID del producto

**Request Body:**

```json
{
  "stock": 0
}
```

**Validaciones:**
- `stock`: Requerido, entero positivo (> 0)

**Response: 200 OK**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Centro",
      "products": [
        {
          "id": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
          "name": "Café Latte",
          "stock": 100
        }
      ]
    }
  ]
}
```

**Errores Posibles:**
- `400 Bad Request` - Stock inválido
- `404 Not Found` - Franquicia, sucursal o producto no encontrado

**Ejemplo curl:**

```bash
curl -X PATCH http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches/fe5dadc0-d393-4e1c-9270-27c99e6f4a28/products/dc1ed759-e5b1-4b22-9e5a-53a4cc48d104/stock \
  -H "Content-Type: application/json" \
  -d '{
    "stock": 100
  }'
```

---

#### 10. Actualizar Nombre de Producto

Actualiza el nombre de un producto.

```http
PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia
- `branchId` (string): ID de la sucursal
- `productId` (string): ID del producto

**Request Body:**

```json
{
  "name": "string"
}
```

**Response: 200 OK**

```json
{
  "id": "6979009bb8fd782cb1964a5d",
  "name": "Starbucks",
  "branches": [
    {
      "id": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
      "name": "Sucursal Centro",
      "products": [
        {
          "id": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
          "name": "Café Latte Grande",
          "stock": 100
        }
      ]
    }
  ]
}
```

**Errores Posibles:**
- `400 Bad Request` - Nombre inválido
- `404 Not Found` - Franquicia, sucursal o producto no encontrado

**Ejemplo curl:**

```bash
curl -X PATCH http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/branches/fe5dadc0-d393-4e1c-9270-27c99e6f4a28/products/dc1ed759-e5b1-4b22-9e5a-53a4cc48d104/name \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Café Latte Grande"
  }'
```

---

### Reportes

#### 11. Productos con Mayor Stock por Sucursal

Obtiene el producto con mayor stock de cada sucursal de una franquicia.

```http
GET /api/franchises/{franchiseId}/top-stock-products
```

**Path Parameters:**
- `franchiseId` (string): ID de la franquicia

**Response: 200 OK**

```json
[
  {
    "branchId": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
    "branchName": "Sucursal Centro",
    "productId": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
    "productName": "Café Latte",
    "stock": 100
  },
  {
    "branchId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "branchName": "Sucursal Norte",
    "productId": "x9y8z7w6-v5u4-3210-9876-543210fedcba",
    "productName": "Frappuccino",
    "stock": 75
  }
]
```

**Respuesta Vacía (sin sucursales o productos):**

```json
[]
```

**Errores Posibles:**
- `404 Not Found` - Franquicia no encontrada

**Notas:**
- Solo retorna sucursales que tienen productos
- Si una sucursal no tiene productos, no aparece en el resultado
- En caso de empate (múltiples productos con el mismo stock máximo), se retorna uno arbitrariamente

**Ejemplo curl:**

```bash
curl http://localhost:8081/api/franchises/6979009bb8fd782cb1964a5d/top-stock-products
```

---

## 📦 Modelos de Datos

### Franchise (Franquicia)

```typescript
{
  id: string,           // MongoDB ObjectId
  name: string,         // Nombre de la franquicia
  branches: Branch[]    // Lista de sucursales
}
```

### Branch (Sucursal)

```typescript
{
  id: string,           // UUID
  name: string,         // Nombre de la sucursal
  products: Product[]   // Lista de productos
}
```

### Product (Producto)

```typescript
{
  id: string,           // UUID
  name: string,         // Nombre del producto
  stock: number         // Cantidad en inventario (entero positivo)
}
```

### ErrorResponse (Respuesta de Error)

```typescript
{
  message: string,      // Descripción del error
  error: string,        // Código de error
  status: number        // Código HTTP
}
```

### TopStockProductResponse (Producto con Mayor Stock)

```typescript
{
  branchId: string,     // ID de la sucursal
  branchName: string,   // Nombre de la sucursal
  productId: string,    // ID del producto
  productName: string,  // Nombre del producto
  stock: number         // Cantidad en stock
}
```

---

## ⚠️ Códigos de Error

### 4xx - Errores del Cliente

| Código | Tipo | Descripción |
|--------|------|-------------|
| `400` | `VALIDATION_ERROR` | Datos de entrada inválidos |
| `404` | `NOT_FOUND` | Recurso no encontrado |
| `409` | `CONFLICT` | Conflicto (ej: nombre duplicado) |

### 5xx - Errores del Servidor

| Código | Tipo | Descripción |
|--------|------|-------------|
| `500` | `INTERNAL_SERVER_ERROR` | Error interno del servidor |

### Ejemplos de Respuestas de Error

#### 400 Bad Request - Validación

```json
{
  "message": "El nombre de la franquicia es requerido, El stock debe ser mayor a 0",
  "error": "VALIDATION_ERROR",
  "status": 400
}
```

#### 404 Not Found

```json
{
  "message": "Franquicia con ID 6979009bb8fd782cb1964a5d no encontrada",
  "error": "NOT_FOUND",
  "status": 404
}
```

#### 409 Conflict

```json
{
  "message": "Producto con nombre 'Café Latte' ya existe",
  "error": "CONFLICT",
  "status": 409
}
```

#### 500 Internal Server Error

```json
{
  "message": "Error interno del servidor",
  "error": "INTERNAL_SERVER_ERROR",
  "status": 500
}
```

---

## ⏱ Rate Limiting

**Estado Actual:** No implementado

**Futuras Versiones:**
- Límite: 100 requests/minuto por IP
- Header de respuesta: `X-RateLimit-Remaining`

---

## 💡 Ejemplos de Uso

### Flujo Completo: Crear Franquicia con Datos

#### 1. Crear Franquicia

```bash
FRANCHISE_ID=$(curl -X POST http://localhost:8081/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name":"Starbucks"}' \
  | jq -r '.id')

echo "Franchise ID: $FRANCHISE_ID"
```

#### 2. Agregar Sucursal

```bash
BRANCH_ID=$(curl -X POST http://localhost:8081/api/franchises/$FRANCHISE_ID/branches \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Centro"}' \
  | jq -r '.branches[0].id')

echo "Branch ID: $BRANCH_ID"
```

#### 3. Agregar Productos

```bash
# Producto 1
curl -X POST http://localhost:8081/api/franchises/$FRANCHISE_ID/branches/$BRANCH_ID/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Café Latte",
    "stock": 50
  }'

# Producto 2
curl -X POST http://localhost:8081/api/franchises/$FRANCHISE_ID/branches/$BRANCH_ID/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Cappuccino",
    "stock": 30
  }'

# Producto 3
curl -X POST http://localhost:8081/api/franchises/$FRANCHISE_ID/branches/$BRANCH_ID/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Espresso",
    "stock": 100
  }'
```

#### 4. Consultar Producto con Mayor Stock

```bash
curl http://localhost:8081/api/franchises/$FRANCHISE_ID/top-stock-products | jq
```

**Resultado Esperado:**

```json
[
  {
    "branchId": "fe5dadc0-d393-4e1c-9270-27c99e6f4a28",
    "branchName": "Sucursal Centro",
    "productId": "dc1ed759-e5b1-4b22-9e5a-53a4cc48d104",
    "productName": "Espresso",
    "stock": 100
  }
]
```

---

### Usar con Postman

1. **Importar Collection:**
    - Descargar [franchise-api.postman_collection.json](./postman/franchise-api.postman_collection.json)
    - Importar en Postman

2. **Configurar Environment:**
   ```json
   {
     "base_url": "http://localhost:8081",
     "franchise_id": "",
     "branch_id": "",
     "product_id": ""
   }
   ```

3. **Ejecutar Requests:**
    - Los IDs se guardan automáticamente en variables

---

### Usar con JavaScript/Fetch

```javascript
const baseURL = 'http://localhost:8081/api';

// Crear franquicia
async function createFranchise(name) {
  const response = await fetch(`${baseURL}/franchises`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ name })
  });
  return response.json();
}

// Listar franquicias
async function getAllFranchises() {
  const response = await fetch(`${baseURL}/franchises`);
  return response.json();
}

// Agregar producto
async function addProduct(franchiseId, branchId, name, stock) {
  const response = await fetch(
    `${baseURL}/franchises/${franchiseId}/branches/${branchId}/products`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name, stock })
    }
  );
  return response.json();
}

// Uso
(async () => {
  const franchise = await createFranchise('Starbucks');
  console.log('Franquicia creada:', franchise);
})();
```

---

### Usar con Python/Requests

```python
import requests

base_url = 'http://localhost:8081/api'

# Crear franquicia
def create_franchise(name):
    response = requests.post(
        f'{base_url}/franchises',
        json={'name': name}
    )
    return response.json()

# Listar franquicias
def get_all_franchises():
    response = requests.get(f'{base_url}/franchises')
    return response.json()

# Agregar producto
def add_product(franchise_id, branch_id, name, stock):
    response = requests.post(
        f'{base_url}/franchises/{franchise_id}/branches/{branch_id}/products',
        json={'name': name, 'stock': stock}
    )
    return response.json()

# Uso
if __name__ == '__main__':
    franchise = create_franchise('Starbucks')
    print(f'Franquicia creada: {franchise}')
```

---

## 📊 Swagger UI

Para una exploración interactiva de la API:

```
http://localhost:8081/swagger-ui.html
```

**Características:**
- ✅ Probar endpoints en tiempo real
- ✅ Ver esquemas de datos
- ✅ Generar código de cliente
- ✅ Descargar especificación OpenAPI

---

## 🔄 Versionado de API

**Estrategia:** Semantic Versioning (SemVer)

- **MAJOR:** Cambios incompatibles (ej: v1 → v2)
- **MINOR:** Nuevas funcionalidades (ej: v1.0 → v1.1)
- **PATCH:** Corrección de bugs (ej: v1.0.0 → v1.0.1)

**URL Futura con Versiones:**
```
http://localhost:8081/api/v1/franchises
http://localhost:8081/api/v2/franchises
```

---

**Documentación generada automáticamente con SpringDoc OpenAPI**

Última actualización: 2026-01-27