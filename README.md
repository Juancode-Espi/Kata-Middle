# 🚀 Release Approval System — PoC Microservicios

Sistema basado en microservicios para la aprobación automática del ciclo de vida de releases de software.

---

## 🏗️ Arquitectura

```
Frontend (Angular 17)  →  API Gateway (8080)
                               │
                       Release Service (8081)  ──→  PostgreSQL (5432)
                               │
                    ┌──────────┼──────────┐
                    ▼          ▼          ▼
            Integration   Rules Svc   Notification
              Svc (8083)  (8082)      Svc (8084)
                              │
                           Mailhog (8025/1025)
```

## 📦 Servicios

| Servicio | Puerto | Descripción |
|---|---|---|
| api-gateway | 8080 | Punto de entrada único |
| release-service | 8081 | Orquestador principal + PostgreSQL |
| rules-service | 8082 | Evaluación de reglas (Strategy pattern) |
| integration-service | 8083 | Mock de GitHub (PR verification) |
| notification-service | 8084 | Envío de correos (Spring Mail) |
| postgres | 5432 | Base de datos |
| mailhog | 1025/8025 | SMTP de desarrollo (Web UI en :8025) |
| frontend | 4200 | Angular 17 + Nginx |

---

## ⚙️ Reglas de Negocio

Solo aplican cuando `tipo = rs`:

| Regla | Criterio |
|---|---|
| CoverageRule | `cobertura >= 80%` |
| DescriptionRule | `descripcion` no vacía |
| PrOrJiraRule | `prId` presente y verificado en Integration Svc |
| StackObsolescenceRule | Stack no obsoleto (Java 8, Angular 1, etc.) |

- ✅ Cumple todas → `APROBADO_AUTO` (tipo_aprobacion = AUTO)
- ⏳ Falla alguna → `PENDIENTE` (tipo_aprobacion = MANUAL) + correo enviado
- Los tipos `fx` y `cv` se aprueban automáticamente sin evaluación

---

## 🐳 Despliegue con Docker

### Requisitos

- Docker Desktop instalado y corriendo
- Puertos libres: 8080-8084, 5432, 4200, 1025, 8025

### Levantar todo el sistema

```bash
# Desde la raíz del proyecto (kata-middle/)
docker compose up --build
```

### Verificar servicios

```bash
# Health check
curl http://localhost:8080/api/releases

# Crear release tipo RS (debería aprobarse)
curl -X POST http://localhost:8080/api/releases \
  -H "Content-Type: application/json" \
  -d '{
    "equipo": "team-alpha",
    "tipo": "rs",
    "descripcion": "Implementación de nueva funcionalidad de pagos",
    "prId": "PR-123",
    "cobertura": 85.5,
    "stack": "Java 17 + Spring Boot 3",
    "notificationEmail": "dev@empresa.com"
  }'

# Crear release tipo RS (debería quedar pendiente + correo)
curl -X POST http://localhost:8080/api/releases \
  -H "Content-Type: application/json" \
  -d '{
    "equipo": "team-beta",
    "tipo": "rs",
    "descripcion": "Fix de seguridad",
    "prId": "",
    "cobertura": 45.0,
    "stack": "Java 8"
  }'
```

### Ver correos enviados (Mailhog)

Abre en el navegador: [http://localhost:8025](http://localhost:8025)

### Ver frontend

Abre en el navegador: [http://localhost:4200](http://localhost:4200)

---

## 🧪 Pruebas unitarias (Rules Service)

```bash
cd back/rules-service
mvn test
```

Cubre:
- Aprobación automática para tipos `fx` y `cv`
- Aprobación de RS con todos los criterios
- Rechazo por cobertura baja
- Rechazo por descripción vacía
- Rechazo por PR ausente o no verificado
- Rechazo por stack obsoleto
- Múltiples fallos simultáneos

---

## 🔑 Variables de Entorno

Configuradas en `docker-compose.yml`:

| Variable | Descripción |
|---|---|
| DB_HOST / DB_PORT / DB_NAME / DB_USER / DB_PASS | Conexión PostgreSQL |
| INTEGRATION_SERVICE_URL | URL del Integration Service |
| RULES_SERVICE_URL | URL del Rules Service |
| NOTIFICATION_SERVICE_URL | URL del Notification Service |
| SMTP_HOST / SMTP_PORT | Configuración SMTP (Mailhog en dev) |
| RELEASE_SERVICE_URL | URL del Release Service (usado por API Gateway) |

---

## 🧩 Stack Tecnológico

- **Backend**: Spring Boot 3.2.5 · Java 17 · Spring Cloud Gateway
- **ORM**: Spring Data JPA + Hibernate
- **Migraciones**: Flyway
- **Base de datos**: PostgreSQL 15
- **Correo**: Spring Mail + Mailhog
- **Frontend**: Angular 17 Standalone + SCSS
- **Contenedores**: Docker + Docker Compose
- **Tests**: JUnit 5 + AssertJ

---

## 💡 Decisiones de Diseño

- **RestTemplate** sobre Feign para minimizar dependencias
- **Strategy Pattern** en Rules Service → extensible sin modificar el orquestador
- **Flyway** para versionado de esquema DB
- **DTOs separados de entidades** → nunca se exponen entidades JPA en la API
- **Fail-safe**: si Integration o Rules Service fallan → release queda PENDIENTE
- **No-blocking notifications**: si el correo falla, el flujo continúa
