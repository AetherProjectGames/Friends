package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PConfigs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Join_Command {

	public Join_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Party.Commands.Join") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party join <Name>"));
			return;
		}
		
		String toJoin = args[1];
		if(!FriendHash.isPlayerValid(toJoin)) {
			p.sendMessage(PMessages.CMD_JOIN_NO_PARTY.getMessage(p).replace("%NAME%", toJoin));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) != null) {
			p.sendMessage(PMessages.CMD_JOIN_IN_PARTY.getMessage(p));
			return;
		}
		
		UUID join = FriendHash.getUUIDFromName(toJoin);
		if(Parties.getParty(join) == null) {
			p.sendMessage(PMessages.CMD_JOIN_NO_PARTY.getMessage(p).replace("%NAME%", toJoin));
			return;
		}
		
		Parties party = Parties.getParty(join);
		if(!party.isPublic()) {
			p.sendMessage(PMessages.CMD_JOIN_PRIVATE.getMessage(p).replace("%NAME%", toJoin));
			return;
		}
		
		if(party.getMembers().size() >= PConfigs.PARTY_MAX_SIZE.getNumber()) {
			p.sendMessage(PMessages.CMD_JOIN_PARTY_LIMIT.getMessage(p));
			return;
		}
		
		party.addParticipant(p.getUniqueId());
		Parties.removeInvite(p.getUniqueId(), join);
		
		if(Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("insert into party_players(uuid,id) values ('" + p.getUniqueId().toString() + "','" + party.getID() + "') on duplicate key update id=values(id)");
			AsyncSQLQueueUpdater.addToQueue("insert into party_members(id,uuid) values ('" + party.getID() + "','" + p.getUniqueId().toString() + "') on duplicate key update id=values(id)");
		}
		
		for(UUID member : party.getMembers())
			if(Bukkit.getPlayer(member) != null && !member.equals(p.getUniqueId()))
				Bukkit.getPlayer(member).sendMessage(PMessages.CMD_ACCEPT_NEW_MEMBER.getMessage(p).replace("%NAME%", p.getName()));
		p.sendMessage(PMessages.CMD_ACCEPT_PARTY_JOIN.getMessage(p).replace("%NAME%", toJoin));
		return;
		
	}
	
}
