package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Accept_Command {
	
	public Accept_Command(Friends friends, Player p, String args[]) {
		if(args.length > 2) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends accept <Name>"));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Basic")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		FriendHash pHash = FriendHash.getFriendHash(p.getUniqueId());
		
		String playerToAccept = null;
		if(args.length > 1) playerToAccept = args[1];
		else {
			if(pHash.getRequestsNew().isEmpty()) {
				p.sendMessage(Messages.CMD_ACCEPT_NO_NEW_REQUEST.getMessage(p));
				return;
			}
			Request rq = pHash.getRequestsNew().getLast();
			if(System.currentTimeMillis() - (rq.getTimestamp()) > (60000*5)) {
				p.sendMessage(Messages.CMD_ACCEPT_NO_NEW_REQUEST.getMessage(p));
				return;
			}
			playerToAccept = Bukkit.getOfflinePlayer(rq.getPlayerToAdd()).getName();
		}
		UUID toAccept = FriendHash.getUUIDFromName(playerToAccept);
		
		for(Request rq : pHash.getRequestsNew()) {
			if(toAccept == null) break;
			if(rq.getPlayerToAdd().equals(toAccept)) {
				
				int limit = p.hasPermission("Friends.FriendLimit.Extended") ? Configs.FRIEND_LIMIT_EXT.getNumber() : Configs.FRIEND_LIMIT.getNumber();
				if(pHash.getFriendsNew().size() >= limit) {
					p.sendMessage(Messages.CMD_ACCEPT_LIMIT_REACHED.getMessage(p).replace("%LIMIT%", String.valueOf(limit)));
					return;
				}
				
				pHash.makeFriend(toAccept);
				p.sendMessage(Messages.CMD_ACCEPT_NEW_FRIEND.getMessage(p).replace("%NAME%", playerToAccept));
				return;
				
			}
		}
		
		p.sendMessage(Friends.getMessage("Messages.Commands.AcceptCommand.NoRequest").replace("%NAME%", playerToAccept));
		return;
	}

}
