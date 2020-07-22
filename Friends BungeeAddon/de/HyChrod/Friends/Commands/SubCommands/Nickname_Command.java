package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Nickname_Command {
	
	public Nickname_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!Configs.NICK_ENABLE.getBoolean()) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNKNOWN_COMMAND.getMessage()));
			return;
		}
		if(!p.hasPermission("Friends.Commands.Nickname") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 3) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends nickname <Name> <Nickname>")));
			return;
		}
		
		String toNick = args[1];
		if(!FriendHash.isPlayerValid(toNick)) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.PLAYER_DOES_NOT_EXIST.getMessage().replace("%NAME%", toNick)));
			return;
		}
		UUID toNickUUID = FriendHash.getUUIDFromName(toNick);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Friendship fs : hash.getFriends())
			if(fs.getFriend().equals(toNickUUID)) {
				if(fs.hasNickname() && fs.getNickname().equals(args[2])) {
					p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_NICKNAME_SAME_NICK.getMessage().replace("%NAME%", toNick).replace("%NICKNAME%", args[2])));
					return;
				}
				
				if(Configs.NICK_CHECK_FOR_ABUSIVE_WORDS.getBoolean())
					for(String tocheck : Configs.getForbiddenPhrases())
						if(args[2].toLowerCase().contains(tocheck.toLowerCase())) {
							p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_NICKNAME_ABUSIVE_PHRASE.getMessage().replace("%FLAGGED%", tocheck)));
							return;
						}
				
				AsyncSQLQueueUpdater.addToQueue("update friends_frienddata set nickname='" + args[2] + "' where uuid='" + p.getUniqueId().toString() + "' and uuid2 = '" + toNickUUID.toString() + "'");
				fs.setNickname(args[2]);
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_NICKNAME_SET_NICK.getMessage().replace("%NAME%", toNick).replace("%NICKNAME%", args[2])));
				return;
			}
		
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_NICKNAME_NOFRIENDS.getMessage().replace("%NAME%", toNick)));
		return;
	}

}
