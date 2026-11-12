# Users Management — Spring Boot, Arquitectura Hexagonal y DDD

Aplicación de gestión de usuarios construida con Java 17 y Spring Boot. La API REST es el punto de entrada activo. El código de la antigua CLI se conserva como adaptador inactivo y no posee un contenedor de dependencias independiente.

Spring es el único *composition root*: `Main` inicia el contexto y las dependencias se resuelven mediante configuración y component scanning de Spring.

## Verificación

`GET /health` es publico y responde HTTP 200 con los metadatos generados desde
`pom.xml` durante el build:

```json
{"status":"UP","name":"users-management","version":"2.2.0"}
```

Indica que la API responde; no comprueba PostgreSQL ni el proveedor de correo.

```bash
./mvnw clean test
./mvnw clean package
```

En Windows se puede utilizar `mvnw.cmd`.

## Base de datos (PostgreSQL)

La persistencia usa PostgreSQL mediante JDBC (`UserRepositoryPostgreSQL`). La tabla
`users` es la misma que la del proyecto PHP `hexagonal-ddd-example-php`, por lo que
ambas aplicaciones pueden compartir la base de datos.

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Servidor PostgreSQL |
| `DB_PORT` | `5432` | Puerto |
| `DB_NAME` | `pgarticlesdb` | Base de datos |
| `DB_USERNAME` | `postgres` | Usuario |
| `DB_PASSWORD` | *(vacío)* | Contraseña |
| `DB_SSL_MODE` | `prefer` | `sslmode` del driver: `disable`, `prefer`, `require`, `verify-full`… Azure Database for PostgreSQL exige `require` o superior. |

Para crear el esquema en un servidor existente:

```bash
psql -h HOST -U USUARIO -d postgres     -c "CREATE DATABASE pgarticlesdb;"
psql -h HOST -U USUARIO -d pgarticlesdb -f src/main/resources/schema.sql
```

El script es idempotente y crea el administrador inicial `admin@example.com`
(contraseña `Admin1234!`) solo si no existe. Cámbiala tras el primer inicio de sesión.

Para levantar PostgreSQL y la API en local con Docker:

```bash
docker compose up -d --build
```

El contenedor de PostgreSQL ejecuta `schema.sql` automáticamente la primera vez
que se crea su volumen.

## Despliegue en Render

El archivo `render.yaml` define el servicio web, el build con Docker y el despliegue
automático de cada commit que llegue a la rama `main`.

1. Crear un Blueprint en Render y seleccionar este repositorio.
2. Completar en el panel los secretos marcados como requeridos: `DB_HOST`,
   `DB_USERNAME`, `DB_PASSWORD`, `SMTP_USERNAME`, `SMTP_PASSWORD` y
   `SMTP_FROM_ADDRESS`.
3. Usar una instancia PostgreSQL accesible desde Internet o desde la red privada de
   Render (por ejemplo, Azure Database for PostgreSQL, permitiendo en su firewall
   las IP de salida de Render) y ejecutar `src/main/resources/schema.sql` una vez
   para crear el esquema. `DB_SSL_MODE` se define como `require`.
4. Desplegar el Blueprint. La API quedará disponible en el subdominio
   `onrender.com` asignado por Render y Swagger UI en `/swagger-ui.html`.

Las credenciales nunca deben guardarse en `application.properties` ni en
`render.yaml`. Para desarrollo local, deben proporcionarse como variables de entorno.
