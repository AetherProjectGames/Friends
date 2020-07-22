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

public class Promote_Command {
	
	public Promote_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Promote") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party promote <Name>")));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_NOPARTY.getMessage()));
			return;
		}
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(party.isLeader(p.getUniqueId())) {
			
			String toPromote = args[1];
			if(!FriendHash.isPlayerValid(toPromote)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_NOT_IN_PARTY.getMessage().replace("%NAME%", toPromote)));
				return;
			}
			UUID uuid = FriendHash.getUUIDFromName(toPromote);
			if(!party.getMembers().contains(uuid)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_NOT_IN_PARTY.getMessage().replace("%NAME%", toPromote)));
				return;
			}
			if(party.getLeader().contains(uuid)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_ALREADY_LEADER.getMessage().replace("%NAME%", toPromote)));
				return;
			}
			
			party.removeParticipant(uuid);
			party.makeLeader(uuid);
			
			AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + uuid.toString() + "'");
			AsyncSQLQueueUpdater.addToQueue("insert into party_leaders(id,uuid) values ('" + party.getID() + "','" + uuid.toString() + "') on duplicate key update id=values(id)");
			
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_PROMOTED.getMessage().replace("%NAME%", toPromote)));
			if(BungeeCord.getInstance().getPlayer(toPromote) != null)
				BungeeCord.getInstance().getPlayer(toPromote).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_NEW_LEADER.getMessage()));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_NO_LEADER.getMessage()));
		return;
	}

}
