package excecoes;

public class ErroPersistenciaException extends Exception {

    public ErroPersistenciaException(String msg) {
        super(msg);
    }

    public ErroPersistenciaException(String msg, Throwable causa) {
        super(msg, causa);
    }
}
