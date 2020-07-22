package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Jump_Command {
	
	public Jump_Command(Friends friends, Player p, String[] args) {
		if(!Configs.JUMPING_ENABLE.getBoolean()) {
			p.sendMessage(Messages.CMD_UNKNOWN_COMMAND.getMessage(p));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Jump") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length < 2) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends jump <Name>"));
			return;
		}
		
		String playerToJump = args[1];
		if(!FriendHash.isPlayerValid(playerToJump)) {
			p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", playerToJump));
			return;
		}
		UUID uuid = FriendHash.getUUIDFromName(playerToJump);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		
		if(hash.getFriendship(uuid) == null) {
			p.sendMessage(Messages.CMD_JUMP_NOFRIENDS.getMessage(p).replace("%NAME%", playerToJump));
			return;
		}
		
		if(FriendHash.isOnline(uuid)) {
			if(!FriendHash.getOptions(uuid).getJumping()) {
				p.sendMessage(Messages.CMD_JUMP_DISABLED.getMessage(p).replace("%NAME%", playerToJump));
				return;
			}
			
			if(Configs.BUNGEEMODE.getBoolean()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF(p.getUniqueId().toString());
				out.writeUTF(uuid.toString());
				p.sendPluginMessage(friends, "friends:connect", out.toByteArray());				
				return;
			}
			
			if(Configs.getForbiddenWorlds().contains(Bukkit.getPlayer(uuid).getWorld().getName())) {
				p.sendMessage(Messages.CMD_JUMP_SERVER_BLOCKED.getMessage(p));
				return;
			}
			
			p.sendMessage(Messages.CMD_JUMP_JUMPTOFRIEND.getMessage(p).replace("%NAME%", playerToJump));
			Bukkit.getScheduler().scheduleSyncDelayedTask(Friends.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					p.teleport(Bukkit.getPlayer(uuid));
				}
			});
			Bukkit.getPlayer(uuid).sendMessage(Messages.CMD_JUMP_JUMPTOYOU.getMessage(Bukkit.getPlayer(uuid)).replace("%NAME%", p.getName()));
			return;
		}
		p.sendMessage(Messages.CMD_JUMP_OFFLINE.getMessage(p).replace("%NAME%", playerToJump));
		return;
	}

}
