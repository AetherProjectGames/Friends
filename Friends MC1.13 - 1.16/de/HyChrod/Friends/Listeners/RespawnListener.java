package de.HyChrod.Friends.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class RespawnListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e) {
		if(Configs.ITEMOPTION_DEATH.getBoolean() && Configs.FRIEND_ITEM_ENABLE.getBoolean()) {
			Bukkit.getScheduler().runTaskLater(Friends.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					if(Configs.getForbiddenWorlds().contains(e.getPlayer().getWorld().getName())) return;
					e.getPlayer().getInventory().setItem(ItemStacks.FRIEND_ITEM.getInventorySlot(), ItemStacks.setSkin(ItemStacks.FRIEND_ITEM.getItem(e.getPlayer()), e.getPlayer().getName(), false, null));
				}
			}, 10);
		}
	}
	
}
