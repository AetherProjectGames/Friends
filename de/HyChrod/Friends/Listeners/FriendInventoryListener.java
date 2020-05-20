package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Party.Listeners.PartyInventoryListener;
import de.HyChrod.Party.Utilities.PInventoryBuilder;
import de.HyChrod.Party.Utilities.Parties;

public class FriendInventoryListener implements Listener {
	
	private static ConcurrentHashMap<UUID, HashMap<String, Friendship>> cashedPositions = new ConcurrentHashMap<>();
	private static HashMap<UUID, Integer> page = new HashMap<UUID, Integer>();
	
	public static void setPositions(UUID uuid, HashMap<String, Friendship> positions) {
		cashedPositions.put(uuid, positions);
	}
	
	public static int getPage(UUID uuid) {
		return page.containsKey(uuid) ? page.get(uuid) : 0;
	}
	
	public static int setPage(UUID uuid, int pg) {
		page.put(uuid, pg);
		return pg;
	}
	
	@EventHandler
	public void onOpenInv(PlayerInteractEvent e) {
		if(e.getAction() == null) return;
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getItem() != null)
				if(e.getItem().hasItemMeta())
					if(e.getItem().getItemMeta().hasDisplayName()) {
						if(e.getItem().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem((Player)e.getPlayer()).getItemMeta().getDisplayName())) {
							e.setCancelled(true);
							Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
								
								@Override
								public void run() {
									InventoryBuilder.openFriendInventory(e.getPlayer(), e.getPlayer().getUniqueId(), getPage(e.getPlayer().getUniqueId()), true);
								}
							});
							return;
						}
					}
		}
	}
	
	@EventHandler
	public void onInventory(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(e.getView() != null)
				if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.FRIEND_INVENTORY.getTitle(p)
						.replace("%NAME%", p.getName()).replace("%PAGE%", ""+(getPage(p.getUniqueId())+1)))) {
					e.setCancelled(true);
					
					FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
					if(e.getCurrentItem() != null)
						if(e.getCurrentItem().hasItemMeta())
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(
										ItemStacks.replace(ItemStacks.INV_FRIEND_REQUESTS.getItem(p), "%REQUESTS_COUNT%", ""+hash.getRequestsNew().size()).getItemMeta().getDisplayName())) {
									RequestsInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openRequestsInventory(p, p.getUniqueId(), RequestsInventoryListener.setPage(p.getUniqueId(), 0), true));
									return;
								}
								LinkedList<Blockplayer> blocked = hash.getBlockedNew();
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(
										ItemStacks.replace(ItemStacks.INV_FRIEND_BLOCKED.getItem(p), "%BLOCKED_COUNT%", ""+blocked.size()).getItemMeta().getDisplayName())) {
									BlockedInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openBlockedInventory(p, p.getUniqueId(), BlockedInventoryListener.setPage(p.getUniqueId(), 0), true));
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIEND_OPTIONS.getItem(p).getItemMeta().getDisplayName())) {
									OptionsInventoryListener.setEditing(p.getUniqueId(), hash.getOptions());
									InventoryBuilder.openOptionsInventory(p, hash.getOptions());
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIEND_PARTY.getItem(p).getItemMeta().getDisplayName())) {
									if(Parties.getParty(p.getUniqueId()) == null) PInventoryBuilder.openCreateInventory(p);
									else PartyInventoryListener.setPositions(p.getUniqueId(), PInventoryBuilder.openPartyInventory(p, Parties.getParty(p.getUniqueId())));
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIEND_NEXTPAGE.getItem(p).getItemMeta().getDisplayName())) {
									int page = getPage(p.getUniqueId());
									InventoryBuilder.openFriendInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), page+1), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIEND_PREVIOUSPAGE.getItem(p).getItemMeta().getDisplayName())) {
									int page = getPage(p.getUniqueId());
									if(page == 0) return;
									InventoryBuilder.openFriendInventory(p, p.getUniqueId(), setPage(p.getUniqueId(), page-1), false);
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIEND_SORTING.getItem(p).getItemMeta().getDisplayName())) {
									int sorting = hash.getSorting();
									if(sorting == 3) sorting = -1;
									hash.setSorting(sorting+1);
									InventoryBuilder.openFriendInventory(p, p.getUniqueId(), getPage(p.getUniqueId()), false);
									return;
								}
								
								String invName = "FriendInventory";
								for(int customIndex = 0; customIndex < ItemStacks.getItemCount(invName); customIndex++)
									if(e.getCurrentItem().getItemMeta().getDisplayName().contentEquals(ItemStacks.getCutomItem(invName, customIndex, p).getItemMeta().getDisplayName())) {
										String cmd = ItemStacks.getCustomCommand(invName, customIndex);
										if(cmd.length() > 0) p.performCommand(cmd.replace("%NAME%", p.getName()));
										return;
									}
								
								HashMap<String, Friendship> positions = cashedPositions.get(p.getUniqueId());
								for(String identifier : positions.keySet()) {
									if(("§f"+e.getCurrentItem().getItemMeta().getDisplayName()).contains(identifier)) {
										Friendship fs = positions.get(identifier);
										FriendEditInventoryListener.setEditing(p.getUniqueId(), fs);
										InventoryBuilder.openFriendEditInventory(p, fs);
									}
								}
								
							}
					
				}
		}
	}

}
