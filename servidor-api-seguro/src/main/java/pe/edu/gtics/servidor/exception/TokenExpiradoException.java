package pe.edu.gtics.servidor.exception;

public class TokenExpiradoException extends RuntimeException {
    public TokenExpiradoException() { super("El token JWT ya expiró. Inicia sesión otra vez."); }
}
