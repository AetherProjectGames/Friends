package de.HyChrod.Party.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class WorldChangeListener implements Listener {
	
	@EventHandler
	public void onConnect(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		if(Parties.getParty(p.getUniqueId()) == null) return;
		if(e.getFrom().getWorld() == e.getTo().getWorld()) return;
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(party.isLeader(p.getUniqueId()) && !party.getInfo().equals(e.getTo().getWorld().getName())) {
			party.setInfo(e.getTo().getWorld().getName());
			for(UUID members : party.getMembers())
				if(Bukkit.getPlayer(members) != null && !members.equals(p.getUniqueId())) {
					Bukkit.getPlayer(members).sendMessage(PMessages.SWTICH_SERVER_SWITCH.getMessage(p).replace("%WORLD%", p.getWorld().getName()));
					Bukkit.getPlayer(members).teleport(p.getLocation());
				}
			return;
		}
		if(party.getInfo().equals(e.getTo().getWorld().getName())) return;
		e.setCancelled(true);
		p.sendMessage(PMessages.SWITCH_SERVER_NO_LEADER.getMessage(p));
		
	}

}
