package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Messages;

public class List_Command {
	
	public List_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Basic")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length > 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends list"));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		String online = "", offline = "";
		int on = 0, off = 0;
		for(Friendship fs : hash.getFriendsNew()) {
			Player friend = Bukkit.getPlayer(fs.getFriend());
			if(friend == null) {
				off++;
				if(offline.length() < 50)
					offline = offline + ", " + Bukkit.getOfflinePlayer(fs.getFriend()).getName();
				else if(!offline.endsWith("(...)"))
					offline = offline + ", (...)";
			}
			else {
				on++;
				online = online + ", " + friend.getName();
			}
		}
		if(online.length() > 0) online = online.substring(2);
		if(offline.length() > 0) offline = offline.substring(2);
		if(online.length() < 1 && offline.length() < 1) {
			p.sendMessage(Messages.CMD_LIST_NO_FRIENDS.getMessage(p));
			return;
		}
		p.sendMessage(Messages.CMD_LIST_LIST.getMessage(p).replace("%ONLINE_FRIENDS%", online).replace("%OFFLINE_FRIENDS%", offline)
				.replace("%ONLINE_COUNT%", String.valueOf(on)).replace("%OFFLINE_COUNT%", String.valueOf(off)));
		return;
	}

}
