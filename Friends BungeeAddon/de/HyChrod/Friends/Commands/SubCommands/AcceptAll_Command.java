package de.HyChrod.Friends.Commands.SubCommands;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AcceptAll_Command {
	
	public AcceptAll_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(args.length > 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends acceptall")));
			return;
		}
		if(!p.hasPermission("Friends.Commands.AcceptAll") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(!hash.getRequests().isEmpty()) {
			int count = hash.getRequests().size();
			for(Request rq : hash.getRequests())
				hash.makeFriend(rq.getPlayerToAdd());
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPTALL.getMessage().replace("%REQUESTS_COUNT%", ""+count)));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_NO_NEW_REQUEST.getMessage()));
		return;
	}

}
