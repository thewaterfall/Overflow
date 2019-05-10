package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.model.Account;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

public class LogoutCommand implements CommandAction {

    @Inject
    private CommandUtil commandUtil;

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Account account = clientHandler.getAccount();
        Command response = commandUtil.constructCommand(
                CommandConstants.COMMAND_LOGOUT,
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS);

        if(account.isLoggedIn()) {
            logout(account);
            response.setMessage("You have successfully logged out");
        } else {
            response.setMessage("You are not logged in yet");
            response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
        }

        return response;
    }

    private void logout(Account account) {
        account.setUser(null);
    }

}
