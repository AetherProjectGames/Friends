package de.HyChrod.Party.Listeners;

import java.util.UUID;

import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(ChatEvent e) {
		if(e.getMessage().startsWith(Configs.PARTY_CHAT_FORMAT.getText()) && Configs.PARTY_CHAT_ENABLE.getBoolean()) {
			e.setCancelled(true);
			ProxiedPlayer p = (ProxiedPlayer) e.getSender();
			
			if(Parties.getParty(p.getUniqueId()) == null) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CHAT_NO_PARTY.getMessage()));
				return;
			}
			Parties party = Parties.getParty(p.getUniqueId());
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CHAT_MESSAGE.getMessage().replace("%NAME%", p.getName()).replace("%MESSAGE%", e.getMessage().replace(Configs.PARTY_CHAT_FORMAT.getText(), ""))));
			for(UUID members : party.getMembers())
				if(BungeeCord.getInstance().getPlayer(members) != null && !members.equals(p.getUniqueId()))
					BungeeCord.getInstance().getPlayer(members).sendMessage(TextComponent.fromLegacyText(PMessages.CHAT_MESSAGE.getMessage().replace("%NAME%", p.getName())
							.replace("%MESSAGE%", e.getMessage().replace(Configs.PARTY_CHAT_FORMAT.getText(), ""))));
		}
		
	}
	
}
