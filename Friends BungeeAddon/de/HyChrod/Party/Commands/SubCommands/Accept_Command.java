package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.Invite;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Accept_Command {
	
	public Accept_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Accept") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party accept <Name>")));
			return;
		}
		String toAccept = null;
		UUID acceptTo = null;
		if(args.length == 2) {
			toAccept = args[1];
			if(!FriendHash.isPlayerValid(toAccept)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_NO_INVITE.getMessage().replace("%NAME%", toAccept)));
				return;
			}
			acceptTo = FriendHash.getUUIDFromName(toAccept);
			if(!Parties.hasInvite(p.getUniqueId(), acceptTo)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_NO_INVITE.getMessage().replace("%NAME%", toAccept)));
				return;
			}
		}
		if(args.length == 1) {
			if(Parties.getInvites(p.getUniqueId()).isEmpty() || ((Parties.getInvites(p.getUniqueId()).getFirst().getTimestamp() + (Configs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000)) < System.currentTimeMillis())) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_NO_NEW_INVITE.getMessage()));
				return;
			}
			toAccept = FriendHash.getName(Parties.getInvites(p.getUniqueId()).getFirst().getSender());
			acceptTo = FriendHash.getUUIDFromName(toAccept);
		}
		
		if(BungeeCord.getInstance().getPlayer(acceptTo) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_EXPIRED.getMessage()));
			return;
		}
		
		for(Invite inv : Parties.getInvites(p.getUniqueId()))
			if(inv.getSender().equals(acceptTo)) {
				if((inv.getTimestamp() + (Configs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000)) < System.currentTimeMillis()) {
					p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_EXPIRED.getMessage()));
					return;
				}
				if(Parties.getParty(acceptTo) == null) {
					p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_PARTY_CLOSED.getMessage()));
					return;
				}
				
				Parties party = inv.getParty();
				if(party.getSize() >= Configs.PARTY_MAX_SIZE.getNumber()) {
					p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_LIMIT_REACHED.getMessage()));
					return;
				}
				
				party.addParticipant(p.getUniqueId());
				Parties.removeInvite(p.getUniqueId(), acceptTo);
				
				AsyncSQLQueueUpdater.addToQueue("insert into party_members(id,uuid) values ('" + party.getID() + "','" + p.getUniqueId().toString() + "') on duplicate key update id=values(id)");
				AsyncSQLQueueUpdater.addToQueue("insert into party_players(uuid,id) values ('" + p.getUniqueId().toString() + "','" + party.getID() + "') on duplicate key update id=values(id)");
				
				for(UUID member : party.getMembers())
					if(BungeeCord.getInstance().getPlayer(member) != null)
						BungeeCord.getInstance().getPlayer(member).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_NEW_MEMBER.getMessage().replace("%NAME%", p.getName())));
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_PARTY_JOIN.getMessage().replace("%NAME%", toAccept)));
				return;
			}
	}

}
