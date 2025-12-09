import java.util.*;

public class GameModes {

    // MODE 1: MOST GOLD RACE
    public static void startMostGoldRace(DungeonData.GameData g) {
        Scanner in = new Scanner(System.in);
        DungeonData d = g.dungeon;

        int hp = 100;
        int gold = 0;
        int current = 0; // start dari Entrance hall

        System.out.println("\n=== MOST GOLD RACE ===");
        System.out.println("You start in: " + d.rooms.get(current).name);
        System.out.println("Your HP: " + hp + "\n");

        while (true) { //looping sampe exit

            //print location, HP, and gold sekarang
            System.out.println("You are at: " + d.rooms.get(current).name);
            System.out.println("Current HP: " + hp + " | Current Gold: " + gold);
            List<DungeonData.Edge> outs = d.graph.get(current); //nunjukin semua possible path

            if (outs == null || outs.isEmpty()) { //kalo ga jalan berenti
                System.out.println("  (No outgoing paths from here.)");
                break;
            }

            //print all edge, nama and hpcost
            System.out.println("\nAvailable paths:");
            for (int i = 0; i < outs.size(); i++) {
                DungeonData.Edge e = outs.get(i);
                DungeonData.Room dest = d.rooms.get(e.to);
                System.out.printf("  %d. %s (%+d HP)\n", i + 1, dest.name, e.hpCost);
            }

            //search or quit
            System.out.println("  s. Search (nearest treasure/checkpoint/boss)");
            System.out.println("  q. Quit and save result");
            System.out.print("\nChoose: ");

            String choice = in.nextLine().trim();
            if (choice.equalsIgnoreCase("q")) break;

            if (choice.equalsIgnoreCase("s")) {
                System.out.println("\nSearch:");
                System.out.println("1. Nearest Treasure Room");
                System.out.println("2. Nearest Checkpoint");
                System.out.println("3. Nearest Boss Room");
                System.out.print("Choose: ");
                String s = in.nextLine().trim();
                List<Integer> path = null;

                //manggil bfs sesuai destination
                if (s.equals("1")) path = Algorithms.nearestTreasure(d, current);
                else if (s.equals("2")) path = Algorithms.nearestCheckpoint(d, current);
                else if (s.equals("3")) path = Algorithms.nearestBoss(d, current);
                else {
                    System.out.println("Cancelled.\n");
                    continue;
                }

                if (path == null || path.isEmpty()) {
                    System.out.println("No matching room found.\n");
                    continue;
                }

                // follow path, update hp
                for (int i = 1; i < path.size(); i++) {
                    int from = path.get(i - 1);
                    int to = path.get(i);
                    int change = Algorithms.getHpChange(d, from, to);
                    hp += change;
                    System.out.printf("\nMoving to %s (%+d HP)\n", d.rooms.get(to).name, change);
                    if (hp <= 0) {
                        System.out.println("You died!"); //kalau mati auto save score/gold
                        endRunAndSave(g, gold, in);
                        return;
                    }
                    current = to;
                    gold += collectTreasureAndKey(g, current); //collect gold
                }
                System.out.println("\nArrived. HP: " + hp + " | Gold: " + gold + "\n");
                continue;
            }

            //parsing input kalo pake index
            int index = -1;
            try {
                index = Integer.parseInt(choice) - 1;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input.\n");
                continue;
            }
            if (index < 0 || index >= outs.size()) {
                System.out.println("Invalid option.\n");
                continue;
            }

            DungeonData.Edge move = outs.get(index);
            int next = move.to;
            hp += move.hpCost;
            System.out.printf("\nTraveling to %s (%+d HP)\n", d.rooms.get(next).name, move.hpCost);
            System.out.println("HP now: " + hp);

            if (hp <= 0) {
                System.out.println("\nYou died!");
                endRunAndSave(g, gold, in);
                return;
            }

            //collect gold & key
            current = next;
            gold += collectTreasureAndKey(g, current);
            System.out.println();
        }

        // Normal end
        System.out.println("\n=== RESULT ===");
        System.out.println("Total Gold Collected: " + gold);
        System.out.println("Final HP: " + hp);
        System.out.print("\nEnter your name for leaderboard: ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) name = "Anonymous";
        g.leaderboard.add(new DungeonData.LeaderboardEntry(name, gold));
        System.out.println("Score saved!");
    }

    private static void endRunAndSave(DungeonData.GameData g, int gold, Scanner in) {
        System.out.println("\n=== RUN ENDED ===");
        System.out.println("Gold collected: " + gold);
        System.out.print("Enter your name for leaderboard: ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) name = "Anonymous";
        g.leaderboard.add(new DungeonData.LeaderboardEntry(name, gold));
        System.out.println("Score saved!");
    }

    private static int collectTreasureAndKey(DungeonData.GameData g, int roomId) {
        DungeonData d = g.dungeon;
        DungeonData.Room room = d.rooms.get(roomId);
        int collected = 0;

        if (!room.treasures.isEmpty()) {
            System.out.println("\n💰 TREASURE ROOM DISCOVERED!");
            List<DungeonData.Treasure> sorted = Algorithms.sortTreasures(room.treasures);
            for (DungeonData.Treasure t : sorted) {
                System.out.println("  - " + t);
                collected += t.value;
            }
            room.treasures.clear();
            System.out.println("Collected: " + collected + " gold");
        }

        if (room.hasGoldKey) {
            System.out.println(">>> You found the GOLD KEY!");
            room.hasGoldKey = false;
            d.hasGoldKey = true;
        }

        return collected;
    }

    // MODE 2: IMPOSSIBLE ROUTE CHALLENGE
    private static final int START = 0;
    private static final int DEST  = 22; // Dragon Hoard

    public static void startImpossibleRoute(DungeonData.GameData g) {
        Scanner in = new Scanner(System.in);
        DungeonData d = g.dungeon;

        System.out.println("\n=== IMPOSSIBLE ROUTE CHALLENGE (Step-by-Step Mode) ===");
        System.out.println("Goal: Reach the Dragon Hoard with 100 HP.");
        System.out.println("There is NO gold collection in this mode.\n");

        int current = START;
        int hp = 100;
        List<Integer> pathTaken = new ArrayList<>();
        pathTaken.add(current);

        while (true) {
            System.out.println("\nYou are at: " + d.rooms.get(current).name);
            System.out.println("Current HP: " + hp);

            if (current == DEST) {
                System.out.println("\n🎉 You reached the Dragon Hoard!");
                break;
            }

            List<DungeonData.Edge> options = d.graph.get(current);
            if (options == null || options.isEmpty()) {
                System.out.println("\nNo outgoing paths. You are stuck!");
                break;
            }

            System.out.println("\nAvailable paths:");
            for (int i = 0; i < options.size(); i++) {
                DungeonData.Edge e = options.get(i);
                System.out.printf("  %d. %s (%+d HP)\n", i + 1, d.rooms.get(e.to).name, e.hpCost);
            }
            System.out.println("  s. Search (nearest special room)");
            System.out.println("  q. Quit challenge");
            System.out.print("\nChoose (number / s / q): ");

            String choice = in.nextLine().trim();
            if (choice.equalsIgnoreCase("q")) {
                System.out.println("\nYou quit the challenge.");
                break;
            }

            if (choice.equalsIgnoreCase("s")) {
                runSearchMode(d, current, pathTaken);
                current = pathTaken.get(pathTaken.size() - 1);
                hp = recalcHP(d, pathTaken);
                continue;
            }

            int index;
            try {
                index = Integer.parseInt(choice) - 1;
            } catch (Exception e) {
                System.out.println("Invalid choice.");
                continue;
            }

            if (index < 0 || index >= options.size()) {
                System.out.println("Invalid option.");
                continue;
            }

            DungeonData.Edge move = options.get(index);
            int nextRoom = move.to;
            hp += move.hpCost;
            System.out.println("\nTraveling to: " + d.rooms.get(nextRoom).name);
            System.out.println("HP change: " + move.hpCost);
            System.out.println("HP now: " + hp);

            pathTaken.add(nextRoom);
            current = nextRoom;

            if (hp <= 0) {
                System.out.println("\n💀 You died during the run.");
                break;
            }
        }

        printPlayerResult(d, pathTaken, hp);
        compareWithAlgorithms(d, pathTaken, hp);
    }

    private static void runSearchMode(DungeonData d, int current, List<Integer> pathTaken) {
        Scanner in = new Scanner(System.in);
        System.out.println("\nSearch options:");
        System.out.println("  1. Nearest Checkpoint");
        System.out.println("  2. Nearest Treasure Room");
        System.out.println("  3. Nearest Boss Room");
        System.out.print("Choose: ");
        String s = in.nextLine().trim();
        List<Integer> path = null;
        if (s.equals("1")) path = Algorithms.nearestCheckpoint(d, current);
        else if (s.equals("2")) path = Algorithms.nearestTreasure(d, current);
        else if (s.equals("3")) path = Algorithms.nearestBoss(d, current);
        else {
            System.out.println("Cancelled.");
            return;
        }

        if (path == null || path.isEmpty()) {
            System.out.println("No matching room found.");
            return;
        }

        System.out.println("\nFollowing path:");
        for (int i = 1; i < path.size(); i++) {
            int from = path.get(i - 1);
            int to = path.get(i);
            int hpChange = Algorithms.getHpChange(d, from, to);
            System.out.printf(" -> %s (%+d HP)\n", d.rooms.get(to).name, hpChange);
            pathTaken.add(to);
        }
    }

    private static int recalcHP(DungeonData d, List<Integer> path) {
        int hp = 100;
        for (int i = 0; i < path.size() - 1; i++) {
            hp += Algorithms.getHpChange(d, path.get(i), path.get(i + 1));
        }
        return hp;
    }

    private static void printPlayerResult(DungeonData d, List<Integer> path, int hp) {
        System.out.println("\n=== PLAYER RESULT ===");
        System.out.println("Path taken:");
        for (int id : path) {
            System.out.println(" -> " + id + ": " + d.rooms.get(id).name);
        }
        System.out.println("Final HP: " + hp);
    }

    private static void compareWithAlgorithms(DungeonData d, List<Integer> playerPath, int playerHp) {
        System.out.println("\n=== ALGORITHM COMPARISON ===");
        List<Integer> bfsPath = Algorithms.bfs(d, START, DEST);
        printAlgo("BFS (fewest rooms)", d, bfsPath);

        List<Integer> bfPath = Algorithms.bellmanFord(d, START, DEST);
        printAlgo("Bellman-Ford (minimum HP loss)", d, bfPath);

        System.out.println("\nYour result:");
        System.out.println("Rooms visited: " + playerPath.size());
        System.out.println("Final HP: " + playerHp);
    }

    private static void printAlgo(String title, DungeonData d, List<Integer> path) {
        System.out.println("\n" + title + ":");
        if (path == null || path.isEmpty()) {
            System.out.println("  No path found.");
            return;
        }
        int netHP = 0;
        System.out.println("  Path:");
        for (int i = 0; i < path.size(); i++) {
            int id = path.get(i);
            System.out.println("   -> " + id + ": " + d.rooms.get(id).name);
            if (i < path.size() - 1) {
                netHP += Algorithms.getHpChange(d, path.get(i), path.get(i + 1));
            }
        }
        System.out.println("  Net HP change: " + netHP);
        System.out.println("  Final HP (start=100): " + (100 + netHP));
    }
}
