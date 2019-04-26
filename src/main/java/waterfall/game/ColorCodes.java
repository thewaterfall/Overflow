package waterfall.game;

public enum ColorCodes {
    RESET("\033[0m"),

    WHITE_UNDERLINED("\033[4;30m"),
    BLACK_UNDERLINED("\033[4;37m"),

    BLACK_BACKGROUND_BRIGHT("\033[0;100m"),
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");

    private final String code;

    ColorCodes(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
