package waterfall.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import waterfall.communication.client.Client;
import waterfall.exception.ClientIsStoppedException;
import waterfall.game.Board;
import waterfall.game.Color;
import waterfall.game.Tile;
import waterfall.injection.Module;

import java.util.Scanner;

import static waterfall.game.ColorCodes.*;

public class ConsoleGUI implements GUI {
    private Client client;
    private Board board;
    private Injector injector;

    char alphaCoordinates[] = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

    private Scanner scanner;

    public ConsoleGUI(String iphost, int port) {
        this.injector = Guice.createInjector(new Module());

        this.client = injector.getInstance(Client.class);
        this.client.configure(iphost, port, this);

        this.scanner = new Scanner(System.in);
    }

    @Override
    public void write(String message) {
        System.out.println(message);
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }

    @Override
    public void update() {
        Tile[][] boardArray = board.getBoardArray();
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("    0    1    2    3    4    5    6    7\n\n");

        for (int i = 0; i < boardArray.length; i++) {
            toReturn.append(alphaCoordinates[i] + "  ");
            for (int j = 0; j < boardArray.length; j++) {
                if (boardArray[i][j].getColor().name().equals("White")) {
                    toReturn.append(WHITE_BACKGROUND_BRIGHT);
                } else {
                    toReturn.append(BLACK_BACKGROUND_BRIGHT);
                }

                if (!boardArray[i][j].getValue().equals("[ ]")) {
                    if (boardArray[i][j].getPiece().getColor().equals(Color.White)) {
                        toReturn.append(WHITE_UNDERLINED + boardArray[i][j].getValue());
                    } else {
                        toReturn.append(BLACK_UNDERLINED + boardArray[i][j].getValue());
                    }
                } else {
                    toReturn.append(boardArray[i][j].getValue());
                }

                toReturn.append(RESET + "  ");
            }
            toReturn.append("\n\n");
        }

        write(toReturn.toString());
    }

    @Override
    public void updateBoard(Board board) {
        this.board = board;
    }

    public Client getClient() {
        return this.client;
    }

    public static void main(String[] args) {
        ConsoleGUI gui = new ConsoleGUI("localhost", 8088);


        gui.getClient().startConnection();

        while (!gui.getClient().isStopped()) {
            String getText = gui.read();

            try {
                gui.getClient().communicate(getText);
            } catch (ClientIsStoppedException e) {
                e.printStackTrace();
                gui.write(e.getMessage());
            }
        }

    }
}
