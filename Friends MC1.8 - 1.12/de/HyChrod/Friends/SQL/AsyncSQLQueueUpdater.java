package de.HyChrod.Friends.SQL;

import java.util.LinkedList;

import org.bukkit.Bukkit;

import de.HyChrod.Friends.Friends;

public class AsyncSQLQueueUpdater implements Runnable {

	private static LinkedList<String> updates = new LinkedList<String>();
	private static AsyncSQLQueueUpdater queueUpdater;
	
	public static void addToQueue(String statement) {
		updates.add(statement);
	}
	
	public static boolean isEmpty() {
		return updates.isEmpty();
	}
	
	public static void kill() {
		if(queueUpdater != null) {
			Bukkit.getScheduler().cancelTask(queueUpdater.taskID);
			queueUpdater.running = false;
		}
	}

	private int taskID;
	
	private boolean running = true;
	
	public AsyncSQLQueueUpdater() {
		taskID = Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), this).getTaskId();
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
