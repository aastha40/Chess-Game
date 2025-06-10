♟ Java Console-Based Chess Game

This is a simple console-based chess game in Java that supports basic movement rules for each piece and checks for checkmate. The game records results to a MySQL database with player names and the winner.



🎮 Features

✅ Two-player chess game (e.g., Aastha vs Disha, Arnav vs Swati)
✅ Basic chess rules: Pawn, Rook, Bishop, Queen, Knight, King movement
✅ Turn-based play with valid move checks
✅ Checkmate detection added – game ends when a king is checkmated
✅ Game results saved in MySQL: game number, both players, and winner



📁 Project Structure

ChessGame/
├── DBConnection.java        # Handles MySQL DB connection
├── GameScoreDAO.java        # Inserts and retrieves game scores
├── TestDBConnection.java    # Tests the DB connection
├── GameCode.java            # Main game logic (console-based)
├── chess_game.sql           # SQL file to create the required table
└── README.md                # This file


---

🛠️ How to Run

1. Compile all Java files:

javac *.java


2. Run the main game:

java GameCode


3. Gameplay Instructions:

Enter names for Player 1 (White) and Player 2 (Black).

Play by entering moves like:

e2 e4
b8 c6

Type exit at any time to end the game manually.

The game ends automatically when one player's king is checkmated.




🔑 Notes

Implements basic piece movement rules only.

New: Checkmate detection is implemented.

Support advanced rules (en passant, castling, promotion, etc.)

Make sure the MySQL JDBC driver is in your classpath:

Example: -cp .:mysql-connector-java-8.0.xx.jar



---

Let me know if you’d like to generate or update the SQL file or GameScoreDAO.java to support additional queries.
