# 🎮 Juegos1000tres

Plataforma de minijuegos online donde los jugadores se conectan desde navegador, crean salas y participan en partidas. En algunos juegos, un dispositivo puede actuar como “pantalla” para mostrar el estado de la partida.

## 🧩 Estado actual del proyecto
- **Frontend web** en Angular con autenticación, lobby y catálogo de juegos (incluye minijuegos como **TapTap** y **Space Invaders**).
- **Backend** en Spring Boot con API REST, autenticación JWT, persistencia en PostgreSQL y soporte de comunicación en tiempo real.
- **Infraestructura local** con Docker Compose para levantar backend + base de datos.

## 🛠️ Tecnologías en uso
**Frontend**
- Angular 21 + TypeScript
- RxJS
- Angular CLI
- Vitest (tests)

**Backend**
- Java 21
- Spring Boot 4 (Web MVC, Data JPA, Data REST, Security)
- JWT (jjwt)
- WebSocket (Java-WebSocket)
- Maven

**Datos e infraestructura**
- PostgreSQL
- Docker Compose

## 🗂️ Estructura del repositorio
- `juegos1000tres-backend/` → API y lógica de servidor.
- `juegos1000tres-frontend/` → Aplicación web Angular.
- `docker-compose.yml` → Arranque de backend + PostgreSQL.
- `GUIA_DOCKER_COMPOSE.md` → Guía rápida de arranque con Docker.
- `ComoProbar.txt` → Notas de prueba.

## 🚀 Puesta en marcha rápida
**Backend + base de datos (Docker Compose)**
- Ver `GUIA_DOCKER_COMPOSE.md`.

**Frontend**
```bash
cd juegos1000tres-frontend
npm install
npm start
```

