package com.chess.root;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AIThread  extends Thread implements Runnable{
	Game game;
	Player player;
	Object lock;
	private volatile boolean running = true;
	private volatile boolean paused = true;
	private volatile boolean blocked = false;
	private static final Logger LOG = Logger.getLogger(String.class.getName());

	public AIThread(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.lock = player.getLock();
	}

	@Override 
	public void run() {
		while (running) {
			synchronized (lock) {
				if (!running) {
					break;
				}
				if (paused || blocked) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						LOG.log(Level.SEVERE, e.getMessage());
						Thread.currentThread().interrupt();
					}
				}
			}

			if(!blocked) {
				game.getBoard().performAIMove();
			}
		}
	}
	
	// ---------------------------------- HELPER METHODS ----------------------------------
		
	public void requestStop() {
		running = false;
		requestResume();
	}
	
	public void requestPause() {
		paused = true;
	}
	
	public void requestResume() {
		synchronized (lock) {
			paused = false;
			lock.notifyAll();
		}
	}
	
	public void block(boolean blockthis) {
		this.blocked = blockthis;
	}

}
