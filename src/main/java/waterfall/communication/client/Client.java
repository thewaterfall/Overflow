package waterfall.communication.client;

import waterfall.exception.ClientIsStoppedException;
import waterfall.protocol.Command;

public interface Client {
    public void startConnection();

    public void stopConnection();

    public void sendRequest(String request) throws ClientIsStoppedException;

    public Command receiveResponse(String response) throws ClientIsStoppedException;

    public void processCommand(Command command);
}
