package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Accept_Command {
	
	public Accept_Command(Friends friends, ProxiedPlayer p, String args[]) {
		if(args.length > 2) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends accept <Name>")));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Accept") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		FriendHash pHash = FriendHash.getFriendHash(p.getUniqueId());
		
		String playerToAccept = null;
		if(args.length > 1) playerToAccept = args[1];
		else {
			if(pHash.getRequests().isEmpty()) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_NO_NEW_REQUEST.getMessage()));
				return;
			}
			Request rq = pHash.getRequests().getLast();
			if(System.currentTimeMillis() - (rq.getTimestamp()) > (60000*5)) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_NO_NEW_REQUEST.getMessage()));
				return;
			}
			playerToAccept = BungeeCord.getInstance().getPlayerByOfflineUUID(rq.getPlayerToAdd()).getName();
		}
		UUID toAccept = FriendHash.getUUIDFromName(playerToAccept);
		
		for(Request rq : pHash.getRequests()) {
			if(toAccept == null) break;
			if(rq.getPlayerToAdd().equals(toAccept)) {
				
				int limit = p.hasPermission("Friends.FriendLimit.Extended") ? Configs.FRIEND_LIMIT_EXT.getNumber() : Configs.FRIEND_LIMIT.getNumber();
				if(pHash.getFriends().size() >= limit) {
					p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_LIMIT_REACHED.getMessage().replace("%LIMIT%", String.valueOf(limit))));
					return;
				}
				
				pHash.makeFriend(toAccept);
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_NEW_FRIEND.getMessage().replace("%NAME%", playerToAccept)));
				return;
				
			}
		}
		
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_NO_REQUEST.getMessage().replace("%NAME%", playerToAccept)));
		return;
	}

}
