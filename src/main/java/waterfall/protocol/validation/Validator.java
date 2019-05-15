package waterfall.protocol.validation;


import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import waterfall.protocol.Command;
import waterfall.protocol.CommandConstants;
import waterfall.protocol.CommandUtil;

public class Validator {

    @Inject
    private CommandUtil commandUtil;

    public ValidationResult validate(Command command, List<Validation> validations) {
        Command errorResponse = null;

        List<String> errors = new ArrayList<>();

        for(Validation validation: validations) {
            errors.addAll(validation.validate(command));
        }

        if(!errors.isEmpty()) {
            errorResponse = commandUtil.constructCommand(
                    CommandConstants.COMMAND_LOGIN,
                    CommandConstants.COMMAND_TYPE_RESPONSE,
                    CommandConstants.COMMAND_TYPE_HANDLER,
                    CommandConstants.COMMAND_STATUS_FAILURE
            );

            for(String error: errors) {
                errorResponse.setMessage(errorResponse.getMessage() + error + "\n");
            }

            return new ValidationResult(false, errors, errorResponse);
        }

        return new ValidationResult(true);
    }
}
