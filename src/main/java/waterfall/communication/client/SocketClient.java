package waterfall.communication.client;

import com.google.inject.Inject;
import waterfall.exception.ClientIsStoppedException;
import waterfall.game.Board;
import waterfall.gui.GUI;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

import java.io.*;
import java.net.Socket;

public class SocketClient implements Client {
    private GUI gui;
    private Board board;


    private boolean isStopped = true;

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    private String iphost;
    private int port;

    @Inject
    private CommandUtil commandUtil;

    public SocketClient() {

    }

    public SocketClient(String iphost, int port, GUI gui) {
        this.iphost = iphost;
        this.port = port;
        this.gui = gui;
    }

    @Override
    public void configure(String iphost, int port, GUI gui) {
        this.iphost = iphost;
        this.port = port;
        this.gui = gui;
    }

    @Override
    public void startConnection() {
        try {
            socket = new Socket(iphost, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new SocketReader()).start();

        isStopped = false;
    }

    @Override
    public void stopConnection() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isStopped = true;
    }

    @Override
    public void sendRequest(String request) throws ClientIsStoppedException {
        if (isStopped())
            throw new ClientIsStoppedException("Can't send request. Client-side is stopped.");

        Command command = commandUtil.constructCommand(request,
                CommandConstants.COMMAND_TYPE_REQUEST,
                CommandConstants.COMMAND_SOURCE_CLIENT,
                null);

        try {
            output.write(commandUtil.covertToString(command));
            output.newLine();
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command receiveResponse() throws ClientIsStoppedException {
        if (isStopped())
            throw new ClientIsStoppedException("Can't receive response. Client-side is stopped.");

        String response = "";
        try {
            response = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Command command = commandUtil.convertToCommand(response);

        return command;
    }

    @Override
    public void communicate(String request) throws ClientIsStoppedException {
        sendRequest(request);
//        Command command = receiveResponse();
//        processCommand(command);
    }

    @Override
    public void processCommand(Command command) {
        if (command.getTypeCommand().equals("/exit")) {
            stopConnection();
        }

        if (command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS) &&
                command.getParameter("board") != null) {
            board = (Board) command.getParameter("board");
            gui.updateBoard(board);
            gui.update();
        }

        gui.write(command.getMessage());
    }

    @Override
    public boolean isStopped() {
        return isStopped;
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public String getIphost() {
        return iphost;
    }

    public void setIphost(String iphost) {
        this.iphost = iphost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public class SocketReader implements Runnable {

        @Override
        public void run() {
            while (!isStopped()) {
                try {
                    processCommand(receiveResponse());
                } catch (ClientIsStoppedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
