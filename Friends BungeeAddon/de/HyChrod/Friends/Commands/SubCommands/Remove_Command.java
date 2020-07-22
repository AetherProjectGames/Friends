package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Remove_Command {
	
	public Remove_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Remove") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 2 || args.length == 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends remove <Name>")));
			return;
		}
		
		String playerToRemove = args[1];
		UUID toRemove = FriendHash.getUUIDFromName(playerToRemove);
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Friendship fs : hash.getFriends()) {
			if(toRemove == null) break;
			if(fs.getFriend().equals(toRemove)) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage().replace("%NAME%", playerToRemove)));
				hash.deleteFriend(toRemove);
				return;
			}
		}
		
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_REMOVE_NO_FRIENDS.getMessage().replace("%NAME%", playerToRemove)));
		return;
	}

}
