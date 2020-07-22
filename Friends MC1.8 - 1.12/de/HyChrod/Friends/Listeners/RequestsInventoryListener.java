package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.AcceptAll_Command;
import de.HyChrod.Friends.Commands.SubCommands.DenyAll_Command;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class RequestsInventoryListener implements Listener {
	
	private static HashMap<UUID, HashMap<String, Request>> cashedPositionsByUUID = new HashMap<>();
	private static HashMap<UUID, Integer> page = new HashMap<UUID, Integer>();
	
	public static void setPositions(UUID uuid, HashMap<String, Request> positions) {
		cashedPositionsByUUID.put(uuid, positions);
	}
	
	public static Integer getPage(UUID uuid) {
		return page.containsKey(uuid) ? page.get(uuid) : 0;
	}
	
	public static int setPage(UUID uuid, int pg) {
		page.put(uuid, pg);
		return pg;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(e.getView() != null)
				if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.REQUESTS_INVENTORY.getTitle(p, getPage(p.getUniqueId())+1).replace("%NAME%", p.getName()))) {
					e.setCancelled(true);
					if(e.getCurrentItem() != null)
						if(e.getCurrentItem().hasItemMeta())
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_REQUESTS_BACK.getItem(p).getItemMeta().getDisplayName())) {
									InventoryBuilder.openFriendInventory(p, p.getUniqueId(), 0, true);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.replace(ItemStacks.INV_REQUESTS_ACCEPTALL.getItem(p), "%REQUESTS_COUNT%", ""+hash.getRequests().size())
										.getItemMeta().getDisplayName())) {
									int count = hash.getRequestsNew().size();
									new AcceptAll_Command(Friends.getInstance(), p, new String[] {"acceptall"});
									if(count != hash.getRequests().size())
										InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), 0), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.replace(ItemStacks.INV_REQUESTS_DENYALL.getItem(p), "%REQUESTS_COUNT%", ""+hash.getRequests().size())
										.getItemMeta().getDisplayName())) {
									int count = hash.getRequests().size();
									new DenyAll_Command(Friends.getInstance(), p, new String[] {"denyall"});
									if(count != hash.getRequests().size())
										InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), 0), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_REQUESTS_NEXTPAGE.getItem(p).getItemMeta().getDisplayName())) {
									int page = getPage(p.getUniqueId());
									InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), page+1), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_REQUESTS_PREVIOUSPAGE.getItem(p).getItemMeta().getDisplayName())) {
									int page = getPage(p.getUniqueId());
									if(page == 0) return;
									InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), page-1), false);
									return;
								}
								
								String invName = "RequestsInventory";
								for(int customIndex = 0; customIndex < ItemStacks.getItemCount(invName); customIndex++)
									if(e.getCurrentItem().getItemMeta().getDisplayName().contentEquals(ItemStacks.getCutomItem(invName, customIndex, p).getItemMeta().getDisplayName())) {
										String cmd = ItemStacks.getCustomCommand(invName, customIndex);
										if(cmd.length() > 0) p.performCommand(cmd.replace("%NAME%", p.getName()));
										return;
									}
								
								HashMap<String, Request> positions = cashedPositionsByUUID.get(p.getUniqueId());
								if(positions == null) return;
								for(String identifier : positions.keySet())
									if(("§f"+e.getCurrentItem().getItemMeta().getDisplayName()).contains(identifier)) {
										Request rq = positions.get(identifier);
										RequestEditInventoryListener.setEditing(p.getUniqueId(), rq);
										InventoryBuilder.openRequestEditInventory(p, rq);
										return;
									}
									
							}
				}
		}
	}
	
}
