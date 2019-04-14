package waterfall.exception;

public class ClientIsStoppedException extends Exception {

    public ClientIsStoppedException() {

    }

    public ClientIsStoppedException(String msg) {
        super(msg);
    }
}