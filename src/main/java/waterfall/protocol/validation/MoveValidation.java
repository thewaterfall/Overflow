package waterfall.protocol.validation;

import waterfall.protocol.Command;

import java.util.ArrayList;
import java.util.List;

public class MoveValidation implements Validation {

    @Override
    public List<String> validate(Command command) {
        List<String> errors = new ArrayList<>();

        if(command.getAttributesCommand().size() != 2) {
            errors.add("You should specify both [from] tile and [to] tile");
        }

        return errors;
    }
}
