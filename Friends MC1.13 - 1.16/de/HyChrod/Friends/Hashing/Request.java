package de.HyChrod.Friends.Hashing;

import java.util.UUID;

public class Request {
	
	private UUID player;
	private UUID playerToAdd;
	private String message;
	private long timestamp;
	
	public Request(UUID player, UUID playerToAdd, String message, long timestamp) {
		this.player = player;
		this.playerToAdd = playerToAdd;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	public String getMessage() {
		return message;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public UUID getPlayerToAdd() {
		return playerToAdd;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
