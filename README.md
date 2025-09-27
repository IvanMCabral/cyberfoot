# Cyberfoot Monorepo

Proyecto full-stack inspirado en Cyberfoot (manager de fútbol).

## Estructura

- `/backend`: Spring Boot WebFlux + R2DBC + Flyway
- `/frontend`: Angular 17+ + NgRx + Material
- `/ops`: Docker Compose, k8s opcional


## Comandos de arranque

1. Levantar base de datos y backend:

```sh
docker compose -f ops/docker-compose.yml up -d db
mvn -f backend/pom.xml spring-boot:run
```

2. Levantar frontend Angular:

```sh
npm start
```

## Flujo de simulación
- Presiona **Play** en el frontend para iniciar la simulación.
- El backend emite eventos minuto a minuto por SSE.
- Al finalizar, el fixture queda en estado `FT` y puedes consultar el resultado final por API.

## Notas
- El frontend consume `/api` vía proxy, evitando problemas de CORS.
- El motor de partido usa seed fija para pruebas.
- Arquitectura hexagonal: dominio, puertos, adapters.

## Semilla demo

- 3 ligas, 20 clubes por liga, 25 jugadores por club.
- Un fixture demo reproducible.

## Roadmap

- MVP jugable offline (M1)
- Beta gestionable (M2)
- v1.0 completo (M3)

## Reset DB + reseed

```sh
# En PowerShell
# (pendiente script)
```

---

// TODO[M1]: Implementar entidades, servicios, endpoints y seed inicial.
// TODO[M2]: Seguridad JWT, transferencias, entrenamiento.
// TODO[M3]: SaveGames múltiples, finanzas, CI/CD.
