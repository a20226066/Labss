package pe.edu.gtics.servidor.dto;

// El servidor entrega estos datos tras autenticar con Basic Auth.
// En JSON, expiraEnSegundos se convierte en expira_en_segundos.
public class TokenRespuesta {
    private final String token;
    private final String tipo = "Bearer";
    private final String rol;
    private final long expiraEnSegundos;

    public TokenRespuesta(String token, String rol, long expiraEnSegundos) {
        this.token = token;
        this.rol = rol;
        this.expiraEnSegundos = expiraEnSegundos;
    }
    public String getToken() { return token; }
    public String getTipo() { return tipo; }
    public String getRol() { return rol; }
    public long getExpiraEnSegundos() { return expiraEnSegundos; }
}
