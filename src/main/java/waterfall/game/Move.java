package waterfall.game;

public class Move {
    private Coordinates start;
    private Coordinates destination;

    public Move() {

    }

    public Move(Coordinates start, Coordinates destination) {
        this.start = start;
        this.destination = destination;
    }

    public Coordinates getStart() {
        return start;
    }

    public void setStart(Coordinates start) {
        this.start = start;
    }

    public Coordinates getDestination() {
        return destination;
    }

    public void setDestination(Coordinates destination) {
        this.destination = destination;
    }
}
