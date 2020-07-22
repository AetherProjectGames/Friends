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
	
	public static void kill() {
		if(queueUpdater != null) Bukkit.getScheduler().cancelTask(queueUpdater.taskID);
	}

	
	private int taskID;
	
	public AsyncSQLQueueUpdater() {
		taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Friends.getInstance(), this, 0, 1L).getTaskId();
		queueUpdater = this;
	}

	@Override
	public void run() {
		if(!updates.isEmpty()) {
			Friends.getSMgr().perform(updates.removeFirst());
		}
	}
	
}
