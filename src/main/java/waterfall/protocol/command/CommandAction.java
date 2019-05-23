package waterfall.protocol.command;

import waterfall.communication.server.ClientHandler;
import waterfall.protocol.Command;

public interface CommandAction {
    public Command execute(ClientHandler clientHandler, Command command);
}
