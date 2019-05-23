package waterfall.protocol.validation;

import java.util.ArrayList;
import java.util.List;
import waterfall.protocol.Command;

public class LoginValidation implements Validation {


    @Override
    public List<String> validate(Command command) {
       List<String> errors = new ArrayList<>();

       if(command.getAttributesCommand().size() != 2) {
           errors.add("/login command should have both [username] and [password]");
       }

       return errors;
    }
}
