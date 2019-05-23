package waterfall.model;

import waterfall.communication.server.ClientHandler;
import waterfall.game.Player;

import java.util.List;

public class Account {
    private User user;
    private Player player;
    private Lobby lobby;
    private List<ClientHandler> clientHandlers;
    private ClientHandler opponentHandler;
    private ClientHandler currentClientHandler;

    public Account() {

    }

    public Account(ClientHandler currentClientHandler) {
        this.currentClientHandler = currentClientHandler;
    }

    public boolean isInLobby() {
        return lobby != null;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void setClientHandlers(List<ClientHandler> clientHandlers) {
        this.clientHandlers = clientHandlers;
    }

    public ClientHandler getOpponentHandler() {
        return opponentHandler;
    }
    public void setOpponentHandler(ClientHandler opponentHandler) {
        this.opponentHandler = opponentHandler;
    }

    public ClientHandler getCurrentClientHandler() {
        return currentClientHandler;
    }

    public void setCurrentClientHandler(ClientHandler currentClientHandler) {
        this.currentClientHandler = currentClientHandler;
    }

    public boolean findOpponentHandler() {
        for(ClientHandler handler: clientHandlers) {
            Account account = handler.getAccount();
            if (lobby.getUsers().contains(account.getUser()) &&
                    !account.getUser().equals(user)) {
                opponentHandler = handler;
                account.setOpponentHandler(currentClientHandler);
                return true;
            }
        }
        return false;
    }
}
