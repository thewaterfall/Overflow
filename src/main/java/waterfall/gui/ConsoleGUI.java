package waterfall.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import waterfall.communication.client.Client;
import waterfall.exception.ClientIsStoppedException;
import waterfall.game.Board;
import waterfall.injection.Module;

import java.util.Scanner;

public class ConsoleGUI implements GUI {
    private Client client;
    private Board board;
    private Injector injector;

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
        write(board.toString());
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
