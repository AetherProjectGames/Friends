package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Nickname_Command {
	
	public Nickname_Command(Friends friends, Player p, String[] args) {
		if(!Configs.NICK_ENABLE.getBoolean()) {
			p.sendMessage(Messages.CMD_UNKNOWN_COMMAND.getMessage(p));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Nickname") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 3) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends nickname <Name> <Nickname>"));
			return;
		}
		
		String toNick = args[1];
		if(!FriendHash.isPlayerValid(toNick)) {
			p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", toNick));
			return;
		}
		UUID toNickUUID = FriendHash.getUUIDFromName(toNick);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Friendship fs : hash.getFriends())
			if(fs.getFriend().equals(toNickUUID)) {
				if(fs.hasNickname() && fs.getNickname().equals(args[2])) {
					p.sendMessage(Messages.CMD_NICKNAME_SAME_NICK.getMessage(p).replace("%NAME%", toNick).replace("%NICKNAME%", args[2]));
					return;
				}
				
				if(Configs.NICK_CHECK_FOR_ABUSIVE_WORDS.getBoolean())
					for(String tocheck : Configs.getForbiddenPhrases())
						if(args[2].toLowerCase().contains(tocheck.toLowerCase())) {
							p.sendMessage(Messages.CMD_NICKNAME_ABUSIVE_PHRASE.getMessage(p).replace("%FLAGGED%", tocheck));
							return;
						}
				
				fs.setNickname(args[2]);
				p.sendMessage(Messages.CMD_NICKNAME_SET_NICK.getMessage(p).replace("%NAME%", toNick).replace("%NICKNAME%", args[2]));
				return;
			}
		
		p.sendMessage(Messages.CMD_NICKNAME_NOFRIENDS.getMessage(p).replace("%NAME%", toNick));
		return;
	}

}
