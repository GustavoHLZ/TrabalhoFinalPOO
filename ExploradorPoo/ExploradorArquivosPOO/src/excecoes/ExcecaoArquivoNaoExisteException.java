package excecoes;

public class ExcecaoArquivoNaoExisteException extends Exception {
    
    
       public ExcecaoArquivoNaoExisteException(String message) {
        super(message);
    }
    
    public ExcecaoArquivoNaoExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}
