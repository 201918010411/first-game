package src.main.java;

import src.main.java.exception.CommandNotFoundException;
import src.main.java.exception.EndGameException;
import src.main.java.exception.GameException;
import src.main.java.exception.InvalidArgsException;
import src.main.java.gui.Writer;

import java.util.HashMap;
import java.util.Map;

public class Command {

    public static Map<String, Command> commandMap = new HashMap<>();

    public static void init() {
        commandMap.put("help", new Help());
        commandMap.put("go", new Go());
        commandMap.put("look", new Look());
        commandMap.put("check", new Check());
        commandMap.put("use", new Use());
        commandMap.put("pick", new Pick());
        commandMap.put("fight", new fight());
        commandMap.put("attack", new attack());
        commandMap.put("escape", new escape());
        commandMap.put("quit", new quit());
        commandMap.put("skill", new skill());
    }

    static {
        init();
    }
    public void execute(Game game, String[] commandLine) throws GameException {
        if (!commandMap.containsKey(commandLine[0])) {
            throw new CommandNotFoundException(commandLine[0]);
        }
        if (!game.context.hasCommand(commandLine[0])) {
            throw new GameException(commandLine[0]+" can not use in current context");
        }
        commandMap.get(commandLine[0]).execute(game, commandLine);
    }

    public String getDescription() {
        return "";
    }

}

class Help extends Command {

    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        System.out.println(game.context.commandSet);
    }
}

class Go extends Command {

    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        NormalContext context = (NormalContext) game.context;
        if (commandLine.length < 2) {
            throw new GameException("please indicate a direction");
        }
        context.currentRoom = context.currentRoom.goNext(commandLine[1]);
    }
}

class Look extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        NormalContext context = (NormalContext) game.context;
        context.currentRoom.lookRoom();
    }
}

class Check extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        game.player.checkBag();
        game.player.skills.forEach(Writer::write);
        Writer.write("player's health: " + game.player.gethealth());
    }
}

class Use extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        String name; int count = 1;
        if (commandLine.length < 2) {
            throw new InvalidArgsException("miss item name");
        }
        name = commandLine[1];
        if (commandLine.length >= 3) {
            try {
                count = Integer.parseInt(commandLine[2]);
            } catch (Exception e) {
                throw new InvalidArgsException(e.getMessage());
            }
        }
        game.player.use(name, count);
        Thing thing = Thing.getInstance(name);
        thing.use(game, count);
    }
}

class Pick extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        String name; int count = 1;
        if (commandLine.length < 2) {
            throw new InvalidArgsException("miss item name");
        }
        name = commandLine[1];
        if (commandLine.length >= 3) {
            try {
                count = Integer.parseInt(commandLine[2]);
            } catch (Exception e) {
                throw new InvalidArgsException(e.getMessage());
            }
        }
        NormalContext context = (NormalContext) game.context;
        context.currentRoom.removeThing(name, count);
        game.player.pick(name, count);
    }
}

class fight extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        if (commandLine.length != 2) {
            throw new GameException("invalid fight command, miss the name of the monster");
        }
        String monsterName = commandLine[1];
        NormalContext context = (NormalContext) game.context;
        Room currentRoom = context.currentRoom;
        if (!currentRoom.containsMonster(monsterName)) {
            throw new GameException(monsterName + "is not exist");
        }
        Monster monster = currentRoom.getMonster(monsterName);
        game.context = new battleContext(game.context, monster);
    }
}

class attack extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        battleContext context = (battleContext) game.context;
        Monster monster = context.monster;
        Player player = game.player;
        monster.takeDamage(player.getaggressivity());
        if (!monster.isAlive()) {
            game.context = context.previousContext;
            ((NormalContext) game.context).currentRoom.removeMonster(monster.getName());
            throw new GameException("you win");
        }
        player.takeDamage(monster.getAggressivity());
        if (player.isDead()) {
            throw new EndGameException("you are dead");
        }
    }
}

class skill extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        if (commandLine.length != 2) {
            throw new GameException("invalid skill command, miss the name of the skill");
        }
        String skillName = commandLine[1];
        if (Thing.getInstance(skillName) == null) {
            throw new GameException(skillName + " dose not exist");
        }
        Thing.getInstance(skillName).use(game, 0);
    }
}

class escape extends Command {
    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        if (game.context.previousContext == null) {
            throw new RuntimeException("");
        }
        game.context = game.context.previousContext;
    }
}

class quit extends Command {

    @Override
    public void execute(Game game, String[] commandLine) throws GameException {
        throw new EndGameException("bye bye");
    }
}