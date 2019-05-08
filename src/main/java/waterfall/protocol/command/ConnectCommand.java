package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.exception.IllegalCommandException;
import waterfall.game.Player;
import waterfall.game.PlayerFactory;
import waterfall.model.Account;
import waterfall.model.Lobby;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.service.LobbyService;

public class ConnectCommand implements CommandAction {

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private LobbyService lobbyService;

    private PlayerFactory playerFactory;

    public ConnectCommand() {
        playerFactory = new PlayerFactory();
    }

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Account account = clientHandler.getAccount();
        Command response = null;
        try {
            response = commandUtil.constructCommand(command.getTypeCommand(),
                    CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_SUCCESS);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }


        if(!account.isInLobby()) {
            Lobby lobby = lobbyService.findById(Integer.valueOf(command.getAttributesCommand().get(0)));
            if (!lobby.isLobbyFull()) {
                lobby.addUser(account.getUser());
                lobbyService.update(lobby);

                account.setPlayer(playerFactory.getBean(lobby.getGameType().getType()));

                lobby.setGame(account.getOpponentHandler().getAccount().getLobby().getGame());
                lobby.getGame().registerPlayer(account.getPlayer());

                account.setLobby(lobby);

                command.setMessage("You have successfully connected");
                command.addParameter("board", lobby.getGame().getBoard());
            } else {
                command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
                command.setMessage("Lobby is full");
            }
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("You are already in lobby");
        }

        return response;
    }
}
