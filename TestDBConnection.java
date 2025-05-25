public class TestDBConnection {
    public static void main(String[] args) {
        try {
            DBConnection.getConnection();
            System.out.println("Connected to MySQL database successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
