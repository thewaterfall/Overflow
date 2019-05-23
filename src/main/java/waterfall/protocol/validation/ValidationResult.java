package waterfall.protocol.validation;

import waterfall.protocol.Command;

import java.util.List;

public class ValidationResult {
    private boolean isValid;
    private List<String> errors;
    private Command errorCommand;

    public ValidationResult() {

    }

    public ValidationResult(boolean isValid) {
        this.isValid = isValid;
    }

    public ValidationResult(boolean isValid, List<String> errors, Command errorCommand) {
        this.isValid = isValid;
        this.errors = errors;
        this.errorCommand = errorCommand;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Command getErrorCommand() {
        return errorCommand;
    }

    public void setErrorCommand(Command errorCommand) {
        this.errorCommand = errorCommand;
    }

    public int countErrors() {
        return errors.size();
    }
}
