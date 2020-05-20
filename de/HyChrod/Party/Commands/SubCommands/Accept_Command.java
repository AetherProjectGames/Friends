package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.Invite;
import de.HyChrod.Party.Utilities.PConfigs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Accept_Command {
	
	public Accept_Command(Friends friends, Player p, String[] args) {
		if(args.length > 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party accept <Name>"));
			return;
		}
		String toAccept = null;
		UUID acceptTo = null;
		if(args.length == 2) {
			toAccept = args[1];
			if(!FriendHash.isPlayerValid(toAccept)) {
				p.sendMessage(PMessages.CMD_ACCEPT_NO_INVITE.getMessage(p).replace("%NAME%", toAccept));
				return;
			}
			acceptTo = FriendHash.getUUIDFromName(toAccept);
			if(!Parties.hasInvite(p.getUniqueId(), acceptTo)) {
				p.sendMessage(PMessages.CMD_ACCEPT_NO_INVITE.getMessage(p).replace("%NAME%", toAccept));
				return;
			}
		}
		if(args.length == 1) {
			if(Parties.getInvites(p.getUniqueId()).isEmpty() || ((Parties.getInvites(p.getUniqueId()).getFirst().getTimestamp() + (PConfigs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000)) < System.currentTimeMillis())) {
				p.sendMessage(PMessages.CMD_ACCEPT_NO_NEW_INVITE.getMessage(p));
				return;
			}
			toAccept = FriendHash.getName(Parties.getInvites(p.getUniqueId()).getFirst().getSender());
			acceptTo = FriendHash.getUUIDFromName(toAccept);
		}
		
		if(Bukkit.getPlayer(acceptTo) == null) {
			p.sendMessage(PMessages.CMD_ACCEPT_EXPIRED.getMessage(p));
			return;
		}
		
		for(Invite inv : Parties.getInvites(p.getUniqueId()))
			if(inv.getSender().equals(acceptTo)) {
				if((inv.getTimestamp() + (PConfigs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000)) < System.currentTimeMillis()) {
					p.sendMessage(PMessages.CMD_ACCEPT_EXPIRED.getMessage(p));
					return;
				}
				if(Parties.getParty(acceptTo) == null) {
					p.sendMessage(PMessages.CMD_ACCEPT_PARTY_CLOSED.getMessage(p));
					return;
				}
				
				Parties party = inv.getParty();
				if(party.getSize() >= PConfigs.PARTY_MAX_SIZE.getNumber()) {
					p.sendMessage(PMessages.CMD_ACCEPT_LIMIT_REACHED.getMessage(p));
					return;
				}
				
				party.addParticipant(p.getUniqueId());
				Parties.removeInvite(p.getUniqueId(), acceptTo);
				
				if(Configs.BUNGEEMODE.getBoolean()) {
					AsyncSQLQueueUpdater.addToQueue("insert into party_members(id,uuid) values ('" + party.getID() + "','" + p.getUniqueId().toString() + "') on duplicate key update id=values(id)");
					AsyncSQLQueueUpdater.addToQueue("insert into party_players(uuid,id) values ('" + p.getUniqueId().toString() + "','" + party.getID() + "') on duplicate key update id=values(id)");
				}
				
				for(UUID member : party.getMembers())
					if(Bukkit.getPlayer(member) != null)
						Bukkit.getPlayer(member).sendMessage(PMessages.CMD_ACCEPT_NEW_MEMBER.getMessage(p).replace("%NAME%", p.getName()));
				p.sendMessage(PMessages.CMD_ACCEPT_PARTY_JOIN.getMessage(p).replace("%NAME%", toAccept));
				return;
			}
	}

}
