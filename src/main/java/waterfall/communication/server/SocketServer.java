package waterfall.communication.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer implements Server {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<SocketClientHandler> clientHandlerList;

    private int port;

    private boolean isStopped = true;

    public SocketServer(int port, int clients) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(clients);
        this.clientHandlerList = new ArrayList<>(clients);
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        isStopped = false;
        while (!isStopped()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SocketClientHandler clientHandler = new SocketClientHandler(socket, clientHandlerList);
            clientHandlerList.add(clientHandler);

            threadPool.execute(clientHandler);
        }
    }

    @Override
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isStopped = true;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public static void main(String[] args) {
        new SocketServer(8088, 10).start();
    }
}
