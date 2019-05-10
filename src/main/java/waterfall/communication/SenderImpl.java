package waterfall.communication;

import java.util.List;
import waterfall.communication.server.ClientHandler;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.command.CommandHandler;

public class SenderImpl implements Sender {

    @Override
    public void send(List<ClientHandler> clientHandlers, Command command) {
        Command response = CommandHandler.getCommand(CommandConstants.COMMAND_MESSAGE).execute(clientHandlers.get(0), command);

        for(ClientHandler clientHandler: clientHandlers) {
            clientHandler.sendResponse(response);
        }
    }
}
