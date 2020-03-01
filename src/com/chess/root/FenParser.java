package com.chess.root;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.chess.root.pieces.PawnPiece;
import com.chess.root.pieces.Piece;

public class FenParser {
	
	private static final Logger LOG = Logger.getLogger(String.class.getName());
	private static String numericPattern = "\\d+";
	
	private FenParser() {
	}
		
	// ---------------------------------- IMPORT ----------------------------------
	
	public static String[][] parseBoard(String board) {
		String[][] newBoard = null;
		try {
			newBoard = new String[8][8];
			int col = 0;
			int row = 0;
			for (int i = 0; i < board.length(); i++) {
				String s = Character.toString(board.charAt(i)); 
				if (s.matches(numericPattern)) {
					Integer x = Integer.parseInt(s);
					for (int k = 0; k < x; k++) {
						col++;
					}
				} else if (!s.contentEquals("/")) {
					if (col < 8 && row < 8) {
						newBoard[col][row] = s;
					}
					col++;
				} else {
					col = 0;
					row++;
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		} 
		return newBoard;
	}
		
	public static int parseInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
		return 0;
	}
	
	public static void parseCastling(String cas, Board board) {
		if (cas != null && !cas.contentEquals("KQkq")) {
			try {
				board.getKing(false).initializeFenCastling(cas); // white
				board.getKing(true).initializeFenCastling(cas);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
			}
		}
	}
		
	public static Piece parsePassing(String pass, List<Piece> blackPieces, List<Piece> whitePieces) {
		if (pass != null && !pass.contentEquals("-")) {
			try {
				int row = parseInteger(Character.toString(pass.charAt(1)));
				String col = Character.toString(pass.charAt(0));
				if (row == 3) {
					return getPassingPiece(whitePieces, col+(row+1));
				} else if (row == 6) {
					return getPassingPiece(blackPieces, col+(row-1));
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
			}
		}
		return null;
	}
	
	private static Piece getPassingPiece(List<Piece> pieces, String position) {
		for (Piece p : pieces) {
			if (p instanceof PawnPiece && p.getField().getNotation().contentEquals(position)) {
				return p;
			}
		}
		return null;
	}

	public static Double parseMoveCounter(String s) {
		int c = parseInteger(s);
		return Double.valueOf(c);
	}

	// ---------------------------------- EXPORT ----------------------------------
	
	public static String build(Board board) {
		return getBoard(board) + " " + getPlayer(board) + " " + getCastling(board) + " " + getPassing(board) + " " + getCountdown(board) + " " + getMoveCount(board);
	}
	
	public static String getBoard(Board board) {
		StringBuilder bld = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			if (i != 0) {
				bld.append("/");
			}
			int counter = 0;
			for (int j = 0; j < 8; j++) {
				String s = board.getField(j, i).getFen();
				if (s != null) {
					if (counter != 0) {
						bld.append(counter);
						counter = 0;
					}
					bld.append(s);
				} else {
					counter++;
					if (j == 7) {
						bld.append(counter);
						counter = 0;
					}
				}
			}
		}
		return bld.toString();
	}
	
	public static String getPlayer(Board board) {
		return board.getPlayerColor() ? "b" : "w";
	}
	
	public static String getCastling(Board board) {
 		String s = "";
 		try {
	 		s += board.getKing(false).getCastlingFen();
	 		s += board.getKing(true).getCastlingFen();
	 		if (s.contentEquals("")) {
	 			s = "-";
	 		}
 		} catch (Exception e) {
 			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
 		}
 		return s;
 	}

	public static String getPassing(Board board) {
		Piece p = board.getEnPassantPiece();
		if (p != null) {
			if (p.getRow() == 3) {
				return p.getField().getColNotation() + (p.getField().getRowNotation() + 1);
			} else {
				return p.getField().getColNotation() + (p.getField().getRowNotation() - 1);
			}	
		}
		return "-";
	}
	
	public static String getCountdown(Board board) {
		return Integer.toString(board.getCountdown() / 2);
	}
	
	public static String getMoveCount(Board board) {
		int counter = board.getGame().getMoveCounter().intValue();
		return Integer.toString(counter);
	}
}
