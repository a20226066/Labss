# GTICS — Laboratorio 9, guía de práctica con comentarios

**Tema:** Servicios Web RESTful seguros con Spring Boot, Spring Security, MySQL, JWT y cliente Thymeleaf.

> **Uso educativo:** el enunciado original establece que está prohibido generar soluciones o fragmentos significativos con IA sin comprenderlos profundamente. Este material se entrega como **ejemplo de práctica para estudiar y experimentar**. Léelo, ejecuta pruebas, cambia las partes que entiendas y no lo presentes tal cual como trabajo individual.

## 1. Estructura del ZIP

```text
GTICS_LAB9_PRACTICA_COMENTADA/
├── README.md                       ← Comienza aquí
├── servidor-api-seguro/            ← PRIMER proyecto Maven, puerto 8080
│   ├── pom.xml
│   └── src/main/java/pe/edu/gtics/servidor/
│       ├── controller/             ← @RestController, GET/POST/PUT/DELETE, @ControllerAdvice
│       ├── dto/                    ← Cuerpos JSON de entrada/salida
│       ├── entity/                 ← @Entity, @ManyToOne, @JoinColumn
│       ├── exception/              ← Errores personalizados
│       ├── repository/             ← JpaRepository
│       ├── security/               ← SecurityFilterChain, BCrypt, JWT
│       └── service/                ← CRUD de APIs con JPA
├── cliente-web/                    ← SEGUNDO proyecto Maven, puerto 8081
│   ├── pom.xml
│   └── src/main/
│       ├── java/pe/edu/gtics/cliente/
│       │   ├── controller/         ← @Controller, Model, RedirectAttributes
│       │   ├── dto/                ← Objetos recibidos de JSON y del formulario
│       │   └── service/            ← RestClient consume el servidor
│       └── resources/templates/    ← Thymeleaf: login, lista, detalle, formulario
├── sql/01_crear_bd.sql             ← Tablas y datos iniciales MySQL
├── documentacion/                  ← Explicación por pasos y comparación con clases
└── ejemplos/                       ← Ejemplos JSON y archivo .http para probar
```

## 2. Requisitos locales

- Java **17 o superior** y **IntelliJ IDEA** con soporte Maven.
- **MySQL Server 8.x** + Workbench (o un cliente SQL compatible).
- Conexión a Internet **la primera vez que Maven descarga dependencias**.
- No necesitas Docker, AWS, JavaScript complejo, React, Node ni otra base de datos.
- El proyecto utiliza Spring Boot **3.5.16**, por ser compatible con Java 17 y mantener APIs cercanas a las de las clases. La clase introductoria menciona Spring Boot 4, pero el ejemplo usa un conjunto coherente de dependencias 3.5.x. No mezcles versiones en `pom.xml`.

## 3. Crear la base de datos

1. Inicia MySQL Server.
2. Abre **MySQL Workbench**.
3. Ejecuta todo `sql/01_crear_bd.sql`.
4. Comprueba que existe la base **`gtics_lab9`** con las tablas `equipos`, `apis`, `endpoints`, `users`, `authorities`.
5. Se incluyen **dos cuentas de prueba** (solo uso local):

   | Usuario | Contraseña | Rol | Permisos REST |
   |---|---|---|---|
   | `usuario` | `Usuario123!` | `ROLE_USER` | GET (consulta) |
   | `admin` | `Admin123!` | `ROLE_ADMIN` | GET, POST, PUT, DELETE |

Las contraseñas se guardan mediante hashes BCrypt, como en la clase de Spring Security. **No uses estas cuentas ni los hashes de demostración en una aplicación real.**

> El laboratorio indica que se adjuntaría un script SQL, pero no está entre los archivos recibidos. El incluido aquí es una interpretación didáctica del esquema dibujado en el PDF (más tablas de seguridad), no el script original del docente.

## 4. Abrir el SERVIDOR en IntelliJ

1. **File → Open** → selecciona **`servidor-api-seguro`** (carpeta que contiene `pom.xml`).
2. Espera a que Maven descargue dependencias.
3. Abre `src/main/resources/application.properties`.
4. Configura el usuario y contraseña de MySQL. Puedes establecer las variables de entorno `DB_USER` y `DB_PASSWORD` o sustituir los valores de prueba del archivo local.
5. Para aprender en tu PC, puedes usar la clave JWT marcada como **solo demostración**. Si quieres cambiarla, establece `JWT_SECRET` con una cadena aleatoria de al menos 32 bytes y no la subas a GitHub.
6. Ejecuta **`ServidorApplication.java`**.
7. El servidor escucha en **`http://localhost:8080`** (no muestra páginas HTML; entrega JSON).

El servidor requiere MySQL; si aparece `Access denied for user`, verifica `spring.datasource.username` y `spring.datasource.password`. Si aparece `Table ... doesn't exist`, ejecuta el script SQL.

## 5. Abrir el CLIENTE en IntelliJ

1. Abre **un segundo proyecto o ventana** en IntelliJ: carpeta **`cliente-web`**.
2. Espera las dependencias Maven.
3. Verifica que `servidor.api.base-url=http://localhost:8080` en `cliente-web/src/main/resources/application.properties`.
4. Ejecuta **`ClienteApplication.java`**. Debe estar funcionando el servidor en paralelo.
5. En el navegador ingresa **`http://localhost:8081/login`**.
6. Ingresa con `usuario` para probar solo lectura y con `admin` para crear/editar/eliminar.

**No confundas los puertos:** `8080` es la API REST, `8081` es la página web que consulta esa API.

## 6. Flujo completo de autenticación (MUY IMPORTANTE)

1. En `login.html` escribes usuario/contraseña y presionas **Iniciar sesión**.
2. `LoginController` del **cliente** llama a `CatalogoApiCliente.iniciarSesion()`.
3. El cliente hace `GET http://localhost:8080/login` con cabecera `Authorization: Basic ...`.
4. **Spring Security del servidor** consulta `users`/`authorities` de MySQL y compara la contraseña con BCrypt.
5. Si son correctas, `LoginRestController` crea un **JWT firmado** con el usuario, rol y fecha de expiración.
6. El cliente guarda **token y rol** en `HttpSession` (no guarda la contraseña).
7. Cada petición del cliente a `/api/...` lleva `Authorization: Bearer <token>`.
8. `JwtAuthenticationFilter` verifica la firma y la fecha de expiración del token.
9. `SecurityFilterChain` permite GET al USER/ADMIN y cambios solo a ADMIN.
10. La respuesta vuelve como JSON, el cliente la transforma a objetos y Thymeleaf la muestra.

El PDF contiene **dos requisitos de autenticación que parecen solaparse**: Basic Auth y JWT en `/api/`. Para que ambos sean demostrables sin quitar JWT a `/api/`, este ejemplo usa **Basic exclusivamente para obtener el token en `/login`** y **Bearer JWT para el CRUD REST**. Es una decisión pedagógica explicada; si el profesor espera otro diseño para la convivencia de ambos, ajústalo según sus indicaciones.

## 7. Qué se ve en cada URL

| Método y URL del servidor (8080) | Rol | Resultado |
|---|---|---|
| `GET /login` | Basic Auth válido | JSON con `token`, `tipo`, `rol`, `expira_en_segundos` |
| `GET /api/equipo` | USER / ADMIN con Bearer | Lista de equipos JSON |
| `GET /api/equipo/{id}` | USER / ADMIN con Bearer | Equipo por ID JSON |
| `GET /api/endpoint` | USER / ADMIN con Bearer | Lista endpoints JSON |
| `GET /api/endpoint/{id}` | USER / ADMIN con Bearer | Endpoint por ID JSON |
| `GET /api/api` | USER / ADMIN con Bearer | Lista APIs JSON |
| `GET /api/api/{id}` | USER / ADMIN con Bearer | API por ID JSON |
| `POST /api/api` | ADMIN con Bearer | Registrar API JSON |
| `PUT /api/api/{id}` | ADMIN con Bearer | Editar API JSON |
| `DELETE /api/api/{id}` | ADMIN con Bearer | Borrar API (HTTP 204) |

### Ejemplo realista de JSON de creación

El ejemplo está en `ejemplos/solicitud_crear_api.json`:

```json
{
  "nombre": "API de Reportes",
  "version": "1.0",
  "fecha_registro": "2026-11-05",
  "estado": "activa",
  "equipo_id": 1
}
```

Es intencional que se escriba `fecha_registro` / `equipo_id` en JSON y `fechaRegistro` / `equipoId` en las clases Java. `spring.jackson.property-naming-strategy=SNAKE_CASE` convierte los nombres, como solicita el PDF.

## 8. Probar seguridad y roles sin cliente web

En **PowerShell o terminal** (el símbolo `$` en este ejemplo es una variable, no lo escribas en la URL):

```bash
# Primero: obtener un JWT como administrador (se imprime un JSON).
curl -u 'admin:Admin123!' http://localhost:8080/login

# Copia SOLO el contenido del campo "token" y colócalo aquí:
TOKEN='PEGA_AQUI_TU_JWT'

# Consultar APIs (GET).
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/api

# Crear API (POST) — desde la carpeta raíz de este ZIP.
curl -X POST http://localhost:8080/api/api \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  --data-binary @ejemplos/solicitud_crear_api.json
```

En **Windows PowerShell**, `curl` puede ser un alias distinto; puedes usar el **HTTP Client de IntelliJ** con `ejemplos/pruebas_rest.http` o probar directamente desde la interfaz web.

### Pruebas de error que debes comprender

- GET `/api/api` sin `Authorization`: **HTTP 401**, error JSON `TOKEN_INVALID`.
- GET `/api/api` con token inventado: **HTTP 401**, `TOKEN_INVALID`.
- GET `/api/api` con JWT expirado: **HTTP 401**, `TOKEN_EXPIRED`.
- POST `/api/api` con token de `usuario`: **HTTP 403**, `ACCESS_DENIED`.
- GET `/api/api/999999` con JWT válido: **HTTP 404**, `NOT_FOUND`.
- POST sin campo `nombre`: **HTTP 400**, `VALIDATION_ERROR`.

Un ejemplo de respuesta:

```json
{ "error": "ACCESS_DENIED", "mensaje": "Tu usuario no tiene permisos para esta operación." }
```

## 9. Las clases que debes estudiar primero

Recomendación de orden: `Equipo.java` → `ApiCatalogo.java` → `ApiRepository.java` → `ApiRestController.java` → `ApiService.java` → `WebSecurityConfig.java` → `JwtService.java` → `JwtAuthenticationFilter.java` → `ManejadorGlobal.java` → `CatalogoApiCliente.java` → `ApiWebController.java` → vistas Thymeleaf.

Consulta `documentacion/GUIA_POR_CLASES.md` para ver exactamente qué diapositiva o tema respalda cada clase. También encontrarás `documentacion/DOCUMENTACION_SERVICIO.md` para describir el servicio.

## 10. Limitaciones y decisiones didácticas

- **No se compiló aquí con Maven ni se probó contra un MySQL real.** El ZIP contiene fuentes y configuración para abrirlos, compilar y ejecutarlos en IntelliJ con dependencias descargadas.
- El JWT se firma con una clave local de demostración definida en `application.properties`; **cámbiala para cualquier otro entorno**.
- El cliente conserva JWT en `HttpSession` local. El PDF pide un cliente web; no se añadió Spring Session JDBC porque no es necesario para esta prueba.
- No se usa Docker, AWS ni despliegue, porque el ejercicio no los exige.
- El frontend se limita al CRUD de **APIs**; equipos y endpoints son endpoints REST de consulta, como pide el laboratorio.
- El esquema inicial es para MySQL local y se puede adaptar al script que facilite el profesor.
- Este proyecto NO equivale al repositorio GitHub solicitado por el PDF; se te entrega un ZIP de trabajo local.
