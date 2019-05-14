package waterfall.protocol.command;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import waterfall.communication.Sender;
import waterfall.communication.server.ClientHandler;
import waterfall.game.GameFactory;
import waterfall.game.GameFactoryImpl;
import waterfall.model.Account;
import waterfall.model.Lobby;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.service.LobbyService;

public class ConnectCommand implements CommandAction {

    @Inject
    private Sender sender;

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private LobbyService lobbyService;

    private GameFactory gameFactory;

    public ConnectCommand() {
        gameFactory = new GameFactoryImpl();
    }

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Account account = clientHandler.getAccount();
        Command response = commandUtil.constructCommand(
                CommandConstants.COMMAND_CONNECT,
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS
        );


        if(!account.isInLobby()) {
            Lobby lobby = lobbyService.findById(Integer.valueOf(command.getAttributesCommand().get(0)));
            account.setLobby(lobby);

            if (!lobby.isLobbyFull()) {
                lobby.addUser(account.getUser());
                lobbyService.update(lobby);

                account.setPlayer(gameFactory.getPlayer(lobby.getGameType().getType()));

                account.findOpponentHandler();

                lobby.setGame(account.getOpponentHandler().getAccount().getLobby().getGame());
                lobby.getGame().registerPlayer(account.getPlayer());


                response.setMessage("You have successfully connected");
                response.addParameter("board", lobby.getGame().getBoard());

                onGameReady(clientHandler);
            } else {
                response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
                response.setMessage("Lobby is full");
            }
        } else {
            response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            response.setMessage("You are already in lobby");
        }

        return response;
    }

    private void onGameReady(ClientHandler clientHandler) {
        Account account = clientHandler.getAccount();
        Lobby currentLobby = account.getLobby();
        currentLobby.getGame().start();

        Command command = commandUtil.constructCommand(
                CommandConstants.COMMAND_MESSAGE,
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS
        );

        command.addParameter("board", currentLobby.getGame().getBoard());
        command.setMessage("Game is ready!");

        List<ClientHandler> sendTo = new ArrayList<>();
        sendTo.add(clientHandler);
        sendTo.add(account.getOpponentHandler());

        sender.send(sendTo, command);
    }
}
