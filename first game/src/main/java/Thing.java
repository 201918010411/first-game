package src.main.java;

import src.main.java.exception.EndGameException;
import src.main.java.exception.GameException;
import src.main.java.gui.Writer;

import java.util.HashMap;
import java.util.Map;

public abstract class Thing
{
    public static Map<String, Thing> pool = new HashMap<>();

    public static Thing getInstance(String name) throws GameException {
        if (!pool.containsKey(name)) {
            throw new RuntimeException(name + " is not exist");
        }
        return pool.get(name);
    }

    public static void init() {
        pool.put(Text.APPLE, new Apple());
        pool.put(Text.BAD_APPLE, new BadApple());
        pool.put(Text.Black_Fruit, new BlackFruit());
        pool.put(Text.raw_meat, new RawMeat());
        pool.put(Text.king_Fruit, new  kingFruit());
        pool.put(Text.sandwich, new Sandwich());
        pool.put(Text.zombie_meat, new zombiemeat());
        pool.put("fireBall", new FireBall());
    }

    static {
        init();
    }

    private String name;

    public String getDescription() {
        return description;
    }

    private String description;

    public Thing(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public abstract void use(Game game, int count) throws GameException;

}

abstract class Food extends Thing {

    public int getHealing() {
        return healing;
    }

    public void setHealing(int healing) {
        this.healing = healing;
    }

    private int healing;

    Food(String name) {
        super(name);
    }

    @Override
    public void use(Game game, int count) throws GameException {
        game.player.changeHP(getHealing() * count);
    }

}

class Apple extends Food {

    Apple() {
        super(Text.APPLE);
        setHealing(5);
    }
}
class BadApple extends Food {

     BadApple() {
        super(Text.BAD_APPLE);
        setHealing(-5);
    }
}
class BlackFruit extends Food {

    BlackFruit() {
        super(Text.Black_Fruit);
        setHealing(-20);
    }
}

class RawMeat extends Food {

    RawMeat() {
        super(Text.raw_meat);
        setHealing(10);
    }
}
class kingFruit extends Food {

    kingFruit() {
        super(Text.king_Fruit);
        setHealing(100);
    }
}
class Sandwich extends Food {

    Sandwich() {
        super(Text.sandwich);
        setHealing(20);
    }
}
class zombiemeat extends Food {

    zombiemeat() {
        super(Text.zombie_meat);
        setHealing(-30);
    }
}

abstract class Skills extends Thing {

    public int getAttack() {
        return attack;
    }

    public int getCost() {
        return cost;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    private int attack;
    private int cost;

    public Skills(String name, int attack, int cost) {
        super(name);
        setAttack(attack);
        setCost(cost);
    }

}

class FireBall extends Skills {

    public FireBall(String name, int attack, int cost) {
        super(name, attack, cost);
    }

    public FireBall() {
        super("fireBall", 50, 5);
    }

    @Override
    public void use(Game game, int count) throws GameException {
        battleContext context = (battleContext) game.context;
        Skills skills = (Skills) Thing.getInstance(getName());
        if (!game.player.skills.contains(getName())) {
            throw new GameException("you don't have the skill" + getName());
        }
        if (game.player.sp < skills.getCost()) {
            throw new GameException("you don't have enough sp");
        }
        Writer.write("use skill " + getName());
        game.player.sp -= skills.getCost();
        Writer.write("cost sp" + skills.getCost());
        Writer.write("current sp: " + game.player.sp);
        context.monster.takeDamage(skills.getAttack());
        Monster monster = context.monster;
        if (!monster.isAlive()) {
            game.context = context.previousContext;
            ((NormalContext) game.context).currentRoom.removeMonster(monster.getName());
            throw new GameException("you win");
        }
        game.player.takeDamage(monster.getAggressivity());
        if (game.player.isDead()) {
            throw new EndGameException("you are dead");
        }
    }
}