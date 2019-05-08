package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.exception.IllegalCommandException;
import waterfall.model.Account;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

public class ExitCommand implements CommandAction {

    @Inject
    private CommandUtil commandUtil;

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Command response = null;
        try {
            response = commandUtil.constructCommand(command.getTypeCommand(),
                    CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_SUCCESS);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        CommandHandler.getCommand(CommandConstants.COMMAND_DISCONNECT).execute(clientHandler, command);
        CommandHandler.getCommand(CommandConstants.COMMAND_LOGOUT).execute(clientHandler, command);

        response.setMessage("You have successfully exited.");

        return response;
    }
}
