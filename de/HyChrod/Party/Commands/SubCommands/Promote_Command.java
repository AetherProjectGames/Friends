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

public class Promote_Command {
	
	public Promote_Command(Friends friends, Player p, String[] args) {
		if(args.length != 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party promote <Name>"));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(PMessages.CMD_PROMOTE_NOPARTY.getMessage(p));
			return;
		}
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(party.isLeader(p.getUniqueId())) {
			
			String toPromote = args[1];
			if(!FriendHash.isPlayerValid(toPromote)) {
				p.sendMessage(PMessages.CMD_PROMOTE_NOT_IN_PARTY.getMessage(p).replace("%NAME%", toPromote));
				return;
			}
			UUID uuid = FriendHash.getUUIDFromName(toPromote);
			if(!party.getMembers().contains(uuid)) {
				p.sendMessage(PMessages.CMD_PROMOTE_NOT_IN_PARTY.getMessage(p).replace("%NAME%", toPromote));
				return;
			}
			if(party.getLeader().contains(uuid)) {
				p.sendMessage(PMessages.CMD_PROMOTE_ALREADY_LEADER.getMessage(p).replace("%NAME%", toPromote));
				return;
			}
			
			party.removeParticipant(uuid);
			party.makeLeader(uuid);
			
			if(Configs.BUNGEEMODE.getBoolean()) {
				AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + uuid.toString() + "'");
				AsyncSQLQueueUpdater.addToQueue("insert into party_leaders(id,uuid) values ('" + party.getID() + "','" + uuid.toString() + "') on duplicate key update id=values(id)");
			}
			
			p.sendMessage(PMessages.CMD_PROMOTE_PROMOTED.getMessage(p).replace("%NAME%", toPromote));
			if(Bukkit.getPlayer(toPromote) != null)
				Bukkit.getPlayer(toPromote).sendMessage(PMessages.CMD_PROMOTE_NEW_LEADER.getMessage(p));
			return;
		}
		p.sendMessage(PMessages.CMD_PROMOTE_NO_LEADER.getMessage(p));
		return;
	}

}
