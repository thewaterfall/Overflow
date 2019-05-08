package waterfall.protocol;

public interface CommandConstants {
    public static final String COMMAND_STATUS_SUCCESS = "success";
    public static final String COMMAND_STATUS_FAILURE = "failure";

    public static final String COMMAND_TYPE_REQUEST = "request";
    public static final String COMMAND_TYPE_RESPONSE = "response";

    public static final String COMMAND_SOURCE_CLIENT = "client";
    public static final String COMMAND_TYPE_HANDLER = "handler";

    public static final String COMMAND_LOGIN = "/login";
    public static final String COMMAND_LOGOUT = "/logout";
    public static final String COMMAND_DISCONNECT = "/disconnect";
    public static final String COMMAND_EXIT = "/exit";
    public static final String COMMAND_PLAY = "/play";
    public static final String COMMAND_CONNECT = "/connect";
    public static final String COMMAND_LEADERBOARD = "/leaderboard";
}
