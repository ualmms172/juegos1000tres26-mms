# Guia rapida: ejecutar con Docker Compose

## 0) Configurar JWT_SECRET (solo desarrolladores)

1) Copia el archivo de ejemplo y crea tu archivo local:

```powershell
Copy-Item juegos1000tres-backend\.env.example juegos1000tres-backend\.env
```
o simplemente haz un .env similar a .env.example

2) Abre `juegos1000tres-backend\.env` y define un valor seguro:

```
JWT_SECRET=tu_clave_larga_y_aleatoria_de_32_caracteres_o_mas
```

## 1) Iniciar todo (PostgreSQL + backend)
Desde la raiz del proyecto, ejecuta:

```powershell
cd C:\Users\pepep\juegos1000tres26
docker compose up --build -d
```

## 2) Verificar que todo esta arriba

```powershell
docker compose ps
```

Debes ver:
- `juegos1000tres-postgres` en estado `healthy`
- `juegos1000tres-backend-app` en estado `running`

## 3) URLs de la API
- API base: `http://localhost:8083/api`
- Usuarios: `http://localhost:8083/api/usuarios`

## 4) Registro de usuario (crear cuenta)

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8083/api/usuarios `
  -ContentType "application/json" `
  -Body '{"nombre":"pepe","email":"pepe@example.com","password":"123456"}'
```

## 5) Login (validar email y password)

```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/usuarios/search/login?email=pepe@example.com&password=123456" | ConvertTo-Json -Depth 10
```

Si existe, devuelve el usuario. Si no existe, devolvera vacio.

## 6) Comprobar si un email ya existe

```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/usuarios/search/existe-email?email=pepe@example.com"
```

## 7) Detener todo

```powershell
docker compose down
```

## 8) Detener y borrar tambien la base de datos

```powershell
docker compose down -v
```
