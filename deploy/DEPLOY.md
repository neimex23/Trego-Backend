# Despliegue de Trego (free tier) — RDS + EC2 + Vercel

Arquitectura:

- Frontend (Trego-Web): Vercel.
- Backend (Trego-Backend): contenedor Docker en una instancia EC2 t3.micro, expuesto por HTTP en el DNS publico de AWS.
- Base de datos: RDS MySQL db.t3.micro (free tier).

El frontend hace fetch a rutas relativas (`/api/...`). En Vercel esas rutas se reenvian al backend mediante los rewrites de `vercel.json`. Desde el navegador todo es mismo origen (HTTPS de Vercel), por lo que no hay problemas de CORS ni de mixed content. El tramo Vercel -> EC2 va por HTTP del lado servidor.

Sobre HTTPS y el dominio de AWS: Let's Encrypt no emite certificados para el DNS publico del EC2 (`*.amazonaws.com`), asi que no se usa Caddy. El HTTPS publico lo da Vercel. El webhook de MercadoPago (que exige HTTPS) tambien se resuelve por Vercel: se apunta al dominio de Vercel y este reenvia el POST al backend HTTP (ver seccion 7).

Advertencia de free tier: EC2 y RDS son gratis por 12 meses, no de forma permanente. Vercel es gratis sin limite de tiempo. La t3.micro tiene 1 GB de RAM; el heap de la JVM esta limitado a 512 MB en el Dockerfile.

---

## 1. Base de datos: RDS MySQL

1. Consola AWS > RDS > Create database > Standard create > MySQL.
2. Template: Free tier. Instancia: db.t3.micro. Storage: 20 GB gp2.
3. Identificador, usuario maestro (ej. `admin`) y contrasena. Anotar la contrasena.
4. Connectivity: Public access = Yes (para el demo). VPC security group: crear o elegir uno.
5. Crear la base. Esperar a que quede "Available" y copiar el Endpoint.
6. En el security group del RDS, agregar una regla inbound: tipo MySQL/Aurora (3306), origen = el security group (o la IP) de la EC2.

No hace falta crear la base `tregodb` a mano: la URL incluye `createDatabaseIfNotExist=true` y Hibernate crea las tablas (`ddl-auto=update`).

## 2. Servidor: EC2

1. EC2 > Launch instance. AMI: Ubuntu Server 22.04 LTS. Tipo: t3.micro (free tier).
2. Crear un key pair y descargarlo. Security group inbound: 22 (SSH, tu IP) y 80 (0.0.0.0/0).
3. Asignar una Elastic IP a la instancia (asi la IP no cambia al reiniciar).
4. Anotar el DNS publico de la instancia (ej. `ec2-1-2-3-4.compute-1.amazonaws.com`). Es el que se usa en `vercel.json`.
5. Conectar por SSH e instalar Docker:

   ```
   sudo apt update && sudo apt install -y docker.io docker-compose-plugin git
   sudo usermod -aG docker $USER && newgrp docker
   ```

## 3. Dominio

No se usa dominio externo ni Caddy. El backend queda accesible por HTTP en el DNS publico de AWS (puerto 80). El HTTPS de cara al usuario lo provee Vercel mediante los rewrites. Por eso el security group solo necesita abrir el puerto 80 (ademas del 22 para SSH).

## 4. Desplegar el backend

1. Clonar el repo en la EC2 y entrar a `deploy/`:

   ```
   git clone <repo> Trego-Backend && cd Trego-Backend/deploy
   cp .env.example .env
   ```

2. Completar `.env` con: endpoint de RDS en `DB_URL`, credenciales, dominio de Vercel en `FRONT_URL`/`CORS_ALLOWED_ORIGINS`, `BACKEND_IMAGE` y todos los secrets.
3. Copiar el JSON de Firebase a la EC2 como `~/Trego-Backend/deploy/firebase-service-account.json`. Este archivo es secreto: NO esta en git ni en la imagen; el compose lo monta en runtime. Sin el, la app igual arranca pero no envia notificaciones push. Desde tu maquina:

   ```
   scp -i tu-clave.pem src/main/resources/firebase-service-account.json ubuntu@<EC2_HOST>:~/Trego-Backend/deploy/
   ```

4. Bajar la imagen (publicada por GitHub Actions; ver seccion 6) y levantar:

   ```
   docker compose pull
   docker compose up -d
   ```

   Nota: la EC2 no compila, solo baja la imagen de GHCR. El build ocurre en GitHub Actions. Por eso el primer deploy requiere que el workflow ya haya publicado la imagen al menos una vez (o subirla a mano).

4. Verificar:

   ```
   docker compose logs -f backend
   ```

   Probar `http://EC2-PUBLIC-DNS.compute.amazonaws.com/swagger-ui/index.html` en el navegador (reemplazar por tu DNS publico).

## 5. Frontend en Vercel

1. En `Trego-Web/vercel.json`, poner en el `destination` del rewrite `/api` el DNS publico del EC2 por HTTP (ej. `http://ec2-1-2-3-4.compute-1.amazonaws.com/api/:path*`).
2. Vercel > New Project > importar el repo del frontend. Framework: Vite (autodetectado). Build: `npm run build`, output: `dist`.
3. Cargar las variables `VITE_*` del `.env` del front en Settings > Environment Variables (Firebase, Geoapify, etc.).
4. Deploy. La URL final (ej. `https://tregoapp.vercel.app`) debe coincidir con `FRONT_URL`/`CORS_ALLOWED_ORIGINS` del backend y con los `back_urls` de MercadoPago.

## 6. CI/CD: auto-deploy desde GitHub

### Backend (push a master -> EC2)

El workflow `.github/workflows/deploy.yml` se dispara con cada push a `master`. Hace dos cosas:

1. Compila la imagen Docker en GitHub Actions y la publica en GHCR (`ghcr.io/neimex23/trego-backend`). La imagen se construye en Actions, no en la EC2, porque la t3.micro no tiene RAM para un build de Maven.
2. Entra por SSH a la EC2 y ejecuta `docker compose pull && docker compose up -d`, levantando la imagen nueva.

Importante antes del primer push: `application-prod.properties` debe estar commiteado para que entre en la imagen. El `.gitignore` ya tiene la excepcion (`!src/main/resources/application-prod.properties`), asi que un `git add` normal lo incluye. No contiene secrets, solo placeholders `${ENV}`. El `application.properties` de desarrollo y el `firebase-service-account.json` siguen ignorados y NO deben commitearse.

Pasos para dejarlo andando:

1. Preparar la EC2 una sola vez: clonar el repo (solo se usa la carpeta `deploy/`) y crear el `.env` con los secrets, como en las secciones 2 y 4. El `git clone` debe dejar el repo en `~/Trego-Backend` para que el path del workflow coincida.
2. En GitHub > repo Trego-Backend > Settings > Secrets and variables > Actions, crear estos secrets:
   - `EC2_HOST`: la Elastic IP de la EC2.
   - `EC2_USER`: `ubuntu`.
   - `EC2_SSH_KEY`: el contenido de la clave privada (.pem) usada para entrar por SSH.
3. Permitir que la EC2 baje la imagen de GHCR. Dos opciones:
   - Simple (demo): hacer el package publico. GitHub > tu perfil > Packages > trego-backend > Package settings > Change visibility > Public. Asi `docker compose pull` no necesita login.
   - Privada: en la EC2, `echo <PAT> | docker login ghcr.io -u neimex23 --password-stdin`, con un Personal Access Token con scope `read:packages`.
4. Hacer push a `master`. En la pestaña Actions se ve el build y el deploy. Tambien se puede correr a mano con "Run workflow" (workflow_dispatch).

Nota: el repo hoy no tiene rama `master` (la rama principal del backend es `backEnd`). Ajustar el `branches:` del workflow si el deploy debe salir de otra rama.

### Frontend (Vercel, automatico)

Vercel trae integracion con GitHub nativa: no hace falta workflow. Al importar el repo en Vercel se elige la "Production Branch" (ej. `master`). Desde ahi, cada push a esa rama dispara build y deploy solos. Los pushes a otras ramas generan preview deployments con URL propia.

## 7. MercadoPago

- Webhook: exige HTTPS. Se usa el dominio de Vercel: `MP_WEBHOOK_URL=https://<dominio-vercel>/api/pagos/webhook`. El rewrite `/api` de `vercel.json` reenvia el POST de MercadoPago al backend HTTP del EC2 del lado servidor, asi que MercadoPago solo ve HTTPS. No requiere infra extra.
- Requisito: el webhook debe responder rapido (200) para no agotar el timeout del proxy de Vercel. El payload de MercadoPago es chico, sin problema de tamano.
- back_urls: se arman desde `FRONT_URL` (success/failure/pending) y pasan por Vercel (HTTPS), asi que funcionan sin cambios.

---

## Seguridad: rotar credenciales

El `application.properties` de desarrollo contiene credenciales reales. Antes de exponer el demo conviene rotarlas y dejarlas solo como variables de entorno en la EC2:

- MercadoPago: regenerar access token en el panel de developers.
- Cloudinary: regenerar API secret.
- Gmail: revocar la App Password actual y crear una nueva.
- JWT: usar un `JWT_SECRET` nuevo y largo.
- Firebase Admin: el `firebase-service-account.json` viaja dentro de la imagen; si el repo es publico, regenerar la clave de servicio.

El perfil `prod` no usa los valores del `application.properties`: toma todo desde el `.env`. Aun asi, no publicar el `application.properties` con secrets en un repo publico.
