package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class List_Command {
	
	public List_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.List") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 1) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party list")));
			return;
		}
		
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_LIST_NO_PARTY.getMessage()));
			return;
		}
		
		Parties party = Parties.getParty(p.getUniqueId());
		
		String leaders = "";
		String members = "";
		for(UUID lead : party.getLeader())
			leaders = leaders + ", " + FriendHash.getName(lead);
		for(UUID memb : party.getParticipants())
			members = members + ", " + FriendHash.getName(memb);
		if(leaders.length() > 2) leaders = leaders.substring(2);
		if(members.length() > 2) members = members.substring(2);
		
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_LIST_LIST.getMessage().replace("%LEADER_COUNT%", ""+party.getLeader().size()).replace("%MEMBER_COUNT%",""+party.getParticipants().size())
				.replace("%PARTY_LEADERS%", leaders).replace("%PARTY_MEMBERS%", members)));
		return;
	}

}
