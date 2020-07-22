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
import de.HyChrod.Friends.Commands.SubCommands.Accept_Command;
import de.HyChrod.Friends.Commands.SubCommands.Deny_Command;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class RequestEditInventoryListener implements Listener {
	
	private static HashMap<UUID, Request> currentlyEditing = new HashMap<>();
	
	public static void setEditing(UUID uuid, Request rq) {
		currentlyEditing.put(uuid, rq);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(currentlyEditing.containsKey(p.getUniqueId())) {
				Request rq = currentlyEditing.get(p.getUniqueId());
				String name = FriendHash.getName(rq.getPlayerToAdd());
				if(e.getView() != null)
					if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.REQUESTEDIT_INVENTORY.getTitle(p, 0).replace("%NAME%", name))) {
						e.setCancelled(true);
						OfflinePlayer inEdit = Bukkit.getOfflinePlayer(rq.getPlayerToAdd());
						if(e.getCurrentItem() != null)
							if(e.getCurrentItem().hasItemMeta())
								if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_REQUESTEDIT_BACK.getItem(inEdit).getItemMeta().getDisplayName())) {
										RequestsInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), RequestsInventoryListener.getPage(p.getUniqueId()), false));
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_REQUESTEDIT_ACCEPT.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										new Accept_Command(Friends.getInstance(), p, new String[] {"accept",name});
										RequestsInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), RequestsInventoryListener.getPage(p.getUniqueId()), false));
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_REQUESTEDIT_DENY.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										new Deny_Command(Friends.getInstance(), p, new String[] {"deny",name});
										RequestsInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), RequestsInventoryListener.getPage(p.getUniqueId()), false));
										return;
									}
									
									String invName = "RequestEditInventory";
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
