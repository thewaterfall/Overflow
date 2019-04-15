package waterfall.protocol;

import waterfall.exception.IllegalCommandException;

public interface CommandUtil {
    public Command convertToCommand(String toConvert);

    public String covertToString(Command command);

    public Command constructCommand(String toConstruct, String commandType, String from, String status) throws IllegalCommandException;
}
