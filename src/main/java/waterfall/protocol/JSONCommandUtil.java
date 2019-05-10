package waterfall.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSONCommandUtil implements CommandUtil {
    private ObjectMapper objectMapper;

    public JSONCommandUtil() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enableDefaultTyping(
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
    }

    @Override
    public Command convertToCommand(String toConvert) {
        Command command = null;
        try {
            command = objectMapper.readValue(toConvert, Command.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return command;
    }

    @Override
    public String covertToString(Command command) {
        String stringCommand = null;
        try {
            stringCommand = objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return stringCommand;
    }

    @Override
    public Command constructCommand(String toConstruct, String commandType, String from, String status) {
        Command command = new Command(commandType, status, from, toConstruct);
        command.setFullCommand(toConstruct);

        return command;
    }
}
