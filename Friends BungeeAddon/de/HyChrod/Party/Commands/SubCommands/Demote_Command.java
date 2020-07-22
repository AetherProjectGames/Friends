package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Demote_Command {
	
	public Demote_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Demote") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party demote <Name>")));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_NO_PARTY.getMessage()));
			return;
		}
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(party.isLeader(p.getUniqueId())) {
			
			String toDemote = args[1];
			if(!FriendHash.isPlayerValid(toDemote)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_NOT_IN_PARTY.getMessage().replace("%NAME%", toDemote)));
				return;
			}
			UUID uuid = FriendHash.getUUIDFromName(toDemote);
			if(!party.getMembers().contains(uuid)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_NOT_IN_PARTY.getMessage().replace("%NAME%", toDemote)));
				return;
			}
			if(party.getParticipants().contains(uuid)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_ALREADY_MEMBER.getMessage().replace("%NAME%", toDemote)));
				return;
			}
			
			party.removeLeader(uuid);
			party.addParticipant(uuid);
			
			AsyncSQLQueueUpdater.addToQueue("delete from party_leaders where uuid='" + uuid.toString() + "'");
			AsyncSQLQueueUpdater.addToQueue("insert into party_members(id,uuid) values ('" + party.getID() + "','" + uuid.toString() + "')");
			
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_DEMOTED.getMessage().replace("%NAME%", toDemote)));
			if(BungeeCord.getInstance().getPlayer(toDemote) != null)
				BungeeCord.getInstance().getPlayer(toDemote).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_NEW_MEMBER.getMessage()));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DEMOTE_NO_LEADER.getMessage()));
		return;
	}

}
