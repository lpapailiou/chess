package com.chess.root;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.chess.model.Setting;
import com.chess.root.moves.CastlingMove;
import com.chess.root.moves.Move;
import com.chess.root.moves.PromotionMove;
import com.chess.root.pieces.PawnPiece;

public class PgnParser {
	
	private static final Logger LOG = Logger.getLogger(String.class.getName());
	
	private PgnParser() {
	}
	
	// ---------------------------------- IMPORT ----------------------------------
	
	public static String getEvent(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("Event")) {
					return getMetaValue(data);
				}
			}
		}
		return "Casual waste of time";
	}
	
	public static String getSite(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("Site")) {
					return getMetaValue(data);
				}
			}
		}
		return "Your cave, SWITZERLAND";
	}
	
	public static String getDate(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("Date")) {
					return getMetaValue(data);
				}
			}
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getRound(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("Round")) {
					return getMetaValue(data);
				}
			}
		}
		return null;
	}
	
	public static String getWhite(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("White")) {
					return getMetaValue(data);
				}
			}
		}
		return null;
	}
	
	public static String getBlack(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("Black")) {
					return getMetaValue(data);
				}
			}
		}
		return null;
	}
	
	public static String getResult(Setting settings) {
		String[] meta = settings.getPgnMeta();
		if (meta != null) {
			for (String data : meta) {
				if(getMetaTag(data).contentEquals("Result")) {
					return getMetaValue(data);
				}
			}
		}
		return "*";
	}

	public static List<String> parseMoves(String s) {
		String alpha = "[A-Za-z]+";
		String[] arr = s.replaceAll("\\{.*?\\}","").replaceAll("\\.", "\\. ").replaceAll("\n", "\n ").split(" ");
		LinkedList<String> result = new LinkedList<>();
		for (String str : arr) {
			if (str.length() > 0 && Character.toString(str.charAt(0)).matches(alpha)) {
				result.add(str);
			}
		}
		return result;
	}
	
	public static Move parseMove(String s, List<Move> moves) {
		s = s.replaceAll("\\+", "").replaceAll("\\#", "").replaceAll("\n", "").replaceAll(" ", "").trim();
		if (moves != null) {
			for (Move m : moves) {
				String pieceNotation = m.getPiece().getPgnNotation();
				String pieceCol = m.getPiece().getField().getColNotation();
				String pieceRow = Integer.toString(m.getPiece().getField().getRowNotation());
				String target = m.getField().getNotation();
				if (s.contains("O-O") || s.contains("O-O-O") || s.contains("0-0") || s.contains("0-0-0")) {
					if (m instanceof CastlingMove && m.getNotation().contentEquals(s)) {
						return m;
					}
				} else if (s.contains("=")) {
					if (m instanceof PromotionMove) {
						if (target.contentEquals(s.substring(0, 2)) || target.contentEquals(s.substring(3, 5))) {
							return m;
						}
						if (pieceCol.contentEquals(s.substring(0, 1)) && (target.contentEquals(s.substring(3, 5))) || (target.contentEquals(s.substring(2, 4)))) {
							return m;
						}
					}
				} else if (s.length() == 2) {
					if ((target).contentEquals(s) && m.getPiece() instanceof PawnPiece) {
						return m;
					}
				} else if (s.length() == 3) {
					if (pieceNotation.contentEquals(s.substring(0,1)) && target.contentEquals(s.substring(1))) {
						return m;
					}	
				} else {
					if (pieceNotation.contentEquals(s.substring(0,1))) {
						if (target.contentEquals(s.substring(1))) {
							// Nc6
							return m;
						} else if (target.contentEquals(s.substring(2))) {
							//cxg6
							//Rfd8
							if ((pieceCol.contentEquals(s.substring(1,2)) || pieceRow.contentEquals(s.substring(1,2))) || m.getVictim() != null) {
								return m;
							}
						} else if (target.contentEquals(s.substring(3))) {
							//c1xg6
							if ((pieceCol.contentEquals(s.substring(1,2)) || pieceRow.contentEquals(s.substring(1,2))) && m.getVictim() != null) {
								return m;
							}
							//cg4g6
							// Ng1e2
							if ((pieceCol+pieceRow).contentEquals(s.substring(1,3))) {

								return m;
							}
						} else if (target.contentEquals(s.substring(4)) && (pieceCol+pieceRow).contentEquals(s.substring(1,3))) {
							//cg4xg6
							return m;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static String getFileContent(File file) {
		String text = null;
		try (Scanner scanner = new Scanner(file)){
			text = scanner.useDelimiter("\\A").next();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		} 
		return text;
	}

	// ---------------------------------- EXPORT ----------------------------------
	
	public static String getFullPgn(Game game) {
		return getMetaData(game) + "\n\n" + getMoves(game);
	}
	
	private static String getMetaData(Game game) {
		String difficulty = (parseMetaString("Difficulty", game.getDifficulty()) + "\n");
		if (game.getAIPlayers().isEmpty()) {
			difficulty = "";
		}
		return parseMetaString("Event", game.getEvent()) + "\n" +  parseMetaString("Site", game.getSite()) + "\n" +  parseMetaString("Date", game.getDate()) + "\n" +  parseMetaString("Round", game.getRound()) + "\n" + difficulty +  parseMetaString("White", game.getWhite()) + "\n" +  parseMetaString("Black", game.getBlack()) + "\n" +  parseMetaString("Result", game.getResult());
	}
	
	private static String getMoves(Game game) {
		List<Move> history = game.getHistory();
		StringBuilder bld = new StringBuilder();
		if (history != null && !history.isEmpty()) {
			int j = 0;
			String step = "";
			for (int i = 0; i < history.size(); i++) {
				j = (i+2)/2;
				if (i != 0 && i % 12 == 0) {
					bld.append("\n");
				}
				step = (i % 2 == 0) ? (j + ". ") : " ";
				bld.append(step + history.get(i).getPgnNotation() + " ");
				if (i == history.size()-1) {
					bld.append(history.get(i).getResult());
				}
			}
		}
		String result = bld.toString();
		if (result != null) {
			result = result.replaceAll("  ", " ");
		}
		return result;
	}
	
	private static String getMetaTag(String s) {
		String result = "";
		if (s != null) {
			result = s.split(" ")[0].replaceAll("\n", "").substring(1);
			
		}
		return result;
	}
	
	private static String getMetaValue(String s) {
		String result = null;
		if (s != null) {
			try {
				result = s.split("\"")[1].replaceAll("\n", "");
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
			}
		}
		return result;
	}
	
	private static String parseMetaString(String tag, String s) {
		return "[" + tag + " \"" + s + "\"]";
	}	


}
