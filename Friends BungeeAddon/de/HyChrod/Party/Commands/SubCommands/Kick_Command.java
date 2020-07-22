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

public class Kick_Command {
	
	public Kick_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Kick") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party kick <Name>")));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_KICK_NO_PARTY.getMessage()));
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
					
					AsyncSQLQueueUpdater.addToQueue("delete from party_players where uuid='" + uuid.toString() + "'");
					AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + uuid.toString() + "'");
					AsyncSQLQueueUpdater.addToQueue("delete from party_leaders where uuid='" + uuid.toString() + "'");
					
					if(BungeeCord.getInstance().getPlayer(uuid) != null)
						BungeeCord.getInstance().getPlayer(uuid).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_KICK_KICKED.getMessage()));
					for(UUID memb : party.getMembers())
						if(BungeeCord.getInstance().getPlayer(memb) != null)
							BungeeCord.getInstance().getPlayer(memb).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_KICK_KICK.getMessage().replace("%NAME%", toKick)));
					return;
				}
			}
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_KICK_NOT_IN_PARTY.getMessage().replace("%NAME%", toKick)));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_KICK_NO_LEADER.getMessage()));
		return;
	}

}
