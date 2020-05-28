package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Messages;

public class Remove_Command {
	
	public Remove_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Remove")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length > 2 || args.length == 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends remove <Name>"));
			return;
		}
		
		String playerToRemove = args[1];
		UUID toRemove = FriendHash.getUUIDFromName(playerToRemove);
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Friendship fs : hash.getFriendsNew()) {
			if(toRemove == null) break;
			if(fs.getFriend().equals(toRemove)) {
				p.sendMessage(Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage(p).replace("%NAME%", playerToRemove));
				hash.deleteFriend(toRemove);
				return;
			}
		}
		
		p.sendMessage(Messages.CMD_REMOVE_NO_FRIENDS.getMessage(p).replace("%NAME%", playerToRemove));
		return;
	}

}
