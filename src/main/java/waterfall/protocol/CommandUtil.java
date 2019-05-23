package waterfall.protocol;

public interface CommandUtil {
    public Command convertToCommand(String toConvert);

    public String covertToString(Command command);

    public Command constructCommand(String toConstruct, String commandType, String from, String status);
}
