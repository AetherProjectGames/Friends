package de.HyChrod.Friends.Commands.SubCommands;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class List_Command {
	
	public List_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.List") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends list")));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		String online = "", offline = "";
		int on = 0, off = 0;
		for(Friendship fs : hash.getFriends()) {
			ProxiedPlayer friend = BungeeCord.getInstance().getPlayer(fs.getFriend());
			if(friend == null) {
				off++;
				if(offline.length() < 50)
					offline = offline + ", " + FriendHash.getName(fs.getFriend());
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
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_LIST_NO_FRIENDS.getMessage()));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_LIST_LIST.getMessage().replace("%ONLINE_FRIENDS%", online).replace("%OFFLINE_FRIENDS%", offline)
				.replace("%ONLINE_COUNT%", String.valueOf(on)).replace("%OFFLINE_COUNT%", String.valueOf(off))));
		return;
	}

}
