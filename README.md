# Trego - Backend

API REST del sistema Trego, una plataforma de compras a locales de comida con reparto a domicilio. Este repositorio contiene únicamente el backend de la aplicación; el frontend web y la aplicación mobile se desarrollan por separado.

El proyecto se enmarca en la asignatura Proyecto de la carrera Tecnólogo en Informática (C.E.T.P - UdelaR / ANEP-UTU).

## Descripción

El backend expone una API REST que da soporte a tres perfiles de usuario: administrador, local (restaurante) y cliente. Centraliza la gestión de usuarios, locales, platos y promociones, el ciclo de vida de los pedidos, los pagos, las notificaciones, la facturación y las calificaciones.

Las responsabilidades principales son:

- Autenticación y autorización mediante JWT, con contraseñas cifradas (BCrypt).
- Registro de locales y aprobación por parte del administrador.
- Gestión de productos, platos y promociones.
- Carrito de compras y creación de pedidos.
- Procesamiento de pagos a través de MercadoPago.
- Generación de facturas en PDF.
- Envío de notificaciones por correo electrónico y notificaciones push (Firebase).
- Resolución de direcciones y geolocalización mediante Geoapify.

## Stack tecnológico

- Java 21
- Spring Boot (Web, Data JPA, Security, Validation, Mail)
- Maven (con wrapper incluido, `mvnw`)
- MySQL como base de datos principal (H2 disponible como dependencia de runtime)
- JWT (jjwt) para la gestión de tokens
- Firebase Admin SDK para notificaciones push
- MercadoPago SDK para pagos
- Geoapify para geolocalización
- OpenPDF para la generación de facturas
- springdoc-openapi (Swagger UI) para la documentación de la API

## Requisitos previos

- JDK 21 instalado y configurado en el `PATH`.
- MySQL 8 en ejecución (local o remoto).
- Una cuenta de MercadoPago para obtener el access token (modo de pruebas).
- Una API key de Geoapify.
- Un archivo de credenciales de servicio de Firebase.

No es necesario instalar Maven de forma global: el repositorio incluye el wrapper (`mvnw` / `mvnw.cmd`).

## Configuración

La configuración sensible se mantiene fuera del control de versiones. El repositorio incluye una plantilla en `src/main/resources/application.properties.example`.

1. Copiar la plantilla al archivo real de configuración:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. Completar los valores en `application.properties`:

   - `spring.datasource.url`, `spring.datasource.username` y `spring.datasource.password`: conexión a MySQL. La URL incluye `createDatabaseIfNotExist=true`, por lo que la base se crea automáticamente si no existe.
   - `mercadopago.access.token`: token obtenido desde el panel de desarrolladores de MercadoPago.
   - `geoapify.api.key`: clave obtenida desde el panel de Geoapify.
   - `jwt.secret`: cadena larga y aleatoria utilizada para firmar los tokens.
   - Parámetros de correo (`mail.from`, `spring.mail.host`, `spring.mail.port`) según el entorno de envío de correo que se utilice.

3. Colocar el archivo de credenciales de Firebase en `src/main/resources/firebase-service-account.json`.

`application.properties` y el archivo de credenciales de Firebase no deben commitearse, ya que contienen información sensible.

## Compilación y ejecución

Desde la raíz del repositorio.

Compilar el proyecto y empaquetar el artefacto:

```bash
./mvnw clean package
```

En Windows:

```cmd
mvnw.cmd clean package
```

Ejecutar la aplicación en modo desarrollo:

```bash
./mvnw spring-boot:run
```

Ejecutar el artefacto generado directamente:

```bash
java -jar target/trego-0.0.1-SNAPSHOT.jar
```

Por defecto la aplicación queda disponible en `http://localhost:8080`.

## Pruebas

Ejecutar la suite de tests:

```bash
./mvnw test
```

## Documentación de la API

Con la aplicación en ejecución, la documentación interactiva (Swagger UI) está disponible en:

```
http://localhost:8080/swagger-ui.html
```

La especificación OpenAPI en formato JSON se expone en:

```
http://localhost:8080/v3/api-docs
```

## Seguridad y autenticación

La API es stateless y utiliza tokens JWT. Las rutas bajo `/api/auth/**` y las de la documentación (`/swagger-ui/**`, `/v3/api-docs/**`) son públicas; el resto de los endpoints requieren un token válido.

Para autenticarse, se obtiene un token a través de los endpoints de autenticación y luego se envía en cada petición mediante el encabezado:

```
Authorization: Bearer <token>
```

## Estructura del proyecto

El código fuente sigue la organización habitual de una aplicación Spring Boot, bajo el paquete `com.backend.trego`:

- `config` — configuración de seguridad, JWT, Firebase, correo, Swagger y manejo global de excepciones.
- `controller` — controladores REST que exponen los endpoints (autenticación, usuarios, restaurantes, productos, carrito, pedidos, pagos, notificaciones).
- `service` — lógica de negocio y servicios de integración (MercadoPago, Geoapify, generación de PDF, autenticación).
- `entity` — entidades JPA, DTOs y enumeraciones del dominio.
- `repository` — repositorios Spring Data JPA para el acceso a datos.

## Notas

Las claves y credenciales incluidas en la plantilla de configuración corresponden a entornos de prueba. No deben utilizarse credenciales reales ni dejarse activadas las trazas de logging de depuración (`logging.level...=TRACE`) en la entrega final.
