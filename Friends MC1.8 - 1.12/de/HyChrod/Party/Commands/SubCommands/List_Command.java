package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class List_Command {
	
	public List_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Party.Commands.List") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 1) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party list"));
			return;
		}
		
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(PMessages.CMD_LIST_NO_PARTY.getMessage(p));
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
		
		p.sendMessage(PMessages.CMD_LIST_LIST.getMessage(p).replace("%LEADER_COUNT%", ""+party.getLeader().size()).replace("%MEMBER_COUNT%",""+party.getParticipants().size())
				.replace("%PARTY_LEADERS%", leaders).replace("%PARTY_MEMBERS%", members));
		return;
	}

}
