package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Jump_Command {
	
	public Jump_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!Configs.JUMPING_ENABLE.getBoolean()) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNKNOWN_COMMAND.getMessage()));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Jump") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length < 2) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends jump <Name>")));
			return;
		}
		
		String playerToJump = args[1];
		if(!FriendHash.isPlayerValid(playerToJump)) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.PLAYER_DOES_NOT_EXIST.getMessage().replace("%NAME%", playerToJump)));
			return;
		}
		UUID uuid = FriendHash.getUUIDFromName(playerToJump);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		
		for(Friendship fs : hash.getFriends())
			if(fs.getFriend().equals(uuid)) {
				String name = fs.hasNickname() ? fs.getNickname() : playerToJump;
				if(FriendHash.isOnline(uuid)) {
					if(!FriendHash.getOptions(uuid).getJumping()) {
						p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_DISABLED.getMessage().replace("%NAME%", name)));
						return;
					}
					String server = BungeeCord.getInstance().getPlayer(uuid).getServer().getInfo().getName();
					if(Configs.getForbiddenServers().contains(server)) {
						p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_SERVER_DISABLED.getMessage().replace("%SERVER%", server)));
						return;
					}
					p.connect(BungeeCord.getInstance().getPlayer(uuid).getServer().getInfo());
					p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_JUMPTOFRIEND.getMessage().replace("%NAME%", name)));
					BungeeCord.getInstance().getPlayer(uuid).sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_JUMPTOYOU.getMessage().replace("%NAME%", p.getName())));
					return;
				}
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_OFFLINE.getMessage().replace("%NAME%", name)));
				return;
			}
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_NOFRIENDS.getMessage().replace("%NAME%", playerToJump)));
	}

}
