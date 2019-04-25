package waterfall.communication.client;

import com.google.inject.Inject;
import waterfall.exception.ClientIsStoppedException;
import waterfall.exception.IllegalCommandException;
import waterfall.game.Board;
import waterfall.game.chess.ChessBoard;
import waterfall.gui.GUI;
import waterfall.model.User;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class SocketClient implements Client {
    private GUI gui;
    private Board board;

    private boolean isStopped = true;

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    private String iphost;
    private int port;

    private User user;

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

        Command command = null;
        try {
            command = commandUtil.constructCommand(request, CommandConstants.COMMAND_TYPE_REQUEST,
                    CommandConstants.COMMAND_SOURCE_CLIENT, null);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
            gui.write(e.getMessage());
            // TODO when exception is caught, then no need to go next
            return;
        }

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
        // /login [username] [password]
        if (command.getTypeCommand().equals("/login") &&
                command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS)) {
            user = commandUtil.getParameter(command, "user", User.class);
        } else if (command.getTypeCommand().equals("/logout") &&
                command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS)) { // /logout
            user = null;
        } else if (command.getTypeCommand().equals("/play")) { // play [game] [player/bot]

        } else if (command.getTypeCommand().equals("/connect")) {  // /connect [lobbyId]

        } else if (command.getTypeCommand().equals("/message")) {
            board = commandUtil.getParameter(command, "board", ChessBoard.class);
            if(board != null) {
                gui.updateBoard(board);
                gui.update();
            }
        } else if (command.getTypeCommand().equals("/move")) {  // /move [from] [to]
            if(command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS)) {
                board = commandUtil.getParameter(command, "board", ChessBoard.class);
                gui.updateBoard(board);
                gui.update();
            }
        } else if (command.getTypeCommand().equals("/leaderboard")) { // /leaderboard [gameType]
            if (command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS)) {
                gui.write(commandUtil.getParameter(command, "leaderboard", List.class).toString());
            }
        } else if(command.getTypeCommand().equals("/disconnect")) {

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
