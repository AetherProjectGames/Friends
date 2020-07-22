package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Messages;

public class UnblockAll_Command {
	
	public UnblockAll_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.UnblockAll") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length > 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends unblockall"));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(!hash.getBlockedNew().isEmpty()) {
			int count = hash.getBlockedNew().size();
			for(Blockplayer bl : hash.getBlockedNew())
				hash.removeBlocked(bl.getBlocked());
			p.sendMessage(Messages.CMD_UNBLOCKALL_UNBLOCK.getMessage(p).replace("%BLOCKED_COUNT%", ""+count));
			return;
		}
		p.sendMessage(Messages.CMD_UNBLOCKALL_NOPLAYER.getMessage(p));
		return;
	}

}
