Java Console-Based Chess Game
This is a simple console-based chess game in Java with basic movement rules for each piece. The game allows two players to enter their names, play against each other, and saves the winner's name and both players' names to a SQL database.



Features:

*Two-player chess game.
*Basic rules: pawn, rook, bishop, queen, knight, king movement
*Valid move checks, turn-based play
*Winner is declared when the opponent's king is captured
*Game results (players, winner, game number) are saved to SQLit



Project Structure:

ChessGame/
├── DBConnection.java       # Handles SQLite DB connection
├── GameScoreDAO.java       # Inserts and retrieves game scores
├── TestDBConnection.java   # Tests the DB connection
├── GameCode.java           # Main game logic (console-based)
└── chess_game.db           # SQLite database file (created automatically)



How to Run:

*Compile the Java files:
javac *.java
*Run the main game:
java GameCode
*Enter player names and play by entering moves like e2 e4.
*Type exit to end the game manually.




Notes:
*Only basic chess rules are implemented (no check/checkmate).
*You must manually place the JDBC driver in your classpath if needed.

