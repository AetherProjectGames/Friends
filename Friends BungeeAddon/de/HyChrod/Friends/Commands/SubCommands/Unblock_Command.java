package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Unblock_Command {
	
	public Unblock_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Unblock") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length == 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends unblock <Name>")));
			return;
		}
		String playerToRemove = args[1];
		if(!FriendHash.isPlayerValid(playerToRemove)) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.PLAYER_DOES_NOT_EXIST.getMessage().replace("%NAME%", playerToRemove)));
			return;
		}
		
		UUID toUnblock = FriendHash.getUUIDFromName(playerToRemove);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Blockplayer bl : hash.getBlocked())
			if(bl.getBlocked().equals(toUnblock)) {
				hash.removeBlocked(toUnblock);
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNBLOCK_UNBLOCKED.getMessage().replace("%NAME%", playerToRemove)));
				return;
			}
		
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNBLOCK_PLAYER_NOT_BLOCKED.getMessage().replace("%NAME%", playerToRemove)));
		return;
	}

}
