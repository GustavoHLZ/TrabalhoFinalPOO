
package excecoes;


public class ExcecaoCampoException extends Exception {
    
   
    public ExcecaoCampoException(String message) {
        super(message);
    }
    
    
    public ExcecaoCampoException(String message, Throwable cause) {
        super(message, cause);
    }
}