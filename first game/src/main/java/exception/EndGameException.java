package src.main.java.exception;

public class EndGameException extends GameException {
    public EndGameException() {
        super("Thank you for playing.  Good bye.");
    }
    public EndGameException(String msg) {
        super(msg);
    }
}