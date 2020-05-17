package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class MSG_Command {
	
	public MSG_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Basic")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length < 3) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends msg <Name> <Message>"));
			return;
		}
		if(!Configs.FRIEND_MSG_ENABLE.getBoolean()) {
			p.sendMessage(Messages.CMD_UNKNOWN_COMMAND.getMessage(p));
			return;
		}		
		if(!p.hasPermission("Friends.Commands.Msg")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		
		String playerToSend = args[1];
		if(!FriendHash.isPlayerValid(playerToSend)) {
			p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", playerToSend));
			return;
		}
		
		UUID toSend = FriendHash.getUUIDFromName(playerToSend);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Friendship fs : hash.getFriendsNew())
			if(fs.getFriend().equals(toSend)) {
				String name = fs.hasNickname() ? fs.getNickname() : playerToSend;
				if(FriendHash.isOnline(toSend)) {
					if(!hash.getOptions().getMessages() && !hash.getOptions().getFavMessages()) {
						p.sendMessage(Messages.CMD_MSG_MSG_DISABLED.getMessage(p));
						return;
					}
					
					FriendHash fhash = FriendHash.getFriendHash(toSend);
					Friendship ffs = fhash.getFriendship(p.getUniqueId());
					if(!ffs.getCanSendMessages() || (!fhash.getOptions().getMessages() && !fhash.getOptions().getFavMessages()) || (fhash.getOptions().getFavMessages() && !ffs.getFavorite())) {
						p.sendMessage(Messages.CMD_MSG_NOMSG.getMessage(p).replace("%NAME%", name));
						return;
					}
					
					String msg = "";
					for(int i = 2; i < args.length; i++)
						msg = msg + " " + args[i];
					msg = msg.substring(1);
					
					if(Configs.FIENED_MSG_FLAG.getBoolean()) {
						for(String phrase : Configs.getForbiddenPhrases()) {
							if(msg.toUpperCase().contains(phrase.toUpperCase())) {
								p.sendMessage(Messages.CMD_MSG_ABUSIVE_PHRASE.getMessage(p).replace("%PHRASE%", phrase));
								return;
							}
						}
					}
					
					p.sendMessage(Messages.CMD_MSG_MSG.getMessage(p).replace("%NAME%", name).replace("%SENDER%", p.getName()).replace("%MESSAGE%", msg));
					Bukkit.getPlayer(toSend).sendMessage(Messages.CMD_MSG_MSG.getMessage(Bukkit.getPlayer(toSend)).replace("%NAME%", playerToSend).replace("%SENDER%", p.getName()).replace("%MESSAGE%", msg));
					return;
				}
				p.sendMessage(Messages.CMD_MSG_OFFLINE.getMessage(p).replace("%NAME%", name));
				return;
			}
		p.sendMessage(Messages.CMD_MSG_NOFRIENDS.getMessage(p).replace("%NAME%", playerToSend));
		return;
	}

}
