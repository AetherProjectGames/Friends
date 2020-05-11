package de.HyChrod.Friends.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Hashing.Options;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Listeners.BlockedInventoryListener;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.Listeners.RequestsInventoryListener;

public enum InventoryBuilder {
	
	FRIEND_INVENTORY("Friends.FriendInventory.InventoryTitle","Friends.FriendInventory.InventorySize"),
	REQUESTS_INVENTORY("Friends.RequestsInventory.InventoryTitle","Friends.RequestsInventory.InventorySize"),
	BLOCKED_INVENTORY("Friends.BlockedInventory.InventoryTitle","Friends.BlockedInventory.InventorySize"),
	REQUESTEDIT_INVENTORY("Friends.RequestEditInventory.InventoryTitle","Friends.RequestEditInventory.InventorySize"),
	BLOCKEDIT_INVENTORY("Friends.BlockedEditInventory.InventoryTitle","Friends.BlockedEditInventory.InventorySize"),
	FRIENDEDIT_INVENTORY("Friends.FriendEditInventory.InventoryTitle","Friends.FriendEditInventory.InventorySize"),
	OPTIONS_INVENTORY("Friends.OptionsInventory.InventoryTitle","Friends.OptionsInventory.InventorySize");
	
	private String title, title_path, size_path;
	private int size;
	
	private InventoryBuilder(String title, String size) {
		this.title_path = title;
		this.size_path = size;
	}
	
	private void load(FileConfiguration cfg) {
		this.title = ChatColor.translateAlternateColorCodes('&', cfg.getString(title_path));
		this.size = cfg.getInt(size_path);
	}
	
	public String getTitle(Player player) {
		String title = this.title;
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && player != null) {
			try {
				title = (String) Class.forName("me.clip.placeholderapi.PlaceholderAPI").getMethod("setPlaceholders", Player.class, String.class).invoke(null, player, title);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return title;
	}
	
	public int getSize() {
		return size;
	}
	
	public static void loadInventorys() {
		FileConfiguration cfg = FileManager.CONFIG.getConfig();
		for(InventoryBuilder builders : InventoryBuilder.values())
			builders.load(cfg);
	}

	private static String[] sorting = new String[] {Configs.SORTING_ONOFF.getText(), Configs.SORTING_FAVORITE.getText(), Configs.SORTING_LONGFRIEND.getText(), Configs.SORTING_ALPHABETIC.getText()};

	public static HashMap<String, Friendship> openFriendInventory(Player player, UUID toOpen, int page, boolean fresh) {
		FileConfiguration cfg = FileManager.CONFIG.getConfig();
		Inventory inv = Bukkit.createInventory(null, FRIEND_INVENTORY.getSize(), FRIEND_INVENTORY.getTitle(player).replace("%NAME%", player.getName())
				.replace("%PAGE%", ""+(FriendInventoryListener.getPage(toOpen)+1)));

		for (String slots : cfg.getStringList("Friends.FriendInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots) - 1, ItemStacks.INV_FRIEND_PLACEHOLDERS.getItem(player));

		HashMap<String, Friendship> cashedPositions = new HashMap<String, Friendship>();
		inv.setItem(ItemStacks.INV_FRIEND_BLOCKED.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_FRIEND_BLOCKED.getItem(player), "%BLOCKED_COUNT%", "0"));
		inv.setItem(ItemStacks.INV_FRIEND_REQUESTS.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_FRIEND_REQUESTS.getItem(player),"%REQUESTS_COUNT%","0"));
		inv.setItem(ItemStacks.INV_FRIEND_OPTIONS.getInventorySlot(), ItemStacks.INV_FRIEND_OPTIONS.getItem(player));
		if(Configs.INV_FRIEND_PREVIOUSPAGE_ENABLE.getBoolean() && (!Configs.INV_FRIEND_HIDEPAGES.getBoolean() || (Configs.INV_FRIEND_HIDEPAGES.getBoolean() && page > 0)))
			inv.setItem(ItemStacks.INV_FRIEND_PREVIOUSPAGE.getInventorySlot(), ItemStacks.INV_FRIEND_PREVIOUSPAGE.getItem(player));
		
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				FriendHash hash = FriendHash.getFriendHash(toOpen);
				LinkedList<Friendship> friends = fresh ? hash.getFriendsNew() : hash.getFriends();

				inv.setItem(ItemStacks.INV_FRIEND_BLOCKED.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_FRIEND_BLOCKED.getItem(player), "%BLOCKED_COUNT%", ""+hash.getBlockedNew().size()));
				inv.setItem(ItemStacks.INV_FRIEND_REQUESTS.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_FRIEND_REQUESTS.getItem(player),"%REQUESTS_COUNT%",""+hash.getRequestsNew().size()));
				if(Configs.INV_FRIEND_SORT_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_FRIEND_SORTING.getInventorySlot(), ItemStacks
						.replace(ItemStacks.INV_FRIEND_SORTING.getItem(player), "%SORTING%", ChatColor.translateAlternateColorCodes('&', sorting[hash.getSorting()])));
				
				int freeSlots = 0;
				for(ItemStack item : inv.getContents())
					if(item == null) freeSlots++;
				
				if(Configs.INV_FRIEND_NEXTPAGE_ENABLE.getBoolean() && (!Configs.INV_FRIEND_HIDEPAGES.getBoolean() || (Configs.INV_FRIEND_HIDEPAGES.getBoolean() && friends.size() > freeSlots)))
					inv.setItem(ItemStacks.INV_FRIEND_NEXTPAGE.getInventorySlot(), ItemStacks.INV_FRIEND_NEXTPAGE.getItem(player));
				
				LinkedList<Friendship> friendsOnPage = new LinkedList<Friendship>();
				if(hash.getOptions().getSorting() == 0) {
					LinkedList<Friendship> friendsOnPageOnline = new LinkedList<Friendship>();
					LinkedList<Friendship> friendsOnPageOffline = new LinkedList<Friendship>();
					for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
						if(friends.size() > i) {
							Friendship fs = friends.get(i);
							if(FriendHash.isOnline(fs.getFriend()))
								friendsOnPageOnline.add(fs);
							else
								friendsOnPageOffline.add(fs);
						}
					friendsOnPage.addAll(friendsOnPageOnline);
					friendsOnPage.addAll(friendsOnPageOffline);
				}
				if(hash.getOptions().getSorting() == 1) {
					LinkedList<Friendship> favorites = new LinkedList<Friendship>();
					LinkedList<Friendship> nofavorite = new LinkedList<Friendship>();
					for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
						if(friends.size() > i) {
							Friendship fs = friends.get(i);
							if(fs.getFavorite())
								favorites.add(fs);
							else
								nofavorite.add(fs);
						}
					friendsOnPage.addAll(favorites);
					friendsOnPage.addAll(nofavorite);
				}
				if(hash.getOptions().getSorting() == 2) {
					List<Long> timestamps = new ArrayList<Long>();
					for(Friendship fs : friends)
						timestamps.add(fs.getTimestamp());
					Collections.sort(timestamps);
					for(Long ts : timestamps)
						for(Friendship fs : friends)
							if(fs.getTimestamp() == ts) friendsOnPage.add(fs);
				}
				if(hash.getOptions().getSorting() == 3) {
					List<String> names = new ArrayList<String>();
					for(Friendship fs : friends)
						names.add(FriendHash.getName(fs.getFriend()));
					Collections.sort(names);
					for(String nm : names)
						for(Friendship fs : friends)
							if(FriendHash.getName(fs.getFriend()).equals(nm)) friendsOnPage.add(fs);
				}

				for (Friendship fs : friendsOnPage) 
					cashedPositions.put(addFriend(inv, fs, cfg, hash), fs);
				
				FriendInventoryListener.setPositions(player.getUniqueId(), cashedPositions);
				return;
			}
		});
		player.openInventory(inv);
		return cashedPositions;
	}
	
	private static String addFriend(Inventory inv, Friendship fs, FileConfiguration cfg, FriendHash hash) {
		String name = FriendHash.getName(fs.getFriend());
		
		boolean changeSkull = (Configs.INV_FRIENDS_FRIENDS_CHANGESKULL.getBoolean() && !FriendHash.isOnline(fs.getFriend()));
		String status = Bukkit.getPlayer(fs.getFriend()) != null ? FriendHash.getFriendHash(fs.getFriend()).getStatus() : fs.getStatus();
		status = status == null || fs.getStatus().length() < 1 ? Configs.INV_FRIENDS_NO_STATUS_REPLACEMENT.getText() : status;
		
		ItemStack item = new ItemStack(changeSkull ? Material.SKELETON_SKULL : Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if(!changeSkull) meta.setOwningPlayer(Bukkit.getOfflinePlayer(fs.getFriend()));
		
		Date date = new Date(fs.getTimestamp());
		SimpleDateFormat sdf = new SimpleDateFormat(Configs.DATE_FORMAT.getText());
		
		Date lastOnline = new Date(hash.getLastOnline(fs.getFriend()));
		SimpleDateFormat sdl_lastonline = new SimpleDateFormat(Configs.LASTONLINE_DATE_FORMAT.getText());
		
		String identifier = createUniqueIdentifier();
		
		boolean online = FriendHash.isOnline(fs.getFriend());
		String displayName = Configs.ITEM_FRIENDS_NAME.getText().replace("%ONLINE_STATUS%", online ? Configs.ITEM_FRIENDS_ONLINE_STAT_ON.getText() : Configs.ITEM_FRIENDS_ONLINE_STAT_OFF.getText())
				.replace("%SERVER%", FriendHash.getServer(fs.getFriend())).replace("%WORLD%", FriendHash.getWorld(fs.getFriend()));
		meta.setDisplayName(identifier + ChatColor.translateAlternateColorCodes('&', setPlaceholders(displayName.replace("%NAME%", name), Bukkit.getOfflinePlayer(fs.getFriend()))));
		ArrayList<String> l = new ArrayList<String>();
		
		String[] friendlore = (online ? Configs.ITEM_FRIENDS_LORE_ONLINE.getText() : Configs.ITEM_FRIENDS_LORE_OFFLINE.getText()).split("//");
		for(String lore : friendlore)
			l.add(setPlaceholders(ChatColor.translateAlternateColorCodes('&', lore.replace("%DATE%", sdf.format(date))
					.replace("%ONLINE_STATUS%", online ? Configs.ITEM_FRIENDS_ONLINE_STAT_ON.getText() : Configs.ITEM_FRIENDS_ONLINE_STAT_OFF.getText())
					.replace("%LAST_ONLINE%", sdl_lastonline.format(lastOnline)).replace("%SERVER%", FriendHash.getServer(fs.getFriend())).replace("%WORLD%", FriendHash.getWorld(fs.getFriend())))
					.replace("%STATUS%", (Configs.ALLOW_STATUS_COLOR.getBoolean() ? ChatColor.translateAlternateColorCodes('&', status) : status)), 
					Bukkit.getOfflinePlayer(fs.getFriend())));
		meta.setLore(l);
		item.setItemMeta(meta);
		
		inv.addItem(item);
		return identifier;
	}
	
	public static HashMap<String, Request> openRequestsInventory(Player player, UUID toOpen, int page, boolean fresh) {
		Inventory inv = Bukkit.createInventory(null, REQUESTS_INVENTORY.getSize(), REQUESTS_INVENTORY.getTitle(player).replace("%NAME%", player.getName())
				.replace("%PAGE%", (page+1)+""));
		
		for(String slots : FileManager.CONFIG.getConfig().getStringList("Friends.RequestsInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, ItemStacks.INV_REQUESTS_PLACEHOLDERS.getItem(player));
		
		if(Configs.INV_REQUEST_ACCEPTALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTS_ACCEPTALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTS_ACCEPTALL.getItem(player), "%REQUESTS_COUNT%", "0"));
		if(Configs.INV_REQUEST_DENYALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTS_DENYALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTS_DENYALL.getItem(player), "%REQUESTS_COUNT%", "0"));
		inv.setItem(ItemStacks.INV_REQUESTS_BACK.getInventorySlot(), ItemStacks.INV_REQUESTS_BACK.getItem(player));
		if(Configs.INV_REQUEST_PREVIOUSPAGE_ENABLE.getBoolean() && (!Configs.INV_REQUEST_HIDEPAGES.getBoolean() || (Configs.INV_REQUEST_HIDEPAGES.getBoolean() && page > 0))) 
			inv.setItem(ItemStacks.INV_REQUESTS_PREVIOUSPAGE.getInventorySlot(), ItemStacks.INV_REQUESTS_PREVIOUSPAGE.getItem(player));
		
		HashMap<String, Request> cashedPositions = new HashMap<String, Request>();
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				LinkedList<Request> requests = fresh ? FriendHash.getFriendHash(player.getUniqueId()).getRequestsNew() : FriendHash.getFriendHash(player.getUniqueId()).getRequests();
				
				if(Configs.INV_REQUEST_ACCEPTALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTS_ACCEPTALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTS_ACCEPTALL.getItem(player), "%REQUESTS_COUNT%", requests.size()+""));
				if(Configs.INV_REQUEST_DENYALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTS_DENYALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTS_DENYALL.getItem(player), "%REQUESTS_COUNT%", requests.size()+""));
				
				int freeSlots = 0;
				for(ItemStack item : inv.getContents())
					if(item == null) freeSlots++;
				
				if(Configs.INV_REQUEST_NEXTPAGE_ENABLE.getBoolean() && (!Configs.INV_REQUEST_HIDEPAGES.getBoolean() || (Configs.INV_REQUEST_HIDEPAGES.getBoolean() && requests.size() > freeSlots))) 
					inv.setItem(ItemStacks.INV_REQUESTS_NEXTPAGE.getInventorySlot(), ItemStacks.INV_REQUESTS_NEXTPAGE.getItem(player));
				
				List<Request> requestsOnPage = new ArrayList<Request>();
				for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
					if(requests.size() > i) requestsOnPage.add(requests.get(i));
				
				for(Request rq : requestsOnPage) {
					String name = FriendHash.getName(rq.getPlayerToAdd());
					String msg = rq.getMessage() == null || rq.getMessage().length() < 1 ? Configs.INV_REQUEST_NO_MSG_REPLACEMENT.getText() : rq.getMessage();
					
					Date date = new Date(rq.getTimestamp());
					SimpleDateFormat sdf = new SimpleDateFormat(Configs.DATE_FORMAT.getText());
					
					ItemStack item = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					
					String identifier = createUniqueIdentifier();
					cashedPositions.put(identifier, rq);
					
					meta.setDisplayName(identifier + ChatColor.translateAlternateColorCodes('&', setPlaceholders(Configs.ITEM_REQUEST_PLAYER_NAME.getText().replace("%NAME%", name), Bukkit.getOfflinePlayer(rq.getPlayerToAdd()))));
					meta.setOwningPlayer(Bukkit.getOfflinePlayer(rq.getPlayerToAdd()));
					ArrayList<String> lore = new ArrayList<String>();
					for(String l : Configs.ITEM_REQUEST_PLAYER_LORE.getText().split("//"))
						lore.add(setPlaceholders(ChatColor.translateAlternateColorCodes('&', l.replace("%MESSAGE%", msg).replace("%DATE%", sdf.format(date))), Bukkit.getOfflinePlayer(rq.getPlayerToAdd())));
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.addItem(item);
				}
				RequestsInventoryListener.setPositions(player.getUniqueId(), cashedPositions);
			}
		});
		player.openInventory(inv);
		return cashedPositions;
	}
	
	public static HashMap<String, Blockplayer> openBlockedInventory(Player player, UUID toOpen, int page, boolean fresh) {
		Inventory inv = Bukkit.createInventory(null, BLOCKED_INVENTORY.getSize(), BLOCKED_INVENTORY.getTitle(player).replace("%NAME%", player.getName())
				.replace("%PAGE%", (page+1)+""));
		
		for(String slots : FileManager.CONFIG.getConfig().getStringList("Friends.BlockedInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, ItemStacks.INV_BLOCKED_PLACEHOLDERS.getItem(player));
		
		inv.setItem(ItemStacks.INV_BLOCKED_BACK.getInventorySlot(), ItemStacks.INV_BLOCKED_BACK.getItem(player));
		if(Configs.INV_BLOCKED_PREVIOUSPAGE_ENABLE.getBoolean() && (!Configs.INV_BLOCKED_HIDEPAGES.getBoolean() || (Configs.INV_BLOCKED_HIDEPAGES.getBoolean() && page > 0))) 
			inv.setItem(ItemStacks.INV_BLOCKED_PREVIOUSPAGE.getInventorySlot(), ItemStacks.INV_BLOCKED_PREVIOUSPAGE.getItem(player));
		if(Configs.INV_BLOCKED_UNBLOCKALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_BLOCKED_UNBLOCKALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_BLOCKED_UNBLOCKALL.getItem(player), "%BLOCKED_COUNT%", "0"));
		
		HashMap<String, Blockplayer> cashedPositions = new HashMap<String, Blockplayer>();
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				LinkedList<Blockplayer> blockedplayers = fresh ? FriendHash.getFriendHash(player.getUniqueId()).getBlockedNew() : FriendHash.getFriendHash(player.getUniqueId()).getBlocked();
				
				if(Configs.INV_BLOCKED_UNBLOCKALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_BLOCKED_UNBLOCKALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_BLOCKED_UNBLOCKALL.getItem(player), "%BLOCKED_COUNT%", blockedplayers.size()+""));
				
				int freeSlots = 0;
				for(ItemStack item : inv.getContents())
					if(item == null) freeSlots++;
				
				if(Configs.INV_BLOCKED_NEXTPAGE_ENABLE.getBoolean() && (!Configs.INV_BLOCKED_HIDEPAGES.getBoolean() || (Configs.INV_BLOCKED_HIDEPAGES.getBoolean() && blockedplayers.size() > freeSlots)))
					inv.setItem(ItemStacks.INV_BLOCKED_NEXTPAGE.getInventorySlot(), ItemStacks.INV_BLOCKED_NEXTPAGE.getItem(player));
					
				LinkedList<Blockplayer> blockedOnPage = new LinkedList<Blockplayer>();
				for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
					if(blockedplayers.size() > i) blockedOnPage.add(blockedplayers.get(i));
				
				for(Blockplayer bl : blockedOnPage) {
					String name = FriendHash.getName(bl.getBlocked());
					String msg = bl.getMessage() == null || bl.getMessage().length() < 1 ? Configs.INV_BLOCKED_NO_NOTE_REPLACEMENT.getText() : bl.getMessage();
					
					Date date = new Date(bl.getTimestamp());
					SimpleDateFormat sdf = new SimpleDateFormat(Configs.DATE_FORMAT.getText());
					
					String identifier = createUniqueIdentifier();
					cashedPositions.put(identifier, bl);
					
					ItemStack item = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					meta.setDisplayName(identifier + ChatColor.translateAlternateColorCodes('&', setPlaceholders(Configs.ITEM_BLOCKED_PLAYER_NAME.getText().replace("%NAME%", name), Bukkit.getOfflinePlayer(bl.getBlocked()))));
					meta.setOwningPlayer(Bukkit.getOfflinePlayer(bl.getBlocked()));
					ArrayList<String> lore = new ArrayList<String>();
					for(String l : Configs.ITEM_BLOCKED_PLAYER_LORE.getText().split("//"))
						lore.add(setPlaceholders(ChatColor.translateAlternateColorCodes('&', l.replace("%NOTE%", msg).replace("%DATE%", sdf.format(date))), Bukkit.getOfflinePlayer(bl.getBlocked())));
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.addItem(item);
				}
				BlockedInventoryListener.setPositions(player.getUniqueId(), cashedPositions);
			}
		});
		player.openInventory(inv);
		return cashedPositions;
	}
	
	public static void openRequestEditInventory(Player player, Request rq) {
		String name = FriendHash.getName(rq.getPlayerToAdd());
		Inventory inv = Bukkit.createInventory(null, REQUESTEDIT_INVENTORY.getSize(), REQUESTEDIT_INVENTORY.getTitle(player).replace("%NAME%", name));
		
		for(String slots : FileManager.CONFIG.getConfig().getStringList("Friends.RequestEditInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, ItemStacks.INV_REQUESTEDIT_PLACEHOLDERS.getItem(player));
		
		String msg = rq.getMessage() == null || rq.getMessage().length() < 1 ? Configs.INV_RQ_EDIT_NO_MSG_REPLACEMENT.getText() : rq.getMessage();
		
		inv.setItem(ItemStacks.INV_REQUESTEDIT_ACCEPT.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTEDIT_ACCEPT.getItem(player), "%NAME%", name));
		inv.setItem(ItemStacks.INV_REQUESTEDIT_DENY.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTEDIT_DENY.getItem(player), "%NAME%", name));
		inv.setItem(ItemStacks.INV_REQUESTEDIT_BACK.getInventorySlot(), ItemStacks.INV_REQUESTEDIT_BACK.getItem(player));
		if(Configs.INV_REQUESTEDIT_MESSAGE_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTEDIT_MESSAGE.getInventorySlot(), ItemStacks.replace(ItemStacks
				.replace(ItemStacks.INV_REQUESTEDIT_MESSAGE.getItem(player), "%MESSAGE%", msg), "%NAME%", name));
		
		player.openInventory(inv);
	}
	
	public static void openBlockedEditInventory(Player player, Blockplayer bl) {
		String name = FriendHash.getName(bl.getBlocked());
		Inventory inv = Bukkit.createInventory(null, BLOCKEDIT_INVENTORY.getSize(), BLOCKEDIT_INVENTORY.getTitle(player).replace("%NAME%", name));
		
		for(String slots : FileManager.CONFIG.getConfig().getStringList("Friends.BlockedEditInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, ItemStacks.INV_BLOCKEDIT_PLACEHOLDERS.getItem(player));
		
		String msg = bl.getMessage() == null || bl.getMessage().length() < 1 ? Configs.INV_BL_EDIT_NO_NOTE_REPLACEMENT.getText() : bl.getMessage();
		
		inv.setItem(ItemStacks.INV_BLOCKEDIT_BACK.getInventorySlot(), ItemStacks.INV_BLOCKEDIT_BACK.getItem(player));
		if(Configs.INV_BLOCKEDIT_NOTE_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_BLOCKEDIT_NOTE.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_BLOCKEDIT_NOTE.getItem(player), "%NOTE%", msg));
		inv.setItem(ItemStacks.INV_BLOCKEDIT_UNBLOCK.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_BLOCKEDIT_UNBLOCK.getItem(player), "%NAME%", name));
		
		player.openInventory(inv);
	}
	
	public static void openFriendEditInventory(Player player, Friendship fs) {
		String name = FriendHash.getName(fs.getFriend());
		Inventory inv = Bukkit.createInventory(null, FRIENDEDIT_INVENTORY.getSize(), FRIENDEDIT_INVENTORY.getTitle(player).replace("%NAME%", name));
		
		for(String slots : FileManager.CONFIG.getConfig().getStringList("Friends.FriendEditInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, ItemStacks.INV_FRIENDEDIT_PLACEHOLDERS.getItem(player));
		
		String nickname = fs.getNickname() == null || fs.getNickname().length() < 1 ? Configs.ITEM_FRIEND_NO_NICK_REPLACEMENT.getText() : fs.getNickname();
		String favorite_status = fs.getFavorite() ? Configs.ITEM_FRIEND_FAV_STATUS_ON.getText() : Configs.ITEM_FRIEND_FAV_STATUS_OFF.getText();
		String message_status = fs.getCanSendMessages() ? Configs.ITEM_FRIEND_SENDMSG_STATUS_ON.getText() : Configs.ITEM_FRIEND_SENDMSG_STATUS_OFF.getText();
		
		inv.setItem(ItemStacks.INV_FRIENDEDIT_BACK.getInventorySlot(), ItemStacks.INV_FRIENDEDIT_BACK.getItem(player));
		inv.setItem(ItemStacks.INV_FRIENDEDIT_REMOVE.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_FRIENDEDIT_REMOVE.getItem(player), "%NAME%", name));
		if(Configs.INV_FRIENDEDIT_FAVORITE_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_FRIENDEDIT_FAVORITE.getInventorySlot(), ItemStacks.replace(ItemStacks.replace(ItemStacks.INV_FRIENDEDIT_FAVORITE.getItem(player), "%NAME%", name),
				"%FAVORITE_STATUS%", ChatColor.translateAlternateColorCodes('&', favorite_status)));
		if(Configs.INV_FRIENDEDIT_CSM_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_FRIENDEDIT_CANSENDMESSAGES.getInventorySlot(), ItemStacks.replace(ItemStacks.replace(ItemStacks.INV_FRIENDEDIT_CANSENDMESSAGES.getItem(player), "%NAME%", name), 
				"%SENDMESSAGES_STATUS%", ChatColor.translateAlternateColorCodes('&', message_status)));
		if(Configs.INV_FRIENDEDIT_NICK_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_FRIENDEDIT_NICKNAME.getInventorySlot(), ItemStacks.replace(ItemStacks.replace(ItemStacks.INV_FRIENDEDIT_NICKNAME.getItem(player), "%NAME%", name), 
				"%NICKNAME%", nickname));
		if(Configs.INV_JUMPING_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_FRIENDEDIT_JUMP.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_FRIENDEDIT_JUMP.getItem(player), "%NAME%", name));
	
		player.openInventory(inv);
	}
	
	public static void openOptionsInventory(Player p, Options opt) {
		Inventory inv = Bukkit.createInventory(null, OPTIONS_INVENTORY.getSize(), OPTIONS_INVENTORY.getTitle(p).replace("%NAME%", p.getName()));
		
		for(String slots : FileManager.CONFIG.getConfig().getStringList("Friends.OptionsInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, ItemStacks.INV_OPTIONS_PLACEHOLDERS.getItem(p));
		
		String offline = ChatColor.translateAlternateColorCodes('&', opt.isOffline() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		String messages = ChatColor.translateAlternateColorCodes('&', opt.getMessages() ? Configs.OPTIONS_ON.getText() : opt.getFavMessages() ? Configs.OPTIONS_MESSAGES_ONLY_FAV_STATUS.getText() : Configs.OPTIONS_OFF.getText());
		String requests = ChatColor.translateAlternateColorCodes('&', opt.getRequests() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		String status = opt.getStatus() == null || opt.getStatus().length() < 1 ? Configs.INV_FRIENDS_NO_STATUS_REPLACEMENT.getText() : opt.getStatus();
		String jumping = ChatColor.translateAlternateColorCodes('&', opt.getJumping() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		if(Configs.ALLOW_STATUS_COLOR.getBoolean()) status = ChatColor.translateAlternateColorCodes('&', status);
		
		inv.setItem(ItemStacks.INV_OPTIONS_BACK.getInventorySlot(), ItemStacks.INV_OPTIONS_BACK.getItem(p));
		if(Configs.OPTIONS_MESSAGES_SHOW.getBoolean()) inv.setItem(ItemStacks.INV_OPTIONS_MESSAGES.getInventorySlot(), ItemStacks
				.replace(ItemStacks.INV_OPTIONS_MESSAGES.getItem(p), "%OPTION_MESSAGES_STATUS%", messages));
		if(Configs.OPTIONS_OFFLINEMODE_SHOW.getBoolean()) inv.setItem(ItemStacks.INV_OPTIONS_OFFLINEMODE.getInventorySlot(), ItemStacks
				.replace(ItemStacks.INV_OPTIONS_OFFLINEMODE.getItem(p), "%OPTION_OFFLINEMODE_STATUS%", offline));
		if(Configs.OPTIONS_REQUESTS_SHOW.getBoolean()) inv.setItem(ItemStacks.INV_OPTIONS_REQUESTS.getInventorySlot(), ItemStacks
				.replace(ItemStacks.INV_OPTIONS_REQUESTS.getItem(p), "%OPTION_REQUESTS_STATUS%", requests));
		if(Configs.OPTIONS_STATUS_SHOW.getBoolean()) inv.setItem(ItemStacks.INV_OPTIONS_STATUS.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_OPTIONS_STATUS.getItem(p), "%STATUS%", status));
		if(Configs.OPTIONS_JUMP_SHOW.getBoolean()) inv.setItem(ItemStacks.INV_OPTIONS_JUMP.getInventorySlot(), ItemStacks
				.replace(ItemStacks.INV_OPTIONS_JUMP.getItem(p), "%OPTION_JUMPING_STATUS%", jumping));
		
		p.openInventory(inv);
	}
	
	private static String[] keys = new String[] {"§a","§b","§c","§d","§e","§f","§1","§2","§3","§4","§5","§6","§7","§8","§9","§o"};
	
	private static String createUniqueIdentifier() {
		String identifier = "";
		for(int i = 0; i < 15; i++)
			identifier = identifier + keys[new Random().nextInt(keys.length)];
		return identifier+"§r";
	}
	
	private static String setPlaceholders(String text, OfflinePlayer player) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && player != null) {
			try {
				text = (String) Class.forName("me.clip.placeholderapi.PlaceholderAPI").getMethod("setPlaceholders", OfflinePlayer.class, String.class).invoke(null, player, text);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

}
