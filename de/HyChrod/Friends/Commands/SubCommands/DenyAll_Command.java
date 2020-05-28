package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class DenyAll_Command {
	
	public DenyAll_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.DenyAll")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length > 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends denyall"));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(!hash.getRequestsNew().isEmpty()) {
			int count = hash.getRequestsNew().size();
			for(Request rq : hash.getRequestsNew()) {
				hash.removeRequest(rq.getPlayerToAdd());
				if(FriendHash.isOnline(rq.getPlayerToAdd()))
					if(Configs.BUNGEEMODE.getBoolean()) FriendHash.sendPluginMessage(rq.getPlayerToAdd(), Messages.CMD_DENY_DENIED_REQUEST.getMessage(Bukkit.getPlayer(rq.getPlayerToAdd())).replace("%NAME%", p.getName()));
					else Bukkit.getPlayer(rq.getPlayerToAdd()).sendMessage(Messages.CMD_DENY_DENIED_REQUEST.getMessage(Bukkit.getPlayer(rq.getPlayerToAdd())).replace("%NAME%", p.getName()));
			}
			p.sendMessage(Messages.CMD_DENYALL.getMessage(p).replace("%REQUESTS_COUNT%", ""+count));
			return;
		}
		p.sendMessage(Messages.CMD_DENY_NO_NEW_REQUEST.getMessage(p));
		return;
		
	}

}
