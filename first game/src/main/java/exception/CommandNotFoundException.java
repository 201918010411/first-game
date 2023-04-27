package src.main.java.exception;

public class CommandNotFoundException extends GameException{
    public CommandNotFoundException(String command) {
        super(command + " is not a valid command");
    }
}
