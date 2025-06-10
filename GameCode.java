import java.util.Scanner;

public class GameCode {
    private static String[][] board = new String[8][8]; // 8x8 chess board
    private static Scanner scanner = new Scanner(System.in);
    private static boolean isWhiteTurn = true; // true for White's turn, false for Black
    private static String whitePlayer;
    private static String blackPlayer;
    private static int gameNumber = 1;

    public static void main(String[] args) {
        System.out.print("Enter name for White player: ");
        whitePlayer = scanner.nextLine();
        System.out.print("Enter name for Black player: ");
        blackPlayer = scanner.nextLine();

        initializeBoard();
        printBoard();

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

            if ((isWhiteTurn && !isWhitePiece(piece)) || (!isWhiteTurn && isWhitePiece(piece))) {
                System.out.println("It's " + (isWhiteTurn ? whitePlayer : blackPlayer) + "'s turn!");
                continue;
            }

            String target = board[endX][endY];
            if (!target.equals(".") && isWhitePiece(target) == isWhitePiece(piece)) {
                System.out.println("Cannot capture your own piece!");
                continue;
            }

            if (!isValidMove(startX, startY, endX, endY, piece)) {
                System.out.println("Invalid move for " + piece);
                continue;
            }

            // Simulate move
            String captured = board[endX][endY];
            board[endX][endY] = piece;
            board[startX][startY] = ".";

            // Check if move leaves own king in check
            if (isKingInCheck(isWhiteTurn)) {
                // Undo move
                board[startX][startY] = piece;
                board[endX][endY] = captured;
                System.out.println("You cannot leave your king in check!");
                continue;
            }

            printBoard();

            if (captured.equals("♚")) {
                System.out.println(whitePlayer + " wins! (Black king captured)");
                GameScoreDAO.insertGameResult(gameNumber++, whitePlayer + " vs " + blackPlayer, whitePlayer);
                break;
            } else if (captured.equals("♔")) {
                System.out.println(blackPlayer + " wins! (White king captured)");
                GameScoreDAO.insertGameResult(gameNumber++, whitePlayer + " vs " + blackPlayer, blackPlayer);
                break;
            }

            // Checkmate and stalemate
            boolean opponentInCheck = isKingInCheck(!isWhiteTurn);
            boolean opponentHasMoves = hasLegalMoves(!isWhiteTurn);

            if (opponentInCheck && !opponentHasMoves) {
                System.out.println((isWhiteTurn ? whitePlayer : blackPlayer) + " wins by checkmate!");
                GameScoreDAO.insertGameResult(gameNumber++, whitePlayer + " vs " + blackPlayer, isWhiteTurn ? whitePlayer : blackPlayer);
                break;
            } else if (!opponentInCheck && !opponentHasMoves) {
                System.out.println("Stalemate! It's a draw.");
                GameScoreDAO.insertGameResult(gameNumber++, whitePlayer + " vs " + blackPlayer, "Draw");
                break;
            }

            isWhiteTurn = !isWhiteTurn;
        }
    }

    private static void initializeBoard() {
        String[] setup = {
            "♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜",
            "♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟",
            ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".",
            "♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙",
            "♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"
        };

        int idx = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = setup[idx++];
    }

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

    private static boolean isValidFormat(String move) {
        return move.matches("[a-h][1-8] [a-h][1-8]");
    }

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

    private static boolean isWhitePiece(String piece) {
        return "♙♖♘♗♕♔".contains(piece);
    }

    private static boolean isValidMove(int startX, int startY, int endX, int endY, String piece) {
        int dx = endX - startX;
        int dy = endY - startY;

        switch (piece) {
            case "♙":
                if (dy == 0 && dx == -1 && board[endX][endY].equals(".")) return true;
                if (dy == 0 && dx == -2 && startX == 6 && board[5][startY].equals(".") && board[endX][endY].equals(".")) return true;
                if (Math.abs(dy) == 1 && dx == -1 && !board[endX][endY].equals(".") && !isWhitePiece(board[endX][endY])) return true;
                return false;

            case "♟":
                if (dy == 0 && dx == 1 && board[endX][endY].equals(".")) return true;
                if (dy == 0 && dx == 2 && startX == 1 && board[2][startY].equals(".") && board[endX][endY].equals(".")) return true;
                if (Math.abs(dy) == 1 && dx == 1 && !board[endX][endY].equals(".") && isWhitePiece(board[endX][endY])) return true;
                return false;

            case "♖": case "♜":
                if (dx != 0 && dy != 0) return false;
                return isPathClear(startX, startY, endX, endY);

            case "♗": case "♝":
                if (Math.abs(dx) != Math.abs(dy)) return false;
                return isPathClear(startX, startY, endX, endY);

            case "♕": case "♛":
                if (dx == 0 || dy == 0 || Math.abs(dx) == Math.abs(dy))
                    return isPathClear(startX, startY, endX, endY);
                return false;

            case "♘": case "♞":
                return (Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2);

            case "♔": case "♚":
                return Math.abs(dx) <= 1 && Math.abs(dy) <= 1;

            default:
                return false;
        }
    }

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

    private static boolean isKingInCheck(boolean whiteKing) {
        String king = whiteKing ? "♔" : "♚";
        int kingX = -1, kingY = -1;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j].equals(king)) {
                    kingX = i;
                    kingY = j;
                }

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                if (!piece.equals(".") && isWhitePiece(piece) != whiteKing)
                    if (isValidMove(i, j, kingX, kingY, piece))
                        return true;
            }

        return false;
    }

    private static boolean hasLegalMoves(boolean whiteTurn) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                String piece = board[i][j];
                if (!piece.equals(".") && isWhitePiece(piece) == whiteTurn) {
                    for (int x = 0; x < 8; x++)
                        for (int y = 0; y < 8; y++) {
                            String target = board[x][y];
                            if (!target.equals(".") && isWhitePiece(target) == whiteTurn) continue;
                            if (isValidMove(i, j, x, y, piece)) {
                                String temp = board[x][y];
                                board[x][y] = piece;
                                board[i][j] = ".";
                                boolean check = isKingInCheck(whiteTurn);
                                board[i][j] = piece;
                                board[x][y] = temp;
                                if (!check) return true;
                            }
                        }
                }
            }
        return false;
    }
}
