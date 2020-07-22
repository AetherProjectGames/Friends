package de.HyChrod.Party.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.HyChrod.Party.Utilities.PConfigs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.getMessage().startsWith(PConfigs.PARTY_CHAT_FORMAT.getText()) && PConfigs.PARTY_CHAT_ENABLE.getBoolean()) {
			e.setCancelled(true);
			Player p = (Player) e.getPlayer();
			
			if(Parties.getParty(p.getUniqueId()) == null) {
				p.sendMessage(PMessages.CHAT_NO_PARTY.getMessage(p));
				return;
			}
			Parties party = Parties.getParty(p.getUniqueId());
			p.sendMessage(PMessages.CHAT_MESSAGE.getMessage(p).replace("%NAME%", p.getName()).replace("%MESSAGE%", e.getMessage().replace(PConfigs.PARTY_CHAT_FORMAT.getText(), "")));
			for(UUID members : party.getMembers())
				if(Bukkit.getPlayer(members) != null && !members.equals(p.getUniqueId()))
					Bukkit.getPlayer(members).sendMessage(PMessages.CHAT_MESSAGE.getMessage(p).replace("%NAME%", p.getName())
							.replace("%MESSAGE%", e.getMessage().replace(PConfigs.PARTY_CHAT_FORMAT.getText(), "")));
		}
		
	}
	
}
