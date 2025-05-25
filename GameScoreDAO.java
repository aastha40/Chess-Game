import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GameScoreDAO {
    public void saveGameResult(int gameId, String players, String winnerName) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO game_scores (game_id, players, winner_name) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gameId);
            pstmt.setString(2, players);
            pstmt.setString(3, winnerName);
            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}

