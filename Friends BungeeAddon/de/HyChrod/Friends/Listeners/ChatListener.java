package de.HyChrod.Friends.Listeners;

import java.util.LinkedList;

import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(ChatEvent e) {
		if(e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) e.getSender();
			
			if(e.getMessage().startsWith(Configs.FRIENDCHAT_FORMAT.getText()))
				if(Configs.FRIENDCHAT_ENABLE.getBoolean()) {
					e.setCancelled(true);
					
					if(Configs.FRIENDCHAT_FLAG.getBoolean()) {
						for(String phrase : Configs.getForbiddenPhrases())
							if(e.getMessage().toUpperCase().contains(phrase.toUpperCase())) {
								p.sendMessage(TextComponent.fromLegacyText(Messages.FRIENDCHAT_ABUSIVE_PHRASE.getMessage().replace("%PHRASE%", phrase)));
								return;
							}
					}
					
					FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
					if(!hash.getOptions().getMessages() && !hash.getOptions().getFavMessages()) {
						p.sendMessage(TextComponent.fromLegacyText(Messages.FRIENDCHAT_DISABLED.getMessage()));
						return;
					}
					LinkedList<Friendship> friends = hash.getFriends();
					if(!friends.isEmpty())
						for(Friendship fs : friends)
							if(FriendHash.isOnline(fs.getFriend())) {
								if(!hash.getOptions().getFavMessages() || (hash.getOptions().getFavMessages() && fs.getFavorite())) {
									
									FriendHash fHash = FriendHash.getFriendHash(fs.getFriend());
									if(!fHash.getOptions().getMessages() && !fHash.getOptions().getFavMessages()) continue;
									Friendship ffs = fHash.getFriendship(p.getUniqueId());
									if(fHash.getOptions().getFavMessages() && !ffs.getFavorite() || !ffs.getCanSendMessages()) continue;
									BungeeCord.getInstance().getPlayer(fs.getFriend()).sendMessage(TextComponent.fromLegacyText(Messages.FRIENDCHAT_FORMAT.getMessage().replace("%MESSAGE%", e.getMessage().replace(Configs.FRIENDCHAT_FORMAT.getText(), ""))
											.replace("%NAME%", p.getName())));
								}
							}
					p.sendMessage(TextComponent.fromLegacyText(Messages.FRIENDCHAT_FORMAT.getMessage().replace("%MESSAGE%", e.getMessage().replace(Configs.FRIENDCHAT_FORMAT.getText(), ""))
							.replace("%NAME%", p.getName())));
				}
		}
	}
	
}
