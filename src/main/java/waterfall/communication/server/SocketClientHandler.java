package waterfall.communication.server;

import com.google.inject.Inject;
import waterfall.exception.IllegalCommandException;
import waterfall.model.Account;
import waterfall.model.Lobby;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.protocol.command.CommandHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;

// TODO: refactor
public class SocketClientHandler implements ClientHandler {

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    @Inject
    private CommandUtil commandUtil;

    private Account account;

    private boolean isStopped = true;

    public SocketClientHandler() {
        this.account = new Account(this);
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        isStopped = false;

        onConnect();
        Command command = null;
        try {
            while (!isStopped()) {
                command = receiveRequest();
                Command response = processCommand(command);
                sendResponse(response);
            }
        } finally {
            CommandHandler.getCommand(CommandConstants.COMMAND_EXIT).execute(this, command);
            stop();
        }
    }

    @Override
    public void stopConnection() {
        isStopped = true;
    }

    private void stop() {
        try {
            account.getClientHandlers().remove(this);
            output.close();
            input.close();
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command receiveRequest() {
        String request = "";
        try {
            request = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commandUtil.convertToCommand(request);
    }

    @Override
    public void sendResponse(Command response) {
        try {
            output.write(commandUtil.covertToString(response));
            output.newLine();
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command processCommand(Command command) {
        command = verifyAuth(command);
        Command response = CommandHandler.getCommand(command.getTypeCommand()).execute(this, command);
//        // /login [username] [password]
//        if(command.getTypeCommand().equals("/login")) {
//            processLogin(command);
//        } else if(command.getTypeCommand().equals("/logout")) { // /logout
//            processLogout(command);
//        } else if (command.getTypeCommand().equals("/exit")) {
//            processExit(command);
//        } else if(command.getTypeCommand().equals("/play")) { // play [game] [player/bot]
//            processPlay(command);
//        } else if(command.getTypeCommand().equals("/connect")) { // /connect [lobbyId]
//            processConnect(command);
//        onGameReady();
//        } else if (command.getTypeCommand().equals("/move")) { // /move [from] [to]
//            processMove(command);
//        } else if (command.getTypeCommand().equals("/leaderboard")) { // /leaderboard [gameType]
//            processLeaderboard(command);
//        } else if (command.getTypeCommand().equals("/disconnect")) { // /disconnect
//            processDisconnect(command);
//        }

        return response;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    public boolean isStopped() {
        return isStopped;
    }

    private void onGameReady() {
        Lobby currentLobby = account.getLobby();
        currentLobby.getGame().start();

        Command broadcastCommand = null;
        try {
            broadcastCommand = commandUtil.constructCommand("/message",
                    CommandConstants.COMMAND_TYPE_RESPONSE, CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_SUCCESS);
            broadcastCommand.addParameter("board", currentLobby.getGame().getBoard());
            broadcastCommand.setMessage("Game is ready!");
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        broadcast(broadcastCommand, true);
    }

    private void broadcast(Command command, boolean isEveryone) {
        String typeCommand = command.getTypeCommand();
        ClientHandler opponent = account.getOpponentHandler();

        command.setTypeCommand("/message");
        if (opponent != null) {
            if (isEveryone) {
                opponent.sendResponse(command);
                sendResponse(command);
            } else {
                opponent.sendResponse(command);
            }
        }

        command.setTypeCommand(typeCommand);
    }

    private Command verifyAuth(Command command) {
        if (!account.isLoggedIn() && !command.getTypeCommand().equals("/login") && !command.getTypeCommand().equals("/exit")) {
            command = constuctLogin();
        }

        return command;
    }

    private Command constuctLogin() {
        String loginMessage = "Type /login [username] [password] to log in.";

        Command command = null;
        try {
            command = commandUtil.constructCommand("/message", CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER, CommandConstants.COMMAND_STATUS_SUCCESS);
            command.setMessage(loginMessage);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        return command;
    }

    private void onConnect() {
        Command loginCommand = constuctLogin();
        sendResponse(loginCommand);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public List<ClientHandler> getClientHandlerList() {
        return this.account.getClientHandlers();
    }

    public void setClientHandlerList(List<ClientHandler> clientHandlerList) {
        this.account.setClientHandlers(clientHandlerList);
    }

}
