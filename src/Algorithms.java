import java.util.*;

public class Algorithms {

    // BFS — shortest path by number of rooms
    public static List<Integer> bfs(DungeonData dungeon, int start, int dest) {
        //kalo start = finish output: sama
        if (start == dest) return Collections.singletonList(start);

        int n = dungeon.rooms.size(); // num of room
        boolean[] visited = new boolean[n]; //track yg udh
        int[] parent = new int[n]; //store previous room
        Arrays.fill(parent, -1); //set initial parent

        //queue baru
        Queue<Integer> q = new LinkedList<>();
        visited[start] = true; //marked visited
        q.offer(start);

        //looping bfs
        while (!q.isEmpty()) {
            int cur = q.poll();
            if (cur == dest) break;

            List<DungeonData.Edge> outs = dungeon.graph.get(cur);
            if (outs == null) continue;

            for (DungeonData.Edge e : outs) {
                int nxt = e.to;
                // kalo blom disamper, store parent and push
                if (!visited[nxt]) { // if dest ga disamper = return empty list
                    visited[nxt] = true;
                    parent[nxt] = cur;
                    q.offer(nxt);
                }
            }
        }

        //no path = empty list
        if (!visited[dest]) return new ArrayList<>();

        // start from dest trus nanti outputnya return dari addfirst biar urutannya bener
        LinkedList<Integer> path = new LinkedList<>();
        int cur = dest;
        while (cur != -1) {
            path.addFirst(cur);
            cur = parent[cur];
        }
        return path;
    }


    // BELLMAN–FORD — maximize HP
    public static List<Integer> bellmanFord(DungeonData dungeon, int start, int dest) {
        int n = dungeon.rooms.size();
        int[] cost = new int[n]; //damage kena brp
        int[] parent = new int[n]; //save path

        Arrays.fill(cost, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        cost[start] = 0;

        // Relax edges up to n - 1 times = rumus bellford
        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;

            //loop all rooms yg ada edge
            for (int u : dungeon.graph.keySet()) {
                List<DungeonData.Edge> outs = dungeon.graph.get(u);
                if (outs == null) continue;

                for (DungeonData.Edge e : outs) {
                    int v = e.to; //tujuan edge
                    int w = -e.hpCost; // minimize weight edge nya

                    //update kalo ada cost lebih kecil
                    if (cost[u] != Integer.MAX_VALUE && cost[u] + w < cost[v]) {
                        cost[v] = cost[u] + w;
                        parent[v] = u;
                        updated = true; //note updated
                    }
                }
            }
            if (!updated) break;
        }

        if (cost[dest] == Integer.MAX_VALUE) return new ArrayList<>();

        LinkedList<Integer> path = new LinkedList<>();
        int cur = dest;
        while (cur != -1) {
            path.addFirst(cur);
            cur = parent[cur];
        }
        return path;
    }

    // HP lookup
    public static int getHpChange(DungeonData dungeon, int from, int to) {
        //ngambil hpcost nya
        List<DungeonData.Edge> outs = dungeon.graph.get(from);
        if (outs == null) return 0;  //kalo gaada edge

        //cari edge yg tujuannya sama
        for (DungeonData.Edge e : outs) {
            if (e.to == to) return e.hpCost; //return hpcost
        }
        return 0; //kalo ndak ada
    }

    // BFS nearest search helper
    private static List<Integer> bfsFindNearestByPredicate(
            DungeonData dungeon,
            int start,
            java.util.function.Predicate<DungeonData.Room> predicate) {

        int n = dungeon.rooms.size();
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Queue<Integer> q = new LinkedList<>();
        if (start < 0 || start >= n) return new ArrayList<>();

        visited[start] = true;
        q.offer(start);

        while (!q.isEmpty()) {
            int cur = q.poll();

            DungeonData.Room r = dungeon.rooms.get(cur);
            if (r != null && predicate.test(r)) {
                LinkedList<Integer> path = new LinkedList<>();
                int x = cur;
                while (x != -1) {
                    path.addFirst(x);
                    x = parent[x];
                }
                return path;
            }

            List<DungeonData.Edge> outs = dungeon.graph.get(cur);
            if (outs == null) continue;

            for (DungeonData.Edge e : outs) {
                int nxt = e.to;
                if (!visited[nxt]) {
                    visited[nxt] = true;
                    parent[nxt] = cur;
                    q.offer(nxt);
                }
            }
        }
        return new ArrayList<>();
    }

    public static List<Integer> nearestCheckpoint(DungeonData d, int start) {
        return bfsFindNearestByPredicate(d, start, r -> "Checkpoint".equals(r.type));
    }

    public static List<Integer> nearestTreasure(DungeonData d, int start) {
        return bfsFindNearestByPredicate(d, start,
                r -> "Treasure".equals(r.type) || "BossVault".equals(r.type));
    }

    public static List<Integer> nearestBoss(DungeonData d, int start) {
        return bfsFindNearestByPredicate(d, start, r -> "Boss".equals(r.type));
    }

    public static List<Integer> nearestKeyRoom(DungeonData d, int start) {
        return bfsFindNearestByPredicate(d, start, r -> r.hasGoldKey);
    }

    // Merge Sort utilities
    public static <T> List<T> mergeSort(List<T> list, Comparator<T> cmp) {
        if (list == null || list.size() <= 1) return new ArrayList<>(list);
        //kalo kosong/cmn 1 udh urut

        int mid = list.size() / 2;
        List<T> left = mergeSort(list.subList(0, mid), cmp);
        List<T> right = mergeSort(list.subList(mid, list.size()), cmp);
        return merge(left, right, cmp);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<T> cmp) {
        ArrayList<T> res = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) >= 0)
                res.add(left.get(i++));
            else
                res.add(right.get(j++));
        }
        //merge kiri kanan
        while (i < left.size()) res.add(left.get(i++));
        while (j < right.size()) res.add(right.get(j++));

        return res;
    }

    public static List<DungeonData.Treasure> sortTreasures(List<DungeonData.Treasure> treasures) {
        return mergeSort(treasures, Comparator.comparingInt(t -> t.value));
    }

    public static ArrayList<DungeonData.LeaderboardEntry> sortLeaderboard(
            ArrayList<DungeonData.LeaderboardEntry> list) {

        List<DungeonData.LeaderboardEntry> sorted =
                mergeSort(list, Comparator.comparingInt(e -> e.gold));
        return new ArrayList<>(sorted);
    }
}
