package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Messages;

public class AcceptAll_Command {
	
	public AcceptAll_Command(Friends friends, Player p, String[] args) {
		if(args.length > 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends acceptall"));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Basic")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(!hash.getRequestsNew().isEmpty()) {
			int count = hash.getRequestsNew().size();
			for(Request rq : hash.getRequestsNew())
				hash.makeFriend(rq.getPlayerToAdd());
			p.sendMessage(Messages.CMD_ACCEPTALL.getMessage(p).replace("%REQUESTS_COUNT%", ""+count));
			return;
		}
		p.sendMessage(Messages.CMD_ACCEPT_NO_NEW_REQUEST.getMessage(p));
		return;
	}

}
