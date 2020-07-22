package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Leave_Command {
	
	public Leave_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Party.Commands.Leave") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 1) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party leave"));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(PMessages.CMD_LEAVE_NO_PARTY.getMessage(p));
			return;
		}
		Parties party = Parties.getParty(p.getUniqueId());
		party.removeLeader(p.getUniqueId());
		party.removeParticipant(p.getUniqueId());
		
		for(UUID member : party.getMembers())
			if(Bukkit.getPlayer(member) != null)
				Bukkit.getPlayer(member).sendMessage(PMessages.CMD_LEAVE_MEMBER_LEAVE.getMessage(p).replace("%NAME%", p.getName()));
		p.sendMessage(PMessages.CMD_LEAVE_LEAVE.getMessage(p));
		
		if(Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("delete from party_players where uuid='" + p.getUniqueId().toString() + "'");
			AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + p.getUniqueId().toString() + "'");
			AsyncSQLQueueUpdater.addToQueue("delete from party_leaders where uuid='" + p.getUniqueId().toString() + "'");
			if(party.getMembers().isEmpty()) {
				AsyncSQLQueueUpdater.addToQueue("delete from party where id='" + party.getID() + "'");
				return;
			}
		}
		
		if(party.getLeader().isEmpty()) {
			if(party.getMembers().isEmpty()) return;
			UUID newLeader = party.getMembers().getFirst();
			party.removeParticipant(newLeader);
			party.makeLeader(newLeader);
			
			if(Configs.BUNGEEMODE.getBoolean()) {
				AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + newLeader.toString() + "'");
				AsyncSQLQueueUpdater.addToQueue("insert into party_leaders(id,uuid) values ('" + party.getID() + "','" + newLeader.toString() + "') on duplicate key update id=values(id)");
			}
			
			if(Bukkit.getPlayer(newLeader) != null)
				Bukkit.getPlayer(newLeader).sendMessage(PMessages.CMD_PROMOTE_NEW_LEADER.getMessage(p));
		}
	}

}
