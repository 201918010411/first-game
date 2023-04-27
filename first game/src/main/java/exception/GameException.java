package src.main.java.exception;

public class GameException extends Exception{
    String message = "";

    public GameException(String msg) {
        message = msg;
    }

    public String toString() {
        return message;
    }
}
