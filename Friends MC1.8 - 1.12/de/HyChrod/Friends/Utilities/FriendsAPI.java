package de.HyChrod.Friends.Utilities;

import java.util.UUID;

import org.bukkit.Bukkit;

import de.HyChrod.Friends.Listeners.FriendInventoryListener;

public class FriendsAPI {
	
	public static void openFriendInventory(UUID uuid) {
		InventoryBuilder.openFriendInventory(Bukkit.getPlayer(uuid), uuid, FriendInventoryListener.getPage(uuid), true);
	}

}
