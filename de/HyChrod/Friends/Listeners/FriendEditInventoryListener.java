package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Jump_Command;
import de.HyChrod.Friends.Commands.SubCommands.Remove_Command;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class FriendEditInventoryListener implements Listener {
	
	private static HashMap<UUID, Friendship> currentlyEditing = new HashMap<>();
	
	public static void setEditing(UUID uuid, Friendship fs) {
		currentlyEditing.put(uuid, fs);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(!Configs.BUNGEEMODE.getBoolean()) return;
		Player p = (Player) e.getPlayer();
		if(currentlyEditing.containsKey(p.getUniqueId())) {
			Friendship fs = currentlyEditing.get(p.getUniqueId());
			if(e.getView() != null && e.getView().getTitle() != null)
				if(e.getView().getTitle().equals(InventoryBuilder.FRIENDEDIT_INVENTORY.getTitle(p).replace("%NAME%", FriendHash.getName(fs.getFriend())))) {
					AsyncSQLQueueUpdater.addToQueue("update friends_frienddata set favorite='" + (fs.getFavorite() ? 1 : 0) + "',cansendmessages='" + (fs.getCanSendMessages() ? 1 : 0) + "' where uuid='" + p.getUniqueId().toString() + "' and uuid2='" + fs.getFriend().toString() + "';");
				}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(currentlyEditing.containsKey(p.getUniqueId())) {
				Friendship fs = currentlyEditing.get(p.getUniqueId());
				if(e.getView() != null)
					if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.FRIENDEDIT_INVENTORY.getTitle(p).replace("%NAME%", FriendHash.getName(fs.getFriend())))) {
						e.setCancelled(true);
						
						OfflinePlayer inEdit = Bukkit.getOfflinePlayer(fs.getFriend());
						if(e.getCurrentItem() != null)
							if(e.getCurrentItem().hasItemMeta())
								if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
									String name = FriendHash.getName(fs.getFriend());
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_BACK.getItem(inEdit).getItemMeta().getDisplayName())) {
										InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), false);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_REMOVE.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										new Remove_Command(Friends.getInstance(), p, new String[] {"remove",name});
										InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), false);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_NICKNAME.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										p.sendMessage(Friends.getPrefix() + " §cNicknames can currently only be set by chat! -> /friends nickname <Player> <Nickname>");
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_CANSENDMESSAGES.getItem(inEdit).getItemMeta().getDisplayName()
											.replace("%NAME%", name))) {
										fs.setCanSendMessages(fs.getCanSendMessages() ? false : true);
										InventoryBuilder.openFriendEditInventory(p, fs);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_FAVORITE.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										fs.setFavorite(fs.getFavorite() ? false : true);
										InventoryBuilder.openFriendEditInventory(p, fs);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_JUMP.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										new Jump_Command(Friends.getInstance(), p, new String[] {"jump",name});
										return;
									}
									
								}
						
					}
			}
		}
	}

}
