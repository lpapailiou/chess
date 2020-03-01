package com.chess.root;

import com.chess.root.pieces.BishopPiece;
import com.chess.root.pieces.KingPiece;
import com.chess.root.pieces.KnightPiece;
import com.chess.root.pieces.PawnPiece;
import com.chess.root.pieces.QueenPiece;
import com.chess.root.pieces.RookPiece;

public class PieceInitializer {
	
	private PieceInitializer() {
	}

	public static void initialize(Board board) {
		new PawnPiece(board, board.getField(0,1), true);
		new PawnPiece(board, board.getField(1,1), true);
		new PawnPiece(board, board.getField(2,1), true);
		new PawnPiece(board, board.getField(3,1), true);
		new PawnPiece(board, board.getField(4,1), true);
		new PawnPiece(board, board.getField(5,1), true);
		new PawnPiece(board, board.getField(6,1), true);
		new PawnPiece(board, board.getField(7,1), true);
		new RookPiece(board, board.getField(0,0), true);
		new RookPiece(board, board.getField(7,0), true);
		new KnightPiece(board, board.getField(1,0), true);
		new KnightPiece(board, board.getField(6,0), true);
		new BishopPiece(board, board.getField(2,0), true);
		new BishopPiece(board, board.getField(5,0), true);
		new QueenPiece(board, board.getField(3,0), true, false);
		new KingPiece(board, board.getField(4,0), true);
		
		new PawnPiece(board, board.getField(0,6), false);
		new PawnPiece(board, board.getField(1,6), false);
		new PawnPiece(board, board.getField(2,6), false);
		new PawnPiece(board, board.getField(3,6), false);
		new PawnPiece(board, board.getField(4,6), false);
		new PawnPiece(board, board.getField(5,6), false);
		new PawnPiece(board, board.getField(6,6), false);
		new PawnPiece(board, board.getField(7,6), false);
		new RookPiece(board, board.getField(0,7), false);
		new RookPiece(board, board.getField(7,7), false);
		new KnightPiece(board, board.getField(1,7), false);
		new KnightPiece(board, board.getField(6,7), false);
		new BishopPiece(board, board.getField(2,7), false);
		new BishopPiece(board, board.getField(5,7), false);
		new QueenPiece(board, board.getField(3,7), false, false);
		new KingPiece(board, board.getField(4,7), false);
	}
	
	public static void initializeFen(Board board, String[][] boardString) {
		if (board == null || boardString == null) {
			return;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				String piece = boardString[i][j];
				if (piece != null) {
					switch (piece) {
						case "P":
							new PawnPiece(board, board.getField(i,j), false);
							break;
						case "p":
							new PawnPiece(board, board.getField(i,j), true);
							break;
						case "R":
							new RookPiece(board, board.getField(i,j), false);
							break;
						case "r":
							new RookPiece(board, board.getField(i,j), true);
							break;
						case "N":
							new KnightPiece(board, board.getField(i,j), false);
							break;
						case "n":
							new KnightPiece(board, board.getField(i,j), true);
							break;
						case "B":
							new BishopPiece(board, board.getField(i,j), false);
							break;
						case "b":
							new BishopPiece(board, board.getField(i,j), true);
							break;
						case "Q":
							new QueenPiece(board, board.getField(i,j), false, false);
							break;
						case "q":
							new QueenPiece(board, board.getField(i,j), true, false);
							break;
						case "K":
							new KingPiece(board, board.getField(i,j), false);
							break;
						case "k":
							new KingPiece(board, board.getField(i,j), true);
							break;	
						default:
							break;
					}
				}
			}
		}
	}
	
	/*
	// ---------------------------------- TESTS ----------------------------------
	
	public static void enPassantTest(Board board) {
		PieceType.BLACK.getPawn(board, board.getField(2,4));
		PieceType.BLACK.getKing(board, board.getField(4,0));
		
		PieceType.WHITE.getPawn(board, board.getField(3,6));
		PieceType.WHITE.getKing(board, board.getField(4,7));
	}
	
	public static void enPassantTestInv(Board board) {
		PieceType.BLACK.getPawn(board, board.getField(2,3));
		PieceType.BLACK.getKing(board, board.getField(4,7));
		
		PieceType.WHITE.getPawn(board, board.getField(3,1));
		PieceType.WHITE.getKing(board, board.getField(4,0));
	}
	
	public static void promotionTest(Board board) {
		PieceType.BLACK.getPawn(board, board.getField(2,4));
		PieceType.BLACK.getKing(board, board.getField(4,0));
		
		PieceType.WHITE.getPawn(board, board.getField(7,6));
		PieceType.WHITE.getKing(board, board.getField(7,7));
	}
	
	public static void promotionTestInv(Board board) {
		PieceType.WHITE.getPawn(board, board.getField(2,3));
		PieceType.WHITE.getKing(board, board.getField(4,7));
		
		PieceType.BLACK.getPawn(board, board.getField(7,1));
		PieceType.BLACK.getKing(board, board.getField(7,0));
	}
	
	public static void checkTest(Board board) {
		PieceType.BLACK.getPawn(board, board.getField(2,5));
		PieceType.BLACK.getKing(board, board.getField(4,0));
		
		PieceType.WHITE.getPawn(board, board.getField(7,6));
		PieceType.WHITE.getKing(board, board.getField(3,7));
	}
	
	public static void endgame1(Board board) {
		PieceType.BLACK.getPawn(board, board.getField(2,5));
		PieceType.BLACK.getKing(board, board.getField(4,0));
		
		PieceType.WHITE.getRook(board, board.getField(6,6));
		PieceType.WHITE.getRook(board, board.getField(7,7));
		PieceType.WHITE.getKing(board, board.getField(3,7));
	}
	
	public static void endgame2(Board board) {
		PieceType.BLACK.getKing(board, board.getField(0,0));
		PieceType.WHITE.getRook(board, board.getField(6,6));
		PieceType.WHITE.getRook(board, board.getField(7,7));
		PieceType.WHITE.getKing(board, board.getField(3,7));
	}
	
	public static void stalemate(Board board) {
		//PieceType.BLACK.getPawn(board, board.getField(2,5));
		PieceType.BLACK.getKing(board, board.getField(0,0));
		
		PieceType.WHITE.getKnight(board, board.getField(1,4));
		PieceType.WHITE.getKing(board, board.getField(1,2));
	}*/
	
}
