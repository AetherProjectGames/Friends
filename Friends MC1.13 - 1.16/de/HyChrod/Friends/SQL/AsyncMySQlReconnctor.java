package de.HyChrod.Friends.SQL;

import org.bukkit.Bukkit;

import de.HyChrod.Friends.Friends;

public class AsyncMySQlReconnctor implements Runnable {
	
	private static AsyncMySQlReconnctor connectionRefresher;
	
	public static void kill() {
		if(connectionRefresher != null) Bukkit.getScheduler().cancelTask(connectionRefresher.taskID);
	}

	
	private int taskID;
	
	public AsyncMySQlReconnctor() {
		taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Friends.getInstance(), this, (1200*60), (1200*60)).getTaskId();
		connectionRefresher = this;
	}

	@Override
	public void run() {
		Friends.getSMgr().connect();
	}

}
