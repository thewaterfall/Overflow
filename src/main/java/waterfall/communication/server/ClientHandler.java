package waterfall.communication.server;

import waterfall.game.Game;
import waterfall.model.Account;
import waterfall.protocol.Command;

public interface ClientHandler extends Runnable {
    public void stopConnection();

    public Command receiveRequest();

    public void sendResponse(Command response);

    public Command processCommand(Command command);

    public Account getAccount();
}
