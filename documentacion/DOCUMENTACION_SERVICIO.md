# Documentación del Servicio Web REST — LAB9 (práctica)

**Arquitectura:** Spring Boot REST 8080 (servidor con BD MySQL) + Spring Boot MVC/Thymeleaf 8081 (cliente HTTP).

**Base de datos:** `gtics_lab9` con `equipos (id, nombre, area)`, `apis (id, nombre, version, fecha_registro, estado, equipo_id)` y `endpoints (id, ruta, metodo_http, descripcion, api_id)`; además `users` y `authorities` por Spring Security.

**Autorización:** el recurso `/login` admite credenciales Basic sobre HTTP de **práctica local**. Devuelve JWT. Todo `/api/**` exige `Authorization: Bearer TOKEN_VALIDO`. Recomendación real: HTTPS siempre; JWT_SECRET privado, usuarios no demostrativos.

## Endpoints

| Operación | Método | URL | Entrada | Salida típica | Rol |
|---|---|---|---|---|---|
| Autenticarse | GET | `/login` | Basic username/password | 200 + token JWT | Usuario habilitado |
| Listar equipos | GET | `/api/equipo` | Bearer JWT | 200 + array JSON | USER/ADMIN |
| Equipo por ID | GET | `/api/equipo/{id}` | Bearer JWT | 200 + equipo / 404 | USER/ADMIN |
| Listar endpoints | GET | `/api/endpoint` | Bearer JWT | 200 + array JSON | USER/ADMIN |
| Endpoint por ID | GET | `/api/endpoint/{id}` | Bearer JWT | 200 + endpoint / 404 | USER/ADMIN |
| Listar APIs | GET | `/api/api` | Bearer JWT | 200 + array JSON | USER/ADMIN |
| API por ID | GET | `/api/api/{id}` | Bearer JWT | 200 + API / 404 | USER/ADMIN |
| Registrar API | POST | `/api/api` | Bearer JWT + JSON | 201 + API nueva | ADMIN |
| Editar API | PUT | `/api/api/{id}` | Bearer JWT + JSON | 200 + API editada | ADMIN |
| Eliminar API | DELETE | `/api/api/{id}` | Bearer JWT | 204 sin cuerpo | ADMIN |

## Formato JSON de la API

Solicitud de POST/PUT:

```json
{
  "nombre": "API de Pagos",
  "version": "1.1",
  "fecha_registro": "2026-11-05",
  "estado": "activa",
  "equipo_id": 1
}
```

Respuesta típica de POST/GET:

```json
{
  "id": 3,
  "nombre": "API de Pagos",
  "version": "1.1",
  "fecha_registro": "2026-11-05",
  "estado": "activa",
  "equipo": { "id": 1, "nombre": "Plataforma", "area": "Backend" }
}
```

La respuesta anida el objeto Equipo porque la entidad JPA usa `@ManyToOne`. `fecha_registro` cumple snake_case. El `id` es ilustrativo: el valor real lo asigna MySQL.

Respuesta de login:

```json
{
  "token": "JWT_FIRMADO_GENERADO_POR_EL_SERVIDOR",
  "tipo": "Bearer",
  "rol": "ADMIN",
  "expira_en_segundos": 3600
}
```

## Códigos de error

| HTTP | `error` | Caso |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Falta un dato obligatorio del JSON |
| 401 | `TOKEN_INVALID` | Credenciales incorrectas, Bearer ausente, alterado o mal formado |
| 401 | `TOKEN_EXPIRED` | El JWT está vencido |
| 403 | `ACCESS_DENIED` | Cuenta autenticada sin permiso para el método HTTP |
| 404 | `NOT_FOUND` | ID de equipo, API o endpoint inexistente |
| 409 | `DATA_CONFLICT` | Violación de restricciones SQL |

Ejemplo:

```json
{ "error": "TOKEN_EXPIRED", "mensaje": "El token JWT ya expiró. Inicia sesión otra vez." }
```

El manejador es `ManejadorGlobal.java` con `@RestControllerAdvice`. Los errores de autenticación que ocurren *en los filtros* se delegan desde la configuración de seguridad para que el manejador los procese correctamente.

## Pruebas de aceptación sugeridas

1. Abrir `http://localhost:8081/login` y autenticar como USER.
2. Ver listado y detalle de APIs. Comprobar que no hay botones crear/editar/eliminar.
3. Con ese JWT hacer POST al servidor: debe responder 403.
4. Salir e iniciar como ADMIN. Registrar una API desde el formulario.
5. Verificar con GET `/api/api` y en MySQL que quedó guardada.
6. Editar la API y verificar la nueva versión.
7. Eliminarla y verificar que desapareció.
8. Hacer GET sin JWT: 401 JSON estructurado.
9. Hacer GET con token inventado: 401 JSON estructurado.
10. Probar fecha_registro, equipo_id y metodo_http en JSON (snake_case).

> Estos son **resultados esperados del diseño**, no actas de prueba ya ejecutadas. Debes verificarlos localmente antes de afirmar que funcionan.
