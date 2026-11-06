package pe.edu.gtics.cliente.dto;

// Se usa al llamar GET /login con usuario y contraseña por Basic Auth.
public class TokenRespuesta {
    private String token;
    private String tipo;
    private String rol;
    private long expiraEnSegundos;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public long getExpiraEnSegundos() { return expiraEnSegundos; }
    public void setExpiraEnSegundos(long expiraEnSegundos) { this.expiraEnSegundos = expiraEnSegundos; }
}
