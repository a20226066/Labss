package pe.edu.gtics.servidor.security;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pe.edu.gtics.servidor.exception.AccesoDenegadoException;
import pe.edu.gtics.servidor.exception.TokenInvalidoException;

// Configuración similar a la clase 6.1: SecurityFilterChain, JdbcUserDetailsManager, BCrypt, roles.
@Configuration
public class WebSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService usuarios(DataSource dataSource) {
        // TABLAS users y authorities: son los nombres estándar de Spring Security.
        // La contraseña de la tabla 'users' está en BCrypt (no texto plano).
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSegura(HttpSecurity http, JwtService jwtService,
                                         @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        http.securityMatcher("/api/**")
            // El servicio REST no usa sesión ni formularios HTML.
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(reglas -> reglas
                // RBAC: ambos roles pueden consultar (GET).
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                // Crear, editar y eliminar SOLO para ADMIN.
                .requestMatchers(HttpMethod.POST, "/api/api", "/api/api/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/api", "/api/api/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/api", "/api/api/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().denyAll())
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint((req, resp, ex) ->
                    resolver.resolveException(req, resp, null,
                        new TokenInvalidoException("Falta un token Bearer válido para entrar a /api/.")))
                .accessDeniedHandler((req, resp, ex) ->
                    resolver.resolveException(req, resp, null, new AccesoDenegadoException())))
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, resolver),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain loginBasico(HttpSecurity http,
                                           @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        // El cliente llama a GET /login enviando Authorization: Basic usuario:clave.
        // Basic sirve SOLO para recibir el JWT; después se usa Bearer en /api/.
        http.securityMatcher("/login")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(reglas -> reglas.anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint((req, resp, ex) ->
                resolver.resolveException(req, resp, null,
                    new TokenInvalidoException("Usuario o contraseña incorrectos."))));
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain restantes(HttpSecurity http) throws Exception {
        // Evita exponer sin querer otras rutas que agreguemos por accidente.
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(reglas -> reglas.anyRequest().denyAll())
                .build();
    }
}
