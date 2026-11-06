# Guía de código: ¿qué viene de las clases de GTICS?

Esta guía sigue los conceptos de las diapositivas que compartiste. **Cada archivo Java incluye comentarios en español**. Si una parte NO aparece en tus diapositivas, se indica expresamente.

| Tema de tus clases | En qué archivo lo verás | Para qué se usa en el laboratorio |
|---|---|---|
| **Clase 01 — Introducción y controladores** | `ServidorApplication`, `ClienteApplication`, `ApiWebController` | Inicio Spring Boot, `@Controller`, `@GetMapping`, `@PostMapping`, `@PathVariable`, objeto `Model` |
| **Clase 1.2 — Thymeleaf & recursos** | `cliente-web/src/main/resources/templates` y `static/css` | `th:text`, `th:each`, `th:if`, `th:href`, `th:object`, `th:field`, `th:value` |
| **Clase 1.3 — Data Binding** | `ApiFormulario`, `ApiWebController` | El formulario HTML se vincula automáticamente a un objeto Java |
| **Clase 1.4 — Spring Data JPA** | `Equipo`, `ApiCatalogo`, `EndpointCatalogo`, repositorios | `@Entity`, `@Table`, `@Id`, `@Column`, `JpaRepository`, `findAll`, `findById`, `save`, `deleteById` |
| **Clase 3.1 — Relaciones entre tablas** | `ApiCatalogo`, `EndpointCatalogo` | `@ManyToOne`, `@JoinColumn` para `apis.equipo_id` y `endpoints.api_id` |
| **Clase 3.2 — Redirect Attributes** | `LoginController`, `ApiWebController` | Mensajes flash con `addFlashAttribute` tras guardar/eliminar |
| **Clase 5.1 — Model Attribute** | `ApiFormulario`, `apis/formulario.html` | Reutilizar vista crear/editar con `@ModelAttribute`, `th:object`, `th:field` |
| **Clase 5.2 — Validación** | `ApiSolicitud`, `ApiFormulario`, `ManejadorGlobal` | `@Valid`, `BindingResult`, `@NotBlank`, `@NotNull`, `th:errors` |
| **Clase 6.1 — Spring Security** | `WebSecurityConfig` | `SecurityFilterChain`, BCrypt, `JdbcUserDetailsManager`, autenticación vs. roles |
| **Clase 6.2 — Spring Session** | `LoginController`, `ApiWebController` | `HttpSession` (idea de sesión; sin almacenamiento JDBC extra) |
| **Guía extra — Fechas/horas JPA** | `ApiCatalogo`, `ApiFormulario` | `LocalDate`, formato `yyyy-MM-dd` para fechas |

## Temas nuevos que el PDF del LAB9 exige pero tus clases NO desarrollan completos

**1. `@RestController` y `@RequestBody`:** Spring MVC del curso se usa en formato REST. Un controlador tradicional devuelve un nombre de plantilla, uno REST devuelve objetos convertidos automáticamente a JSON. `@RequestBody` recibe un JSON y lo convierte a Java.

**2. `RestClient`:** para que una segunda app Java consuma el REST del servidor es imprescindible usar alguna clase cliente HTTP. Aquí se eligió la API sencilla `RestClient` de Spring; todo su código está concentrado en **un solo archivo** `CatalogoApiCliente.java`.

**3. JWT y Bearer:** el requisito de token JWT no está desarrollado en la clase 6.1 de Spring Security, que cubre Basic/Form Login, BCrypt y roles. Se añadió la dependencia `jjwt` para firmar y validar tokens. Se concentra en `JwtService.java` y `JwtAuthenticationFilter.java`.

**4. `@RestControllerAdvice`:** es la variante para JSON de `@ControllerAdvice` solicitada por el ejercicio 4. El filtro de seguridad se ejecuta antes de los controladores, así que se utiliza `HandlerExceptionResolver` para que el error del JWT llegue al manejador global.

**5. `SNAKE_CASE`:** Jackson es el conversor automático de JSON de Spring Boot. La propiedad global:

```properties
spring.jackson.property-naming-strategy=SNAKE_CASE
```

hace que `metodoHttp` se entregue como `metodo_http` y `fechaRegistro` como `fecha_registro`. También se configura en el cliente para convertir JSON de vuelta a Java.

## Ruta para entender el código sin memorizar

### Paso A — Base de datos y repositorio

Abre primero `sql/01_crear_bd.sql`. Verás que la tabla `apis` tiene FK `equipo_id`. Luego abre `ApiCatalogo.java` y observa `@ManyToOne` + `@JoinColumn(name="equipo_id")`. Después abre `ApiRepository.java`: no tendrás que escribir `SELECT * FROM apis`, porque `JpaRepository` ya tiene `findAll()`.

### Paso B — Petición GET

Una llamada `GET /api/api` llega a `ApiRestController.listar()` → `ApiService.listar()` → `apiRepository.findAll()` → MySQL. El resultado regresa como Java → JSON (`fecha_registro` en snake_case).

### Paso C — Petición POST

`POST /api/api` llega con JSON → `@RequestBody ApiSolicitud` → `@Valid` revisa los campos → `ApiService.crear()` obtiene el Equipo por `equipoId` → `apiRepository.save()` inserta la nueva API.

### Paso D — Inicio de sesión

`login.html` → `LoginController` (cliente) → `CatalogoApiCliente.iniciarSesion()` → Basic Auth → `JdbcUserDetailsManager` busca usuario y rol en MySQL → `LoginRestController` crea JWT → se guarda en `HttpSession` del cliente.

### Paso E — Petición protegida

`RestClient` envía Bearer → `JwtAuthenticationFilter` valida firma/expiración → `SecurityFilterChain` comprueba el rol → `ApiRestController` atiende la petición → `RestClient` recibe JSON → Thymeleaf lo muestra.

### Paso F — Errores

Token mal formado → `JwtAuthenticationFilter` detecta fallo → `HandlerExceptionResolver` lleva el error a `ManejadorGlobal` → responde JSON y HTTP 401. USER intenta POST → `SecurityFilterChain` rechaza y devuelve HTTP 403 usando el mismo manejador.

## Ejercicios para comprobar que lo comprendiste

1. En `ApiRestController`, explica por qué `@RestController` no retorna `"apis/lista"`.
2. En `ApiService`, reemplaza `save` por un `System.out.println` y explica qué dejaría de persistirse.
3. En `WebSecurityConfig`, cambia el rol del POST a `ROLE_USER`: ¿qué problema de autorización generarías?
4. En el cliente, ¿qué sucede si cambias `server.port=8081` por `8080` mientras el servidor sigue funcionando?
5. Cambia la clave secreta JWT en el servidor **después** de iniciar sesión; ¿por qué el token anterior deja de ser válido?
6. En el cliente, prueba `usuario` y luego `admin` y compara el listado y los botones.
