package pe.edu.gtics.servidor.dto;

// Respuesta legible para errores: {"error":"TOKEN_EXPIRED", "mensaje":"..."}
public class ErrorRespuesta {
    private final String error;
    private final String mensaje;
    public ErrorRespuesta(String error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
    }
    public String getError() { return error; }
    public String getMensaje() { return mensaje; }
}
