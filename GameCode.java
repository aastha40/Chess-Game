import java.util.Scanner;

public class GameCode {
    private static String[][] board = new String[8][8]; // 8x8 chess board
    private static Scanner scanner = new Scanner(System.in);
    private static boolean isWhiteTurn = true; // true for White's turn, false for Black
    private static String whitePlayer; // Player 1
    private static String blackPlayer; // Player 2
    private static int gameNumber = 1;
    private static boolean[][] hasMoved = new boolean[8][8]; // Track if pieces have moved (for castling)
    private static String enPassantPawn = null; // Track en passant pawn position

    public static void main(String[] args) {
        // Ask for player names
        System.out.print("Enter name for White player: ");
        whitePlayer = scanner.nextLine();
        System.out.print("Enter name for Black player: ");
        blackPlayer = scanner.nextLine();

        initializeBoard();
        printBoard();

        // Game loop
        while (true) {
            System.out.println((isWhiteTurn ? whitePlayer : blackPlayer) + "'s move (e.g., e2 e4) or 'exit':");
            String move = scanner.nextLine().trim();

            if (move.equalsIgnoreCase("exit")) {
                System.out.println("Game exited.");
                break;
            }

            if (!isValidFormat(move)) {
                System.out.println("Invalid format! Use format like 'e2 e4'.");
                continue;
            }

            int[] coords = parseMove(move);
            if (coords == null) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            int startX = coords[0], startY = coords[1], endX = coords[2], endY = coords[3];
            String piece = board[startX][startY];

            if (piece.equals(".")) {
                System.out.println("No piece at starting position!");
                continue;
            }

            // Ensure the correct player moves their piece
            if ((isWhiteTurn && !isWhitePiece(piece)) || (!isWhiteTurn && isWhitePiece(piece))) {
                System.out.println("It's " + (isWhiteTurn ? whitePlayer : blackPlayer) + "'s turn!");
                continue;
            }

            String target = board[endX][endY];
            // Prevent capturing own pieces
            if (!target.equals(".") && isWhitePiece(target) == isWhitePiece(piece)) {
                System.out.println("Cannot capture your own piece!");
                continue;
            }

            // Check if move is valid based on piece rules
            if (!isValidMove(startX, startY, endX, endY, piece)) {
                System.out.println("Invalid move for " + piece);
                continue;
            }

            // Handle En Passant
            if (piece.equals("♙") || piece.equals("♟")) {
                if (isEnPassant(startX, startY, endX, endY, piece)) {
                    // Perform en passant
                    board[endX][endY] = piece;
                    board[startX][startY] = ".";
                    board[startX][endY] = ".";
                }
            }

            // Handle Castling
            if (piece.equals("♔") || piece.equals("♚")) {
                if (isCastling(startX, startY, endX, endY, piece)) {
                    // Perform castling
                    int rookX = (startY == 0) ? startX : endX;
                    int rookY = (startY == 0) ? 0 : 7;
                    board[endX][endY] = piece;
                    board[startX][startY] = ".";
                    board[rookX][rookY] = ".";
                    board[endX][endY == 2 ? 3 : 5] = (piece.equals("♔")) ? "♖" : "♖";
                }
            }

            // Handle Pawn Promotion
            if ((piece.equals("♙") && endX == 0) || (piece.equals("♟") && endX == 7)) {
                System.out.print("Promote your pawn to (Q/R/B/N): ");
                String promotionChoice = scanner.nextLine().toUpperCase();
                board[endX][endY] = promotionChoice.equals("Q") ? "♕" : 
                                     promotionChoice.equals("R") ? "♖" : 
                                     promotionChoice.equals("B") ? "♗" : "♘";
            } else {
                // Perform the regular move
                board[endX][endY] = piece;
                board[startX][startY] = ".";
            }

            printBoard();

            // Check for game win (king captured)
            if (target.equals("♚")) {
                System.out.println(whitePlayer + " wins! (Black king captured)");
                new GameScoreDAO().saveGameResult(gameNumber++, whitePlayer + " vs " + blackPlayer, whitePlayer);
                break;
            } else if (target.equals("♔")) {
                System.out.println(blackPlayer + " wins! (White king captured)");
                new GameScoreDAO().saveGameResult(gameNumber++, whitePlayer + " vs " + blackPlayer, blackPlayer);
                break;
            }

            // Change turn
            isWhiteTurn = !isWhiteTurn;
        }
    }

    // Initialize board with pieces in their default positions
    private static void initializeBoard() {
        String[] setup = {
            "♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖",
            "♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙",
            ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".",
            "♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟",
            "♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"
        };

        int idx = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = setup[idx++];
    }

    // Print the current board state
    private static void printBoard() {
        System.out.println("\n    a   b   c   d   e   f   g   h");
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + "   ");
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + "   ");
            }
            System.out.println();
        }
        System.out.println("    a   b   c   d   e   f   g   h\n");
    }

    // Validate format like "e2 e4"
    private static boolean isValidFormat(String move) {
        return move.matches("[a-h][1-8] [a-h][1-8]");
    }

    // Convert move input to board coordinates
    private static int[] parseMove(String move) {
        try {
            String[] parts = move.split(" ");
            int startY = parts[0].charAt(0) - 'a';
            int startX = 8 - Character.getNumericValue(parts[0].charAt(1));
            int endY = parts[1].charAt(0) - 'a';
            int endX = 8 - Character.getNumericValue(parts[1].charAt(1));
            return new int[]{startX, startY, endX, endY};
        } catch (Exception e) {
            return null;
        }
    }

    // Check if piece is a white piece
    private static boolean isWhitePiece(String piece) {
        return "♙♖♘♗♕♔".contains(piece);
    }

    // Validate moves for each piece
    private static boolean isValidMove(int startX, int startY, int endX, int endY, String piece) {
        int dx = endX - startX;
        int dy = endY - startY;

        switch (piece) {
            case "♙": // White pawn
                if (dy == 0 && dx == -1 && board[endX][endY].equals(".")) return true;
                if (dy == 0 && dx == -2 && startX == 6 && board[5][startY].equals(".") && board[endX][endY].equals(".")) return true;
                if (Math.abs(dy) == 1 && dx == -1 && !board[endX][endY].equals(".") && !isWhitePiece(board[endX][endY])) return true;
                return false;

            case "♟": // Black pawn
                if (dy == 0 && dx == 1 && board[endX][endY].equals(".")) return true;
                if (dy == 0 && dx == 2 && startX == 1 && board[2][startY].equals(".") && board[endX][endY].equals(".")) return true;
                if (Math.abs(dy) == 1 && dx == 1 && !board[endX][endY].equals(".") && isWhitePiece(board[endX][endY])) return true;
                return false;

            case "♖": case "♜": // Rook
                if (dx != 0 && dy != 0) return false;
                return isPathClear(startX, startY, endX, endY);

            case "♗": case "♝": // Bishop
                if (Math.abs(dx) != Math.abs(dy)) return false;
                return isPathClear(startX, startY, endX, endY);

            case "♕": case "♛": // Queen
                if (dx == 0 || dy == 0 || Math.abs(dx) == Math.abs(dy))
                    return isPathClear(startX, startY, endX, endY);
                return false;

            case "♘": case "♞": // Knight
                return (Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2);

            case "♔": case "♚": // King
                return Math.abs(dx) <= 1 && Math.abs(dy) <= 1;

            default:
                return false;
        }
    }

    // Check if there are no pieces blocking the path
    private static boolean isPathClear(int startX, int startY, int endX, int endY) {
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);
        int x = startX + dx;
        int y = startY + dy;

        while (x != endX || y != endY) {
            if (!board[x][y].equals(".")) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    // Check for En Passant
    private static boolean isEnPassant(int startX, int startY, int endX, int endY, String piece) {
        if (piece.equals("♙") && endY == startY + 1 && startX == 4 && board[endX][endY].equals("♟")) {
            // En passant capture for white
            return enPassantPawn.equals("♟");
        } else if (piece.equals("♟") && endY == startY - 1 && startX == 3 && board[endX][endY].equals("♙")) {
            // En passant capture for black
            return enPassantPawn.equals("♙");
        }
        return false;
    }

    // Handle Castling logic
    private static boolean isCastling(int startX, int startY, int endX, int endY, String piece) {
        if (piece.equals("♔")) {
            return (endY == 2 || endY == 6) && !hasMoved[startX][startY] && !hasMoved[endX][endY] && isPathClear(startX, startY, endX, endY);
        }
        return false;
    }
}
