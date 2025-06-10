CREATE TABLE games (
  game_id INT AUTO_INCREMENT PRIMARY KEY,
  player1_name VARCHAR(50),
  player2_name VARCHAR(50),
  winner_name VARCHAR(50),
  game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
