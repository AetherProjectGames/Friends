package de.HyChrod.Friends.Hashing;

import java.util.UUID;

public class Blockplayer {
	
	private UUID player;
	private UUID blocked;
	private long timestamp;
	private String message;
	
	public Blockplayer(UUID player, UUID blocked, long timestamp, String message) {
		this.player = player;
		this.blocked = blocked;
		this.timestamp = timestamp;
		this.message = message;
	}
	
	public UUID getBlocked() {
		return blocked;
	}
	
	public String getMessage() {
		return message;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
