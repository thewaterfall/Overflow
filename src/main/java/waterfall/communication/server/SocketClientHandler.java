package waterfall.communication.server;

import com.google.inject.Inject;
import waterfall.exception.IllegalCommandException;
import waterfall.game.*;
import waterfall.model.Account;
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
        try {
            while (!isStopped()) {
                Command command = receiveRequest();
                Command response = processCommand(command);
                sendResponse(response);
            }
        } finally {
            exit();
            stop();
        }
    }

    @Override
    public void stopConnection() {
        isStopped = true;
    }

    private void stop() {
        try {
            clientHandlerList.remove(this);
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
//            onGameReady();
//        } else if (command.getTypeCommand().equals("/move")) { // /move [from] [to]
//            processMove(command);
//        } else if (command.getTypeCommand().equals("/leaderboard")) { // /leaderboard [gameType]
//            processLeaderboard(command);
//        } else if (command.getTypeCommand().equals("/disconnect")) { // /disconnect
//            processDisconnect(command);
//        }

        return command;
    }

    @Override
    public Account getAccount() {
        return account;
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
        if (!isLoggedIn() && !command.getTypeCommand().equals("/login") && !command.getTypeCommand().equals("/exit")) {
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

    public void setClientHandlerList(List<ClientHandler> clientHandlerList) {
        this.account.setClientHandlers(clientHandlerList);
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
