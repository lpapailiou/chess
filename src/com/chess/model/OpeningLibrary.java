package com.chess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import com.chess.root.moves.Move;

public class OpeningLibrary {
	
	// https://www.premiumschach.de/schachecke/eroeffnungsdatenbank3.htm
	
	// C50: italian; B20: sicilian defense; C00: french defense; C60: ruy lopez (spanish); D10: slavic defense: C20 napoleon;
	// D08: queenpawngame; B02: aljechin-defense; A53 old indian, A00: anderssen; C77: spanish anderssen; A07: king fianchetto
	// B34: sicilian; A02 bird; D00: blackmar-diener-gambit; A51: budapest gambit; B10: caro-kann-defense; D04up; queen-pawn-game;
	// C64: cordel-defense; C35: cunningham-gambit; E10 queen-pawn-game; B00: owen-defense; D06: queen-gambit; C46: three-jump game;
	// A10: english; A15: english, vs kingfianchetto; C51: evants-gambit; C31: falkbeer-countergambit; C34: fischer-defense;
	// C57: preussian attack; B21: sicilian, grand-prix; C53: italian, greco-möller; D70: grünfeld-indian; D31: half-slavic;
	// D00b: hodgson-attack; A03: dutchman; A80: dutchman-defense; B07: jugoslavian; E00: catalan; A00b: king-fianchetto; 
	// C30: king-gambit; E60: king-indian; C23: bishop-game; C40: latvian gambit; B32: löwenthal-variant; D06b: marshall-defense
	// C22: mid-gambit; A56: modern benoni-defense; A40: modern defense; B21b: morra-gambit; B90: Njandorf; E20: Nimozowitsch-Indian;
	// A01: Nimzowitsch-Larsen-Attack; C15: Nimzowitsch-System; C21: Nordic gambit; B28: O'Kelly-System; A00c: Orang Utang;
	// A40b: Owen-defense; C10: Paulsen-Variant; B41; Paulsen-Variant, sicilian; C41: Philidor-Defense; B07b: Pirc-Ufimzew-Defense;
	// C44: Ponziani-Opening; C57b: Preussian Attack; A04: Réti-Opening; B30: Rossolino-Variant; C42: Russian defense;
	// A00d: Saragossa-opening; B30b: Scottish party; B23: closed sicilian: A20: Sicilian coming; B70: Sicilian dragon;
	// B21a: Sicilian mid-gambit; A00e: Sleipner-opening; C62: Steinitz-defense; C03: Tarrasch-system; A45: Trompowsky-opening;
	// B53: Techower-variant; C50a: Hungarian defense; A41: Irregular systems; A00f: Van't Kruys-opening; C47: four-knights-game;
	// B12: Caro-Kann-defense; A57: Wolga-gambit; A06: Zuckertort-opening; C55: two-knights-game;
	
	private static Random random = new Random();
	
	private static String[] b00 = {"e4", "b6"};	
	
	private static String[] b20 = {"e4", "c5"};
	private static String[] b21 = {"e4", "c5", "f4"};
	private static String[] b21b = {"e4", "c5", "d4", "d4", "c3"};
	private static String[] b34 = {"e4", "c5", "Nf3", "Nc6", "d4", "Nd4", "Nd4", "g6"};
	private static String[] b32 = {"e4", "c5", "Nf3", "Nc6", "d4", "d4", "Nd4", "e5"};
	private static String[] b10 = {"e4", "c6"};
	private static String[] b07 = {"e4", "d6"};
	private static String[] c50 = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5"};
	private static String[] c20 = {"e4", "e5", "Qf3"};
	private static String[] c60 = {"e4", "e5", "Nf3", "Nc6", "Bb5"}; 
	private static String[] c77 = {"e4", "e5", "Nf3", "Nc6", "Bb5", "Nf6"};
	private static String[] c64 = {"e4", "e5", "Nf3", "Nc6", "Bb5", "Bc5"};
	private static String[] c35 = {"e4", "e5", "f4", "f4", "Nf3", "Be7"};
	private static String[] c46 = {"e4", "e5", "Nf3", "Nc6", "Nc3"};
	private static String[] c51 = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5", "b4"};
	private static String[] c31 = {"e4", "e5", "f4", "d5"};
	private static String[] c34 = {"e4", "e5", "f4", "f4", "Nf3", "d6"};
	private static String[] c57 = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Nf6", "Ng5", "d5", "d5"};
	private static String[] c53 = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Bc5", "c3"};
	private static String[] c30 = {"e4", "e5", "f4"};
	private static String[] c23 = {"e4", "e5", "Bc4"};
	private static String[] c40 = {"e4", "e5", "Nf3", "f5"};
	private static String[] c22 = {"e4", "e5", "d4", "Qd4", "Nc6"};
	private static String[] c00 = {"e4", "e6", "d4", "d5"};
	private static String[] b02 = {"e4", "Nf6"};

	private static String[] d10 = {"d4", "d5", "c4", "c6"};
	private static String[] d08 = {"d4", "d5", "c4", "e5"};
	private static String[] a53 = {"d4", "Nf6", "c4", "d6"};
	private static String[] d00 = {"d4", "d5", "e4"};
	private static String[] a51 = {"d4", "Nf6", "c4", "e5"};
	private static String[] d04 = {"d4", "d5", "Nf3", "Nf6", "e3"};
	private static String[] e10 = {"d4", "Nf6", "c4", "e6", "Nf3"};
	private static String[] d06 = {"d4", "d5", "c4"};
	private static String[] d70 = {"d4", "Nf6", "c4", "g6", "Nc3", "d5"};
	private static String[] d31 = {"d4", "d5", "c4", "e6", "Nc3"};
	private static String[] d00b = {"d4", "d5", "Bg5"};
	private static String[] a80 = {"d4", "f5"};
	private static String[] e00 = {"d4", "Nf6", "c4", "e6", "g3"};
	private static String[] e60 = {"d4", "Nf6", "c4", "g6"};
	private static String[] d06b = {"d4", "d5", "c4", "Nf6"};
	private static String[] a56 = {"d4", "Nf6", "c7", "c5", "d5"};
	private static String[] a40 = {"d4", "g6"};
	
	private static String[] a00 = {"a3"};
	private static String[] a00b = {"g3"};
	
	private static String[] a07 = {"Nf3", "d5", "g3"};

	private static String[] a02 = {"f4"};
	private static String[] a03 = {"f4", "d5"};
	
	private static String[] a10 = {"c4"};
	private static String[] a15 = {"c4", "Nf6"};
	
	private static String[] b90 = {"e4", "c5", "Nf3", "d6", "d4", "d4", "Nd3", "Nf6", "Nc3", "a6"};
	private static String[] e20 = {"d4", "Nf6", "c4", "e6", "Nc3", "Bb4"};
	private static String[] a01 = {"b3"};
	private static String[] c15 = {"e4", "e6", "d4", "d5", "Nc3", "Bb4"};
	private static String[] c21 = {"e4", "e5", "d4", "d4", "c3"};
	private static String[] b28 = {"e4", "c5", "Nf3", "a6"};
	private static String[] a00c = {"b4"};
	private static String[] a40b = {"d4", "b6"};
	private static String[] c10 = {"e4", "e6", "d4", "d5", "Nc3"};
	private static String[] b41 = {"e4", "c5", "Nf3", "e6", "d4", "d4", "Nd4", "a6"};
	private static String[] c41 = {"e4", "e5", "Nf3", "d6"};

	private static String[] b07b = {"e4", "d6"};
	private static String[] c44 = {"e4", "e5", "Nf3", "Nc6", "c3"};
	private static String[] c57b = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Nf6", "Ng5"};
	private static String[] a04 = {"Nf3"};
	private static String[] b30 = {"e4", "c5", "Nf3", "Nc6", "Bb5"};
	private static String[] c42 = {"e4", "e5", "Nf3", "Nf6"};
	private static String[] a00d = {"c3"};
	private static String[] b30b = {"e4", "e5", "Nf3", "Nc6", "d4", "d4"};
	private static String[] b23 = {"e4", "c5", "Nc3"};
	private static String[] a20 = {"c4", "e5"};
	private static String[] b70 = {"e4", "c5", "Nf3", "d6", "d4", "d4", "Nd3", "Nf6", "Nc3", "g6"};
	private static String[] b21a = {"e4", "c5", "d4"};
	private static String[] a00e = {"Nc3"};
	private static String[] c62 = {"e4", "e5", "Nf3", "Nc6", "Bb5", "d6"};
	private static String[] c03 = {"e4", "e6", "d4", "d5", "Nd2"};
	private static String[] a45 = {"d4", "Nf6", "Bg5"};
	private static String[] b53 = {"e4", "c5", "Nf3", "d6", "d4"};
	private static String[] c50a = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Be7"};
	private static String[] a41 = {"d4", "d6"};
	private static String[] a00f = {"e3"};
	private static String[] c47 = {"e4", "e5", "Nf3", "Nf6", "Nc3", "Nc6"};
	private static String[] b12 = {"e4", "c6", "d4", "d5", "e5"};
	private static String[] a57 = {"d4", "Nf6", "c4", "c5", "d5", "b5"};
	private static String[] a06 = {"Nf3", "d5"};
	private static String[] c55 = {"e4", "e5", "Nf3", "Nc6", "Bc4", "Nf6"};

	private static String[][] openings = {c50, b20, c00, c60, d10, c20, d08, b02, a53, a00, c77, a07, b34, a02, 
			d00, a51, b10, d04, c64, c35, e10, b00, d06, c46, a10, a15, c51, c31, c34, c57, b21, c53, d70, d31,
			d00b, a03, a80, b07, e00, a00b, c30, e60, c23, c40, b32, d06b, c22, a56, a40, b21b, b90, e20, a01, c15, 
			c21, b28, a00c, a40b, c10, b41, c41, b07b, c44, c57b, a04, b30, c42, a00d, b30b, b23, a20, b70, b21a,
			a00e, c62, c03, a45, b53, c50a, a41, a00f, c47, b12, a57, a06, c55};
	
	private static final int MAXLENGTH = 10;
	
	private OpeningLibrary() {
	}
	
	public static List<Move> getNext(List<Move> history, List<Move> moves) {
		int size = history.size();
		if (size < MAXLENGTH) {
			List<Move> openingMoves = new LinkedList<>();
			
			addNext(history, moves, openingMoves, openings, size);
			
			if (!openingMoves.isEmpty()) {
				if (size > 1) {
					return openingMoves;
				} else {
					int best = random.nextInt(openingMoves.size());
					return new ArrayList<>(Arrays.asList(openingMoves.get(best)));
				}
			}
		}
		return moves;
	}
	
	private static void addNext(List<Move> history, List<Move> moves, List<Move> openingMoves, String[][] openingsUp, int size) {
		for (Move m : moves) {
			for (String[] notation : openingsUp) {
				if ((size < notation.length) && (history.isEmpty() || matchesHistory(history, notation, size)) && (m.getTargetNotation().contentEquals(notation[size]))) {
					openingMoves.add(m);
				}
			}
		}
	}
	
	private static boolean matchesHistory(List<Move> history, String[] notation, int size) {
		for (int i = size-1; i >= 0; i--) {
			if (!history.get(i).getTargetNotation().contentEquals(notation[i])) {
				return false;
			}
		}
		return true;
	}
	
}
