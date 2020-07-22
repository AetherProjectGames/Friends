package de.HyChrod.Friends.Commands.SubCommands;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DenyAll_Command {
	
	public DenyAll_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.DenyAll") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends denyall")));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(!hash.getRequests().isEmpty()) {
			int count = hash.getRequests().size();
			for(Request rq : hash.getRequests()) {
				hash.removeRequest(rq.getPlayerToAdd());
				if(BungeeCord.getInstance().getPlayer(rq.getPlayerToAdd()) != null)
					BungeeCord.getInstance().getPlayer(rq.getPlayerToAdd()).sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_DENIED_REQUEST.getMessage().replace("%NAME%", p.getName())));
			}
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENYALL.getMessage().replace("%REQUESTS_COUNT%", ""+count)));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_DENY_NO_NEW_REQUEST.getMessage()));
		return;
		
	}

}
