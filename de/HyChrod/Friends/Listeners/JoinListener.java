package de.HyChrod.Friends.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Friends.Utilities.Messages;

public class JoinListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(Configs.FRIEND_ITEM_ENABLE.getBoolean())
			if(Configs.DELAYED_INV_SET.getBoolean())
				Bukkit.getScheduler().runTaskLaterAsynchronously(Friends.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						p.getInventory().setItem(ItemStacks.FRIEND_ITEM.getInventorySlot(), ItemStacks.setSkin(ItemStacks.FRIEND_ITEM.getItem(p), p.getName()));
					}
				}, 5L);
			else p.getInventory().setItem(ItemStacks.FRIEND_ITEM.getInventorySlot(), ItemStacks.setSkin(ItemStacks.FRIEND_ITEM.getItem(p), p.getName()));
		if(Configs.CHECK_FOR_UPDATES.getBoolean()) {
			if((p.hasPermission("Friends.Commands.Version") || p.hasPermission("Friends.Commands.Reload")) && Friends.isUpdateNeeded())
				p.sendMessage(Friends.getPrefix() + " §cThere is a new update available for friends");
		}
		
		if(Configs.BUNGEEMODE.getBoolean()) return;
		
		hash.updateUUID(p.getName(), p.getUniqueId());
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				if(hash.getOptions().isOffline()) return;
				for(Friendship fs : hash.getFriendsNew())
					if(Bukkit.getPlayer(fs.getFriend()) != null) {
						FriendHash fHash = FriendHash.getFriendHash(fs.getFriend());
						Friendship ffs = fHash.getFriendship(p.getUniqueId());
						if(fHash.getOptions() == null) continue;
						if(!fHash.getOptions().getMessages() && !fHash.getOptions().getFavMessages() || !ffs.getCanSendMessages()) continue;
						if(fHash.getOptions().getFavMessages() && !ffs.getFavorite()) continue;
						Bukkit.getPlayer(fs.getFriend()).sendMessage(Messages.JOIN_MESSAGE.getMessage(Bukkit.getPlayer(fs.getFriend())).replace("%NAME%", p.getName()));
					}
			}
		});
	}

}
