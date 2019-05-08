package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
import waterfall.exception.IllegalCommandException;
import waterfall.model.Account;
import waterfall.model.GameType;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;
import waterfall.service.GameTypeService;
import waterfall.service.UserService;

public class LeaderboardCommand implements CommandAction {

    @Inject
    private CommandUtil commandUtil;

    @Inject
    private GameTypeService gameTypeService;

    @Inject
    private UserService userService;

    @Override
    public Command execute(ClientHandler clientHandler, Command command) {
        Command response = null;
        try {
            response = commandUtil.constructCommand(command.getTypeCommand(),
                    CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_SUCCESS);
        } catch (IllegalCommandException e) {
            e.printStackTrace();
        }


        GameType gameType = gameTypeService.findByName(command.getAttributesCommand().get(0));
        if (gameType != null) {
            command.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            command.addParameter("leaderboard", userService.getLeaderboard(gameType));
            command.setMessage("Leaderbord for " + gameType.getType() + " game type");
        } else {
            command.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            command.setMessage("There's no such game");
        }

        return response;
    }
}
