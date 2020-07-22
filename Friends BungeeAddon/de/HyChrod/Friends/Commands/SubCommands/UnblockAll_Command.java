package de.HyChrod.Friends.Commands.SubCommands;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnblockAll_Command {
	
	public UnblockAll_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.UnblokAll") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends unblockall")));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(!hash.getBlocked().isEmpty()) {
			int count = hash.getBlocked().size();
			for(Blockplayer bl : hash.getBlocked())
				hash.removeBlocked(bl.getBlocked());
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNBLOCKALL_UNBLOCK.getMessage().replace("%BLOCKED_COUNT%", ""+count)));
			return;
		}
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNBLOCKALL_NOPLAYER.getMessage()));
		return;
	}

}
