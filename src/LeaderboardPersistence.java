import java.io.*;
import java.util.ArrayList;

public class LeaderboardPersistence {
    private static final String FILE_PATH = "leaderboard.dat";

    /**
     * Save leaderboard to file using ObjectOutputStream
     */
    public static void save(ArrayList<DungeonData.LeaderboardEntry> leaderboard) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_PATH))) {
            oos.writeObject(leaderboard);
            System.out.println("Leaderboard saved.");
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    /**
     * Load leaderboard from file using ObjectInputStream
     * Returns empty list if file doesn't exist or on error
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<DungeonData.LeaderboardEntry> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No saved leaderboard found. Starting fresh.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH))) {
            ArrayList<DungeonData.LeaderboardEntry> leaderboard =
                    (ArrayList<DungeonData.LeaderboardEntry>) ois.readObject();
            System.out.println("Loaded " + leaderboard.size() + " leaderboard entries from disk.");
            return leaderboard;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
            System.out.println("Starting with empty leaderboard.");
            return new ArrayList<>();
        }
    }
}
