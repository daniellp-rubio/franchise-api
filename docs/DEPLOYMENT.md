# 🚀 Guía de Despliegue - Franchise API

Esta guía detalla el proceso completo para desplegar Franchise API en AWS usando Terraform.

---

## 📑 Tabla de Contenidos

- [Arquitectura de Despliegue](#arquitectura-de-despliegue)
- [Requisitos Previos](#requisitos-previos)
- [Configuración Inicial](#configuración-inicial)
- [MongoDB Atlas Setup](#mongodb-atlas-setup)
- [Despliegue Local](#despliegue-local)
- [Despliegue en AWS](#despliegue-en-aws)
- [Verificación](#verificación)
- [Troubleshooting](#troubleshooting)
- [Rollback](#rollback)
- [Costos Estimados](#costos-estimados)

---

## 🏗 Arquitectura de Despliegue

```
┌─────────────────────────────────────────────────────────┐
│                    Internet                              │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│              AWS ECS Fargate                             │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Task: franchise-api                             │  │
│  │  - Container: franchise-api:latest               │  │
│  │  - CPU: 256, Memory: 512MB                       │  │
│  │  - Port: 8081                                    │  │
│  └──────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│              MongoDB Atlas                               │
│  - Cluster: M0 Free Tier                                │
│  - Region: us-east-1                                    │
│  - Database: franchisedb                                │
└─────────────────────────────────────────────────────────┘
```

### Componentes AWS

1. **ECR (Elastic Container Registry)**
    - Almacena las imágenes Docker
    - Versionamiento automático

2. **ECS Fargate**
    - Serverless container orchestration
    - Auto-scaling
    - Sin gestión de servidores

3. **CloudWatch**
    - Logs centralizados
    - Métricas y monitoreo

4. **IAM Roles**
    - Permisos para ECS tasks
    - Políticas de seguridad

---

## 📋 Requisitos Previos

### Software Requerido

```bash
# Verificar instalaciones
java -version          # Java 17+
mvn -version           # Maven 3.6+
docker --version       # Docker 20+
terraform --version    # Terraform 1.0+
aws --version          # AWS CLI 2.0+
```

### Cuentas Necesarias

1. **AWS Account**
    - Acceso a consola
    - Programmatic access
    - Billing configurado

2. **MongoDB Atlas Account** (Gratis)
    - Cluster M0 Free Tier
    - Network access configurado

---

## ⚙️ Configuración Inicial

### 1. Configurar AWS CLI

```bash
# Configurar credenciales
aws configure

# Ingresar:
# AWS Access Key ID: [tu-access-key]
# AWS Secret Access Key: [tu-secret-key]
# Default region: us-east-1
# Default output format: json
```

### 2. Crear IAM User para Terraform (Recomendado)

```bash
# Via AWS Console:
# 1. IAM > Users > Create User
# 2. Username: terraform-user
# 3. Attach policies:
#    - AmazonECS_FullAccess
#    - AmazonEC2ContainerRegistryFullAccess
#    - IAMFullAccess
#    - CloudWatchLogsFullAccess
# 4. Create access key > Download credentials
```

### 3. Variables de Entorno

Crear archivo `.env` en la raíz:

```bash
# AWS Configuration
AWS_REGION=us-east-1
AWS_ACCOUNT_ID=123456789012

# MongoDB Configuration
MONGODB_URI=mongodb+srv://user:password@cluster.mongodb.net/franchisedb?retryWrites=true&w=majority

# Application Configuration
APP_NAME=franchise-api
ENVIRONMENT=production
```

---

## 🍃 MongoDB Atlas Setup

### Paso 1: Crear Cuenta

1. Ir a https://www.mongodb.com/cloud/atlas/register
2. Completar registro (gratis)

### Paso 2: Crear Cluster

1. Click **Build a Database**
2. Seleccionar **Shared** (Free)
3. Elegir **M0 Sandbox**
4. Provider: **AWS**
5. Region: **us-east-1** (N. Virginia)
6. Cluster Name: `franchise-cluster`
7. Click **Create**

### Paso 3: Configurar Database Access

```bash
# 1. Security > Database Access > Add New Database User
Username: franchiseadmin
Password: [generar contraseña segura]
Database User Privileges: Read and write to any database

# 2. Add User
```

### Paso 4: Configurar Network Access

```bash
# 1. Security > Network Access > Add IP Address
# 2. Click "Allow Access from Anywhere" (0.0.0.0/0)
# 3. Confirm

# NOTA: En producción, usa solo IPs específicas
```

### Paso 5: Obtener Connection String

1. Databases > Connect > Connect your application
2. Driver: Java, Version: 4.3 or later
3. Copiar connection string:

```
mongodb+srv://franchiseadmin:<password>@franchise-cluster.xxxxx.mongodb.net/?retryWrites=true&w=majority
```

4. Reemplazar `<password>` con tu contraseña
5. Agregar nombre de base de datos:

```
mongodb+srv://franchiseadmin:TuPassword@franchise-cluster.xxxxx.mongodb.net/franchisedb?retryWrites=true&w=majority
```

---

## 💻 Despliegue Local

### Con Docker Compose

```bash
# 1. Clonar repositorio
git clone https://github.com/tu-usuario/franchise-api.git
cd franchise-api

# 2. Configurar variables
cp .env.example .env
# Editar .env con tus valores

# 3. Levantar servicios
docker-compose up -d

# 4. Ver logs
docker-compose logs -f franchise-api

# 5. Verificar
curl http://localhost:8081/actuator/health
```

### Con Maven (Desarrollo)

```bash
# 1. Iniciar MongoDB local
docker run -d -p 27017:27017 --name franchise-mongodb mongo:7.0

# 2. Ejecutar aplicación
mvn spring-boot:run

# 3. Verificar
curl http://localhost:8081/api/franchises
```

---

## ☁️ Despliegue en AWS

### Fase 1: Preparar Infraestructura con Terraform

#### 1.1 Inicializar Terraform

```bash
cd terraform

# Inicializar
terraform init

# Validar configuración
terraform validate
```

#### 1.2 Planificar Infraestructura

```bash
# Ver qué se va a crear
terraform plan \
  -var="mongodb_uri=mongodb+srv://user:pass@cluster.mongodb.net/franchisedb"

# Deberías ver:
# Plan: 8 to add, 0 to change, 0 to destroy
```

#### 1.3 Aplicar Infraestructura

```bash
# Crear recursos en AWS
terraform apply \
  -var="mongodb_uri=mongodb+srv://user:pass@cluster.mongodb.net/franchisedb"

# Escribir 'yes' cuando pregunte

# Esperar ~2-3 minutos
```

#### 1.4 Verificar Recursos Creados

```bash
# Ver outputs
terraform output

# Deberías ver:
# ecr_repository_url = "123456789.dkr.ecr.us-east-1.amazonaws.com/franchise-api"
# ecs_cluster_name = "franchise-api-cluster"
# ecs_service_name = "franchise-api-service"
```

---

### Fase 2: Construir y Subir Imagen Docker

#### 2.1 Obtener URL del ECR

```bash
# Guardar en variable
ECR_URL=$(terraform output -raw ecr_repository_url)
echo $ECR_URL
```

#### 2.2 Autenticar Docker en ECR

```bash
# Login en ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin $ECR_URL

# Deberías ver: Login Succeeded
```

#### 2.3 Construir Imagen

```bash
# Volver a raíz del proyecto
cd ..

# Construir imagen
docker build -t franchise-api:latest .

# Verificar imagen
docker images | grep franchise-api
```

#### 2.4 Tag y Push a ECR

```bash
# Tag imagen
docker tag franchise-api:latest $ECR_URL:latest

# Push a ECR
docker push $ECR_URL:latest

# Tiempo estimado: 2-5 minutos
```

---

### Fase 3: Desplegar en ECS

#### 3.1 Forzar Nuevo Deployment

```bash
# Actualizar servicio ECS
aws ecs update-service \
  --cluster franchise-api-cluster \
  --service franchise-api-service \
  --force-new-deployment \
  --region us-east-1
```

#### 3.2 Monitorear Deployment

```bash
# Ver estado del servicio
aws ecs describe-services \
  --cluster franchise-api-cluster \
  --services franchise-api-service \
  --region us-east-1 \
  --query "services[0].{Running:runningCount,Desired:desiredCount}"

# Esperar hasta que Running = Desired
```

#### 3.3 Ver Logs

```bash
# Logs en tiempo real
aws logs tail /ecs/franchise-api --region us-east-1 --follow

# Logs de los últimos 10 minutos
aws logs tail /ecs/franchise-api --region us-east-1 --since 10m
```

---

### Fase 4: Obtener IP Pública

#### 4.1 Script Automatizado

```bash
# Obtener Task ARN
TASK_ARN=$(aws ecs list-tasks \
  --cluster franchise-api-cluster \
  --service-name franchise-api-service \
  --region us-east-1 \
  --query "taskArns[0]" \
  --output text)

echo "Task ARN: $TASK_ARN"

# Obtener Network Interface ID
ENI_ID=$(aws ecs describe-tasks \
  --cluster franchise-api-cluster \
  --tasks $TASK_ARN \
  --region us-east-1 \
  --query "tasks[0].attachments[0].details[?name=='networkInterfaceId'].value | [0]" \
  --output text)

echo "ENI ID: $ENI_ID"

# Obtener IP Pública
PUBLIC_IP=$(aws ec2 describe-network-interfaces \
  --network-interface-ids $ENI_ID \
  --region us-east-1 \
  --query "NetworkInterfaces[0].Association.PublicIp" \
  --output text)

echo "========================================="
echo "🎉 API Pública: http://$PUBLIC_IP:8081"
echo "========================================="
```

---

## ✅ Verificación

### Health Check

```bash
# Verificar salud de la aplicación
curl http://$PUBLIC_IP:8081/actuator/health

# Respuesta esperada:
# {"status":"UP"}
```

### Probar Endpoints

```bash
# Crear franquicia
curl -X POST http://$PUBLIC_IP:8081/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name":"Test AWS"}'

# Listar franquicias
curl http://$PUBLIC_IP:8081/api/franchises

# Swagger UI
# Abrir en navegador:
http://$PUBLIC_IP:8081/swagger-ui.html
```

### Verificar Logs

```bash
# Ver logs de la aplicación
aws logs tail /ecs/franchise-api --region us-east-1 --since 5m

# Filtrar errores
aws logs tail /ecs/franchise-api --region us-east-1 --since 1h --filter-pattern "ERROR"
```

### Verificar Métricas

```bash
# CloudWatch Console
# Services > CloudWatch > Metrics > ECS

# Métricas importantes:
# - CPUUtilization
# - MemoryUtilization
# - Running tasks count
```

---

## 🔧 Troubleshooting

### Problema 1: Task No Inicia

**Síntoma:**
```bash
aws ecs describe-services ... 
# runningCount: 0
```

**Diagnóstico:**
```bash
# Ver eventos del servicio
aws ecs describe-services \
  --cluster franchise-api-cluster \
  --services franchise-api-service \
  --region us-east-1 \
  --query "services[0].events[0:5]"
```

**Soluciones Comunes:**
- Imagen no encontrada en ECR → Verificar push exitoso
- Falta de recursos → Aumentar CPU/Memory en Terraform
- Variable de entorno incorrecta → Revisar task definition

---

### Problema 2: MongoDB No Conecta

**Síntoma:**
```json
{"status":"DOWN"}
```

**Diagnóstico:**
```bash
# Ver logs
aws logs tail /ecs/franchise-api --region us-east-1 --since 10m | grep "mongo"
```

**Soluciones:**
1. Verificar MongoDB URI en task definition
2. Comprobar Network Access en MongoDB Atlas (0.0.0.0/0)
3. Verificar credenciales de usuario
4. Verificar nombre correcto de base de datos en URI

---

### Problema 3: Puerto No Accesible

**Síntoma:**
```bash
curl: (7) Failed to connect to X.X.X.X port 8081
```

**Solución:**
```bash
# Verificar Security Group
aws ec2 describe-security-groups \
  --filters "Name=group-name,Values=franchise-api-ecs-tasks-sg" \
  --region us-east-1 \
  --query "SecurityGroups[0].IpPermissions"

# Si puerto 8081 no está abierto, agregar regla:
SG_ID=$(aws ec2 describe-security-groups \
  --filters "Name=group-name,Values=franchise-api-ecs-tasks-sg" \
  --region us-east-1 \
  --query "SecurityGroups[0].GroupId" \
  --output text)

aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 8081 \
  --cidr 0.0.0.0/0 \
  --region us-east-1
```

---

### Problema 4: Variable de Entorno No Se Lee

**Síntoma:**
Logs muestran `localhost:27017` en lugar de MongoDB Atlas

**Solución:**
La variable debe llamarse `SPRING_DATA_MONGODB_URI`, no `MONGODB_URI`.

```bash
# Verificar task definition
aws ecs describe-task-definition \
  --task-definition franchise-api \
  --region us-east-1 \
  --query "taskDefinition.containerDefinitions[0].environment"

# Si está mal, actualizar en terraform/main.tf y re-aplicar
```

---

## ⏪ Rollback

### Rollback de Task Definition

```bash
# Listar revisiones
aws ecs list-task-definitions \
  --family-prefix franchise-api \
  --region us-east-1

# Rollback a revisión anterior
aws ecs update-service \
  --cluster franchise-api-cluster \
  --service franchise-api-service \
  --task-definition franchise-api:REVISION_NUMBER \
  --region us-east-1
```

### Rollback de Imagen Docker

```bash
# Ver tags en ECR
aws ecr list-images \
  --repository-name franchise-api \
  --region us-east-1

# Deployar tag anterior
# (re-tag y re-push de imagen anterior)
```

### Rollback Completo de Infraestructura

```bash
cd terraform

# Destruir todo
terraform destroy \
  -var="mongodb_uri=mongodb+srv://..."

# Escribir 'yes' cuando pregunte
```

---

## 💰 Costos Estimados

### AWS (Mensual)

| Servicio | Uso | Costo Estimado |
|----------|-----|----------------|
| ECS Fargate | 1 task, 24/7 | $5-10 |
| ECR | 1 GB storage | $0.10 |
| CloudWatch Logs | 1 GB/mes | $0.50 |
| Data Transfer | 10 GB/mes | $0.90 |
| **Total** | | **~$6-12/mes** |

### MongoDB Atlas

| Tier | Uso | Costo |
|------|-----|-------|
| M0 Sandbox | 512 MB storage | **Gratis** |

### Total Estimado

**$6-12 USD/mes** (solo AWS, MongoDB gratis)

### Optimizaciones de Costo

1. **Usar Spot Instances** (no disponible en Fargate)
2. **Reducir recursos**: 256 CPU, 512 MB Memory es suficiente
3. **Configurar auto-stop** para dev/staging
4. **Implementar caching** para reducir calls a DB

---

## 📊 Monitoreo y Alertas

### CloudWatch Alarms

```bash
# Crear alarma para CPU alta
aws cloudwatch put-metric-alarm \
  --alarm-name franchise-api-high-cpu \
  --alarm-description "CPU usage > 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2
```

### Dashboards

```bash
# Crear dashboard personalizado
# AWS Console > CloudWatch > Dashboards > Create dashboard
```

---

## 🔄 CI/CD (GitHub Actions)

### Archivo: `.github/workflows/deploy.yml`

```yaml
name: Deploy to AWS

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v1
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: us-east-1
    
    - name: Login to Amazon ECR
      id: login-ecr
      uses: aws-actions/amazon-ecr-login@v1
    
    - name: Build and push Docker image
      env:
        ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
        ECR_REPOSITORY: franchise-api
        IMAGE_TAG: ${{ github.sha }}
      run: |
        docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .
        docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
    
    - name: Force new deployment
      run: |
        aws ecs update-service \
          --cluster franchise-api-cluster \
          --service franchise-api-service \
          --force-new-deployment
```

---

## 📝 Checklist de Despliegue

### Pre-Despliegue

- [ ] Cuenta AWS configurada
- [ ] MongoDB Atlas cluster creado
- [ ] Variables de entorno configuradas
- [ ] AWS CLI instalado y configurado
- [ ] Terraform instalado
- [ ] Docker instalado y corriendo

### Despliegue

- [ ] Terraform init ejecutado
- [ ] Terraform plan revisado
- [ ] Terraform apply exitoso
- [ ] Imagen Docker construida
- [ ] Imagen subida a ECR
- [ ] Servicio ECS desplegado
- [ ] IP pública obtenida

### Post-Despliegue

- [ ] Health check pasa
- [ ] Endpoints responden correctamente
- [ ] Logs sin errores
- [ ] MongoDB conectando
- [ ] Swagger UI accesible
- [ ] Métricas en CloudWatch
- [ ] Alarmas configuradas

---

## 🆘 Soporte

Si encuentras problemas:

1. **Revisar logs:**
   ```bash
   aws logs tail /ecs/franchise-api --region us-east-1 --follow
   ```

2. **Verificar status:**
   ```bash
   aws ecs describe-services --cluster franchise-api-cluster --services franchise-api-service --region us-east-1
   ```

3. **Abrir issue:** [GitHub Issues](https://github.com/tu-usuario/franchise-api/issues)

---

## 📚 Referencias

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- [Spring Boot on AWS](https://spring.io/guides/gs/spring-boot-docker/)

---

¡Feliz despliegue! 🚀