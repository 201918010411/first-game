package src.main.java;

import src.main.java.gui.Writer;

public class Monster
{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAggressivity() {
        return aggressivity;
    }

    public void setAggressivity(int aggressivity) {
        this.aggressivity = aggressivity;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    private String name;
    private int aggressivity;
    private int health;

    public Monster(String name, int aggressivity,int health)
    {
        this.name=name;
        this.aggressivity=aggressivity;
        this.health=health;
    }
   
    public void takeDamage(int damage) {
        if (damage <= 0) {
            throw new RuntimeException("damage must greater than 0");
        }
        if (getHealth() <= 0) {
            throw new RuntimeException("health must greater than 0");
        }
        Writer.write(name + "take damage " + damage);
        setHealth(getHealth() - damage);
    }

    public Monster(String name, int aggressivity) {
        this.name = name;
        this.aggressivity = aggressivity;
        this.health = 100;
    }
    

}
