package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.exception.IllegalCommandException;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

public class MessageCommand implements CommandAction {

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
            response.setMessage(command.getMessage());
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }

        return response;
    }
}
