package waterfall.protocol.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import waterfall.injection.Module;
import waterfall.protocol.CommandConstants;

import java.util.HashMap;
import java.util.Map;

// TODO: refactor
public class CommandHandler {
    private static Injector injector;
    private static Map<String, CommandAction> commandActionMap = new HashMap<>();
    static {
        injector = Guice.createInjector(new Module());

        commandActionMap.put(CommandConstants.COMMAND_LOGIN, injector.getInstance(LoginCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_LOGOUT, injector.getInstance(LogoutCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_DISCONNECT, injector.getInstance(DisconnectCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_EXIT, injector.getInstance(ExitCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_PLAY, injector.getInstance(PlayCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_CONNECT, injector.getInstance(ConnectCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_MOVE, injector.getInstance(MoveCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_LEADERBOARD, injector.getInstance(LeaderboardCommand.class));
        commandActionMap.put(CommandConstants.COMMAND_MESSAGE, injector.getInstance(MessageCommand.class));
    }

    public static CommandAction getCommand(String command) {
        CommandAction commandAction = commandActionMap.get(command);

        if (commandAction != null) {
            return commandAction;
        } else {
            return commandActionMap.get(CommandConstants.COMMAND_MESSAGE);
        }
    }
}
