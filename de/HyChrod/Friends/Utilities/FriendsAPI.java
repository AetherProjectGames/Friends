package de.HyChrod.Friends.Utilities;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;

import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Hashing.Options;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;

public class FriendsAPI {
	
	public static void openFriendInventory(UUID uuid) {
		InventoryBuilder.openFriendInventory(Bukkit.getPlayer(uuid), uuid, FriendInventoryListener.getPage(uuid), true);
	}
	
	public static LinkedList<Friendship> getFriends(UUID uuid) {
		return FriendHash.getFriendHash(uuid).getFriendsNew();
	}
	
	public static LinkedList<Request> getRequests(UUID uuid) {
		return FriendHash.getFriendHash(uuid).getRequestsNew();
	}
	
	public static LinkedList<Blockplayer> getBlocked(UUID uuid) {
		return FriendHash.getFriendHash(uuid).getBlockedNew();
	}
	
	public static Options getOptions(UUID uuid) {
		return FriendHash.getOptions(uuid);
	}
	
	public static String getNickname(UUID uuid) {
		return FriendHash.getName(uuid);
	}
	
	public static void addRequest(UUID requested, UUID requester) {
		FriendHash hash = FriendHash.getFriendHash(requested);
		hash.addRequest(new Request(requested, requester, null, System.currentTimeMillis()));
	}
	
	public static void addBlocked(UUID uuid, UUID blocked) {
		FriendHash hash = FriendHash.getFriendHash(uuid);
		hash.addBlocked(new Blockplayer(uuid, blocked, System.currentTimeMillis(), null));
	}
	
	public static void addFriend(UUID uuid, UUID friend) {
		FriendHash hash = FriendHash.getFriendHash(uuid);
		hash.addFriend(new Friendship(uuid, friend, System.currentTimeMillis(), false, true, null, null));
	}

}
