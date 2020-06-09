package de.HyChrod.Friends.Commands.SubCommands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Status_Command {
	
	public static HashMap<UUID, Long> lastChangedStatus = new HashMap<UUID, Long>();
	
	public Status_Command(Friends friends, Player p, String[] args) {
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(args.length == 1) {
			if(hash.getStatus() == null || (hash.getStatus() != null && hash.getStatus().length() < 1)) {
				p.sendMessage(Messages.CMD_STATUS_NO_STATUS.getMessage(p));
				return;
			}
			p.sendMessage(Messages.CMD_STATUS_CURRENT_STATUS.getMessage(p).replace("%STATUS%", (Configs.ALLOW_STATUS_COLOR.getBoolean() ? ChatColor.translateAlternateColorCodes('&', hash.getStatus()) : hash.getStatus())));
			return;
		}
		
		if(args.length == 2) {
			if(args[1].equalsIgnoreCase("set")) {
				p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends status set <Status>"));
				return;
			}
			if(!p.hasPermission("Friends.Commands.Status.Show") && !p.hasPermission("Friends.Commands.*")) {
				p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
				return;
			}
			
			String playerToCheck = args[1];
			if(!FriendHash.isPlayerValid(playerToCheck)) {
				p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", playerToCheck));
				return;
			}
			UUID toCheck = FriendHash.getUUIDFromName(playerToCheck);
			FriendHash checkHash = FriendHash.getFriendHash(toCheck);
			for(Friendship fs : checkHash.getFriendsNew())
				if(fs.getFriend().equals(p.getUniqueId()) || toCheck.equals(p.getUniqueId())) {
					
					String status = checkHash.getStatus();
					if(status == null || (status != null && status.length() < 1)) {
						p.sendMessage(Messages.CMD_STATUS_FRIENDCHECK_NO_STATUS.getMessage(p).replace("%NAME%", playerToCheck));
						return;
					}
					p.sendMessage(Messages.CMD_STATUS_FRIENDCHECK_SHOW_STATUS.getMessage(p).replace("%NAME%", playerToCheck)
							.replace("%STATUS%", (Configs.ALLOW_STATUS_COLOR.getBoolean() ? ChatColor.translateAlternateColorCodes('&', status) : status)));
					return;
				}
			
			p.sendMessage(Messages.CMD_STATUS_FRIENDCHECK_NO_FRIENDS.getMessage(p).replace("%NAME%", playerToCheck));
			return;
			
		}
		
		if(args.length > 2 && args[1].equalsIgnoreCase("set")) {
			if(!p.hasPermission("Friends.Commands.Status.Set") && !p.hasPermission("Friends.Commands.*")) {
				p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
				return;
			}
			
			String msg = "";
			for(int arg = 2; arg < args.length; arg++)
				msg = msg + " " + args[arg];
			msg = msg.substring(1);
			
			if(msg.length() > Configs.STATUS_LENGHT.getNumber()) {
				p.sendMessage(Messages.CMD_STATUS_STATUS_LENGHT.getMessage(p).replace("%LIMIT%", String.valueOf(Configs.STATUS_LENGHT.getNumber())));
				return;
			}
			
			if(Configs.STATUS_FILTER.getBoolean()) {
				for(String phrases : Configs.getForbiddenPhrases())
					if(msg.toLowerCase().contains(phrases.toLowerCase())) {
						p.sendMessage(Messages.CMD_STATUS_ABUSIVE_PHRASE.getMessage(p).replace("%PHRASE%", phrases));
						return;
					}
				
			}
			
			if(!canChangeStatus(p.getUniqueId())) {
				p.sendMessage(Messages.CMD_STATUS_CANT_CHANGE_YET.getMessage(p).replace("%REMAINING_TIME%", String.valueOf((
						Configs.STATUS_CHANGEDURATION.getNumber()-(System.currentTimeMillis()-lastChangedStatus.get(p.getUniqueId()))/1000))));
				return;
			}
			
			lastChangedStatus.put(p.getUniqueId(), System.currentTimeMillis());
			hash.setStatus(msg);
			if(Configs.ALLOW_STATUS_COLOR.getBoolean()) msg = ChatColor.translateAlternateColorCodes('&', msg);
			p.sendMessage(Messages.CMD_STATUS_STATUS_SET.getMessage(p).replace("%STATUS%", msg));
			return;
		}
		p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends stats (set|<Name>) (Status)"));
		return;
	}
	
	public static boolean canChangeStatus(UUID uuid) {
		if(lastChangedStatus.containsKey(uuid) && !Bukkit.getPlayer(uuid).hasPermission("Friends.Status.ChangeLimit.ByPass")) {
			if((System.currentTimeMillis()-lastChangedStatus.get(uuid)) < (Configs.STATUS_CHANGEDURATION.getNumber()*1000)) return false;
			lastChangedStatus.remove(uuid);
		}
		return true;
	}

}
