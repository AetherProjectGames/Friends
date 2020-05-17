package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.UnblockAll_Command;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;

public class BlockedInventoryListener implements Listener {
	
	private static HashMap<UUID, HashMap<String, Blockplayer>> cashedPositionsByUUID = new HashMap<>();
	private static HashMap<UUID, Integer> page = new HashMap<UUID, Integer>();
	
	public static void setPositions(UUID uuid, HashMap<String, Blockplayer> positions) {
		cashedPositionsByUUID.put(uuid, positions);
	}
	
	public static int getPage(UUID uuid) {
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
			if(e.getView() != null) {
				if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.BLOCKED_INVENTORY.getTitle(p).replace("%NAME%", p.getName())
						.replace("%PAGE%", (getPage(p.getUniqueId())+1)+""))) {
					e.setCancelled(true);
					if(e.getCurrentItem() != null)
						if(e.getCurrentItem().hasItemMeta())
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_BLOCKED_BACK.getItem(p).getItemMeta().getDisplayName())) {
									InventoryBuilder.openFriendInventory(p, p.getUniqueId(), 0, true);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(
										ItemStacks.replace(ItemStacks.INV_BLOCKED_UNBLOCKALL.getItem(p), "%BLOCKED_COUNT%", ""+hash.getBlocked().size()).getItemMeta().getDisplayName())) {
									int count = hash.getBlockedNew().size();
									new UnblockAll_Command(Friends.getInstance(), p, new String[] {"unblockall"});
									if(count != hash.getBlocked().size())
										InventoryBuilder.openBlockedInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), 0), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_BLOCKED_NEXTPAGE.getItem(p).getItemMeta().getDisplayName())) {
									int page = getPage(p.getUniqueId());
									InventoryBuilder.openBlockedInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), page+1), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_BLOCKED_PREVIOUSPAGE.getItem(p).getItemMeta().getDisplayName())) {
									int page = getPage(p.getUniqueId());
									if(page == 0) return;
									InventoryBuilder.openBlockedInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), page-1), false);
									return;
								}
								
								String invName = "BlockedInventory";
								for(int customIndex = 0; customIndex < ItemStacks.getItemCount(invName); customIndex++)
									if(e.getCurrentItem().getItemMeta().getDisplayName().contentEquals(ItemStacks.getCutomItem(invName, customIndex, p).getItemMeta().getDisplayName())) {
										String cmd = ItemStacks.getCustomCommand(invName, customIndex);
										if(cmd.length() > 0) p.performCommand(cmd.replace("%NAME%", p.getName()));
										return;
									}
								
								HashMap<String, Blockplayer> positions = cashedPositionsByUUID.get(p.getUniqueId());
								for(String identifier : positions.keySet()) {
									System.out.println(identifier);
									if(("§f"+e.getCurrentItem().getItemMeta().getDisplayName()).contains(identifier)) {
										Blockplayer bl = positions.get(identifier);
										BlockeditInventoryListener.setEditing(p.getUniqueId(), bl);
										InventoryBuilder.openBlockedEditInventory(p, bl);
										return;
									}
								}
							}
					
				}
			}
		}
	}
	
}
