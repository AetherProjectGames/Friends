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

public class Kick_Command {
	
	public Kick_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Party.Commands.Kick") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party kick <Name>"));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(PMessages.CMD_KICK_NO_PARTY.getMessage(p));
			return;
		}
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(party.isLeader(p.getUniqueId())) {
			
			String toKick = args[1];
			if(FriendHash.isPlayerValid(toKick)) {
				UUID uuid = FriendHash.getUUIDFromName(toKick);
				if(party.getMembers().contains(uuid)) {
					party.removeLeader(uuid);
					party.removeParticipant(uuid);
					
					if(Configs.BUNGEEMODE.getBoolean()) {
						AsyncSQLQueueUpdater.addToQueue("delete from party_players where uuid='" + uuid.toString() + "'");
						AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + uuid.toString() + "'");
						AsyncSQLQueueUpdater.addToQueue("delete from party_leaders where uuid='" + uuid.toString() + "'");
					}
					
					if(Bukkit.getPlayer(uuid) != null)
						Bukkit.getPlayer(uuid).sendMessage(PMessages.CMD_KICK_KICKED.getMessage(p));
					for(UUID memb : party.getMembers())
						if(Bukkit.getPlayer(memb) != null)
							Bukkit.getPlayer(memb).sendMessage(PMessages.CMD_KICK_KICK.getMessage(p).replace("%NAME%", toKick));
					return;
				}
			}
			p.sendMessage(PMessages.CMD_KICK_NOT_IN_PARTY.getMessage(p).replace("%NAME%", toKick));
			return;
		}
		p.sendMessage(PMessages.CMD_KICK_NO_LEADER.getMessage(p));
		return;
	}

}
