package waterfall.communication.server;

import com.google.inject.Inject;
import waterfall.model.Account;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.protocol.command.CommandHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;

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
        start();

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

    private void start() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        isStopped = false;
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

        return response;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    public boolean isStopped() {
        return isStopped;
    }

    private Command verifyAuth(Command command) {
        if (!account.isLoggedIn() &&
                !command.getTypeCommand().equals(CommandConstants.COMMAND_LOGIN) &&
                !command.getTypeCommand().equals(CommandConstants.COMMAND_EXIT)) {
            command = constructLogin();
        }

        return command;
    }

    private Command constructLogin() {
        String loginMessage = "Type /login [username] [password] to log in.";

        Command command = new Command();
        command.setMessage(loginMessage);

        command = CommandHandler.getCommand(CommandConstants.COMMAND_MESSAGE).execute(this, command);

        return command;
    }

    private void onConnect() {
        Command loginCommand = constructLogin();
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
