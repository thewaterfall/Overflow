package waterfall.exception;

public class ServerIsStoppedException extends Exception {

    public ServerIsStoppedException() {

    }

    public ServerIsStoppedException(String msg) {
        super(msg);
    }
}