package com.chess.root;

public class Player {
	
	private boolean color;
	private boolean isAI = false;
	private AIThread thread;
	private final Object lock = new Object();

	public Player(boolean color){
		this.color = color;
	}
		
	// ---------------------------------- AI HANDLING ----------------------------------

	public void setAI() {
		isAI = true;
	}

	public boolean isAI() {
		return isAI;
	}

	public Object getLock() {
		return lock;
	}

	public void setThread(AIThread thread) {
		this.thread = thread;
	}

	public AIThread getThread() {
		return thread;
	}
	
	// ---------------------------------- GENERIC GETTER ----------------------------------

	public boolean isBlack() {
		return color;
	}
	
	// ---------------------------------- HELPER METHODS ----------------------------------

	@Override
	public String toString() {
		return color ? "black" : "white";
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj) && (this instanceof Player && obj instanceof Player);
	}

	@Override
	public int hashCode() {
		int hash = this.isBlack() ? 3 : 4;
		hash += this.toString().hashCode();
		hash += 123;
		return hash;
	}

}
