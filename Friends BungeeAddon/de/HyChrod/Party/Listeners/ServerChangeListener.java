package de.HyChrod.Party.Listeners;

import java.util.HashMap;
import java.util.UUID;

import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerChangeListener implements Listener {
	
	private static HashMap<Integer, String> currentServer = new HashMap<>();
	
	@EventHandler
	public void onConnect(ServerConnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		if(Parties.getParty(p.getUniqueId()) == null) return;
		
		Parties party = Parties.getParty(p.getUniqueId());
		if(currentServer.containsKey(party.getID()) && currentServer.get(party.getID()).equals(e.getTarget().getName())) return;
		if(party.isLeader(p.getUniqueId())) {
			if(Configs.getForbiddenPartyServers().contains(e.getTarget().getName())) {
				e.setCancelled(true);
				p.sendMessage(TextComponent.fromLegacyText(PMessages.SWITCH_SERVER_BLOCKED.getMessage().replace("%SERVER%", e.getTarget().getName())));
				return;
			}
			
			party.setInfo(e.getTarget().getName());
			currentServer.put(party.getID(), e.getTarget().getName());
			AsyncSQLQueueUpdater.addToQueue("insert into party(id,prvt,server) values ('" + party.getID() + "','" + (party.isPublic() ? 0 : 1) + "','" + party.getInfo() + "') on duplicate key update server=values(server)");
			for(UUID members : party.getMembers())
				if(BungeeCord.getInstance().getPlayer(members) != null && !members.equals(p.getUniqueId())) {
					BungeeCord.getInstance().getPlayer(members).sendMessage(TextComponent.fromLegacyText(PMessages.SWTICH_SERVER_SWITCH.getMessage().replace("%SERVER%", e.getTarget().getName())));
					BungeeCord.getInstance().getPlayer(members).connect(e.getTarget());
				}
			return;
		}
		e.setCancelled(true);
		p.sendMessage(TextComponent.fromLegacyText(PMessages.SWITCH_SERVER_NO_LEADER.getMessage()));
		
	}

}
