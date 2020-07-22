package de.HyChrod.Friends.Commands.SubCommands;

import java.util.HashMap;
import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Status_Command {
	
	public static HashMap<UUID, Long> lastChangedStatus = new HashMap<UUID, Long>();
	
	public Status_Command(Friends friends, ProxiedPlayer p, String[] args) {
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(args.length == 1) {
			if(hash.getStatus() == null || (hash.getStatus() != null && hash.getStatus().length() < 1)) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_NO_STATUS.getMessage()));
				return;
			}
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_CURRENT_STATUS.getMessage().replace("%STATUS%", hash.getStatus())));
			return;
		}
		
		if(args.length == 2) {
			if(args[1].equalsIgnoreCase("set")) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends status set <Status>")));
				return;
			}
			if(!p.hasPermission("Friends.Commands.Status.Show") && !p.hasPermission("Friends.Commands.*")) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
				return;
			}
			
			String playerToCheck = args[1];
			if(!FriendHash.isPlayerValid(playerToCheck)) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.PLAYER_DOES_NOT_EXIST.getMessage().replace("%NAME%", playerToCheck)));
				return;
			}
			UUID toCheck = FriendHash.getUUIDFromName(playerToCheck);
			FriendHash checkHash = FriendHash.getFriendHash(toCheck);
			for(Friendship fs : checkHash.getFriends())
				if(fs.getFriend().equals(p.getUniqueId()) || toCheck.equals(p.getUniqueId())) {
					
					String status = checkHash.getStatus();
					if(status == null || (status != null && status.length() < 1)) {
						p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_FRIENDCHECK_NO_STATUS.getMessage().replace("%NAME%", playerToCheck)));
						return;
					}
					p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_FRIENDCHECK_SHOW_STATUS.getMessage().replace("%NAME%", playerToCheck).replace("%STATUS%", status)));
					return;
				}
			
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_FRIENDCHECK_NO_FRIENDS.getMessage().replace("%NAME%", playerToCheck)));
			return;
			
		}
		
		if(args.length > 2 && args[1].equalsIgnoreCase("set")) {
			if(!p.hasPermission("Friends.Commands.Status.Set") && !p.hasPermission("Friends.Commands.*")) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
				return;
			}
			
			String msg = "";
			for(int arg = 2; arg < args.length; arg++)
				msg = msg + " " + args[arg];
			msg = msg.substring(1);
			
			if(msg.length() > Configs.STATUS_LENGHT.getNumber()) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_STATUS_LENGHT.getMessage().replace("%LIMIT%", String.valueOf(Configs.STATUS_LENGHT.getNumber()))));
				return;
			}
			
			if(Configs.STATUS_FILTER.getBoolean()) {
				for(String phrases : Configs.getForbiddenPhrases())
					if(msg.toLowerCase().contains(phrases.toLowerCase())) {
						p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_ABUSIVE_PHRASE.getMessage().replace("%PHRASE%", phrases)));
						return;
					}
				
			}
			
			if(!canChangeStatus(p.getUniqueId())) {
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_CANT_CHANGE_YET.getMessage().replace("%REMAINING_TIME%", String.valueOf((
						Configs.STATUS_CHANGEDURATION.getNumber()-(System.currentTimeMillis()-lastChangedStatus.get(p.getUniqueId()))/1000)))));
				return;
			}
			
			lastChangedStatus.put(p.getUniqueId(), System.currentTimeMillis());
			hash.setStatus(msg);
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_STATUS_STATUS_SET.getMessage().replace("%STATUS%", msg)));
			return;
		}
	}
	
	public static boolean canChangeStatus(UUID uuid) {
		if(lastChangedStatus.containsKey(uuid) && !BungeeCord.getInstance().getPlayer(uuid).hasPermission("Friends.Status.ChangeLimit.ByPass")) {
			if((System.currentTimeMillis()-lastChangedStatus.get(uuid)) < (Configs.STATUS_CHANGEDURATION.getNumber()*1000)) return false;
			lastChangedStatus.remove(uuid);
		}
		return true;
	}

}
