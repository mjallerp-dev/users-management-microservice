# Users Management — Spring Boot, Arquitectura Hexagonal y DDD

API REST para la gestión de **usuarios** y **artículos**, construida con Java 17 y Spring Boot 3
siguiendo arquitectura hexagonal (puertos y adaptadores) y Domain-Driven Design.

- **Usuarios:** registro, autenticación con JWT, consulta, actualización (activación y cambio de rol) y eliminación, con notificaciones por correo.
- **Artículos:** CRUD completo con validación de dominio y permisos por rol.

**Despliegue:** https://users-management-microservice-34nm.onrender.com
(Swagger UI en [`/swagger-ui.html`](https://users-management-microservice-34nm.onrender.com/swagger-ui.html)).

## Contenido

1. [Tecnologías](#tecnologías)
2. [Arquitectura](#arquitectura)
3. [Requisitos](#requisitos)
4. [Configuración](#configuración)
5. [Base de datos](#base-de-datos)
6. [Ejecución local](#ejecución-local)
7. [Docker Compose](#docker-compose)
8. [Apache como proxy inverso (opcional)](#apache-como-proxy-inverso-opcional)
9. [Uso de la API](#uso-de-la-api)
10. [Correo electrónico](#correo-electrónico)
11. [Pruebas con Postman](#pruebas-con-postman)
12. [Despliegue en Render](#despliegue-en-render)
13. [Integración y despliegue continuo](#integración-y-despliegue-continuo)
14. [Control de versiones](#control-de-versiones)

---

## Tecnologías

| Categoría | Tecnología |
|---|---|
| Lenguaje y framework | Java 17, Spring Boot 3.3 (Web, Validation, Security, JDBC) |
| Persistencia | PostgreSQL 16, JDBC con HikariCP |
| Seguridad | JWT (jjwt), contraseñas con BCrypt |
| Correo | SMTP (JavaMail) o API REST de Brevo |
| Documentación | OpenAPI 3 / Swagger UI (springdoc) |
| Pruebas | JUnit 5, Mockito, MockMvc, JaCoCo, Postman / Newman |
| Infraestructura | Docker, Docker Compose, Render, Azure Database for PostgreSQL |
| Integración continua | GitHub Actions |

## Arquitectura

```
src/main/java/com/jcaa/usersmanagement/
├── domain/              Modelos, Value Objects, Enums, excepciones y eventos de dominio
├── application/
│   ├── port/in/         Casos de uso (interfaces)
│   ├── port/out/        Puertos de salida: persistencia y envío de correo
│   └── service/         Implementación de los casos de uso, commands, queries y mappers
└── infrastructure/
    ├── adapter/
    │   ├── persistence/ Repositorios PostgreSQL (JDBC), entidades, DTOs y mappers
    │   └── email/       Adaptadores de correo: SMTP y Brevo
    ├── entrypoint/rest/ Controladores, requests/responses, mappers y manejo de errores
    ├── security/        Filtro y servicio JWT, reglas de acceso por rol
    └── config/          Configuración de Spring (DataSource, correo, OpenAPI, asincronía)
```

Las dependencias apuntan hacia el dominio: el dominio no depende de ningún framework y la capa de
aplicación solo conoce los puertos. Spring es el único *composition root*. El código de la antigua CLI
(`entrypoint/desktop`) se conserva como adaptador inactivo.

## Requisitos

| Herramienta | Versión | Notas |
|---|---|---|
| JDK | **17** | Con JDK 23 o superior la compilación falla por la versión de Lombok. Si tienes varios JDK, define `JAVA_HOME` apuntando al 17. |
| Maven | Incluido | Se usa el wrapper `./mvnw` (en Windows `mvnw.cmd`). |
| PostgreSQL | 16 o superior | Local, en Docker o en la nube. |
| Docker | Opcional | Para ejecutar con Docker Compose. |

## Configuración

La configuración se lee de variables de entorno. Para desarrollo local, copia la plantilla y completa
los valores:

```bash
cp .env.example .env
```

Spring carga el archivo `.env` de la carpeta desde donde se ejecuta la aplicación
(`spring.config.import=optional:file:.env[.properties]`). Las variables de entorno del sistema tienen
prioridad sobre el `.env`. **Los valores van sin comillas**, porque el archivo se lee como `.properties`.

El `.env` está en `.gitignore` y nunca debe subirse al repositorio.

| Variable | Por defecto | Descripción |
|---|---|---|
| `PORT` | `8080` | Puerto HTTP de la aplicación. |
| `DB_HOST` | `localhost` | Servidor PostgreSQL. |
| `DB_PORT` | `5432` | Puerto de PostgreSQL. |
| `DB_NAME` | `pgarticlesdb` | Base de datos. |
| `DB_USERNAME` | `postgres` | Usuario. |
| `DB_PASSWORD` | *(vacío)* | Contraseña. |
| `DB_SSL_MODE` | `prefer` | `disable`, `prefer`, `require` o `verify-full`. Azure exige `require` o superior. |
| `EMAIL_PROVIDER` | `smtp` | Proveedor de correo: `smtp` o `brevo`. |
| `BREVO_API_KEY` | *(vacío)* | Clave de API de Brevo (`xkeysib-…`). Obligatoria si `EMAIL_PROVIDER=brevo`. |
| `BREVO_BASE_URL` | `https://api.brevo.com/v3` | URL de la API de Brevo (normalmente no se cambia). |
| `SMTP_HOST` | `smtp.gmail.com` | Servidor SMTP (solo con `smtp`). |
| `SMTP_PORT` | `587` | Puerto SMTP con STARTTLS (solo con `smtp`). |
| `SMTP_USERNAME` | *(vacío)* | Usuario SMTP (solo con `smtp`). |
| `SMTP_PASSWORD` | *(vacío)* | Contraseña o contraseña de aplicación (solo con `smtp`). |
| `SMTP_FROM_ADDRESS` | *(vacío)* | Remitente de los correos. Con Brevo debe ser un remitente verificado. |
| `SMTP_FROM_NAME` | `Gestion de Usuarios` | Nombre del remitente. |
| `JWT_SECRET` | Clave de desarrollo | Clave para firmar los tokens: al menos 32 bytes aleatorios en Base64 (`openssl rand -base64 48`). **Obligatoria fuera de desarrollo.** |
| `JWT_EXPIRATION_SECONDS` | `900` | Duración del token (15 minutos). |

## Base de datos

El esquema está en [`src/main/resources/schema.sql`](src/main/resources/schema.sql) y crea:

- La tabla `users` (con correo único).
- La tabla `articles`.
- Un **administrador inicial** activo: `admin@example.com` / `Admin1234!`. **Cambia la contraseña tras el primer inicio de sesión.**

Las tablas son idénticas a las del proyecto PHP `hexagonal-ddd-example-php`, por lo que ambas
aplicaciones pueden compartir la misma base de datos.

Para crear la base en un servidor existente:

```bash
psql -h HOST -U USUARIO -d postgres -c "CREATE DATABASE pgarticlesdb;"
psql -h HOST -U USUARIO -d pgarticlesdb -f src/main/resources/schema.sql
```

El script se puede ejecutar varias veces sin error: no recrea tablas existentes ni duplica el
administrador.

**Base de datos en la nube (Azure Database for PostgreSQL):** usa `DB_SSL_MODE=require` y agrega en
el firewall del servidor (*Redes → Reglas de firewall*) la IP pública de tu equipo y las IP de salida
del servicio donde despliegues la API.

## Ejecución local

```bash
./mvnw clean verify                 # compila y ejecuta los tests (con reporte de cobertura)
./mvnw spring-boot:run              # inicia la API en http://localhost:8080
```

Para generar y ejecutar el JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/users-management-2.2.0.jar
```

El reporte de cobertura de JaCoCo queda en `target/site/jacoco/index.html`.

| Recurso | URL |
|---|---|
| Estado del servicio | http://localhost:8080/health |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Especificación OpenAPI | http://localhost:8080/v3/api-docs |

`GET /health` responde `{"status":"UP","name":"users-management","version":"2.2.0"}`. Indica que la
API está en línea; no comprueba la base de datos ni el proveedor de correo.

## Docker Compose

[`compose.yaml`](compose.yaml) levanta PostgreSQL y la API, y construye la imagen con el
[`Dockerfile`](Dockerfile) multi-etapa (compila con Maven y ejecuta con un usuario sin privilegios).

```bash
docker compose up -d --build        # construir e iniciar
docker compose logs -f api          # ver los logs de la API
docker compose down                 # detener (con -v también se borran los datos)
```

| Servicio | Puerto en tu equipo | Notas |
|---|---|---|
| API | `8080` | http://localhost:8080 |
| PostgreSQL | `5433` | Usuario `users_app`, contraseña `local_users_password`, base `pgarticlesdb`. Se publica en 5433 para no chocar con un PostgreSQL local en 5432. |

La primera vez que se crea el volumen, PostgreSQL ejecuta `schema.sql` automáticamente. Docker
Compose toma de tu `.env` la configuración de correo (`EMAIL_PROVIDER`, `BREVO_API_KEY`, `SMTP_*`) y
`JWT_SECRET`; la conexión a la base de datos ya viene definida en `compose.yaml`.

## Apache como proxy inverso (opcional)

Para exponer la API en el puerto 80 de tu equipo con Apache HTTP Server
([Apache Lounge](https://www.apachelounge.com/download/) en Windows, instalado en `C:\Apache24`):

1. En `conf/httpd.conf`, activa los módulos y define el nombre del servidor:
   ```apache
   LoadModule proxy_module modules/mod_proxy.so
   LoadModule proxy_http_module modules/mod_proxy_http.so
   ServerName localhost:80
   Include conf/extra/httpd-api-proxy.conf
   ```
2. Crea `conf/extra/httpd-api-proxy.conf`:
   ```apache
   ProxyRequests Off
   ProxyPreserveHost On
   ProxyTimeout 60

   ProxyPass        "/" "http://127.0.0.1:8080/"
   ProxyPassReverse "/" "http://127.0.0.1:8080/"
   ```
3. Valida la configuración con `httpd -t`, inicia la API y luego Apache (`C:\Apache24\bin\httpd.exe`).

La API queda disponible en http://localhost y Swagger en http://localhost/swagger-ui.html.

## Uso de la API

### Autenticación

Todas las rutas, salvo las públicas, requieren un token JWT en la cabecera
`Authorization: Bearer <token>`.

```http
POST /api/auth/login
Content-Type: application/json

{ "email": "admin@example.com", "password": "Admin1234!" }
```

```json
{ "accessToken": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer", "expiresIn": 900 }
```

En Swagger UI, pulsa **Authorize** y pega solo el valor de `accessToken`.

### Endpoints y permisos

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| GET | `/health` | Público | 200 |
| POST | `/api/auth/login` | Público | 200, 400, 401 |
| POST | `/api/users` | Público (registro) | 201, 400, 409 |
| GET | `/api/users` | ADMIN, REVIEWER | 200 |
| GET | `/api/users/{id}` | ADMIN, REVIEWER | 200, 404 |
| PUT | `/api/users/{id}` | ADMIN | 200, 400, 404, 409 |
| DELETE | `/api/users/{id}` | ADMIN | 204, 404 |
| GET | `/api/articles` | ADMIN, REVIEWER, MEMBER | 200 |
| GET | `/api/articles/{id}` | ADMIN, REVIEWER, MEMBER | 200, 404 |
| POST | `/api/articles` | ADMIN | 201, 400, 409 |
| PUT | `/api/articles/{id}` | ADMIN | 200, 400, 404 |
| DELETE | `/api/articles/{id}` | ADMIN | 204, 404 |

Sin token, las rutas protegidas responden **401**; con un rol sin permiso, **403**.

### Ciclo de vida de los usuarios

1. Un usuario se registra con `POST /api/users` (roles: `ADMIN`, `MEMBER`, `REVIEWER`) y queda en
   estado `PENDING`. Recibe un correo de bienvenida.
2. Mientras esté `PENDING` no puede iniciar sesión.
3. Un ADMIN lo activa con `PUT /api/users/{id}` (`"status": "ACTIVE"`) y puede cambiar su rol.

> **Importante:** el rol lo indica el propio usuario al registrarse. **Antes de activar una cuenta, el
> ADMIN debe verificar el rol solicitado y corregirlo si no corresponde.**

Estados posibles: `PENDING`, `ACTIVE`, `INACTIVE`, `BLOCKED`.

### Artículos

Ejemplo de cuerpo para `POST /api/articles` (en `PUT` se envían los mismos campos sin `id`):

```json
{
  "id": "art-001",
  "description": "Portátil 14 pulgadas",
  "provider": "Proveedor Demo",
  "brand": "Lenovo",
  "model": "ThinkPad E14",
  "category": "electronica",
  "store": "Tienda Centro",
  "quantity": 5,
  "purchasePrice": 2500000.5,
  "salePrice": 3200000,
  "iva": 19
}
```

| Campo | Reglas |
|---|---|
| `id` | Obligatorio y único. |
| `description`, `provider`, `brand`, `model`, `store` | Obligatorios, mínimo 3 caracteres. |
| `category` | Una de: `electronica`, `ropa`, `calzado`, `alimentos`, `bebidas`, `hogar`, `belleza`, `deportes`, `papeleria`, `ferreteria`, `otros` (no distingue mayúsculas). |
| `quantity` | Entero mayor o igual a 0. |
| `purchasePrice`, `salePrice` | Mayores que 0; se guardan con 2 decimales. |
| `iva` | Entre 0 y 100; se guarda con 2 decimales. |

Los nombres de los campos JSON van en *camelCase* (`purchasePrice`), a diferencia de la API del
proyecto PHP (`purchase_price`). `GET /api/articles` devuelve los artículos ordenados por descripción.

### Errores

Todas las respuestas de error tienen el mismo formato:

```json
{ "status": 404, "message": "The article with id 'art-404' was not found." }
```

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos, categoría inexistente o JSON mal formado (`El cuerpo de la solicitud no es un JSON válido.`). |
| 401 | Sin token, token inválido o expirado, o credenciales incorrectas. |
| 403 | El rol del usuario no tiene permiso. |
| 404 | El usuario o artículo no existe. |
| 409 | Correo de usuario o id de artículo ya registrado. |
| 500 | Error interno (sin detalles técnicos en la respuesta). |

## Correo electrónico

Al registrar y al actualizar un usuario se envía una notificación por correo de forma **asíncrona**:
si el envío falla, la operación se completa igual y el error queda registrado en el log.

`EMAIL_PROVIDER` decide el adaptador que se usa:

| Valor | Adaptador | Uso recomendado |
|---|---|---|
| `smtp` (por defecto) | `JavaMailEmailSenderAdapter` (SMTP con STARTTLS) | Desarrollo local, por ejemplo con Gmail y una contraseña de aplicación. |
| `brevo` | `BrevoEmailSenderAdapter` (API REST por HTTPS) | Render y cualquier entorno que bloquee los puertos SMTP. |

Ambos adaptadores implementan el puerto `EmailSenderPort`, por lo que cambiar de proveedor no afecta
al dominio ni a los casos de uso. Si `EMAIL_PROVIDER` tiene un valor distinto de `smtp` o `brevo`, la
aplicación no arranca.

### Configurar Brevo

1. Crea una cuenta gratuita en [brevo.com](https://www.brevo.com) (300 correos al día).
2. Verifica el remitente en *Senders, Domains & Dedicated IPs → Senders*. Ese correo será
   `SMTP_FROM_ADDRESS`.
3. Genera una **clave de API** en *SMTP & API → API Keys & MCP* (empieza por `xkeysib-`; las claves
   SMTP `xsmtpsib-` no sirven):
   - **Access:** Restricted.
   - **Permissions:** solo *Transactional email* en **All** (es el único nivel que permite enviar).
   - No actives *Create MCP server API key*.
4. En *Security → Authorized IPs*, deja **desactivado** el bloqueo de IP para las claves de API
   (servicios como Render no tienen IP fijas).
5. Configura `EMAIL_PROVIDER=brevo` y `BREVO_API_KEY` en el `.env` o en las variables del servicio.

Si `EMAIL_PROVIDER=brevo` y falta `BREVO_API_KEY`, la aplicación no arranca. Si Brevo rechaza un
envío, el log muestra su motivo (por ejemplo `HTTP 401: Key not found`); la clave nunca se escribe en
el log. Brevo desactiva las claves que pasan 90 días sin usarse.

## Pruebas con Postman

La carpeta [`postman/`](postman) contiene la colección y tres entornos:

| Archivo | Uso |
|---|---|
| `users-management-api.postman_collection.json` | Colección: health, autenticación, CRUD de artículos, permisos por rol, usuarios y limpieza (24 peticiones, 44 comprobaciones). |
| `local.postman_environment.json` | `http://localhost:8080` (local o Docker Compose). |
| `apache.postman_environment.json` | `http://localhost` (a través de Apache). |
| `render.postman_environment.json` | Despliegue en Render. Completa `adminEmail` y `adminPassword` en *Current value*. |

1. En Postman, **Import** → selecciona los archivos de `postman/`.
2. Elige el entorno arriba a la derecha.
3. Clic derecho en la colección → **Run collection** → **Run**.

El login guarda el token automáticamente y todas las peticiones lo heredan. La colección se puede
ejecutar varias veces: genera ids únicos y la carpeta *Limpieza* borra los datos creados.

También se puede ejecutar por línea de comandos con Newman:

```bash
npx newman run postman/users-management-api.postman_collection.json \
  -e postman/local.postman_environment.json
```

> La colección registra y activa un usuario de prueba, lo que **envía 2 correos** a direcciones
> `@example.com`. Contra un entorno con correo real (por ejemplo Render con Brevo) esos correos
> rebotan y cuentan para el límite diario.

## Despliegue en Render

[`render.yaml`](render.yaml) define el servicio web (Docker, plan gratuito) y el chequeo de salud en
`/health`. El despliegue automático de Render está desactivado (`autoDeployTrigger: "off"`): los
despliegues los dispara GitHub Actions solo cuando los tests pasan (ver
[Integración y despliegue continuo](#integración-y-despliegue-continuo)). El plan gratuito de Render
**bloquea los puertos SMTP**, por eso el despliegue usa Brevo (`EMAIL_PROVIDER=brevo`).

1. Prepara la base de datos con `schema.sql` (por ejemplo en Azure Database for PostgreSQL).
2. En Render: **New → Blueprint** y selecciona este repositorio.
3. Completa los secretos que pide el Blueprint:

   | Variable | Valor |
   |---|---|
   | `DB_HOST`, `DB_USERNAME`, `DB_PASSWORD` | Datos de la base de datos |
   | `BREVO_API_KEY` | Clave de API de Brevo |
   | `SMTP_FROM_ADDRESS` | Remitente verificado en Brevo |

   El resto ya viene en `render.yaml`: `DB_PORT=5432`, `DB_NAME=pgarticlesdb`, `DB_SSL_MODE=require`,
   `EMAIL_PROVIDER=brevo`, `JWT_SECRET` (generado por Render) y
   `JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75` (aprovecha los 512 MB del plan gratuito).
4. **Permite las IP de Render en la base de datos.** En Render, *Connect → Outbound* muestra los
   rangos de salida del servicio (en Oregón: `74.220.48.0/24` y `74.220.56.0/24`). Agrégalos al
   firewall de la base de datos; en Azure, un `/24` va de `x.x.x.0` a `x.x.x.255`. Si falta este paso,
   el log muestra `Connect timed out` y el despliegue falla.
5. Despliega y verifica `/health` y `/swagger-ui.html` en la URL asignada (`https://<servicio>.onrender.com`).
   El primer despliegue lo hace Render al crear el Blueprint; los siguientes los dispara GitHub Actions
   (o, a mano, **Manual Deploy → Deploy latest commit** en Render).

En el plan gratuito el servicio se suspende tras unos 15 minutos sin uso: la primera petición
posterior tarda entre 30 y 60 segundos.

Las credenciales nunca deben guardarse en `application.properties`, `render.yaml` ni en el
repositorio: se configuran como variables de entorno o secretos del servicio.

## Integración y despliegue continuo

El workflow [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) de GitHub Actions tiene dos
trabajos:

| Trabajo | Cuándo se ejecuta | Qué hace |
|---|---|---|
| **Compilar y ejecutar tests** | En cada `push` a cualquier rama, en cada pull request hacia `main` y a mano (*Run workflow*) | Compila con JDK 17 y ejecuta todos los tests (`mvn clean verify`). Publica el reporte de cobertura (`jacoco-report`) y, si algo falla, los reportes de los tests (`surefire-reports`) como artefactos durante 7 días. |
| **Desplegar en Render** | Solo en `push` a `main` y solo si el trabajo anterior pasó | Llama al *Deploy Hook* de Render para desplegar el commit. |

Así, una versión con tests fallidos nunca llega a producción.

**Configuración (una sola vez):**

1. En Render: tu servicio → **Settings → Deploy Hook** → copia la URL.
2. En GitHub: el repositorio → **Settings → Secrets and variables → Actions → New repository secret**,
   con el nombre `RENDER_DEPLOY_HOOK_URL` y la URL como valor.

La URL del Deploy Hook permite disparar despliegues: guárdala solo como secreto. Si falta el
secreto, el trabajo de despliegue falla con un mensaje que lo indica.

Los resultados se ven en la pestaña **Actions** del repositorio; cada ejecución incluye los artefactos
para descargar, y los despliegues aparecen en el entorno **production**.

## Control de versiones

- **Ramas:** una rama por capa y módulo, con el prefijo del tipo de cambio: `feature/article-domain`,
  `feature/article-application`, `feature/article-infrastructure`; `chore/...` para configuración y
  herramientas (por ejemplo `chore/github-actions`).
- **Commits:** `tipo(módulo-capa): descripción en inglés`, por ejemplo
  `feat(article-domain): add article value objects` o `test(article-infrastructure): add article rest controller tests`,
  con un máximo de 10 archivos por commit.
