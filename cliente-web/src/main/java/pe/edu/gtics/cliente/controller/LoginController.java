package pe.edu.gtics.cliente.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.gtics.cliente.dto.TokenRespuesta;
import pe.edu.gtics.cliente.service.CatalogoApiCliente;

// @Controller muestra VISTAS HTML, a diferencia de @RestController del servidor.
@Controller
public class LoginController {
    private final CatalogoApiCliente servicio;
    public LoginController(CatalogoApiCliente servicio) { this.servicio = servicio; }

    @GetMapping("/")
    public String inicio() { return "redirect:/apis"; }

    @GetMapping("/login")
    public String formulario() { return "login"; }

    @PostMapping("/login")
    public String entrar(@RequestParam String usuario, @RequestParam String contrasena,
                         HttpSession sesion, RedirectAttributes redirectAttributes) {
        try {
            TokenRespuesta respuesta = servicio.iniciarSesion(usuario, contrasena);
            if (respuesta == null || respuesta.getToken() == null) {
                redirectAttributes.addFlashAttribute("error", "El servidor no devolvió un token.");
                return "redirect:/login";
            }
            // Spring Session (clase 6.2): HttpSession mantiene los datos del navegador.
            // Guardamos TOKEN y ROL; nunca guardamos la contraseña.
            sesion.setAttribute("token", respuesta.getToken());
            sesion.setAttribute("rol", respuesta.getRol());
            sesion.setAttribute("usuario", usuario);
            return "redirect:/apis";
        } catch (RestClientResponseException ex) {
            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas (HTTP " + ex.getStatusCode().value() + ").");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String salir(HttpSession sesion) {
        sesion.invalidate(); // Quita token, rol y usuario de esta sesión.
        return "redirect:/login";
    }
}
