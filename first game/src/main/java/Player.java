package src.main.java;

import src.main.java.exception.EndGameException;
 import src.main.java.exception.GameException;
 import src.main.java.exception.InvalidArgsException;
import src.main.java.gui.Writer;

import java.util.*;

public class Player
{


    public static final int INITIAL_HP = 100;
    public static int MAX_HP = INITIAL_HP;
    public static final int INITIAL_SP = 30;
    public static int NAX_SP = INITIAL_SP;
    public static final int INITIAL_ATK = 10;

    public int hp = INITIAL_HP;
    public int atk =INITIAL_ATK;

    public int sp = INITIAL_SP;
    public Map<String, Integer> bag = new HashMap<>();
    public Set<String> skills = new HashSet<>();
    
    //public void step() {
        //strength -= 10;
    //}
      public void sethealth(int health)
    {
        this.hp = health;
    }

    public void changeHP(int change) throws GameException {
        int oldHP = gethealth();
        sethealth(Math.min(Player.MAX_HP, Math.max(gethealth()+change, 0)));
        if (gethealth() >= oldHP) {
            Writer.write("increased HP " + (gethealth() - oldHP));
        } else {
            Writer.write("decreased HP " + (oldHP - gethealth()));
        }
        Writer.write("current HP: " + gethealth());
        checkAlive();
    }

    private void checkAlive() throws EndGameException {
          if (isDead()) {
              throw new EndGameException("you died");
          }
    }

    public int gethealth(){
        return this.hp;
    }
    
    public int getaggressivity(){
        return this.atk;
    }
    
    public void pick(String name, int count) {
        bag.put(name, bag.getOrDefault(name, 0)+count);
        Writer.write("pick "+"["+name+"]");
    }
    public void use(String name, int count) throws GameException {
        if (!bag.containsKey(name)) {
            throw new InvalidArgsException(name + " is not exist in your bag");
        }
        int exists = bag.get(name);
        if (exists < count) {
            throw new InvalidArgsException("not sufficient " + name);
        } else if (exists == count) {
            bag.remove(name);
        } else {
            bag.put(name, exists - count);
        }
    }
    
    public void checkBag() {
        for (Map.Entry<String, Integer> entry: bag.entrySet()) {
            Writer.write(entry.toString());
        }

    }
    
    public boolean isDead() {
        return hp <= 0;
    }

    public boolean isAlive() {
          return gethealth() >= 0;
    }

    public void takeDamage(int damage) {
        if (damage <= 0) {
            throw new RuntimeException("damage must greater than 0");
        }
        if (gethealth() <= 0) {
            throw new RuntimeException("health must greater than 0");
        }
        Writer.write("player take damage " + damage);
        sethealth(gethealth() - damage);
    }
    
}
