package de.HyChrod.Friends.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import de.HyChrod.Friends.Utilities.ItemStacks;

public class FriendItemListener implements Listener {
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(e.getItemInHand() != null)
			if(e.getItemInHand().hasItemMeta())
				if(e.getItemInHand().getItemMeta().hasDisplayName())
					if(e.getItemInHand().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(e.getPlayer()).getItemMeta().getDisplayName()))
						e.setCancelled(true);
	}
	
	@EventHandler
	public void onMove1(InventoryMoveItemEvent e) {
		if(e.getItem() != null)
			if(e.getItem().hasItemMeta())
				if(e.getItem().getItemMeta().hasDisplayName())
					if(e.getItem().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(null).getItemMeta().getDisplayName()))
						e.setCancelled(true);
	}
	
	@EventHandler
	public void onMove2(InventoryPickupItemEvent e) {
		if(e.getItem() != null)
			if(e.getItem().getItemStack() != null)
				if(e.getItem().getItemStack().hasItemMeta())
					if(e.getItem().getItemStack().getItemMeta().hasDisplayName())
						if(e.getItem().getItemStack().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(null).getItemMeta().getDisplayName())) 
							e.setCancelled(true);
	}
	
	@EventHandler
	public void onMove3(InventoryClickEvent e) {
		if(e.getCursor() != null)
			if(e.getCursor().hasItemMeta())
				if(e.getCursor().getItemMeta().hasDisplayName())
					if(e.getCursor().getItemMeta().getDisplayName().equalsIgnoreCase(ItemStacks.FRIEND_ITEM.getItem(null).getItemMeta().getDisplayName()))
						e.setCancelled(true);
		if(e.getCurrentItem() != null)
			if(e.getCurrentItem().hasItemMeta())
				if(e.getCurrentItem().getItemMeta().hasDisplayName())
					if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(null).getItemMeta().getDisplayName()))
						e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(e.getItemDrop() != null)
			if(e.getItemDrop().getItemStack() != null)
				if(e.getItemDrop().getItemStack().hasItemMeta())
					if(e.getItemDrop().getItemStack().getItemMeta().hasDisplayName())
						if(e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(e.getPlayer()).getItemMeta().getDisplayName()))
							e.setCancelled(true);
	}
	
	@EventHandler
	public void onHandSwitch(PlayerSwapHandItemsEvent e) {
		if(e.getMainHandItem() != null)
			if(e.getMainHandItem().hasItemMeta())
				if(e.getMainHandItem().getItemMeta().hasDisplayName())
					if(e.getMainHandItem().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(e.getPlayer()).getItemMeta().getDisplayName()))
						e.setCancelled(true);
		if(e.getOffHandItem() != null)
			if(e.getOffHandItem().hasItemMeta())
				if(e.getOffHandItem().getItemMeta().hasDisplayName())
					if(e.getOffHandItem().getItemMeta().getDisplayName().equals(ItemStacks.FRIEND_ITEM.getItem(e.getPlayer()).getItemMeta().getDisplayName()))
						e.setCancelled(true);
	}

}
