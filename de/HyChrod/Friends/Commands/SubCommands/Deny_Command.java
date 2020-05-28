package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Deny_Command {
	
	public Deny_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Deny")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length > 2) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends deny <Name>"));
			return;
		}
		FriendHash pHash = FriendHash.getFriendHash(p.getUniqueId());
		String playerToDeny = null;
		if(args.length > 1) playerToDeny = args[1];
		else {
			if(pHash.getRequestsNew().isEmpty()) {
				p.sendMessage(Messages.CMD_DENY_NO_NEW_REQUEST.getMessage(p));
				return;
			}
			Request rq = pHash.getRequestsNew().getLast();
			if(System.currentTimeMillis() - (rq.getTimestamp()) > (60000*5)) {
				p.sendMessage(Messages.CMD_DENY_NO_NEW_REQUEST.getMessage(p));
				return;
			}
			playerToDeny = Bukkit.getOfflinePlayer(rq.getPlayerToAdd()).getName();
		}
		UUID toDeny = FriendHash.getUUIDFromName(playerToDeny);
		for(Request rq : pHash.getRequests()) {
			if(toDeny == null) break;
			if(rq.getPlayerToAdd().equals(toDeny)) {
				pHash.removeRequest(toDeny);
				p.sendMessage(Messages.CMD_DENY_DENY_REQUEST.getMessage(p).replace("%NAME%", playerToDeny));
				if(FriendHash.isOnline(toDeny))
					if(Configs.BUNGEEMODE.getBoolean()) FriendHash.sendPluginMessage(toDeny, Messages.CMD_DENY_DENIED_REQUEST.getMessage(Bukkit.getPlayer(toDeny)).replace("%NAME%", p.getName()));
					else Bukkit.getPlayer(toDeny).sendMessage(Messages.CMD_DENY_DENIED_REQUEST.getMessage(Bukkit.getPlayer(toDeny)).replace("%NAME%", p.getName()));
				return;
			}
		}
		p.sendMessage(Messages.CMD_DENY_NO_REQUEST.getMessage(p).replace("%NAME%", playerToDeny));
		return;
	}

}
