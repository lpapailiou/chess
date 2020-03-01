package com.chess.root;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.chess.application.FieldButton;
import com.chess.model.Difficulty;
import com.chess.model.OpeningLibrary;
import com.chess.model.PieceValues;
import com.chess.model.Setting;
import com.chess.root.moves.Move;
import com.chess.root.pieces.BishopPiece;
import com.chess.root.pieces.KingPiece;
import com.chess.root.pieces.KnightPiece;
import com.chess.root.pieces.PawnPiece;
import com.chess.root.pieces.Piece;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class Board {
	
	private Game game;
	private boolean editMode = false;
	private PieceValues pieceValues;
	private Difficulty difficulty;
	private boolean enPassantEnabled = false;
	private boolean touchedMovedEnabled = false;
	private boolean timeoutEnabled;
	
	private Field[][] fields;
	private List<Piece> blackPieces;
	private List<Piece> whitePieces;
	private Piece activePiece;
	private boolean blackPlays = false;
	private boolean check = false;
	private boolean isNextMoveUnlocked = true;
	private List<Move> currentMoves;
	private int recursionDepth = 3;	
	private int delayControl = 300;
	private static final Logger LOG = Logger.getLogger(String.class.getName());
	private Piece enPassantPiece;
	
	private boolean initializing = false;
	private boolean endGame = false;
	private List<Move> moveHistory = new LinkedList<>();
	private List<Move> moveFuture = new LinkedList<>();
	private int countdown;
	private static Random random = new Random();
	private boolean dummyMode;
	
	public Board(Game game, Setting settings) {
		this.game = game;
		this.difficulty = settings.getDifficulty();
		recursionDepth = difficulty.tree();
		this.pieceValues = settings.getPieceValue();
		this.enPassantEnabled = settings.getPassing();
		this.touchedMovedEnabled = settings.getTouched();
		this.timeoutEnabled = settings.getTimeout();
		this.blackPieces = new LinkedList<>();
		this.whitePieces = new LinkedList<>();
		this.fields = new Field[8][8];
		if (settings.hasFen()) {
			this.countdown = settings.getFenCountdown();
		}
		initializeFields(settings);
		initializePieces(settings.getFenBoard());
		if (settings.hasFen()) {
			blackPlays = settings.getFenPlayer();
			enPassantPiece = FenParser.parsePassing(settings.getFenPassingPiece(), blackPieces, whitePieces);
			FenParser.parseCastling(settings.getFenCastlingOptions(), this);
		}
	
		if (!settings.hasPgn()) {
			validateBoard();
		}
		
		if (settings.hasFen() && (isKingVictim())) {
			currentMoves.clear();
			endGame("-", "no playable situation", false);
		}
		
	}
	
	// ---------------------------------- MANUAL GAMEPLAY ----------------------------------
	
	public void performManualMove(MouseEvent event) {  
		if (!getPlayer().isAI()) {
			FieldButton button = (FieldButton) event.getSource();
			Field field = button.getField();
			if (isNextMoveUnlocked) {	
				// start move
				activePiece = field.getPiece();   			
				if (canPieceMove(activePiece)) {
					showHintsForDummies(activePiece, true);
					isNextMoveUnlocked = false;
					field.removePiece(false);
				}
			} else {
				// finish move
				Move move = getMove(activePiece, field); 
				if (move != null) {
					showHintsForDummies(activePiece, false);
					executeMove(move);
					
				} else {
					abortMove();
				}
			}
		}
	}
	
	public void performManualDrag(MouseEvent event) {	
		if (!getPlayer().isAI()) {
			FieldButton button = (FieldButton) event.getSource();
			Field field = button.getField();
			if (isNextMoveUnlocked) {	
				// start move
				activePiece = field.getPiece();   			
				if (canPieceMove(activePiece)) {
					
					Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
					db.setDragView(activePiece.getImage(), 31.0, 31.0);
					
					ClipboardContent content = new ClipboardContent();
					content.putString("");
					db.setContent(content);
	
					isNextMoveUnlocked = false;
					field.removePiece(false);
					showHintsForDummies(activePiece, true);
				}
			} 
		}
	}
	
	public void performManualDrop(DragEvent event) {  
		if (!getPlayer().isAI()) {
			FieldButton button = (FieldButton) event.getSource();
			Field field = button.getField();
			if (!isNextMoveUnlocked) {
				boolean success = false;
				Move move = getMove(activePiece, field); 
				if (move != null) {
					event.acceptTransferModes(TransferMode.MOVE);
					Dragboard db = event.getDragboard();
					db.clear();
					showHintsForDummies(activePiece, false);
					executeMove(move);
					success = true;	
				} else {
					abortMove();
				}
				event.setDropCompleted(success);
			}
		}
	}
	
	// ---------------------------------- MANUAL GAMEPLAY LOGIC HANDLING ONLY ----------------------------------
	
	private boolean canPieceMove(Piece piece) {
		if (piece == null) {
			LOG.log(Level.INFO, "SYSTEM: (no piece)");
			return false;
		}
		if (piece.isBlack() != blackPlays) {
			LOG.log(Level.INFO, "SYSTEM: (wrong color)");
			return false;
		}
		boolean found = false;
		List<Move> touchedMoved = new LinkedList<>();
		if (!currentMoves.isEmpty()) {
			for (Move m : currentMoves) {
				if (m.getPiece().equals(piece)) {
					touchedMoved.add(m);
					found = true;
				}
			}
		}
		if (!found) {
			LOG.log(Level.INFO, "SYSTEM: (not allowed to move)");
		} else if (touchedMovedEnabled) {
			currentMoves = touchedMoved;
		}
		return found;
	}
	
	private Move getMove(Piece piece, Field field) {
		if (piece == null || field == null) {
			return null;
		}
		if (!currentMoves.isEmpty()) {
			for (Move m : currentMoves) {
				if (m.getPiece().equals(piece) && m.getField().equals(field)) {
					return m;				
				} 
			}
		}
		return null;
	}
		
	private void abortMove() {
		showHintsForDummies(activePiece, false);
		LOG.log(Level.INFO, "SYSTEM: (can't move here)");
		isNextMoveUnlocked = true;
		activePiece.getField().setPiece(activePiece, true);
	}
	
	// ---------------------------------- AI GAMEPLAY ----------------------------------
	
	public void performAIMove() {
		if (getPlayer() != null) {
			Move bestMove = getBestMove();
			if (bestMove != null && isNextMoveUnlocked) {
				isNextMoveUnlocked = false;
				shortDelay();
				bestMove.getPiece().getField().removePiece(false);
				shortDelay();
				executeMove(bestMove); 
			}
		}
	}
	
	// ---------------------------------- AI GAMEPLAY LOGIC HANDLING ONLY ----------------------------------
		
	private Move getBestMove() {	
		if (currentMoves.isEmpty()) {
			return null;
		}
		
		List<Move> moves = currentMoves;
		
		if (difficulty.opening()) {
			moves = OpeningLibrary.getNext(moveHistory, moves);
		}
		moves = rateMovesAlphaBeta(moves);
		
		List<Move> bestMoves = new LinkedList<>();
		// spasm generator will - depending on difficulty setting - trigger completely random moves. Otherwise, best rated move is chosen
		int spasm = random.nextInt(difficulty.spasm());
		if (spasm == 0) {
			LOG.log(Level.INFO, "SYSTEM: (oopsie...)");
			bestMoves.addAll(moves);
		} else if (!moves.isEmpty()) {
			Optional<Move> maxMove = moves.stream().max(Comparator.comparing(Move::getRating));
			int maxValue = 0;
			if (maxMove.isPresent()) {
				maxValue = maxMove.get().getRating();
			}
			for (Move m : moves) {
				if (m.getRating() == maxValue) {
					bestMoves.add(m);
				}
			}
		}
		
		// if multiple moves with same rating are given, take random move
		if (!bestMoves.isEmpty()) {
			int best = random.nextInt(bestMoves.size());
			return bestMoves.get(best);
		}
		return null;
	}
	
	// ---------------------------------- MOVE RATING ----------------------------------
	
	private List<Move> rateMovesAlphaBeta(List<Move> moves) {
		if (!moves.isEmpty()) {
			showDelayCursor(true);
			List<Piece> myPieces = new LinkedList<>(getPieces(blackPlays));
			List<Piece> otherPieces = new LinkedList<>(getPieces(getOtherPlayer().isBlack()));   	
			for (Move c : moves) {
				List<Move> startMove = new LinkedList<>();
				startMove.add(c);
				int rating = rateMovesAlphaBeta(startMove, myPieces, otherPieces, true, blackPlays, -1000000, 1000000, recursionDepth);
				c.incRating(rating);
			}
			showDelayCursor(false);
		}
		return moves;
	}
	
	private int rateMovesAlphaBeta(List<Move> moves, List<Piece> myPieces, List<Piece> otherPieces, boolean itsMe, boolean isBlack, int alpha, int beta, int tree) {	
		List<Integer> numlist = new LinkedList<>();
		int currentTreeLevel = tree;
		int vala = -1000000;
		int valb = 1000000;
		for (Move thisMove : moves) {
			int rating = 0;
			
			// ----------------- before move -----------------
			thisMove.executeSimulation(this, otherPieces, myPieces, thisMove);
			// ----------------- after move start -----------------
	
			List<Move> otherMoves = getMoves(otherPieces, !isBlack);
	
			if (tree > 0 && otherMoves != null && !otherMoves.isEmpty()) {
				rating = rateMovesAlphaBeta(otherMoves, otherPieces, myPieces, !itsMe, !isBlack, alpha, beta, tree-1);
				if (currentTreeLevel == recursionDepth) {

					rating = avoidDrawCheck(thisMove, currentTreeLevel, rating);
					if (rating > 600000 && currentTreeLevel == recursionDepth && ((otherMoves != null && avoidStalemateCheck(isBlack, myPieces)) || otherMoves == null) && !(thisMove.getVictim() instanceof KingPiece)) {
						rating = rating / 320;
						LOG.log(Level.INFO, "SYSTEM: avoiding stalemate");
					}
				}
			} else { 
				rating = itsMe ? getMaterial(myPieces) - getMaterial(otherPieces) : getMaterial(otherPieces) - getMaterial(myPieces);

				if ((otherMoves == null || otherMoves.isEmpty()) && (thisMove.getVictim() instanceof KingPiece)) {
					//LOG.log(Level.INFO, "SYSTEM: plotting about direct attack of king");
					rating = rating * 2;
					if (currentTreeLevel != 0) {
						rating = rating * (currentTreeLevel+1);
					}
				}
			}
			numlist.add(rating + thisMove.getRating());
			// ----------------- after move end -----------------
			thisMove.undoSimulation(this, otherPieces, myPieces, thisMove);
			// ----------------- after move restore -----------------
		
			if (tree != 0) {
				if (itsMe) {
					vala = Math.max(vala, rating); 
					alpha = Math.max(alpha,  vala);
					if (alpha >= beta) {
						break;
					}
				} else {
					valb = Math.min(valb, rating); 
					beta = Math.min(beta,  valb);
					if (alpha >= beta) {
						break;
					}
				}
			}
		}
		return itsMe ? numlist.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new) : numlist.stream().mapToInt(v -> v).min().orElseThrow(NoSuchElementException::new);
	}
	
	private boolean avoidStalemateCheck(boolean otherColor, List<Piece> myPieces) {
		Piece king = getKing(!otherColor);
		if (difficulty.draw() && !isPieceEndangered(king, myPieces)) {
			
			List<Move> kingMoves = new LinkedList<>(king.getMoves());
			int size = 0;
			if (!kingMoves.isEmpty()) {
				size = kingMoves.size();
				for (Move m : kingMoves) {
					m.executeSimulation(this, myPieces);
					if (isPieceEndangered(king, myPieces)) {
						size--;
					}
					m.undoSimulation(this, myPieces);
				}
				if(size == 0) {
				return true;
				}
			}
		}
		return false;
	}
	
	private int avoidDrawCheck(Move thisMove, int tree, int rating) {
		if (timeoutEnabled && difficulty.draw()) {
			// avoid draw by threefold repetition
			if (tree == recursionDepth && getBoardOccurences(moveHistory, FenParser.getBoard(this)) >= 1) {
				LOG.log(Level.INFO, "SYSTEM: avoiding threefold rule draw");
				rating = rating / 6;
				}
			// avoid draw by 50 moves rule
			if ((tree == recursionDepth && countdown > 80) && (thisMove.getPiece() instanceof PawnPiece || thisMove.getVictim() != null)) {
				LOG.log(Level.INFO, "SYSTEM: avoiding 50 moves rule draw");
				rating = rating * 2;
			}
		}
		return rating;
	}
	
	private int getMaterial(List<Piece> pieces) {
		int value = 0;
		int count = pieces.size();
		if (count > 0) {
			for (Piece p : pieces) {
				value += p.getValue();
			}
		} 
		return value;
	}
	   
	// ---------------------------------- MOVE EXECUTION ----------------------------------
	  
	private void executeMove(Move move) { 
		countdown++; 
		moveHistory.add(move);
		move.execute(this);
		isNextMoveUnlocked = true;
	}  
	
	public void redoMove(Move move) {
		if (move == null && !moveFuture.isEmpty()) {
			move = moveFuture.get(moveFuture.size()-1);	
		}
		
		if (move != null) {
			moveFuture.remove(move);
			move.getPiece().getField().removePiece(true);
			executeMove(move);
		}
	}
	
	public void undoMove(Move move) {
		if (move == null && !moveHistory.isEmpty()) {
			move = moveHistory.get(moveHistory.size()-1);
		}
		
		if (move != null) {	
			moveHistory.remove(move);
			moveFuture.add(move);
			move.undo(this);
			validateBoard();
		}
	}
	
	public void endMove() {	
		game.updateMoveCounter();
		game.switchPlayer();
	}
	 
	// ---------------------------------- MOVE GENERATION AND VALIDATION ----------------------------------
	
	private List<Move> getValidMoves(boolean isblack, List<Move> moves) {
		List<Move> validMoves = new LinkedList<>();
		
		if (moves.isEmpty() || getPlayer() == null) {
			return validMoves;
		}
	
		boolean checked = false;
		Piece king = getKing(isblack);
		List<Piece> otherPieces = getPieces(!isblack);
		boolean endangered = false;
			
		// check if king is currently in check
		if (isPieceEndangered(king, otherPieces)) {
			checked = true;
		}
	
		for (Move thisMove : moves) {
			thisMove.executeSimulation(this, otherPieces);
			endangered = isPieceEndangered(king, otherPieces);
			if (!endangered) {
				validMoves.add(thisMove);
			} 
			thisMove.undoSimulation(this, otherPieces);
		}
			
		check = checked;
		
		if (!check) {
			// get castling moves
			KingPiece k = (KingPiece) king;
			if (king != null) {
				Optional.ofNullable(k.getCastlingMoves()).ifPresent(validMoves::addAll);
			}
		}
	
		if (!validMoves.isEmpty()) {
			ambiguousCheck(validMoves);
		}
		return validMoves;
	}
	
	private void ambiguousCheck(List<Move> moves) {
		for (Move a : moves) {
			String p = a.getPiece().getNotation();
			String f = a.getField().getNotation();
			String sCol = a.getStartField().getColNotation();
			String sRow = Integer.toString(a.getStartField().getRowNotation());
			for (Move b : moves) {   				
				if (b.getPiece().getNotation().contentEquals(p) && !(b.getPiece() instanceof PawnPiece) && b.getField().getNotation().contentEquals(f) && !b.getStartField().getNotation().contentEquals(f)) {
					boolean breaknow = false;
					if (!sCol.contentEquals(b.getStartField().getColNotation())) {
						a.ambiguousCol();
						breaknow = true;
					}
					if (!sRow.contentEquals(Integer.toString(b.getStartField().getRowNotation()))) {
						a.ambiguousRow();
						breaknow = true;
					}
					if (breaknow) {
						break;
					}
				}
			}
		}
	}
	 
	public Piece getKing(boolean blackPlayer) {
		List<Piece> pieces = getPieces(blackPlayer);
		Piece king = null;
		for (Piece p : pieces) {
			if (p instanceof KingPiece) {
				king = p;
			}
		}
		return king;
	}
	
	public boolean isPieceEndangered(Piece piece, List<Piece> otherPieces) {
		List<Move> otherMoves = getMoves(otherPieces, getOtherPlayer().isBlack());
		if (!otherMoves.isEmpty()) {
			for (Move m : otherMoves) {
				
				if (m.getVictim() != null && m.getVictim().equals(piece)) {
					return true;
				}
			}
		}
		return false;
	}

 	private List<Move> getMoves(List<Piece> p, boolean isBlack) {
 		// get all pieces from specific player
 		if (p == null) {
 			p = getPieces(isBlack);
 		}
 		List<Piece> pieces = new LinkedList<>(p);
 		List<Move> moves = new LinkedList<>();
 		for (Piece piece : pieces) {
 			Optional.ofNullable(piece.getMoves()).ifPresent(moves::addAll);
 		}
 		return moves;
 	}
 	
 	// ---------------------------------- BOARD STATE HANDLING ----------------------------------

 	public boolean validateBoard() {
 		if (getPlayer() == null) {
 			return false;
 		}
 		
 		blackPlays = getPlayer().isBlack();

 		gameStateCheck();
 		currentMoves = getValidMoves(blackPlays, getMoves(null, blackPlays));
 		Move lastMove = getLastMove();
 		String lastMoveNotation = (lastMove == null) ? "-" : lastMove.getNotation();
 		

 		if (timeoutEnabled && (!editMode || !hasFutureMoves())) {
 			int occurrences = getBoardOccurences(moveHistory, FenParser.getBoard(this));
 			String draw = null;
 			if (countdown > 100) {
 				draw = "(50 move rule)";
 			} else if (occurrences >= 2) {
 				draw = "(threefold repetition)";
 			} else if (insufficientMaterialCheck()) {
 				draw = "(insufficient material)";
 			}
 			if (draw != null) {
 				currentMoves.clear();
 				if (hasHistory()) {
 					getLastMove().setResult("1/2:1/2");
 				}
	 			return endGame(lastMoveNotation, "DRAW! " + draw, false);
 			}
 		}
 		
 		if (currentMoves.isEmpty() && (!editMode || !hasFutureMoves())) {
 			if (lastMove == null) {
 				return endGame(lastMoveNotation, "no playable situation", false);
 			}
 			return endGame(lastMove.getNotation());
 		} 
 		
 		if (check) {
 			if (hasHistory()) {
					getLastMove().setCheckSuffix("+");
			}
			if (!initializing) {
				game.getController().setDisplay("CHECK by " + getOtherPlayer().toString() + " player");
			}
 		}
 		if (lastMove != null) {
 			updateLog(getLastMove().getNotation());
 		} 	
 		return false;
 	}
 	
 	private void gameStateCheck() {
 		int minPieces = 4;
 		if (!endGame && difficulty.recursion()) {
 			if (blackPieces.size() <= minPieces) {
 				endGame = true;
 				for (Piece p : blackPieces) {
 					p.setEndTable(true);
 				}
 			}
 			if (whitePieces.size() <= minPieces) {
 				endGame = true;
 				for (Piece p : whitePieces) {
 					p.setEndTable(true);
 				}
 			}
 			if (endGame) {
 				recursionDepth++; 
 				LOG.log(Level.INFO, "SYSTEM: recursive search horizon increased to: {0}", recursionDepth);
 			}
 		} else if (endGame && (getPieces(blackPlays).size() > minPieces && getPieces(!blackPlays).size() > minPieces) && difficulty.recursion()) {
 			endGame = false;
 			for (Piece p : blackPieces) {
				p.setEndTable(false);
			}
 			for (Piece p : whitePieces) {
				p.setEndTable(false);
			}
	 		recursionDepth = difficulty.tree();
 		}
 	}
 	
 	private boolean insufficientMaterialCheck() {
 		int pieceSize = blackPieces.size() + whitePieces.size();
 		if (pieceSize == 2) {
 			return true;
 		} else if (pieceSize == 3) {
 			if (blackPieces.stream().anyMatch(c -> c instanceof BishopPiece) || whitePieces.stream().anyMatch(c -> c instanceof BishopPiece) ||
 					(blackPieces.stream().anyMatch(c -> c instanceof KnightPiece) || whitePieces.stream().anyMatch(c -> c instanceof KnightPiece))) {
 				return true;
 			}
 		} else if (pieceSize == 4 && (blackPieces.stream().anyMatch(c -> c instanceof BishopPiece) && whitePieces.stream().anyMatch(c -> c instanceof BishopPiece))) {
			Piece b1 = null;
			Piece b2 = null;
			for (Piece p : blackPieces) {
				if (p instanceof BishopPiece) {
					b1 = p;
				}
			}
			for (Piece p : whitePieces) {
				if (p instanceof BishopPiece) {
					b2 = p;
				}
			}
			if ((b1 != null && b2 != null) && ((b1.getColumn() + b1.getRow()) % 2) == ((b2.getColumn() + b2.getRow()) % 2)) {
				return true;
			}
 		}
 		return false;
 	}

	private int getBoardOccurences(List<Move> posList, String pos) {
		int count = 0;
		for (Move m : posList) {
			if (pos.contentEquals(m.getFenBoard())) {
				count++;
			}
		}
		return count;
	}
	
	private void updateLog(String moveNotation) {
		LOG.log(Level.INFO, moveNotation);
		game.getController().updateTempFile(moveHistory);
	}
	
	// ---------------------------------- END OF GAME HANDLING ----------------------------------
	
	private boolean endGame(String moveNotation) {
		if (check) {
			if (hasHistory()) {
				getLastMove().setCheckSuffix("#");
			}
			if (blackPlays) {
				if (hasHistory()) {
					getLastMove().setResult("1:0");
				}
			} else {
				if (hasHistory()) {
					getLastMove().setResult("0:1");
				}
			}
			return endGame(moveNotation, "CHECKMATE", true);
		} else {
			if (hasHistory()) {
				getLastMove().setResult("1/2:1/2");
			}
			return endGame(moveNotation, "STALEMATE!", false);
		}
	}
	
	private boolean endGame(String moveNotation, String end, boolean hasWinner) {
		updateLog(moveNotation);
		game.endGame(end, hasWinner);
		return true;
	}
	
	// ---------------------------------- EDIT MODE HANDLING ----------------------------------
	
	public void setDelay(int delay) {
		delayControl = delay;
	}
	
	public boolean isEditable() {
		return !editMode;
	}
	
	public void cleanUpEdit() {
		validateBoard();
		if (hasFutureMoves()) {
			moveFuture.clear();
		}
	}
	
	public boolean hasFutureMoves() {
		return !moveFuture.isEmpty();
	}
	
	// ---------------------------------- PIECE AND FIELD HANDLING ----------------------------------
	
	public List<Piece> getPieces(boolean isBlack) {
		return isBlack ? blackPieces : whitePieces;
	}
	
	public void addPiece(Piece piece) {
		if (piece.isBlack()) {
			blackPieces.add(piece);
		} else {
			whitePieces.add(piece);
		}
	}
	
	public void removePiece(Piece piece) {
		if (piece.isBlack()) {
			blackPieces.remove(piece);
		} else {
			whitePieces.remove(piece);
		}
	}
	
	public Field getField(int column, int row) {
		return fields[row][column];
	}
	
	// ---------------------------------- GENERIC GETTERS AND SETTERS ----------------------------------
	
	// general
	public boolean getPlayerColor() {
	 		return blackPlays;
	 	}
	
	// difficulty
	public PieceValues getPieceValue() {
		return pieceValues;
	}
	
	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	// rules   
	public Piece getEnPassantPiece() {
		return enPassantPiece;
	}
	
	public void setEnPassantPiece(Piece piece) {
		enPassantPiece = piece;
	}
	
	public boolean passingEnabled() {
		return enPassantEnabled;
	}
	   
	public int getCountdown() {
		return countdown;
	}
	
	public void setCountdown(int c) {
		countdown = c;
	}
	
	// edit mode
	public void setEditMode(boolean editing) {
		editMode = editing;
	}
	
	public boolean hasHistory() {
		return !moveHistory.isEmpty();
	}
	
	public List<Move> getHistory() {
		return moveHistory;
	}
	
	public Move getLastMove() {
		if (hasHistory()) {
			return moveHistory.get(moveHistory.size()-1);
		}
		return null;
	}
	
	public String getFen() {
		return FenParser.build(this);
	}
	
	// game
	public Game getGame() {
		return game;
	}
	
	private Player getPlayer() {
		return game.getPlayer();
	}
	
	private Player getOtherPlayer() {
		return game.getOtherPlayer();
	} 
	
	// ---------------------------------- GUI HELPER METHODS ----------------------------------
	
	private void shortDelay() {
		try {
			TimeUnit.MILLISECONDS.sleep(delayControl);
		} catch (InterruptedException e) {
			LOG.log(Level.SEVERE, e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
	
	public void showHints(MouseEvent event) {
		if (!getPlayer().isAI() && !currentMoves.isEmpty()) {
			if (!isNextMoveUnlocked) {
				abortMove();
			} else {
				Field field = ((FieldButton) event.getSource()).getField();
				Piece piece = field.getPiece();
				if (piece != null) {	
					for (Move m : currentMoves) {
						if (m.getPiece() == piece) {
							m.getField().flash();
						}
					}
				}
			}
		}	
	}
	
	public void showHintsForDummies(Piece piece, boolean on) {
		if (dummyMode) {
			Double op = on ? 0.7 : 1.0;
			if (piece != null) {	
				for (Move m : currentMoves) {
					if (m.getPiece() == piece) {
						m.getField().setOpacity(op);
					}
				}
			}
		}
	}
	
	public void setDummyMode(boolean on) {
		dummyMode = on;
	}
	
	public void showPossibleMoves() {
		if (!currentMoves.isEmpty() && (currentMoves.get(0).getPiece().getColor() == getPlayer().isBlack() && !getPlayer().isAI())) {	
			for (Move m : currentMoves) {
				m.getStartField().flash();
			}
		}	
	}
	
	public void showLastMove() {
		if (!moveHistory.isEmpty() ) {
			Move lastMove = moveHistory.get(moveHistory.size()-1);
			lastMove.flash();
		}
	}
	
	private void showDelayCursor(boolean doDelay) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				getField(i, j).waitMode(doDelay);
			}
		}
	}
	
	public void render() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				fields[i][j].render();
			}
		}
	}
   
	// ---------------------------------- INITIALIZATION ----------------------------------
	
	private void initializeFields(Setting settings) {
		boolean black = false;
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				// create field and button
				Field field = new Field(column, row, black);
				FieldButton button = new FieldButton(this, field);
				field.setButton(button);	

				fields[row][column] = field;
				
				// handle appearance on gui
				if (black) {
					button.getStyleClass().add("button-black");
				} else {
					button.getStyleClass().add("button-white");
				}
				
				settings.getGrid().add(button, column, row);
				black = !black;
			}
			black = !black;
		}
	}

	private void initializePieces(String[][] fen) {
		if (fen != null) {
			PieceInitializer.initializeFen(this, fen);
		} else {
			PieceInitializer.initialize(this);
		}
	}
	
	private boolean isKingVictim() {
		Piece k = getKing(!blackPlays);
		for (Move m : currentMoves) {
			if (m.getVictim() == k) {
				return true;
			}
		}
		return false;
	}

	public void executePgn(Setting settings) {
		if (settings.hasPgn()) {
			initializing = true;
			List<Move> pgnMoves;
			boolean turn = blackPlays;
			List<String> moveString = settings.getPgnMoves();
			if (!moveString.isEmpty()) {
				for (String step : moveString) {
					
					pgnMoves = getValidMoves(turn, getMoves(null, turn));
					Move preMove = PgnParser.parseMove(step, pgnMoves);
					
					if (preMove == null) {
						LOG.log(Level.INFO, "SYSTEM: pgn execution break at: {0}", step);
						break;
					}
					preMove.getPiece().getField().removePiece(false);
					executeMove(preMove);
					turn = !turn;
				}
				initializing = false;
				validateBoard();
				render(); // not needed when validateboard renders
			}
		}
	}

}
