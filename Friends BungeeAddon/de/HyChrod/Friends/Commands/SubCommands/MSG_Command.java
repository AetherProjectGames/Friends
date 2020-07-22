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

public class MSG_Command {
	
	private static HashMap<UUID, UUID> reply = new HashMap<UUID, UUID>();
	
	public static UUID getReply(UUID uuid) {
		return reply.containsKey(uuid) ? reply.get(uuid) : null;
	}
	
	public MSG_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Msg") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length < 3) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends msg <Name> <Message>")));
			return;
		}
		if(!Configs.FRIEND_MSG_ENABLE.getBoolean()) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNKNOWN_COMMAND.getMessage()));
			return;
		}
		
		String playerToSend = args[1];
		if(!FriendHash.isPlayerValid(playerToSend)) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.PLAYER_DOES_NOT_EXIST.getMessage().replace("%NAME%", playerToSend)));
			return;
		}
		
		UUID toSend = FriendHash.getUUIDFromName(playerToSend);
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		for(Friendship fs : hash.getFriends())
			if(fs.getFriend().equals(toSend)) {
				String name = fs.hasNickname() ? fs.getNickname() : playerToSend;
				if(FriendHash.isOnline(toSend)) {
					if(!hash.getOptions().getMessages() && !hash.getOptions().getFavMessages()) {
						p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_MSG_DISABLED.getMessage()));
						return;
					}
					
					FriendHash fhash = FriendHash.getFriendHash(toSend);
					Friendship ffs = fhash.getFriendship(p.getUniqueId());
					if(fhash.getOptions() != null)
						if(!ffs.getCanSendMessages() || (!fhash.getOptions().getMessages() && !fhash.getOptions().getFavMessages()) || (fhash.getOptions().getFavMessages() && !ffs.getFavorite())) {
							p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_NOMSG.getMessage().replace("%NAME%", name)));
							return;
						}
					
					String msg = "";
					for(int i = 2; i < args.length; i++)
						msg = msg + " " + args[i];
					msg = msg.substring(1);
					
					if(Configs.FIENED_MSG_FLAG.getBoolean()) {
						for(String phrase : Configs.getForbiddenPhrases()) {
							if(msg.toUpperCase().contains(phrase.toUpperCase())) {
								p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_ABUSIVE_PHRASE.getMessage().replace("%PHRASE%", phrase)));
								return;
							}
						}
					}
					reply.put(toSend, p.getUniqueId());
					
					p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_MSG.getMessage().replace("%NAME%", name).replace("%SENDER%", p.getName()).replace("%MESSAGE%", msg)));
					BungeeCord.getInstance().getPlayer(toSend).sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_MSG.getMessage().replace("%NAME%", playerToSend).replace("%SENDER%", p.getName()).replace("%MESSAGE%", msg)));
					return;
				}
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_OFFLINE.getMessage().replace("%NAME%", name)));
				return;
			}
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_MSG_NOFRIENDS.getMessage().replace("%NAME%", playerToSend)));
		return;
	}

}
