package waterfall.communication.client;

import waterfall.protocol.Command;

public interface Client {
    public void startConnection();

    public void stopConnection();

    public Command sendRequest(String request);

    public void receiveResponse(String response);

    public void processCommand(Command command);
}
