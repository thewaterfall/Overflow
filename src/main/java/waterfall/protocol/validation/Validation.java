package waterfall.protocol.validation;

import java.util.List;
import waterfall.protocol.Command;

public interface Validation {
    public List<String> validate(Command command);
}
