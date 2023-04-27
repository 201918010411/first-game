package src.main.java; /**
 * Class src.main.java.Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "src.main.java.Room" represents one location in the scenery of the game.  It is
 * connected to other rooms via exits.  The exits are labelled north, 
 * east, south, west.  For each direction, the room stores a reference
 * to the neighboring room, or null if there is no exit in that direction.
 * 
 * @author  Michael K?lling and David J. Barnes
 * @version 2016.02.29
 */
import src.main.java.exception.GameException;
import src.main.java.exception.InvalidArgsException;
import src.main.java.gui.Writer;

import java.util.*;

public class Room 
{
    private String description;
    private Map<String, Room> exits = new HashMap<>();
    private Map<String, Integer> things = new HashMap<>();
    private Map<String, Monster> monsters = new HashMap<>();

    public Room(String description) 
    {
        this.description = description;
    }

    public void setExit(String direction, Room room) {
        exits.put(direction, room);
    }

    public boolean hasDirection(String direction) {
        return exits.getOrDefault(direction, null) != null;
    }

    public void addThing(String thing) {
        this.things.put(thing, this.things.getOrDefault(thing, 0)+1);
    }

    public String getDescription()
    {
        return description;
    }

    public void checkDirection(String direction) throws InvalidArgsException {
        if (!hasDirection(direction)) {
            throw new InvalidArgsException(direction + " dose not exist");
        }
    }
    
    public void printExits() {
        Writer.write("Exits: ");
        exits.keySet().stream().forEach(key ->  Writer.write(key + " "));

    }
    
    public Room goNext(String direction) throws GameException {
        checkDirection(direction);
        Room room = exits.get(direction);
        return room;
    }
    
    public boolean containsThing(String name) {
        if  (things.containsKey(name)) {
            if (things.get(name) == 0) {
                throw new RuntimeException("exist zero count item");
            } else if (things.get(name) > 0) {
                return true;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean containsMonster(String name) {
        return monsters.containsKey(name);
    }
    
    public void lookRoom()
    {
        Writer.write(things.toString());
    }
    
    public void removeThing(String thing, int count) throws GameException {
        if (!containsThing(thing)) {
            throw new InvalidArgsException("this item is not exist");
        }
        if (things.get(thing) < count) {
            throw new InvalidArgsException("insufficient things");
        } else if (things.get(thing) == count) {
            things.remove(thing);
        } else {
            things.put(thing, things.get(thing)-count);
        }
    }

    public void removeMonster(String name){
        monsters.remove(name);
    }

    public Monster getMonster(String name) {
        return monsters.get(name);
    }

    public void addMonster(Monster monster) {
        monsters.put(monster.getName(), monster);
    }

    public void printMonsters() {
        Writer.write("Monsters: ");
        monsters.keySet().stream().forEach(key ->  Writer.write(key + " "));

    }
}