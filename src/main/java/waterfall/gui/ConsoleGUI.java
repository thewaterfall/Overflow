package waterfall.gui;

import waterfall.communication.client.SocketClient;
import waterfall.exception.ClientIsStoppedException;
import waterfall.game.Board;

import java.util.Scanner;

public class ConsoleGUI implements GUI {
    private SocketClient socketClient;
    private Board board;

    private Scanner scanner;

    public ConsoleGUI(String iphost, int port) {
        this.socketClient = new SocketClient(iphost, port, this);
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
        write(board.toString());
    }

    @Override
    public void updateBoard(Board board) {
        this.board = board;
    }

    public SocketClient getSocketClient() {
        return this.socketClient;
    }

    public static void main(String[] args) {
        ConsoleGUI gui = new ConsoleGUI("localhost", 8088);


        gui.getSocketClient().startConnection();

        while (!gui.getSocketClient().isStopped()) {
            String getText = gui.read();

            try {
                gui.getSocketClient().communicate(getText);
            } catch (ClientIsStoppedException e) {
                e.printStackTrace();
                gui.write(e.getMessage());
            }
        }
    }
}
