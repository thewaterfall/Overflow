package waterfall.communication;

import java.util.List;
import waterfall.communication.server.ClientHandler;
import waterfall.protocol.Command;

public interface Sender {
    public void send(List<ClientHandler> clientHandlers, Command command);
}
