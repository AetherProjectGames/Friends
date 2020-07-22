package de.HyChrod.Party.Utilities;

import java.util.LinkedList;
import java.util.UUID;

public class PartyAPI {
	
	public static Parties getParty(UUID uuid) {
		return Parties.getParty(uuid);
	}
	
	public static LinkedList<UUID> getPlayerFromParty(Parties party) {
		return party.getMembers();
	}

}
