package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Deny_Command {
	
	public Deny_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Deny") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 2) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends deny <Name>")));
			return;
		}
		FriendHash pHash = FriendHash.getFriendHash(p.getUniqueId());
		String playerToDeny = null;
		if(args.length > 1) playerToDeny = args[1];
		else {
			if(pHash.getRequests().isEmpty()) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_NO_NEW_REQUEST.getMessage()));
				return;
			}
			Request rq = pHash.getRequests().getLast();
			if(System.currentTimeMillis() - (rq.getTimestamp()) > (60000*5)) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_NO_NEW_REQUEST.getMessage()));
				return;
			}
			playerToDeny = FriendHash.getName(rq.getPlayerToAdd());		
		}
		UUID toDeny = FriendHash.getUUIDFromName(playerToDeny);
		for(Request rq : pHash.getRequests()) {
			if(toDeny == null) break;
			if(rq.getPlayerToAdd().equals(toDeny)) {
				pHash.removeRequest(toDeny);
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_DENY_REQUEST.getMessage().replace("%NAME%", playerToDeny)));
				if(BungeeCord.getInstance().getPlayer(toDeny) != null)
					BungeeCord.getInstance().getPlayer(toDeny).sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_DENIED_REQUEST.getMessage().replace("%NAME%", p.getName())));
				return;
			}
		}
		
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_NO_REQUEST.getMessage().replace("%NAME%", playerToDeny)));
		return;
	}

}
