package waterfall.communication.server;

import com.google.inject.Inject;
import waterfall.exception.IllegalCommandException;
import waterfall.game.*;
import waterfall.model.GameStat;
import waterfall.model.GameType;
import waterfall.model.Lobby;
import waterfall.model.User;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.security.Security;
import waterfall.service.GameStatService;
import waterfall.service.GameTypeService;
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
    private GameTypeService gameTypeService;

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
        command = verifyAuth(command);

        command.setSource(CommandConstants.COMMAND_TYPE_HANDLER);
        command.setType(CommandConstants.COMMAND_TYPE_RESPONSE);

        // /login [username] [password]
        if(command.getTypeCommand().equals("/login")) {
            processLogin(command);
        } else if(command.getTypeCommand().equals("/logout")) { // /logout
            processLogout(command);
        } else if(command.getTypeCommand().equals("/play")) { // play [game] [player/bot]
            processPlay(command);
        } else if(command.getTypeCommand().equals("/connect")) { // /connect [lobbyId]
            processConnect(command);
            onGameReady();
        } else if (command.getTypeCommand().equals("/move")) { // /move [from] [to]
            processMove(command);
        } else if (command.getTypeCommand().equals("/leaderboard")) { // /leaderboard [gameType]
            processLeaderboard(command);
        } else if (command.getTypeCommand().equals("/disconnect")) { // /disconnect
            processDisconnect(command);
        }

        return command;
    }

    @Override
    public Game getCurrentGame() {
        return currentLobby.getGame();
    }

    private void broadCast(Command command, boolean isEveryone) {
        String typeCommand = command.getTypeCommand();

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

    private void disconnect() {
        currentLobby.removeUser(currentUser);
        currentLobby.getGame().unregisterPlayer(currentPlayer);
        lobbyService.update(currentLobby);

        if (currentLobby.getUsers().isEmpty()) {
            lobbyService.remove(currentLobby);
        }

        currentLobby = null;
        currentPlayer = null;

        if (opponent != null) {
            ((SocketClientHandler) opponent).setOpponent(null);
            opponent = null;
        }
    }

    private void processDisconnect(Command command) {
        if(isInLobby()) {
            command.setMessage(currentUser.getUsername() + " has disconnected");
            broadCast(command, false);
            disconnect();
        } else {
            command.setMessage("There's no lobby to disconnect from");
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
        }
    }

    private void processLeaderboard(Command command) {
        GameType gameType = gameTypeService.findByName(command.getAttributesCommand().get(0));
        if (gameType != null) {
            command.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            command.addParameter("leaderboard", userService.getLeaderboard(gameType));
            command.setMessage("Leaderbord for " + gameType.getType() + " game type");
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("There's no such game");
        }
    }

    private void processMove(Command command) {
        if (isInLobby()) {
            if (!currentLobby.isLobbyFull()) {
                Game game = currentLobby.getGame();
                currentLobby = lobbyService.findById(currentLobby.getId());
                currentLobby.setGame(game);
            }

            if (currentLobby.getGame().isReady()) {
                Move move = currentLobby.getGame().convertToMove(
                        command.getAttributesCommand().get(0) + " " + command.getAttributesCommand().get(1));
                command.setMessage(currentPlayer.makeMove(currentLobby.getGame(), move));
            } else {
                command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
                command.setMessage("Game is not ready");
            }

            if (command.getMessage().startsWith("Moved from")) {
                command.addParameter("board", currentLobby.getGame().getBoard());
                command.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            } else {
                command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            }

            if (currentLobby.getGame().isFinished()) {
                if (!currentUser.hasGameStat(currentLobby.getGameType())) {
                    GameStat gameStat = new GameStat(currentLobby.getGameType(), 0, 0, 0);
                    gameStatService.save(gameStat);

                    currentUser.addGameStat(gameStat);
                    userService.update(currentUser);
                }

                if (!currentLobby.getOpponentFor(currentUser).hasGameStat(currentLobby.getGameType())) {
                    GameStat gameStat = new GameStat(currentLobby.getGameType(), 0, 0, 0);
                    gameStatService.save(gameStat);

                    currentLobby.getOpponentFor(currentUser).addGameStat(gameStat);
                    userService.update(currentLobby.getOpponentFor(currentUser));
                }

                if (currentLobby.getGame().getWinner() == currentPlayer) {
                    currentUser.getGameStat(currentLobby.getGameType()).addWin();
                    currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()).addLose();

                    gameStatService.update(currentUser.getGameStat(currentLobby.getGameType()));
                    command.setMessage(command.getMessage() + currentUser.getUsername() + " has won.");
                } else {
                    currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()).addWin();
                    currentUser.getGameStat(currentLobby.getGameType()).addLose();

                    gameStatService.update(currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()));
                    command.setMessage(command.getMessage() + currentLobby.getOpponentFor(currentUser).getUsername() + " has won.");
                }

                disconnect();
                lobbyService.remove(currentLobby);
            }

            if (command.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS))
                broadCast(command, false);
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("You are not in a game");
        }
    }

    private void processConnect(Command command) {
        if(!isInLobby()) {
            Lobby lobby = lobbyService.findById(Integer.valueOf(command.getAttributesCommand().get(0)));
            if (!lobby.isLobbyFull()) {
                currentLobby = lobby;
                currentLobby.addUser(currentUser);
                lobbyService.update(currentLobby);

                currentPlayer = (Player) playerFactory.getBean(currentLobby.getGameType().getType());

                findOpponentHandler();

                currentLobby.setGame(opponent.getCurrentGame());
                currentLobby.getGame().registerPlayer(currentPlayer);

                command.setMessage("You have successfully connected");
                command.addParameter("board", currentLobby.getGame().getBoard());
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
            currentLobby.addUser(currentUser);

            currentLobby.setGame((Game) gameFactory.getBean(command.getAttributesCommand().get(0)));
            currentLobby.setGameType(gameTypeService.findByName(command.getAttributesCommand().get(0)));
            currentPlayer = (Player) playerFactory.getBean(command.getAttributesCommand().get(0));
            currentLobby.getGame().registerPlayer(currentPlayer);

            lobbyService.save(currentLobby);

            if (command.getAttributesCommand().get(1) != null && command.getAttributesCommand().get(1).equals("bot")) {
                // TODO add logic to play vs bot
                command.setMessage("The game has been started");
            } else {
                command.setMessage("Lobby has been created with id: " + currentLobby.getId());
            }
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
                command.setMessage("Hello " + command.getAttributesCommand().get(0));
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

    private boolean findOpponentHandler() {
        for(SocketClientHandler handler: clientHandlerList) {
            if (currentLobby.getUsers().contains(handler.getCurrentUser()) &&
                    !handler.getCurrentUser().equals(currentUser)) {
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

        broadCast(broadcastCommand, true);
    }

    private Command verifyAuth(Command command) {
        if (!isLoggedIn() && !command.getTypeCommand().equals("/login")) {
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
