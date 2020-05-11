package de.HyChrod.Friends.Hashing;

import java.util.UUID;

import org.bukkit.ChatColor;

import de.HyChrod.Friends.Utilities.Configs;

public class Friendship {
	
	private UUID player;
	private UUID friend;
	private boolean favorite = false;
	private long timestamp, friendLastOnline;
	private String nickname = ChatColor.translateAlternateColorCodes('&', Configs.ITEM_FRIEND_NO_NICK_REPLACEMENT.getText());
	private String status = "";
	private boolean canSendMessages = true;
	private boolean updated = false;
	
	public Friendship(UUID player, UUID friend, long timestamp, boolean favorite, boolean canSendMessages, String status) {
		this.player = player;
		this.friend = friend;
		this.timestamp = timestamp;
		this.favorite = favorite;
		this.canSendMessages = canSendMessages;
		this.status = status;
	}
	
	public Friendship(UUID player, UUID friend, long timestamp, boolean favorite, boolean canSendMessages, String status, long lastOnline) {
		this.player = player;
		this.friend = friend;
		this.timestamp = timestamp;
		this.favorite = favorite;
		this.canSendMessages = canSendMessages;
		this.status = status;
		this.friendLastOnline = lastOnline;
	}
	
	public long getLastOnline() {
		return friendLastOnline;
	}
	
	public String getStatus() {
		return status;
	}
	
	public UUID getFriend() {
		return friend;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public boolean getFavorite() {
		return favorite;
	}
	
	public boolean getCanSendMessages() {
		return canSendMessages;
	}
	
	public void setFavorite(boolean bool) {
		favorite = bool;
		updated = true;
	}
	
	public void setCanSendMessages(boolean bool) {
		canSendMessages = bool;
		updated = true;
	}
	
	public boolean getUpdated() {
		return updated;
	}
	
}
