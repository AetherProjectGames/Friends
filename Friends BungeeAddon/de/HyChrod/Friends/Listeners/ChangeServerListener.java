package de.HyChrod.Friends.Listeners;

import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChangeServerListener implements Listener {

	@EventHandler
	public void onChange(ServerSwitchEvent e) {
		ProxiedPlayer p = e.getPlayer();
		AsyncSQLQueueUpdater.addToQueue("update friends_playerdata set server='" + p.getServer().getInfo().getName() + "' where uuid='" + p.getUniqueId().toString() + "'");
	}
	
}
