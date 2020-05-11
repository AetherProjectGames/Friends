package de.HyChrod.Friends.Listeners;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class ChatListener implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(Configs.BUNGEEMODE.getBoolean()) return;
		if(e.getMessage().startsWith(Configs.FRIENDCHAT_FORMAT.getText()))
			if(Configs.FRIENDCHAT_ENABLE.getBoolean()) {
				e.setCancelled(true);
				
				if(Configs.FRIENDCHAT_FLAG.getBoolean()) {
					for(String phrase : Configs.getForbiddenPhrases())
						if(e.getMessage().toUpperCase().contains(phrase.toUpperCase())) {
							p.sendMessage(Messages.FRIENDCHAT_ABUSIVE_PHRASE.getMessage(p).replace("%PHRASE%", phrase));
							return;
						}
				}
				
				FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
				if(!hash.getOptions().getMessages() && !hash.getOptions().getFavMessages()) {
					p.sendMessage(Messages.FRIENDCHAT_DISABLED.getMessage(p));
					return;
				}
				LinkedList<Friendship> friends = hash.getFriendsNew();
				if(!friends.isEmpty())
					for(Friendship fs : friends)
						if(FriendHash.isOnline(fs.getFriend())) {
							if(!hash.getOptions().getFavMessages() || (hash.getOptions().getFavMessages() && fs.getFavorite())) {
								
								FriendHash fHash = FriendHash.getFriendHash(fs.getFriend());
								if(!fHash.getOptions().getMessages() && !fHash.getOptions().getFavMessages()) continue;
								Friendship ffs = fHash.getFriendship(p.getUniqueId());
								if(fHash.getOptions().getFavMessages() && !ffs.getFavorite() || !ffs.getCanSendMessages()) continue;
								Bukkit.getPlayer(fs.getFriend()).sendMessage(Messages.FRIENDCHAT_FORMAT.getMessage(Bukkit.getPlayer(fs.getFriend())).replace("%MESSAGE%", e.getMessage().replace(Configs.FRIENDCHAT_FORMAT.getText(), ""))
										.replace("%NAME%", p.getName()));
							}
						}
				p.sendMessage(Messages.FRIENDCHAT_FORMAT.getMessage(p).replace("%MESSAGE%", e.getMessage().replace(Configs.FRIENDCHAT_FORMAT.getText(), ""))
						.replace("%NAME%", p.getName()));
			}
	}

}
