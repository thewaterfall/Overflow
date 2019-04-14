package waterfall.communication.server;

public interface Server {
    public void start(int port, int clients);

    public void stop();
}
