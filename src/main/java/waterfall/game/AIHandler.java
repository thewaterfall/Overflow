package waterfall.game;

import waterfall.communication.server.ClientHandler;
import waterfall.model.Account;
import waterfall.protocol.Command;

import java.util.Random;

public class AIHandler implements ClientHandler {
    private boolean isStopped;
    private boolean isActivated;
    private Account account;

    public AIHandler() {
        account = new Account(this);
    }

    @Override
    public void run() {
        Random rand = new Random();

        isStopped = false;
        while(!isStopped) {
            while(isActivated) {
                try {
                    Thread.sleep(rand.nextInt(5000 + 1) + 5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                account.getPlayer().makeMove(account.getLobby().getGame(), null);

                deactivate();
            }
        }
    }

    @Override
    public void stopConnection() {
        isStopped = true;
    }

    @Override
    public Command receiveRequest() {
        return null;
    }

    @Override
    public void sendResponse(Command response) {
        activate();
    }

    @Override
    public Command processCommand(Command command) {
        return null;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    private void deactivate() {
        isActivated = false;
    }

    private void activate() {
        isActivated = true;
    }
}
