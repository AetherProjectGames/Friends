package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Unblock_Command;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class BlockeditInventoryListener implements Listener {
	
	private static HashMap<UUID, Blockplayer> currentlyEditing = new HashMap<>();
	
	public static void setEditing(UUID uuid, Blockplayer bl) {
		currentlyEditing.put(uuid, bl);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(currentlyEditing.containsKey(p.getUniqueId())) {
				Blockplayer bl = currentlyEditing.get(p.getUniqueId());
				if(e.getView() != null)
					if(e.getView().getTitle() != null && e.getView().getTitle().equalsIgnoreCase(InventoryBuilder.BLOCKEDIT_INVENTORY.getTitle(p,0).replace("%NAME%", FriendHash.getName(bl.getBlocked())))) {
						e.setCancelled(true);
						
						OfflinePlayer inEdit = Bukkit.getOfflinePlayer(bl.getBlocked());
						String name = FriendHash.getName(bl.getBlocked());
						if(e.getCurrentItem() != null)
							if(e.getCurrentItem().hasItemMeta())
								if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
									
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_BLOCKEDIT_BACK.getItem(inEdit).getItemMeta().getDisplayName())) {
										BlockedInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openBlockedInventory(p, p.getUniqueId(), BlockedInventoryListener.getPage(p.getUniqueId()), false));
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_BLOCKEDIT_UNBLOCK.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										new Unblock_Command(Friends.getInstance(), p, new String[] {"unblock",name});
										BlockedInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openBlockedInventory(p, p.getUniqueId(), BlockedInventoryListener.getPage(p.getUniqueId()), false));
										return;
									}
									
									String invName = "BlockedEditInventory";
									for(int customIndex = 0; customIndex < ItemStacks.getItemCount(invName); customIndex++)
										if(e.getCurrentItem().getItemMeta().getDisplayName().contentEquals(ItemStacks.getCutomItem(invName, customIndex, p).getItemMeta().getDisplayName())) {
											String cmd = ItemStacks.getCustomCommand(invName, customIndex);
											if(cmd.length() > 0) p.performCommand(cmd.replace("%NAME%", p.getName()));
											return;
										}
									
								}
					}
			}
		}
	}

}
