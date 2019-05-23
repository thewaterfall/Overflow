package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

public class MessageCommand implements CommandAction {

    @Inject
    private CommandUtil commandUtil;

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Command response = commandUtil.constructCommand(
                CommandConstants.COMMAND_MESSAGE,
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS);
        response.setMessage(command.getMessage());
        response.setParameters(command.getParameters());

        return response;
    }
}
