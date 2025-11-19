package excecoes;

public class FormatoNaoSuportadoException extends Exception {
    
    
     public FormatoNaoSuportadoException(String message) {
        super(message);
    }
    
    public FormatoNaoSuportadoException(String message,Throwable cause) {
        super(message,cause);
    }
}
