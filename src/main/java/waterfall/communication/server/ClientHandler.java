package waterfall.communication.server;

import waterfall.protocol.Command;

public interface ClientHandler {
    public void stopConnection();

    public Command receiveRequest(String request);

    public void sendResponse(String response);

    public String processCommand(Command command);
}
