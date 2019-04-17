package waterfall.protocol;

import java.util.List;

public class Command {
    private String type;
    private String status;
    private String source;
    private String fullCommand;
    private String typeCommand;
    private List<String> attributesCommand;
    private String message;

    public Command() {

    }

    public Command(String type, String status, String source, String fullCommand) {
        this.type = type;
        this.status = status;
        this.source = source;
        this.fullCommand = fullCommand;
        this.message = fullCommand;

        splitCommand(fullCommand);
    }

    private void splitCommand(String fullCommand) {
        String[] splittedCommand = fullCommand.split("\\s+");

        typeCommand = splittedCommand[0];
        for (int i = 1; i < splittedCommand.length; i++) {
            this.attributesCommand.add(splittedCommand[i]);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFullCommand() {
        return fullCommand;
    }

    public void setFullCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    public String getTypeCommand() {
        return typeCommand;
    }

    public void setTypeCommand(String typeCommand) {
        this.typeCommand = typeCommand;
    }

    public List<String> getAttributesCommand() {
        return attributesCommand;
    }

    public void setAttributesCommand(List<String> attributesCommand) {
        this.attributesCommand = attributesCommand;
    }

    public void addAttributeCommand(String attr) {
        this.attributesCommand.add(attr);
    }
}
