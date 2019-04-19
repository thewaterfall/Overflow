package waterfall.communication.server;

import com.google.inject.Inject;
import waterfall.exception.IllegalCommandException;
import waterfall.game.*;
import waterfall.model.GameStat;
import waterfall.model.Lobby;
import waterfall.model.User;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.security.Security;
import waterfall.service.GameStatService;
import waterfall.service.LobbyService;
import waterfall.service.UserService;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class SocketClientHandler implements ClientHandler {

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    private List<SocketClientHandler> clientHandlerList;
    private ClientHandler opponent;

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private Security security;

    private Lobby currentLobby;
    private User currentUser;
    private Player currentPlayer;

    @Inject
    private LobbyService lobbyService;

    @Inject
    private GameStatService gameStatService;

    @Inject
    private UserService userService;

    private Factory gameFactory;
    private Factory playerFactory;

    private boolean isStopped = true;

    public SocketClientHandler() {
        this.gameFactory = new GameFactory();
        this.playerFactory = new PlayerFactory();
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
        while (!isStopped()) {
            Command command = receiveRequest();
            Command response = processCommand(command);
            sendResponse(response);
        }

    }

    @Override
    public void stopConnection() {
        try {
            output.close();
            input.close();
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        isStopped = true;
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
        command.setSource(CommandConstants.COMMAND_TYPE_HANDLER);

        // /login [username] [password]
        if(command.getTypeCommand().equals("/login")) {
            processLogin(command);
        } else if(command.getTypeCommand().equals("/logout")) { // /logout
            processLogout(command);
        } else if(command.getTypeCommand().equals("/play")) { // play [game] [player/bot]
            processPlay(command);
        // /connect [lobbyId]
        } else if(command.getTypeCommand().equals("/connect")) { // /connect [lobbyId]
            processConnect(command);
            onGameReady();
        } else if (command.getTypeCommand().equals("/move")) { // /move [from] [to]
            processMove(command);
        }

        return command;
    }

    private void broadCast(Command command) {
        if(currentLobby.isLobbyFull()) {
            sendResponse(command);
            opponent.sendResponse(command);
        }
    }

    private void processMove(Command command) {
        command.setSource(CommandConstants.COMMAND_TYPE_HANDLER);
        command.setTypeCommand(CommandConstants.COMMAND_TYPE_RESPONSE);

        String response = currentPlayer.makeMove(currentLobby.getGame(), currentLobby.getGame().convertToMove(
                command.getAttributesCommand().get(1) + " " + command.getAttributesCommand().get(2)));

        command.setMessage(response);
        if(currentLobby.getGame().isFinished()) {
            if(!currentUser.hasGameStat(currentLobby.getGameType())) {
                GameStat gameStat = new GameStat(currentLobby.getGameType(), 0, 0, 0);
                gameStatService.save(gameStat);

                currentUser.addGameStat(gameStat);
                userService.update(currentUser);
            }

            if(!currentLobby.getOpponentFor(currentUser).hasGameStat(currentLobby.getGameType())) {
                GameStat gameStat = new GameStat(currentLobby.getGameType(), 0, 0, 0);
                gameStatService.save(gameStat);

                currentLobby.getOpponentFor(currentUser).addGameStat(gameStat);
                userService.update(currentLobby.getOpponentFor(currentUser));
            }

            if(currentLobby.getGame().getWinner() == currentPlayer) {
                currentUser.getGameStat(currentLobby.getGameType()).addWin();
                currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()).addLose();

                gameStatService.update(currentUser.getGameStat(currentLobby.getGameType()));
                command.setMessage(currentUser.getUsername() + " has won.");
            } else {
                currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()).addWin();
                currentUser.getGameStat(currentLobby.getGameType()).addLose();

                gameStatService.update(currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()));
                command.setMessage(currentLobby.getOpponentFor(currentUser).getUsername() + " has won.");
            }
        }

        command.addParameter("board", currentLobby.getGame().getBoard());
        broadCast(command);
    }

    private void processConnect(Command command) {
        if(!isInLobby()) {
            if(!currentLobby.isLobbyFull()) {
                currentLobby = lobbyService.findById(Integer.valueOf(command.getAttributesCommand().get(1)));
                currentLobby.setToVacantSlot(currentUser);
                lobbyService.update(currentLobby);

                currentPlayer = (Player) playerFactory.getBean(currentLobby.getGameType().getType());
                currentLobby.getGame().registerPlayer(currentPlayer);

                findOpponent();
                command.setMessage("You have successfully connected");
            } else {
                command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
                command.setMessage("Lobby is full");
            }
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("You are already in lobby");
        }
    }

    private void processPlay(Command command) {
        if(!isInLobby()) {
            command.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            currentLobby = new Lobby();
            currentLobby.setToVacantSlot(currentUser);
            lobbyService.save(currentLobby);

            currentLobby.setGame((Game) gameFactory.getBean(command.getAttributesCommand().get(1)));
            currentPlayer = (Player) playerFactory.getBean(command.getAttributesCommand().get(1));
            currentLobby.getGame().registerPlayer(currentPlayer);

            if(command.getAttributesCommand().get(2).equals("bot")) {
            // TODO add logic to play vs bot
            command.setMessage("The game has been started");
            }
            command.setMessage("Lobby has been created with id + " + currentLobby.getId());
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("You are already in lobby");
        }
    }

    private void processLogin(Command command) {
        if(!isLoggedIn()) {
            currentUser = security.authorize(command.getAttributesCommand().get(0), command.getAttributesCommand().get(1));
            if (currentUser != null) {
                command.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
                command.setMessage("Hello + " + command.getAttributesCommand().get(0));
                command.addParameter("user", currentUser);
            } else {
                command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
                command.setMessage("User is not found");
            }
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("You are already logged in");
        }
    }

    private void processLogout(Command command) {
        if(isLoggedIn()) {
            currentUser = null;
            command.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            command.setMessage("You have successfully logged out");
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("You are not logged in yet");
        }
    }

    private boolean findOpponent() {
        for(SocketClientHandler handler: clientHandlerList) {
            if((handler.getCurrentUser().equals(currentLobby.getFirstUser()) ||
                handler.getCurrentUser().equals(currentLobby.getSecondUser())
                ) &&
                !handler.getCurrentUser().equals(currentUser)
            ) {
                opponent = handler;
                handler.setOpponent(this);
                return true;
            }
        }
        return false;
    }

    private boolean isInLobby() {
        return currentLobby != null;
    }

    private boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isStopped() {
        return isStopped;
    }

    private void onGameReady() {
        Command broadcastCommand = null;
        try {
            broadcastCommand = commandUtil.constructCommand("/broadcast",
                    CommandConstants.COMMAND_TYPE_RESPONSE, CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_SUCCESS);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        broadCast(broadcastCommand);
    }

    public void onConnect() {
        while(currentUser == null) {
            loginForm();
        }
    }

    public void loginForm() {
        String loginMessage = "Type /login [username] [password] to log in.";

        Command command = null;
        try {
            command = commandUtil.constructCommand("/login", CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER, CommandConstants.COMMAND_STATUS_SUCCESS);
            command.setMessage(loginMessage);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        sendResponse(command);
        Command request = receiveRequest();
        Command response = processCommand(request);
        sendResponse(response);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public ClientHandler getOpponent() {
        return opponent;
    }

    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public List<SocketClientHandler> getClientHandlerList() {
        return clientHandlerList;
    }

    public void setClientHandlerList(List<SocketClientHandler> clientHandlerList) {
        this.clientHandlerList = clientHandlerList;
    }

    public Factory getGameFactory() {
        return gameFactory;
    }

    public void setGameFactory(Factory gameFactory) {
        this.gameFactory = gameFactory;
    }

    public Factory getPlayerFactory() {
        return playerFactory;
    }

    public void setPlayerFactory(Factory playerFactory) {
        this.playerFactory = playerFactory;
    }
}