import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class GameScoreDAO {

    public void createTableIfNotExists() throws SQLException, ClassNotFoundException {
        String sql = "CREATE TABLE IF NOT EXISTS game_results (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "players TEXT NOT NULL," +
                     "winner TEXT NOT NULL)";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertGameResult(String players, String winner) {
        String sql = "INSERT INTO game_results (players, winner) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, players);
            pstmt.setString(2, winner);
            pstmt.executeUpdate();
            System.out.println("Game result saved to database.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
