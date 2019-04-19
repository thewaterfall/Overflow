package waterfall.communication.client;

import waterfall.exception.ClientIsStoppedException;
import waterfall.exception.IllegalCommandException;
import waterfall.game.Board;
import waterfall.gui.GUI;
import waterfall.model.User;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.protocol.JSONCommandUtil;

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

    private User user;

    private CommandUtil commandUtil;

    public SocketClient(String iphost, int port, GUI gui) {
        this.iphost = iphost;
        this.port = port;
        this.gui = gui;
        this.commandUtil = new JSONCommandUtil();
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
        Command command = receiveResponse();
        processCommand(command);
    }

    @Override
    public void processCommand(Command command) {
        // /login [username] [password]
        if (command.getTypeCommand().equals("/login") &&
                command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS)) {
            user = (User) command.getParameter("user");
        } else if (command.getTypeCommand().equals("/logout") &&
                command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS)) { // /logout
            user = null;
        } else if (command.getTypeCommand().equals("/play")) { // play [game] [player/bot]

        } else if (command.getTypeCommand().equals("/connect")) {  // /connect [lobbyId]

        } else if (command.getTypeCommand().equals("/broadcast")) {

        } else if (command.getTypeCommand().equals("/move")) {
            board = (Board) command.getParameter("board");
            gui.updateBoard(board);
            gui.update();
        }


        gui.write(command.getMessage());
    }

    public boolean isStopped() {
        return isStopped;
    }
}
