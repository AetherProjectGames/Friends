package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Messages;

public class Unblock_Command {
	
	public Unblock_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Unblock")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length == 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends unblock <Name>"));
			return;
		}
		String playerToRemove = args[1];
		if(!FriendHash.isPlayerValid(playerToRemove)) {
			p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", playerToRemove));
			return;
		}
		
		UUID toUnblock = FriendHash.getUUIDFromName(playerToRemove);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Blockplayer bl : hash.getBlockedNew())
			if(bl.getBlocked().equals(toUnblock)) {
				hash.removeBlocked(toUnblock);
				p.sendMessage(Messages.CMD_UNBLOCK_UNBLOCKED.getMessage(p).replace("%NAME%", playerToRemove));
				return;
			}
		
		p.sendMessage(Messages.CMD_UNBLOCK_PLAYER_NOT_BLOCKED.getMessage(p).replace("%NAME%", playerToRemove));
		return;
	}

}
