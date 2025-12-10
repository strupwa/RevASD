import java.util.*;

public class Main {

    public static void main(String[] args) {
        // prepare dungeon & game data
        DungeonData dungeon = new DungeonData();
        dungeon.initializeDungeon();
        DungeonData.GameData game = new DungeonData.GameData(dungeon);
        
        // Load saved leaderboard
        game.leaderboard = LeaderboardPersistence.load();

        Scanner input = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1":
                    printDungeonMap(game);
                    break;
                case "2":
                    GameModes.startMostGoldRace(game);
                    break;
                case "3":
                    showTop5Leaderboard(game);
                    break;
                case "4":
                    GameModes.startImpossibleRoute(game);
                    break;
                case "5":
                    findPathBFSMenu(game, input);
                    break;
                case "6":
                    findPathBellmanFordMenu(game, input);
                    break;
                case "7":
                    searchRoomsMenu(game, input);
                    break;
                case "8":
                    System.out.println("Thanks for playing! Goodbye.");
                    return;
                default:
                    System.out.println("Invalid choice. Enter 1-8.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n========== DUNGEON TREASURE HUNTER ==========");
        System.out.println("1. View Dungeon Map & Room List");
        System.out.println("2. Play: MOST GOLD RACE 🏆");
        System.out.println("3. View Leaderboard (Top 5)");
        System.out.println("4. Play: IMPOSSIBLE ROUTE CHALLENGE ⚔️");
        System.out.println("5. Find Shortest Path (BFS Algorithm)");
        System.out.println("6. Find Optimal Path (Bellman-Ford Algorithm)");
        System.out.println("7. Search for Rooms 🔍");
        System.out.println("8. Exit Game");
        System.out.print("Choose (1-8): ");
    }

    // Menu actions
    private static void printDungeonMap(DungeonData.GameData g) {
        DungeonData d = g.dungeon;
        System.out.println("\n==== DUNGEON MAP & ROOM LIST ====");
        System.out.println("Total Rooms: " + d.rooms.size());
        System.out.println();

        // group by type
        Set<String> typesSet = new LinkedHashSet<>();
        for (DungeonData.Room r : d.rooms.values()) typesSet.add(r.type);
        for (String t : typesSet) {
            System.out.println(">> " + t + " ROOMS:");
            for (int id : d.rooms.keySet()) {
                DungeonData.Room r = d.rooms.get(id);
                if (r.type.equals(t)) {
                    System.out.println("  [" + r.id + "] " + r.name + (r.hasGoldKey ? "  (GOLD KEY)" : ""));
                }
            }
            System.out.println();
        }

        // list edges or connections nya
//        System.out.println("==== ADJACENCY (simple list) ====");
//        for (int id : d.graph.keySet()) {
//            DungeonData.Room r = d.rooms.get(id);
//            System.out.print("[" + id + "] " + r.name + " -> ");
//            List<DungeonData.Edge> edges = d.graph.get(id);
//            if (edges == null || edges.isEmpty()) {
//                System.out.println("none");
//                continue;
//            }
//            List<String> outs = new ArrayList<>();
//            for (DungeonData.Edge e : edges) {
//                String toName = d.rooms.get(e.to).name;
//                outs.add(toName + " (" + e.hpCost + ")");
//            }
//            System.out.println(String.join(", ", outs));
//        }
    }

    private static void showTop5Leaderboard(DungeonData.GameData g) {
        System.out.println("\n==== LEADERBOARD: TOP 5 ====");
        if (g.leaderboard.isEmpty()) {
            System.out.println("No scores yet.");
            return;
        }
        ArrayList<DungeonData.LeaderboardEntry> sorted = Algorithms.sortLeaderboard(g.leaderboard);
        int limit = Math.min(5, sorted.size());
        for (int i = 0; i < limit; i++) {
            DungeonData.LeaderboardEntry e = sorted.get(i);
            System.out.printf("%d. %s = %d Gold%n", i + 1, e.name, e.gold);
        }
    }

    private static void findPathBFSMenu(DungeonData.GameData g, Scanner input) {
        System.out.println("\n-- Find Shortest Path (BFS) --");
        int src = readRoomIdFromUser(g, input, "Enter origin (id or name): ");
        int dst = readRoomIdFromUser(g, input, "Enter destination (id or name): ");
        if (!g.dungeon.rooms.containsKey(src) || !g.dungeon.rooms.containsKey(dst)) {
            System.out.println("One or both rooms not found.");
            return;
        }
        List<Integer> path = Algorithms.bfs(g.dungeon, src, dst);
        printPathAndHealthImpact(g, path);
    }

    private static void findPathBellmanFordMenu(DungeonData.GameData g, Scanner input) {
        System.out.println("\n-- Find Optimal Path (Bellman-Ford) --");
        int src = readRoomIdFromUser(g, input, "Enter origin (id or name): ");
        int dst = readRoomIdFromUser(g, input, "Enter destination (id or name): ");
        if (!g.dungeon.rooms.containsKey(src) || !g.dungeon.rooms.containsKey(dst)) {
            System.out.println("One or both rooms not found.");
            return;
        }
        List<Integer> path = Algorithms.bellmanFord(g.dungeon, src, dst);
        printPathAndHealthImpact(g, path);
    }

    private static void searchRoomsMenu(DungeonData.GameData g, Scanner input) {
        System.out.println("\n-- Search for Rooms --");
        System.out.println("1. Find nearest Checkpoint");
        System.out.println("2. Find nearest Treasure Room");
        System.out.println("3. Find nearest Boss Room");
        System.out.println("4. Find a room with Gold Key");
        System.out.print("Choose: ");
        String c = input.nextLine().trim();
        int start = readRoomIdFromUser(g, input, "Start from (id or name): ");
        List<Integer> path = new ArrayList<>();
        switch (c) {
            case "1":
                path = Algorithms.nearestCheckpoint(g.dungeon, start);
                break;
            case "2":
                path = Algorithms.nearestTreasure(g.dungeon, start);
                break;
            case "3":
                path = Algorithms.nearestBoss(g.dungeon, start);
                break;
            case "4":
                path = Algorithms.nearestKeyRoom(g.dungeon, start);
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        if (path.isEmpty()) {
            System.out.println("No matching room found.");
            return;
        }
        printPathAndHealthImpact(g, path);
    }

    // Helpers
    private static int readRoomIdFromUser(DungeonData.GameData g, Scanner input, String prompt) {
        System.out.print(prompt);
        String line = input.nextLine().trim();
        if (line.isEmpty()) return -1;
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException ex) {
            Integer id = g.dungeon.nameToId.get(line.toLowerCase());
            return id == null ? -1 : id;
        }
    }

    private static void printPathAndHealthImpact(DungeonData.GameData g, List<Integer> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }
        System.out.println("\nPath found (room sequence):");
        for (int id : path) {
            System.out.println("[" + id + "] " + g.dungeon.rooms.get(id).name);
        }
        int net = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            net += Algorithms.getHpChange(g.dungeon, path.get(i), path.get(i + 1));
        }
        System.out.println("Net HP change along this path: " + net);
        int finalHp = 100 + net;
        System.out.println("If starting HP = 100, final HP would be: " + finalHp + (finalHp > 0 ? " (ALIVE)" : " (DEAD)"));
    }
}
