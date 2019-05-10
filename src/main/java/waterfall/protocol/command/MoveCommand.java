package waterfall.protocol.command;

import com.google.inject.Inject;
import java.util.Arrays;
import waterfall.communication.Sender;
import waterfall.communication.server.ClientHandler;
import waterfall.game.Game;
import waterfall.game.Move;
import waterfall.game.Player;
import waterfall.model.Account;
import waterfall.model.GameStat;
import waterfall.model.Lobby;
import waterfall.model.User;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.service.GameStatService;
import waterfall.service.LobbyService;
import waterfall.service.UserService;

public class MoveCommand implements CommandAction {

    @Inject
    private Sender sender;

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private LobbyService lobbyService;

    @Inject
    private UserService userService;

    @Inject
    private GameStatService gameStatService;


    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Account account = clientHandler.getAccount();
        Lobby currentLobby = account.getLobby();
        Player currentPlayer = account.getPlayer();
        User currentUser = account.getUser();

        Command response = commandUtil.constructCommand(command.getTypeCommand(),
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS);

        if (account.isInLobby()) {
            if (!currentLobby.isLobbyFull()) {
                Game game = currentLobby.getGame();
                currentLobby = lobbyService.findById(currentLobby.getId());
                currentLobby.setGame(game);
            }

            if (currentLobby.getGame().isReady()) {
                Move move = currentLobby.getGame().convertToMove(
                        command.getAttributesCommand().get(0) + " " + command.getAttributesCommand().get(1));
                response.setMessage(currentPlayer.makeMove(currentLobby.getGame(), move));
            } else {
                response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
                response.setMessage("Game is not ready");
            }

            if (response.getMessage().startsWith("Moved from")) {
                response.addParameter("board", currentLobby.getGame().getBoard());
                response.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            } else {
                response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
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
                    response.setMessage(response.getMessage() + currentUser.getUsername() + " has won.");
                } else {
                    currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()).addWin();
                    currentUser.getGameStat(currentLobby.getGameType()).addLose();

                    gameStatService.update(currentLobby.getOpponentFor(currentUser).getGameStat(currentLobby.getGameType()));
                    response.setMessage(response.getMessage() + currentLobby.getOpponentFor(currentUser).getUsername() + " has won.");
                }

                CommandHandler.getCommand(CommandConstants.COMMAND_DISCONNECT);
                lobbyService.remove(currentLobby);
            }

            if (response.getStatus().equals(CommandConstants.COMMAND_STATUS_SUCCESS))
                sender.send(Arrays.asList(account.getOpponentHandler()), response);

        } else {
            response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            response.setMessage("You are not in a game");
        }
        return null;
    }
}
