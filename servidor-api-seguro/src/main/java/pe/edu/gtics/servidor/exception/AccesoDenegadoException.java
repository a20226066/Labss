package pe.edu.gtics.servidor.exception;

public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException() { super("Tu usuario no tiene permisos para esta operación."); }
}
