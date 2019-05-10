package waterfall.protocol.command;

import com.google.inject.Inject;
import waterfall.communication.server.ClientHandler;
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
        Command response = commandUtil.constructCommand(
                command.getTypeCommand(),
                CommandConstants.COMMAND_TYPE_RESPONSE,
                CommandConstants.COMMAND_TYPE_HANDLER,
                CommandConstants.COMMAND_STATUS_SUCCESS
        );


        GameType gameType = gameTypeService.findByName(command.getAttributesCommand().get(0));
        if (gameType != null) {
            response.setStatus(CommandConstants.COMMAND_STATUS_SUCCESS);
            response.setMessage("Leaderbord for " + gameType.getType() + " game type: \n" + userService.getLeaderboard(gameType).toString());
        } else {
            response.setStatus(CommandConstants.COMMAND_STATUS_FAILURE);
            response.setMessage("There's no such game");
        }

        return response;
    }
}
