package src.main.java;

import src.main.java.gui.GameWindow;
import src.main.java.gui.Writer;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Game
{
    public static void main(String[] args) {
        invokeOnEventDispatchThreadAndWait(() -> getGame().gameWindow = new GameWindow());
        getGame().play();
    }

    private static Game game;

    static {
        try {
            game = new Game();
        } catch (InterruptedException e) {

        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Game getGame() {
        return game;
    }

    private Scanner scanner = new Scanner(System.in);
    public Player player;

    public Command command = new Command();

    public Context context;

    GameWindow gameWindow;

    public Game() throws InterruptedException, InvocationTargetException {
        this.context = new NormalContext(null, Text.InitWorld());
        player = new Player();
        player.skills.add("fireBall");
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }


    private void createRooms()
    {
//        src.main.java.Room room1_e, room1_w, room1,room2,room3,room2_w,room2_e,
//        room3_e,room3_w,room4;
//
//        room1 = new src.main.java.Room("DESERT\n" +
//                "This is an area of very dry land where the living conditions seem to be hostile for most plant and animal life.",new src.main.java.Thing("meat"));
//        room1_e = new src.main.java.Room("FOREST\n" +
//                "This is a piece of land where tall trees grow and animal life seems abundant. It is hard to see much further due to the tree density. The canopy is the only thing you see when you look up.",new src.main.java.Monster("frogs",2,10));
//        room1_w = new src.main.java.Room("GRAVEYARD\n" +
//                "This is an eerie place where rows of tombstones stand erect in silence to the left and right, in front and behind, like a sea of the dead. Some of these tombstones are laid on the ground as if something overturned them. They all seem crumbled with the weathering of centuries, and most of them are overgrown and unkempt, for now even their mourners had joined them under the clay soil.",new src.main.java.Monster("fish man",2,13));
//        room2 = new src.main.java.Room("MEADOW\n" +
//                "This is a field vegetated by primarily grass. This open, sunny area seems to attract and support flora and fauna that couldn't thrive in other conditions.");
//        room2_w = new src.main.java.Room("POND\n" +
//                "This is a reasonably small body of standing water in a depression. The air seems fresher here and some wild creatures seem to enjoy this place.",new src.main.java.Monster("zombie",1,9));
//        room2_e =new src.main.java.Room("SAVANNAH\n" +
//                "This is an area on which the trees are sufficiently widely spaced so that the canopy does not close. The open canopy allows sufficient light to reach the ground to support an unbroken herbaceous layer consisting primarily of grasses. You notice that these trees are more regularly spaced than the trees of a forest.",new src.main.java.Monster("长翅鸥",2,8));
//        room3 = new src.main.java.Room("BARREN\n" +
//                "This is an area of tundra. Something here hinders the growth of trees and big vegetation, you suspect it is some sort of curse.",new src.main.java.Monster("zombie",3,18));
//        room3_w = new src.main.java.Room("RIVERSIDE\n" +
//                "The is an area with a flowing river. The river seems to be brimming with fish.",new src.main.java.Monster("zombie",4,16));
//        room3_e = new src.main.java.Room("extra room",new src.main.java.Thing("super drug"));
//        room4=new src.main.java.Room("final room",new src.main.java.Monster("king of the dungeon",10,50));
//
//        room1.setExit("east", room1_e);
//        room1.setExit("north", room2);
//        room1.setExit("west", room1_w);
//        room1_e.setExit("west",room1);
//        room1_w.setExit("east", room1);
//        room1_w.setExit("north", room2_w);
//        room2.setExit("west", room2_w);
//        room2.setExit("east", room2_e);
//        room2.setExit("south", room1);
//        room2_w.setExit("east",room2);
//        room2_e.setExit("west",room2);
//        room2_w.setExit("south",room1_w);
//        room2.setExit("north", room3);
//        room3.setExit("west", room3_w);
//        room3.setExit("east", room3_e);
//        room3.setExit("south", room2);
//        room3_e.setExit("west",room3);
//        room3_w.setExit("east",room3);
//        room3_e.setExit("north",room4);
//
    }


    public void play() {
        printWelcome();
        invokeOnEventDispatchThreadAndWait(new Runnable() {
            @Override
            public void run() {
                gameWindow.startAcceptingCommands();
                System.out.println("Signaled the window to start accepting commands.");
            }
        });
    }

    private static void invokeOnEventDispatchThreadAndWait(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException | InvocationTargetException fatal) {
            System.out.println(fatal);
        }
    }

    private void printWelcome()
    {
        Writer.write("welcome");
        game.context.print();
    }
}