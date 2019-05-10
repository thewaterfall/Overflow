package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.Sender;
import waterfall.communication.server.ClientHandler;
import waterfall.model.Account;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.service.LobbyService;

import java.util.Arrays;

public class DisconnectCommand implements CommandAction {

    @Inject
    private Sender sender;

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private LobbyService lobbyService;

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Account account = clientHandler.getAccount();
        Command response = commandUtil.constructCommand(
                CommandConstants.COMMAND_DISCONNECT,
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS
        );

        if(account.isInLobby()) {
            disconnect(clientHandler, command);
            response.setMessage("You have disconnected");
        } else {
            response.setMessage("There's no lobby to disconnect from");
            response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
        }

        return response;
    }

    private void disconnect(ClientHandler clientHandler, Command command) {
        Account account = clientHandler.getAccount();
        command.setMessage(account.getUser().getUsername() + " has disconnected");

        sender.send(Arrays.asList(account.getOpponentHandler()), command);

        account.getLobby().removeUser(account.getUser());
        account.getLobby().getGame().unregisterPlayer(account.getPlayer());
        lobbyService.update(account.getLobby());

        if (account.getLobby().getUsers().isEmpty()) {
            lobbyService.remove(account.getLobby());
        }

        account.setLobby(null);
        account.setPlayer(null);

        if (account.getOpponentHandler() != null) {
            account.getOpponentHandler().getAccount().setOpponentHandler(null);
            account.setOpponentHandler(null);
        }
    }
}
