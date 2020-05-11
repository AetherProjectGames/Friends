package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Block_Command {

	public Block_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Basic")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length == 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends block <Name>"));
			return;
		}
		
		String playerToAdd = args[1];
		if(playerToAdd.equals(p.getName())) {
			p.sendMessage(Messages.CMD_BLOCK_BLOCK_SELF.getMessage(p));
			return;
		}
		
		if(!FriendHash.isPlayerValid(playerToAdd)) {
			p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		
		UUID UUIDtoBlock = FriendHash.getUUIDFromName(playerToAdd);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(hash.isBlocked(UUIDtoBlock)) {
			p.sendMessage(Messages.CMD_BLOCK_ALREADY_BLOCKED.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		
		String msg = "";
		if(args.length > 2) {
			if(!Configs.BLOCKCOMMENT_ENABLE.getBoolean()) {
				p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends block <Name>"));
				return;
			}
			
			for(int arg = 2; arg < args.length; arg++)
				msg = msg + " " + args[arg];
			msg = msg.substring(1);
			
			if(msg.length() > Configs.BLOCKCOMMENT_CHARLIMIT.getNumber()) {
				p.sendMessage(Messages.CMD_BLOCK_NOTE_LIMIT.getMessage(p).replace("%LIMIT%", String.valueOf(Configs.BLOCKCOMMENT_CHARLIMIT.getNumber())));
				return;
			}
			
		}
		
		hash.blockPlayer(UUIDtoBlock, msg);
		
		p.sendMessage(Messages.CMD_BLOCK_BLOCK_PLAYER.getMessage(p).replace("%NAME%", playerToAdd));
		if(msg.length() > 0) p.sendMessage(Messages.CMD_BLOCK_BLOCK_NOTE.getMessage(p).replace("%NOTE%", msg));
		return;
	}
	
}
