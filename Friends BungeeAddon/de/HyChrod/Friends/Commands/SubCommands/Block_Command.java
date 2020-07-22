package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Block_Command {

	public Block_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Block") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length == 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends block <Name> (Note)")));
			return;
		}
		
		String playerToAdd = args[1];
		if(playerToAdd.equals(p.getName())) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_BLOCK_BLOCK_SELF.getMessage()));
			return;
		}
		
		if(!FriendHash.isPlayerValid(playerToAdd)) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.PLAYER_DOES_NOT_EXIST.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		
		UUID UUIDtoBlock = FriendHash.getUUIDFromName(playerToAdd);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(hash.isBlocked(UUIDtoBlock)) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_BLOCK_ALREADY_BLOCKED.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		
		String msg = "";
		if(args.length > 2) {
			if(!Configs.BLOCKCOMMENT_ENABLE.getBoolean()) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_BLOCK_ALREADY_BLOCKED.getMessage().replace("%NAME%", playerToAdd)));
				return;
			}
			
			for(int arg = 2; arg < args.length; arg++)
				msg = msg + " " + args[arg];
			msg = msg.substring(1);
			
			if(msg.length() > Configs.BLOCKCOMMENT_CHARLIMIT.getNumber()) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_BLOCK_NOTE_LIMIT.getMessage().replace("%LIMIT%", String.valueOf(Configs.BLOCKCOMMENT_CHARLIMIT.getNumber()))));
				return;
			}
			
		}
		
		hash.blockPlayer(UUIDtoBlock, msg);
		
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_BLOCK_BLOCK_PLAYER.getMessage().replace("%NAME%", playerToAdd)));
		if(msg.length() > 0) p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_BLOCK_BLOCK_NOTE.getMessage().replace("%NOTE%", msg)));
		return;
	}
	
}
