package de.HyChrod.Friends.Hashing;

import java.util.UUID;

public class Options {
	
	private UUID uuid;
	private boolean offline = false;
	private boolean receive_requests = true;
	private boolean jumping = true;
	private int receive_messages = 0;
	private boolean party_invites = true;
	private int sorting = 0;
	private String status = null;
	
	public Options(UUID uuid, boolean offline, boolean requests, int messages, String status, int sorting, boolean jumping, boolean party) {
		this.uuid = uuid;
		this.offline = offline;
		this.receive_messages = messages;
		this.receive_requests = requests;
		this.status = status;
		this.sorting = sorting;
		this.party_invites = party;
		this.jumping = jumping;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public int getSorting() {
		return sorting;
	}
	
	public String getStatus() {
		return status;
	}
	
	public boolean getPartyInvites() {
		return party_invites;
	}
	
	public void setPartyInvites(boolean bool) {
		this.party_invites = bool;
	}
	
	public boolean getRequests() {
		return receive_requests;
	}
	
	public boolean getMessages() {
		return receive_messages == 1;
	}
	
	public boolean getFavMessages() {
		return receive_messages == 2;
	}
	
	public boolean isOffline() {
		return offline;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setOffline(boolean offline) {
		this.offline = offline;
	}
	
	public void setReceive_messages(int receive_messages) {
		this.receive_messages = receive_messages;
	}
	
	public void setReceive_requests(boolean receive_requests) {
		this.receive_requests = receive_requests;
	}
	
	public void setSorting(int sorting) {
		this.sorting = sorting;
	}
	
	public boolean getJumping() {
		return jumping;
	}
	
	public void setJumping(boolean bool) {
		jumping = bool;
	}

}
