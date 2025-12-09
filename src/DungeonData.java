import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonData {

    public static class Room {
        public int id;
        public String name;
        public String type;
        public boolean hasGoldKey = false;
        public List<Treasure> treasures = new ArrayList<>();

        public Room(int id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }

    public static class Treasure {
        public String name;
        public String rarity;
        public int value;

        public Treasure(String name, String rarity, int value) {
            this.name = name;
            this.rarity = rarity;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + " (" + rarity + ", " + value + " gold)";
        }
    }


    public static class Edge {
        public int to;
        public int hpCost;

        public Edge(int to, int hpCost) {
            this.to = to;
            this.hpCost = hpCost;
        }
    }

    public HashMap<Integer, Room> rooms = new HashMap<>();
    public HashMap<String, Integer> nameToId = new HashMap<>();
    public HashMap<Integer, List<Edge>> graph = new HashMap<>();

    public boolean hasGoldKey = false;

    // ---------------------------------------------------------
    // Room Registration
    // ---------------------------------------------------------
    public void addRoom(int id, String name, String type) {
        Room r = new Room(id, name, type);
        rooms.put(id, r);
        nameToId.put(name.toLowerCase(), id);
        graph.put(id, new ArrayList<>());
    }

    public void addEdge(int from, int to, int hpCost) {
        graph.get(from).add(new Edge(to, hpCost));
    }

    // Load Rooms
    public void loadRooms() {
        addRoom(0, "Entrance Hall", "Safe");
        addRoom(1, "Goblin Tunnel", "Monster");
        addRoom(2, "Spider's Nest", "Monster");
        addRoom(3, "Skeleton Crypt", "Monster");
        addRoom(4, "Orc Barracks", "Monster");
        addRoom(5, "Zombie Chamber", "Monster");
        addRoom(6, "Bandit Hideout", "Monster");
        addRoom(7, "Wraith Corridor", "Monster");
        addRoom(8, "Slime Pit", "Monster");
        addRoom(9, "Vampire Lair", "Monster");

        addRoom(10, "Healing Spring", "Checkpoint");
        addRoom(11, "Ancient Shrine", "Checkpoint");
        addRoom(12, "Mystic Fountain", "Checkpoint");
        addRoom(13, "Sacred Temple", "Checkpoint");

        addRoom(14, "Bronze Chest Room", "Treasure");
        addRoom(15, "Silver Cache", "Treasure");
        addRoom(16, "Gold Stash", "Treasure");
        addRoom(17, "Jewel Chamber", "Treasure");
        addRoom(18, "Artifact Alcove", "Treasure");

        addRoom(19, "Goblin King's Throne", "Boss");
        addRoom(20, "Goblin Treasury", "BossVault");

        addRoom(21, "Dragon Lord's Lair", "Boss");
        addRoom(22, "Dragon Hoard", "BossVault");

        addRoom(23, "Lich Emperor's Sanctum", "Boss");
        addRoom(24, "Royal Vault", "BossVault");

        addRoom(25, "Ancient Sanctum", "HiddenVault");
    }

    // Load Edges (Directed, Exact Case Study HP Values)
    public void loadEdges() {

        // Entrance
        addEdge(0, 1, -10);
        addEdge(0, 2, -15);
        addEdge(0, 10, -8);

        // Left Wing (Goblin Path)
        addEdge(1, 14, -12);
        addEdge(1, 3, -15);
        addEdge(14, 19, -20);
        addEdge(3, 4, -18);
        addEdge(4, 19, -25);
        addEdge(19, 20, -35);
        addEdge(20, 25, -10);

        // Center Wing (Dragon Path)
        addEdge(2, 11, -12);
        addEdge(2, 6, -20);
        addEdge(11, 12, +30);
        addEdge(6, 15, -15);
        addEdge(12, 16, -10);
        addEdge(15, 7, -22);
        addEdge(16, 21, -18);
        addEdge(7, 21, -28);
        addEdge(21, 22, -45);
        addEdge(22, 25, -8);

        // Right Wing (Lich Path)
        addEdge(10, 5, +25);
        addEdge(5, 13, -15);
        addEdge(5, 8, -20);
        addEdge(13, 17, +20);
        addEdge(8, 9, -25);
        addEdge(17, 18, -12);
        addEdge(9, 18, -18);
        addEdge(18, 23, -22);
        addEdge(23, 24, -50);
        addEdge(24, 25, -5);

        // Cross Connections
        addEdge(12, 13, -15);
        addEdge(16, 17, -20);
        addEdge(7, 9, -15);
        addEdge(14, 11, -10);
        addEdge(15, 16, -8);
    }

    // Treasures for Each Room
    public void loadTreasures() {
        rooms.get(14).treasures.add(new Treasure("Bronze Coins", "Common", 30));
        rooms.get(15).treasures.add(new Treasure("Silver Coins", "Uncommon", 60));
        rooms.get(16).treasures.add(new Treasure("Gold Bars", "Rare", 120));
        rooms.get(17).treasures.add(new Treasure("Precious Gems", "Rare", 150));
        rooms.get(18).treasures.add(new Treasure("Ancient Artifact", "Epic", 200));
        rooms.get(20).treasures.add(new Treasure("Goblin Crown", "Legendary", 300));
        rooms.get(22).treasures.add(new Treasure("Dragon Relic", "Legendary", 400));
        rooms.get(24).treasures.add(new Treasure("Royal Scepter", "Mythic", 500));
        rooms.get(25).treasures.add(new Treasure("Ancient Crystal", "Mythic", 600));
    }

    // ---------------------------------------------------------
    // Gold Key Placement
    // ---------------------------------------------------------
    public void placeGoldKey() {
        int[] treasureRooms = {14, 15, 16, 17, 18};
        int pick = treasureRooms[(int)(Math.random() * treasureRooms.length)];
        rooms.get(pick).hasGoldKey = true;
        hasGoldKey = true;
    }

    // Initialize Dungeon
    public void initializeDungeon() {
        loadRooms();
        loadEdges();
        loadTreasures();
        placeGoldKey();
    }

    // Display Rooms (Map Viewer)
    public void printMap() {
        System.out.println("=== DUNGEON MAP (Rooms Only) ===");
        for (Room r : rooms.values()) {
            System.out.println(r.id + " - " + r.name + " (" + r.type + ")");
        }
    }

    // Leaderboard + GameData
    public static class LeaderboardEntry {
        public String name;
        public int gold;

        public LeaderboardEntry(String name, int gold) {
            this.name = name;
            this.gold = gold;
        }
    }

    public static class GameData {
        public DungeonData dungeon;
        public ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();

        public GameData(DungeonData dungeon) {
            this.dungeon = dungeon;
        }
    }
}
