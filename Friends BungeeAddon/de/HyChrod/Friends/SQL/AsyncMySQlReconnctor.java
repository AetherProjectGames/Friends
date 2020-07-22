package de.HyChrod.Friends.SQL;

import de.HyChrod.Friends.Friends;
import net.md_5.bungee.BungeeCord;

public class AsyncMySQlReconnctor implements Runnable {
	
	private static AsyncMySQlReconnctor connectionRefresher;
	
	public static void kill() {
		if(connectionRefresher != null) BungeeCord.getInstance().getScheduler().cancel(connectionRefresher.taskID);
	}

	
	private int taskID;
	
	public AsyncMySQlReconnctor() {
		taskID = BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), this).getId();
		connectionRefresher = this;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(60*60000);
			} catch (InterruptedException e) {}
			Friends.getSMgr().connect();
		}
	}

}
