package waterfall.protocol;

public class Command {
    private String type;
    private String status;
    private String source;
    private String fullCommand;
    private String typeCommand;
    private String message;

    public Command() {

    }

    public Command(String type, String status, String source, String message) {
        this.type = type;
        this.status = status;
        this.source = source;
        this.message = message;
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
}
