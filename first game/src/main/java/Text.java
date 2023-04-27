package src.main.java;

import java.util.Random;

public abstract class Text {
    public static String[] descriptions = new String[]{
            "a piece of land where tall trees grow and animal life seems abundant. It is hard to see much further due to the tree density. The canopy is the only thing you see when you look up.",
            "an area of very dry land where the living conditions seem to be hostile for most plant and animal life.",
            "an eerie place where rows of tombstones stand erect in silence to the left and right, in front and behind, like a sea of the dead. Some of these tombstones are laid on the ground as if something overturned them. They all seem crumbled with the weathering of centuries, and most of them are overgrown and unkempt, for now even their mourners had joined them under the clay soil.",
            "a reasonably small body of standing water in a depression. The air seems fresher here and some wild creatures seem to enjoy this place.",
            "an area on which the trees are sufficiently widely spaced so that the canopy does not close. The open canopy allows sufficient light to reach the ground to support an unbroken herbaceous layer consisting primarily of grasses. You notice that these trees are more regularly spaced than the trees of a forest.",
            "an area of tundra. Something here hinders the growth of trees and big vegetation, you suspect it is some sort of curse.",
            "an area with a flowing river. The river seems to be brimming with fish.",
    };

    public static final String APPLE = "apple";
    public static final String BAD_APPLE = "badApple";
    public static final String Black_Fruit = "blackFruit";
    public static final String raw_meat = "rawMeat";
    public static final String king_Fruit = "King!!!fruit";
    public static final String sandwich = "sandwich";

    public static final String zombie_meat = "zombiemeat";

    public static String[] things = new String[]{
            APPLE, BAD_APPLE, Black_Fruit,raw_meat, king_Fruit,sandwich, zombie_meat
    };

    private static final int maxThingCount = 10;

    private static final int MAX_PATH = 100;

    private static int currentSize = 0;

    private static final Random random = new Random();

    public static Room InitWorld() {
        Room currentRoom = generateSingleRoom();
        expandWorld(currentRoom);
        return currentRoom;
    }

    public static void expandWorld(Room currentRoom) {
        if (currentSize >= MAX_PATH) {
            return;
        }
        if (!currentRoom.hasDirection("east")) {
            if (random.nextInt(2) == 0) {
                Room room = generateSingleRoom();
                room.setExit("west", currentRoom);
                currentRoom.setExit("east", room);
                expandWorld(room);
            }
        }
        if (!currentRoom.hasDirection("west")) {
            if (random.nextInt(2) == 0) {
                Room room = generateSingleRoom();
                room.setExit("east", currentRoom);
                currentRoom.setExit("west", room);
                expandWorld(room);
            }
        }
        if (!currentRoom.hasDirection("north")) {
            if (random.nextInt(2) == 0) {
                Room room = generateSingleRoom();
                room.setExit("south", currentRoom);
                currentRoom.setExit("north", room);
                expandWorld(room);
            }
        }
        if (!currentRoom.hasDirection("south")) {
            if (random.nextInt(2) == 0) {
                Room room = generateSingleRoom();
                room.setExit("north", currentRoom);
                currentRoom.setExit("south", room);
                expandWorld(room);
            }
        }
    }

    public static Room generateSingleRoom() {
        currentSize++;
        Room room = new Room(descriptions[random.nextInt(descriptions.length)]);
        for (int i = 0; i < maxThingCount; i++) {
            if (random.nextBoolean()) {
                break;
            }
            room.addThing(things[random.nextInt(things.length)]);
            room.addMonster(new Monster("testMonster", 5));
        }
        return room;
    }
}
