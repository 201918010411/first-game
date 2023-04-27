package src.main.java;

import src.main.java.gui.Writer;

import java.util.HashSet;
import java.util.Set;

public abstract class Context {

    public Context previousContext;

    public Set<String> commandSet;

    public abstract void print();

    public boolean hasCommand(String commandName) {
        return commandSet.contains(commandName);
    }

    public Context(Context prev, String... commands) {
        previousContext = prev;
        commandSet = new HashSet<>();
        for (String command : commands) {
            commandSet.add(command);
        }
    }
}

class NormalContext extends Context {

    public Room currentRoom;

    @Override
    public void print() {
        Writer.write("you are in " + currentRoom.getDescription());
        currentRoom.printExits();
        currentRoom.printMonsters();
    }

    public NormalContext(Context prev, Room currentRoom) {
        super(prev, "check", "go", "help", "look", "pick", "use", "fight", "quit");
        this.currentRoom = currentRoom;
    }

}

class battleContext extends Context {

    public Monster monster;

    @Override
    public void print() {
        Writer.write("you are fighting with " + monster.getName());
        Writer.write("monster health: " + monster.getHealth());
        Writer.write("monster atk: " + monster.getAggressivity());
        Writer.write( "you can:"+" attack "+" escape"+" skill");
    }

    public battleContext(Context prev, Monster monster) {
        super(prev,"attack", "skill", "escape", "check", "help", "skill");
        this.monster = monster;
    }
}