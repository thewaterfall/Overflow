package waterfall.protocol.command;

import java.util.HashMap;
import java.util.Map;
import waterfall.protocol.CommandConstants;

public class CommandHandler {
    private static Map<String, CommandAction> commandActionMap = new HashMap<>();
    static {
        commandActionMap.put(CommandConstants.COMMAND_LOGIN, new LoginCommand());
        commandActionMap.put(CommandConstants.COMMAND_LOGOUT, new LogoutCommand());
        commandActionMap.put(CommandConstants.COMMAND_DISCONNECT, new DisconnectCommand());
        commandActionMap.put(CommandConstants.COMMAND_EXIT, new ExitCommand());
        commandActionMap.put(CommandConstants.COMMAND_PLAY, new PlayCommand());
    }

    public static CommandAction getCommand(String command) {
        return commandActionMap.get(command);
    }
}
