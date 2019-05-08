package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.exception.IllegalCommandException;
import waterfall.model.Account;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.service.LobbyService;

public class DisconnectCommand implements CommandAction {

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private LobbyService lobbyService;

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

        if(account.isInLobby()) {
            disconnect(clientHandler);
            command.setMessage("You have disconnected");
        } else {
            command.setMessage("There's no lobby to disconnect from");
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
        }

        return response;
    }

    private void disconnect(ClientHandler clientHandler) {
        Command command = null;
        Account account = clientHandler.getAccount();
        try {
            command = commandUtil.constructCommand("/message",
                    CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_SUCCESS);
            command.setMessage(account.getUser().getUsername() + " has disconnected");
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        broadcast(clientHandler ,command, false);

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
    private void broadcast(ClientHandler clientHandler, Command command, boolean isEveryone) {
        String typeCommand = command.getTypeCommand();
        ClientHandler opponentHandler = clientHandler.getAccount().getOpponentHandler();

        command.setTypeCommand("/message");
        if (opponentHandler != null) {
            if (isEveryone) {
                opponentHandler.sendResponse(command);
                clientHandler.sendResponse(command);
            } else {
                opponentHandler.sendResponse(command);
            }
        }

        command.setTypeCommand(typeCommand);
    }
}
