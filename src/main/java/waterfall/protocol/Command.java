package waterfall.protocol;

public interface Command {
    public Command convertToCommand(String toConvert);

    public String covertToString(Command command);

    public Command constructCommand(String toConstruct);
}
