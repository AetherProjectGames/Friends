package de.HyChrod.Friends.SQL;

import java.util.LinkedList;

import de.HyChrod.Friends.Friends;
import net.md_5.bungee.BungeeCord;

public class AsyncSQLQueueUpdater implements Runnable {

	private static LinkedList<String> updates = new LinkedList<String>();
	private static AsyncSQLQueueUpdater queueUpdater;
	
	public static void addToQueue(String statement) {
		updates.add(statement);
	}
	
	public static void kill() {
		if(queueUpdater != null) 
			queueUpdater.running = false;
	}

	
	private boolean running = true;
	
	public AsyncSQLQueueUpdater() {
		BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), this).getId();
		queueUpdater = this;
	}

	@Override
	public void run() {
		while(running) {
			if(!updates.isEmpty()) {
				Friends.getSMgr().perform(updates.removeFirst());
			}
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
