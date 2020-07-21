package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Demote_Command {
	
	public Demote_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Party.Commands.Demote") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party demote <Name>"));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(PMessages.CMD_DEMOTE_NO_PARTY.getMessage(p));
			return;
		}
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(party.isLeader(p.getUniqueId())) {
			
			String toDemote = args[1];
			if(!FriendHash.isPlayerValid(toDemote)) {
				p.sendMessage(PMessages.CMD_DEMOTE_NOT_IN_PARTY.getMessage(p).replace("%NAME%", toDemote));
				return;
			}
			UUID uuid = FriendHash.getUUIDFromName(toDemote);
			if(!party.getMembers().contains(uuid)) {
				p.sendMessage(PMessages.CMD_DEMOTE_NOT_IN_PARTY.getMessage(p).replace("%NAME%", toDemote));
				return;
			}
			if(party.getParticipants().contains(uuid)) {
				p.sendMessage(PMessages.CMD_DEMOTE_ALREADY_MEMBER.getMessage(p).replace("%NAME%", toDemote));
				return;
			}
			
			party.removeLeader(uuid);
			party.addParticipant(uuid);
			
			if(Configs.BUNGEEMODE.getBoolean()) {
				AsyncSQLQueueUpdater.addToQueue("delete from party_leaders where uuid='" + uuid.toString() + "'");
				AsyncSQLQueueUpdater.addToQueue("insert into party_members(id,uuid) values ('" + party.getID() + "','" + uuid.toString() + "')");
			}
			
			p.sendMessage(PMessages.CMD_DEMOTE_DEMOTED.getMessage(p).replace("%NAME%", toDemote));
			if(Bukkit.getPlayer(toDemote) != null)
				Bukkit.getPlayer(toDemote).sendMessage(PMessages.CMD_DEMOTE_NEW_MEMBER.getMessage(p));
			return;
		}
		p.sendMessage(PMessages.CMD_DEMOTE_NO_LEADER.getMessage(p));
		return;
	}

}
