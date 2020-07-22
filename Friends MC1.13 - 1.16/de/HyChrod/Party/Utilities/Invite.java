package de.HyChrod.Party.Utilities;

import java.util.UUID;

public class Invite {
	
	private UUID sender;
	private UUID receiver;
	private Parties party;
	private long timestamp;
	
	public Invite(UUID sender, UUID receiver, Parties party) {
		this.sender = sender;
		this.receiver = receiver;
		this.party = party;
		this.timestamp = System.currentTimeMillis();
	}
	
	public Parties getParty() {
		return party;
	}
	
	public UUID getReceiver() {
		return receiver;
	}
	
	public UUID getSender() {
		return sender;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
