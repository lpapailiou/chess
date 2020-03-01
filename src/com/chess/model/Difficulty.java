package com.chess.model;

public enum Difficulty {
	
	SUICIDE("suicide", 0, 0, 1000000000, false, true, false), 
	RANDOM("random", 1, 0, 1, false, true, false), 
	SUPEREASY("super easy", 2, 1, 2, false, true, false), 
	VERYEASY("very easy", 3, 1, 4, false, true, false),
	EASY("easy", 4, 1, 8, true, true, false),
	MEDIUM("medium", 5, 2, 10, true, true, false),
	MEDIUMER("mediumer", 6, 2, 10, true, true, false),
	HARD("hard", 7, 2, 30, true, true, false),
	HARDER("harder", 8, 3, 50, true, true, true),
	SUPERHARD("super hard", 9, 3, 100, true, true, true),
	SUPERSUPERHARD("super super hard", 10, 3, 1000000000, true, true, true); // default
	
	private final String name;
	private final int level;
	private final int tree;
	private final int spasm;
	private final boolean recursionDepthChanges;
	private final boolean drawCheck;
	private final boolean opening;

	Difficulty(String name, int level, int tree, int spasm, boolean recursionDepthChanges, boolean drawCheck, boolean opening) {
		this.name = name;
		this.level = level;
		this.tree = tree;
		this.spasm = spasm;
		this.recursionDepthChanges = recursionDepthChanges;
		this.drawCheck = drawCheck;
		this.opening = opening;
	}
	
	// ---------------------------------- GENERIC GETTERS ----------------------------------

	public String get() {
		return name;
	}
	
	public int level() {
		return level;
	}

	public int tree() {
		return tree;
	}
	
	public int spasm() {
		return spasm;
	}
	
	public boolean recursion() {
		return recursionDepthChanges;
	}
	
	public boolean draw() {
		return drawCheck;
	}
	
	public boolean opening() {
		return opening;
	}

}
