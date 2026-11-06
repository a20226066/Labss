package pe.edu.gtics.cliente.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.gtics.cliente.dto.*;
import pe.edu.gtics.cliente.service.CatalogoApiCliente;

// Controller MVC: recibe formularios, hace consultas HTTP y devuelve nombres de plantillas.
@Controller
@RequestMapping("/apis")
public class ApiWebController {
    private final CatalogoApiCliente servicio;
    public ApiWebController(CatalogoApiCliente servicio) { this.servicio = servicio; }

    private String token(HttpSession sesion) { return (String) sesion.getAttribute("token"); }
    private boolean esAdmin(HttpSession sesion) { return "ADMIN".equals(sesion.getAttribute("rol")); }

    @GetMapping
    public String listar(Model model, HttpSession sesion) {
        if (token(sesion) == null) return "redirect:/login";
        model.addAttribute("listaApis", servicio.listar(token(sesion))); // Clase Model + Thymeleaf
        return "apis/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model, HttpSession sesion) {
        if (token(sesion) == null) return "redirect:/login";
        model.addAttribute("api", servicio.buscarPorId(token(sesion), id));
        return "apis/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession sesion) {
        if (token(sesion) == null) return "redirect:/login";
        if (!esAdmin(sesion)) return "redirect:/apis";
        model.addAttribute("api", new ApiFormulario()); // Objeto vacío para nuevo.
        model.addAttribute("equipos", servicio.listarEquipos(token(sesion)));
        return "apis/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, HttpSession sesion) {
        if (token(sesion) == null) return "redirect:/login";
        if (!esAdmin(sesion)) return "redirect:/apis";
        ApiResumen existente = servicio.buscarPorId(token(sesion), id);
        ApiFormulario formulario = new ApiFormulario();
        formulario.setId(existente.getId());
        formulario.setNombre(existente.getNombre());
        formulario.setVersion(existente.getVersion());
        formulario.setFechaRegistro(existente.getFechaRegistro());
        formulario.setEstado(existente.getEstado());
        formulario.setEquipoId(existente.getEquipo().getId());
        model.addAttribute("api", formulario); // Objeto lleno para editar.
        model.addAttribute("equipos", servicio.listarEquipos(token(sesion)));
        return "apis/formulario"; // MISMA vista para nuevo/editar, como clase @ModelAttribute.
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("api") ApiFormulario api,
                          BindingResult result, Model model, HttpSession sesion,
                          RedirectAttributes redirectAttributes) {
        if (token(sesion) == null) return "redirect:/login";
        if (!esAdmin(sesion)) return "redirect:/apis";
        // @Valid y BindingResult deben ir juntos, como explica la clase de validaciones.
        if (result.hasErrors()) {
            model.addAttribute("equipos", servicio.listarEquipos(token(sesion)));
            return "apis/formulario";
        }
        if (api.getId() == null) servicio.crear(token(sesion), api);
        else servicio.editar(token(sesion), api);
        // RedirectAttributes: el mensaje sobrevive SOLO al siguiente redirect.
        redirectAttributes.addFlashAttribute("mensaje", "API guardada correctamente.");
        return "redirect:/apis";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, HttpSession sesion,
                           RedirectAttributes redirectAttributes) {
        if (token(sesion) == null) return "redirect:/login";
        if (!esAdmin(sesion)) return "redirect:/apis";
        servicio.eliminar(token(sesion), id);
        redirectAttributes.addFlashAttribute("mensaje", "API eliminada.");
        return "redirect:/apis";
    }

    @ExceptionHandler(RestClientException.class)
    public String errorRest(RestClientException ex, Model model) {
        // Ej.: servidor apagado, JWT expirado, recurso que no existe.
        model.addAttribute("error", "No se pudo comunicar con la API: " + ex.getMessage());
        return "error";
    }
}
