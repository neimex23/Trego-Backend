# Trego — Backend

API REST de Trego, una plataforma de pedidos a locales de comida con reparto a domicilio. Este repositorio contiene únicamente el backend; el frontend web (Trego-Web) y la aplicación Android (Trego-android) se desarrollan en repositorios separados.

Proyecto final de la asignatura Proyecto, carrera Tecnólogo en Informática (CETP-UTU / UdelaR).

## Índice

- [Descripción](#descripción)
- [Stack tecnológico](#stack-tecnológico)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Compilación y ejecución](#compilación-y-ejecución)
- [Documentación de la API](#documentación-de-la-api)
- [Autenticación](#autenticación)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Despliegue](#despliegue)

## Descripción

El backend expone una API REST que da soporte a tres perfiles de usuario: cliente, local (restaurante) y administrador. Centraliza la gestión de usuarios y locales, el catálogo de productos y ofertas, el ciclo de vida de los pedidos, los pagos, los reclamos y las notificaciones.

Responsabilidades principales:

- Autenticación con Firebase (Google y SMS) y JWT propio para la sesión; login con email y contraseña (BCrypt) para administradores.
- Registro de locales con verificación por código y aprobación del administrador.
- Catálogo de productos: platos, artículos, combos, ingredientes, subcategorías y ofertas.
- Carrito de compras y ciclo de vida completo del pedido (confirmación, preparación, entrega, cancelación, reembolso).
- Pagos con MercadoPago (checkout web y mobile, webhook de notificaciones).
- Reclamos de clientes y resolución con reintegro.
- Notificaciones por correo (SMTP) y push (Firebase Cloud Messaging).
- Facturas de pedidos en PDF.
- Geolocalización y cálculo de zonas de entrega con Geoapify.
- Imágenes de productos y locales en Cloudinary.
- Apertura y cierre programado de locales mediante schedulers.

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot (Web, Data JPA, Security, Validation, Mail) |
| Build | Maven (wrapper incluido: `mvnw` / `mvnw.cmd`) |
| Base de datos | MySQL 8 (H2 disponible como dependencia de runtime) |
| Autenticación | Firebase Admin SDK + JWT (jjwt) |
| Pagos | MercadoPago SDK |
| Geolocalización | Geoapify |
| Imágenes | Cloudinary |
| PDF | OpenPDF |
| Documentación | springdoc-openapi (Swagger UI) |

## Requisitos previos

- JDK 21 en el `PATH`.
- MySQL 8 en ejecución (local o remoto).
- Cuenta de MercadoPago con access token de pruebas.
- API key de Geoapify.
- Credenciales de Cloudinary.
- Archivo de credenciales de servicio de Firebase.

No es necesario instalar Maven: el repositorio incluye el wrapper.

## Configuración

La configuración sensible queda fuera del control de versiones. El repositorio incluye la plantilla `src/main/resources/application.properties.example`.

1. Copiar la plantilla:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. Completar los valores:

   | Propiedad | Descripción |
   |---|---|
   | `spring.datasource.*` | Conexión a MySQL. La URL incluye `createDatabaseIfNotExist=true`, la base se crea sola. |
   | `mercadopago.access.token` | Token del panel de desarrolladores de MercadoPago. |
   | `mercadopago.webhook.url` | URL pública del webhook. En local, un túnel (ngrok); vacía si no hay túnel. |
   | `geoapify.api.key` | Clave del panel de Geoapify. |
   | `cloudinary.*` | Credenciales del dashboard de Cloudinary. |
   | `firebase.web.*` | Project ID y API key del proyecto Firebase (los mismos que usa el front). |
   | `jwt.secret` | Cadena larga y aleatoria para firmar los tokens. |
   | `admin.email` / `admin.password` | Administrador por defecto, se crea al arrancar si no existe. |
   | `app.cors.*` | Orígenes y métodos permitidos para el frontend. |
   | `spring.mail.*` / `mail.from` | Cuenta SMTP para el envío de correos (Gmail con App Password). |
   | `app.timezone` | Zona horaria del negocio, usada por los schedulers de cierre de locales. |

3. Colocar las credenciales de Firebase en `src/main/resources/firebase-service-account.json`.

`application.properties` y `firebase-service-account.json` contienen información sensible y no deben commitearse (ya están en `.gitignore`).

## Compilación y ejecución

Desde la raíz del repositorio:

```bash
# compilar y empaquetar
./mvnw clean package

# ejecutar en modo desarrollo
./mvnw spring-boot:run

# o ejecutar el artefacto generado
java -jar target/trego-0.0.1-SNAPSHOT.jar
```

En Windows usar `mvnw.cmd` en lugar de `./mvnw`.

La aplicación queda disponible en `http://localhost:8080`.

Para ejecutar los tests:

```bash
./mvnw test
```

## Documentación de la API

Con la aplicación en ejecución:

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Especificación OpenAPI (JSON) | `http://localhost:8080/v3/api-docs` |

## Autenticación

La API es stateless. El flujo general:

1. El cliente se autentica contra Firebase (Google o SMS) y envía el ID token a `POST /api/auth/google` o `POST /api/auth/sms`. Los administradores usan `POST /api/auth/login/admin` con email y contraseña.
2. El backend valida el token, resuelve o crea el usuario y devuelve un JWT propio.
3. Cada petición posterior envía ese JWT en el encabezado:

   ```
   Authorization: Bearer <token>
   ```

Un mismo usuario puede vincular sus proveedores de Firebase (SMS y Google) bajo la misma cuenta mediante `POST /api/auth/vincular`. El cierre de sesión (`POST /api/auth/cerrarSesion`) invalida el token en una blacklist.

Son públicas las rutas de autenticación, el registro de restaurantes, el menú público de cada local, la geolocalización, el webhook de MercadoPago y la documentación de Swagger. El resto de los endpoints requiere token.

## Estructura del proyecto

Código fuente bajo el paquete `com.backend.trego`:

```
src/main/java/com/backend/trego
├── config       Seguridad, JWT, Firebase, MercadoPago, Cloudinary, Swagger,
│                manejo global de excepciones
├── controller   Controladores REST (auth, usuarios, clientes, restaurantes,
│                productos, subcategorías, carrito, pedidos, pagos, reclamos, geo)
├── service      Lógica de negocio e integraciones (MercadoPago, Geoapify,
│                Cloudinary, notificaciones, PDF, schedulers)
├── entity       Entidades JPA, DTOs y enumeraciones del dominio
└── repository   Repositorios Spring Data JPA
```

## Despliegue

El despliegue de referencia usa el free tier de AWS: la base en RDS MySQL, el backend como contenedor Docker en una EC2 (imagen publicada en GHCR por GitHub Actions) y el frontend en Vercel, que hace de proxy HTTPS hacia el backend.

La guía completa, junto con el `docker-compose.yml` y el workflow de CI/CD, está en [`deploy/DEPLOY.md`](deploy/DEPLOY.md).

## Repositorios relacionados

- Trego-Web — frontend web (React/Vite).
- Trego-android — aplicación móvil.

## Notas

Las credenciales de la plantilla corresponden a entornos de prueba. Antes de una entrega o de exponer un demo público, rotar las credenciales reales y revisar que no queden trazas de logging de depuración activas.
