package waterfall.protocol.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import waterfall.injection.Module;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.validation.LoginValidation;
import waterfall.protocol.validation.Validation;

import java.util.*;

// TODO: refactor
public class CommandHandler {
    private static Injector injector;
    private static Map<String, CommandAction> commandActions = new HashMap<>();
    private static Map<String, List<Validation>> commandActionValidations = new HashMap<>();

    static {
        injector = Guice.createInjector(new Module());

        commandActions.put(CommandConstants.COMMAND_LOGIN, injector.getInstance(LoginCommand.class));
        commandActionValidations.put(CommandConstants.COMMAND_LOGIN, Arrays.asList(new LoginValidation()));

        commandActions.put(CommandConstants.COMMAND_LEADERBOARD, injector.getInstance(LeaderboardCommand.class));
        commandActions.put(CommandConstants.COMMAND_PLAY, injector.getInstance(PlayCommand.class));
        commandActions.put(CommandConstants.COMMAND_CONNECT, injector.getInstance(ConnectCommand.class));
        commandActions.put(CommandConstants.COMMAND_MOVE, injector.getInstance(MoveCommand.class));


        commandActions.put(CommandConstants.COMMAND_LOGOUT, injector.getInstance(LogoutCommand.class));
        commandActions.put(CommandConstants.COMMAND_DISCONNECT, injector.getInstance(DisconnectCommand.class));
        commandActions.put(CommandConstants.COMMAND_EXIT, injector.getInstance(ExitCommand.class));
        commandActions.put(CommandConstants.COMMAND_MESSAGE, injector.getInstance(MessageCommand.class));
    }

    public static CommandAction getCommand(String command) {
        CommandAction commandAction = commandActions.get(command);

        if (commandAction != null) {
            return commandAction;
        } else {
            return commandActions.get(CommandConstants.COMMAND_MESSAGE);
        }
    }

    public static List<Validation> getValidations(String command) {
        return commandActionValidations.get(command);
    }
}
