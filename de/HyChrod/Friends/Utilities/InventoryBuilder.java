package de.HyChrod.Friends.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
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

@SuppressWarnings("unchecked")
public enum InventoryBuilder {
	
	FRIEND_INVENTORY("FriendInventory", "Friends.FriendInventory",
			ItemStacks.INV_FRIEND_PLACEHOLDERS, ItemStacks.INV_FRIEND_BLOCKED, ItemStacks.INV_FRIEND_REQUESTS, ItemStacks.INV_FRIEND_OPTIONS, 
			ItemStacks.INV_FRIEND_PARTY),
	REQUESTS_INVENTORY("RequestsInventory", "Friends.RequestsInventory",
			ItemStacks.INV_REQUESTS_PLACEHOLDERS, ItemStacks.INV_REQUESTS_ACCEPTALL, ItemStacks.INV_REQUESTS_DENYALL, ItemStacks.INV_REQUESTS_BACK),
	BLOCKED_INVENTORY("BlockedInventory", "Friends.BlockedInventory",
			ItemStacks.INV_BLOCKED_PLACEHOLDERS, ItemStacks.INV_BLOCKED_BACK, ItemStacks.INV_BLOCKED_UNBLOCKALL),
	REQUESTEDIT_INVENTORY("RequestEditInventory", "Friends.RequestEditInventory",
			ItemStacks.INV_REQUESTEDIT_PLACEHOLDERS, ItemStacks.INV_REQUESTEDIT_ACCEPT, ItemStacks.INV_REQUESTEDIT_DENY, ItemStacks.INV_REQUESTEDIT_BACK, 
			ItemStacks.INV_REQUESTEDIT_MESSAGE),
	BLOCKEDIT_INVENTORY("BlockedEditInventory", "Friends.BlockedEditInventory",
			ItemStacks.INV_BLOCKEDIT_PLACEHOLDERS, ItemStacks.INV_BLOCKEDIT_BACK, ItemStacks.INV_BLOCKEDIT_NOTE, ItemStacks.INV_BLOCKEDIT_UNBLOCK),
	FRIENDEDIT_INVENTORY("FriendEditInventory", "Friends.FriendEditInventory",
			ItemStacks.INV_FRIENDEDIT_PLACEHOLDERS, ItemStacks.INV_FRIENDEDIT_BACK, ItemStacks.INV_FRIENDEDIT_REMOVE, ItemStacks.INV_FRIENDEDIT_FAVORITE,
			ItemStacks.INV_FRIENDEDIT_CANSENDMESSAGES, ItemStacks.INV_FRIENDEDIT_NICKNAME, ItemStacks.INV_FRIENDEDIT_JUMP, ItemStacks.INV_FRIENDEDIT_PARTY),
	OPTIONS_INVENTORY("OptionsInventory", "Friends.OptionsInventory",
			ItemStacks.INV_OPTIONS_PLACEHOLDERS, ItemStacks.INV_OPTIONS_BACK, ItemStacks.INV_OPTIONS_MESSAGES, ItemStacks.INV_OPTIONS_OFFLINEMODE,
			ItemStacks.INV_OPTIONS_REQUESTS, ItemStacks.INV_OPTIONS_STATUS, ItemStacks.INV_OPTIONS_JUMP, ItemStacks.INV_OPTIONS_PARTY);
	
	private String title, title_path, size_path, slots_path, name;
	private int size;
	private ItemStacks[] items;
	
	private InventoryBuilder(String name, String path, ItemStacks...items) {
		this.title_path = path + ".InventoryTitle";
		this.size_path = path + ".InventorySize";
		this.items = items;
		this.name = name;
		this.slots_path = path + ".Placeholders.InventorySlots";
	}
	
	private ItemStacks[] getItems() {
		return items;
	}
	
	private void load(FileConfiguration cfg) {
		this.title = ChatColor.translateAlternateColorCodes('&', cfg.getString(title_path));
		this.size = cfg.getInt(size_path);
	}
	
	public String getTitle(Player player, int page) {
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
		return title.replace("%PAGE%", ""+page);
	}
	
	public int getSize() {
		return size;
	}
	
	public Inventory createInventory(Player player, String[] toReplace, String[] replacements, int page) {
		String titl = getTitle(player, page);
		for(int i = 0; i < toReplace.length; i++)
			titl = titl.replace(toReplace[i], replacements[i]);
		
		Inventory inv = Bukkit.createInventory(null, size, titl);
		for(String slots : FileManager.CONFIG.getConfig().getStringList(slots_path))
			inv.setItem(Integer.parseInt(slots) - 1, getItems()[0].getItem(player));
		for(ItemStacks item : getItems()) {
			if(item.name().toLowerCase().contains("PLACEHOLDER".toLowerCase())) continue;
			if(item.show()) inv.setItem(item.getInventorySlot(), ItemStacks.replaceMulti(item.getItem(player), toReplace, replacements));
		}
		for(int customItem = 0; customItem < ItemStacks.getItemCount(name); customItem++) 
			inv.setItem(ItemStacks.getCustomInventorySlot(name, customItem) - 1, ItemStacks.getCutomItem(name, customItem, player));
		return inv;
	}
	
	public static void loadInventorys() {
		FileConfiguration cfg = FileManager.CONFIG.getConfig();
		for(InventoryBuilder builders : InventoryBuilder.values())
			builders.load(cfg);
	}

	private static String[] sorting = new String[] {Configs.SORTING_ONOFF.getText(), Configs.SORTING_FAVORITE.getText(), Configs.SORTING_LONGFRIEND.getText(), Configs.SORTING_ALPHABETIC.getText()};

	public static HashMap<String, Friendship> openFriendInventory(Player player, UUID toOpen, int page, boolean fresh) {
		Inventory inv = FRIEND_INVENTORY.createInventory(player, new String[] {"%BLOCKED_COUNT%","%REQUESTS_COUNT%"}, new String[] {"0","0"}, FriendInventoryListener.getPage(toOpen)+1);
		HashMap<String, Friendship> cashedPositions = new HashMap<String, Friendship>();
		
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
				
				if(Configs.INV_FRIEND_PREVIOUSPAGE_ENABLE.getBoolean() && (!Configs.INV_FRIEND_HIDEPAGES.getBoolean() || (Configs.INV_FRIEND_HIDEPAGES.getBoolean() && page > 0)))
					inv.setItem(ItemStacks.INV_FRIEND_PREVIOUSPAGE.getInventorySlot(), ItemStacks.INV_FRIEND_PREVIOUSPAGE.getItem(player));
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
					for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
						if(friends.size() > i)
							timestamps.add(friends.get(i).getTimestamp());
					Collections.sort(timestamps);
					for(Long ts : timestamps)
						for(Friendship fs : friends)
							if(fs.getTimestamp() == ts) friendsOnPage.add(fs);
				}
				if(hash.getOptions().getSorting() == 3) {
					Collection<String> names = new TreeSet<String>(Collator.getInstance());
					for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
						if(friends.size() > i)
							names.add(FriendHash.getName(friends.get(i).getFriend()));
					
					for(String nm : names)
						for(Friendship fs : friends)
							if(FriendHash.getName(fs.getFriend()).equals(nm)) friendsOnPage.add(fs);
				}

				for (Friendship fs : friendsOnPage) {
					String identifier = createUniqueIdentifier();
					boolean changeSkull = (Configs.INV_FRIENDS_FRIENDS_CHANGESKULL.getBoolean() && !FriendHash.isOnline(fs.getFriend()));
					String status = Bukkit.getPlayer(fs.getFriend()) != null ? FriendHash.getFriendHash(fs.getFriend()).getStatus() : fs.getStatus();
					status = status == null || fs.getStatus().length() < 1 ? Configs.INV_FRIENDS_NO_STATUS_REPLACEMENT.getText() : status;
					
					boolean online = FriendHash.isOnline(fs.getFriend());
					inv.addItem(createHead(identifier, fs.getFriend(), FriendHash.getName(fs.getFriend()), Configs.ITEM_FRIENDS_NAME.getText(), (online ? Configs.ITEM_FRIENDS_LORE_ONLINE.getText() : Configs.ITEM_FRIENDS_LORE_OFFLINE.getText()), 
							new String[] {"%ONLINE_STATUS%", "%SERVER%", "%WORLD%", "%DATE%", "%LAST_ONLINE%", "%STATUS%"}, 
							new String[] {
									online ? Configs.ITEM_FRIENDS_ONLINE_STAT_ON.getText() : Configs.ITEM_FRIENDS_ONLINE_STAT_OFF.getText(),
									FriendHash.getServer(fs.getFriend()),
									FriendHash.getWorld(fs.getFriend()),
									new SimpleDateFormat(Configs.DATE_FORMAT.getText()).format(new Date(fs.getTimestamp())),
									new SimpleDateFormat(Configs.LASTONLINE_DATE_FORMAT.getText()).format(new Date(hash.getLastOnline(fs.getFriend()))),
									(Configs.ALLOW_STATUS_COLOR.getBoolean() ? ChatColor.translateAlternateColorCodes('&', status) : status)
							}, !changeSkull));
					cashedPositions.put(identifier, fs);
				}
				
				FriendInventoryListener.setPositions(player.getUniqueId(), cashedPositions);
				return;
			}
		});
		player.openInventory(inv);
		return cashedPositions;
	}
	
	public static HashMap<String, Request> openRequestsInventory(Player player, UUID toOpen, int page, boolean fresh) {
		Inventory inv = REQUESTS_INVENTORY.createInventory(player, new String[] {"REQUESTS_COUNT%"}, new String[] {"0"}, page+1);
		HashMap<String, Request> cashedPositions = new HashMap<String, Request>();
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				LinkedList<Request> requests = fresh ? FriendHash.getFriendHash(player.getUniqueId()).getRequestsNew() : FriendHash.getFriendHash(player.getUniqueId()).getRequests();
				Object[] onpageObject = getOnPage(requests, inv, page);
				
				if(Configs.INV_REQUEST_ACCEPTALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTS_ACCEPTALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTS_ACCEPTALL.getItem(player), "%REQUESTS_COUNT%", requests.size()+""));
				if(Configs.INV_REQUEST_DENYALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_REQUESTS_DENYALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_REQUESTS_DENYALL.getItem(player), "%REQUESTS_COUNT%", requests.size()+""));
				if(Configs.INV_REQUEST_NEXTPAGE_ENABLE.getBoolean() && (!Configs.INV_REQUEST_HIDEPAGES.getBoolean() || (Configs.INV_REQUEST_HIDEPAGES.getBoolean() && requests.size() > ((int)onpageObject[0])))); 
					inv.setItem(ItemStacks.INV_REQUESTS_NEXTPAGE.getInventorySlot(), ItemStacks.INV_REQUESTS_NEXTPAGE.getItem(player));
				if(Configs.INV_REQUEST_PREVIOUSPAGE_ENABLE.getBoolean() && (!Configs.INV_REQUEST_HIDEPAGES.getBoolean() || (Configs.INV_REQUEST_HIDEPAGES.getBoolean() && page > 0))) 
					inv.setItem(ItemStacks.INV_REQUESTS_PREVIOUSPAGE.getInventorySlot(), ItemStacks.INV_REQUESTS_PREVIOUSPAGE.getItem(player));
				
				for(Request rq : (List<Request>)onpageObject[1]) {
					String msg = rq.getMessage() == null || rq.getMessage().length() < 1 ? Configs.INV_REQUEST_NO_MSG_REPLACEMENT.getText() : rq.getMessage();
					String identifier = createUniqueIdentifier();
					cashedPositions.put(identifier, rq);
					inv.addItem(createHead(identifier, rq.getPlayerToAdd(), FriendHash.getName(rq.getPlayerToAdd()), Configs.ITEM_REQUEST_PLAYER_NAME.getText(), Configs.ITEM_REQUEST_PLAYER_LORE.getText(), 
							new String[] {"%MESSAGE%", "%DATE%"}, new String[] {msg, new SimpleDateFormat(Configs.DATE_FORMAT.getText()).format(new Date(rq.getTimestamp()))}, true));
				}
				RequestsInventoryListener.setPositions(player.getUniqueId(), cashedPositions);
			}
		});
		player.openInventory(inv);
		return cashedPositions;
	}
	
	public static HashMap<String, Blockplayer> openBlockedInventory(Player player, UUID toOpen, int page, boolean fresh) {
		Inventory inv = BLOCKED_INVENTORY.createInventory(player, new String[] {"%BLOCKED_COUNT%"}, new String[] {"0"}, page+1);
		HashMap<String, Blockplayer> cashedPositions = new HashMap<String, Blockplayer>();
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				LinkedList<Blockplayer> blockedplayers = fresh ? FriendHash.getFriendHash(player.getUniqueId()).getBlockedNew() : FriendHash.getFriendHash(player.getUniqueId()).getBlocked();
				Object[] onpageObject = getOnPage(blockedplayers, inv, page);
				
				if(Configs.INV_BLOCKED_UNBLOCKALL_ENABLE.getBoolean()) inv.setItem(ItemStacks.INV_BLOCKED_UNBLOCKALL.getInventorySlot(), ItemStacks.replace(ItemStacks.INV_BLOCKED_UNBLOCKALL.getItem(player), "%BLOCKED_COUNT%", blockedplayers.size()+""));
				if(Configs.INV_BLOCKED_NEXTPAGE_ENABLE.getBoolean() && (!Configs.INV_BLOCKED_HIDEPAGES.getBoolean() || (Configs.INV_BLOCKED_HIDEPAGES.getBoolean() && blockedplayers.size() > ((int)onpageObject[0]))))
					inv.setItem(ItemStacks.INV_BLOCKED_NEXTPAGE.getInventorySlot(), ItemStacks.INV_BLOCKED_NEXTPAGE.getItem(player));
				if(Configs.INV_BLOCKED_PREVIOUSPAGE_ENABLE.getBoolean() && (!Configs.INV_BLOCKED_HIDEPAGES.getBoolean() || (Configs.INV_BLOCKED_HIDEPAGES.getBoolean() && page > 0))) 
					inv.setItem(ItemStacks.INV_BLOCKED_PREVIOUSPAGE.getInventorySlot(), ItemStacks.INV_BLOCKED_PREVIOUSPAGE.getItem(player));
				
				for(Blockplayer bl : (List<Blockplayer>)onpageObject[1]) {
					String msg = bl.getMessage() == null || bl.getMessage().length() < 1 ? Configs.INV_BLOCKED_NO_NOTE_REPLACEMENT.getText() : bl.getMessage();
					String identifier = createUniqueIdentifier();
					cashedPositions.put(identifier, bl);
					inv.addItem(createHead(identifier, bl.getBlocked(), FriendHash.getName(bl.getBlocked()), Configs.ITEM_BLOCKED_PLAYER_NAME.getText(), Configs.ITEM_BLOCKED_PLAYER_LORE.getText(), 
							new String[] {"%NOTE%","%DATE%"}, new String[] {msg, new SimpleDateFormat(Configs.DATE_FORMAT.getText()).format(new Date(bl.getTimestamp()))}, true));
				}
				BlockedInventoryListener.setPositions(player.getUniqueId(), cashedPositions);
			}
		});
		player.openInventory(inv);
		return cashedPositions;
	}
	
	private static ItemStack createHead(String identifier, UUID uuid, String name, String display, String lore, String[] toReplace, String[] replacements, boolean changeSkull) {
		ItemStack item = new ItemStack(changeSkull ? Material.PLAYER_HEAD : Material.SKELETON_SKULL);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(identifier + setPlaceholders(display.replace("%NAME%", name), Bukkit.getOfflinePlayer(uuid)));
		for(int i = 0; i < toReplace.length; i++)
			meta.setDisplayName(meta.getDisplayName().replace(toReplace[i], replacements[i]));
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));
		if(changeSkull) meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		lore = setPlaceholders(ChatColor.translateAlternateColorCodes('&', lore), Bukkit.getOfflinePlayer(uuid));
		meta.setLore(Arrays.asList(lore.split("//")));
		for(int i = 0; i < toReplace.length; i++) {
			ArrayList<String> lorelist = new ArrayList<String>();
			for(String l : meta.getLore())
				lorelist.add(l.replace(toReplace[i], replacements[i]));
			meta.setLore(lorelist);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public static void openRequestEditInventory(Player player, Request rq) {
		String msg = rq.getMessage() == null || rq.getMessage().length() < 1 ? Configs.INV_RQ_EDIT_NO_MSG_REPLACEMENT.getText() : rq.getMessage();
		Inventory inv = REQUESTEDIT_INVENTORY.createInventory(player, new String[] {"%NAME%","%MESSAGE%"}, new String[] {FriendHash.getName(rq.getPlayerToAdd()), msg}, 0);
		player.openInventory(inv);
	}
	
	public static void openBlockedEditInventory(Player player, Blockplayer bl) {
		String msg = bl.getMessage() == null || bl.getMessage().length() < 1 ? Configs.INV_BL_EDIT_NO_NOTE_REPLACEMENT.getText() : bl.getMessage();
		Inventory inv = BLOCKEDIT_INVENTORY.createInventory(player, new String[] {"%NAME%","%NOTE%"}, new String[] {FriendHash.getName(bl.getBlocked()),msg}, 0);
		player.openInventory(inv);
	}
	
	public static void openFriendEditInventory(Player player, Friendship fs) {
		String name = FriendHash.getName(fs.getFriend());
		String nickname = fs.getNickname() == null || fs.getNickname().length() < 1 ? Configs.ITEM_FRIEND_NO_NICK_REPLACEMENT.getText() : fs.getNickname();
		String favorite_status = ChatColor.translateAlternateColorCodes('&', fs.getFavorite() ? Configs.ITEM_FRIEND_FAV_STATUS_ON.getText() : Configs.ITEM_FRIEND_FAV_STATUS_OFF.getText());
		String message_status = ChatColor.translateAlternateColorCodes('&', fs.getCanSendMessages() ? Configs.ITEM_FRIEND_SENDMSG_STATUS_ON.getText() : Configs.ITEM_FRIEND_SENDMSG_STATUS_OFF.getText());
		Inventory inv = FRIENDEDIT_INVENTORY.createInventory(player, new String[] {"%NAME%", "%FAVORITE_STATUS%", "%SENDMESSAGES_STATUS%", "%NICKNAME%"}, new String[] {
			name, favorite_status, message_status, nickname	}, 0);
		player.openInventory(inv);
	}
	
	public static void openOptionsInventory(Player p, Options opt) {
		String offline = ChatColor.translateAlternateColorCodes('&', opt.isOffline() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		String messages = ChatColor.translateAlternateColorCodes('&', opt.getMessages() ? Configs.OPTIONS_ON.getText() : opt.getFavMessages() ? Configs.OPTIONS_MESSAGES_ONLY_FAV_STATUS.getText() : Configs.OPTIONS_OFF.getText());
		String requests = ChatColor.translateAlternateColorCodes('&', opt.getRequests() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		String status = opt.getStatus() == null || opt.getStatus().length() < 1 ? Configs.INV_FRIENDS_NO_STATUS_REPLACEMENT.getText() : opt.getStatus();
		String jumping = ChatColor.translateAlternateColorCodes('&', opt.getJumping() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		String party = ChatColor.translateAlternateColorCodes('&', opt.getPartyInvites() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
		if(Configs.ALLOW_STATUS_COLOR.getBoolean()) status = ChatColor.translateAlternateColorCodes('&', status);
		Inventory inv = OPTIONS_INVENTORY.createInventory(p, new String[] {"%OPTION_MESSAGES_STATUS%", "%OPTION_OFFLINEMODE_STATUS%", "%OPTION_REQUESTS_STATUS%",
				"%OPTION_JUMPING_STATUS%", "%OPTION_PARTY_STATUS%", "%STATUS%"}, new String[] {messages, offline, requests, jumping, party, status}, 0);
		p.openInventory(inv);
	}
	
	private static <E> Object[] getOnPage(LinkedList<E> all, Inventory inv, int page) {
		int freeSlots = 0;
		for(ItemStack item : inv.getContents())
			if(item == null) freeSlots++;
		List<E> onpage = new ArrayList<E>();
		for(int i = (page*freeSlots); i < ((page*freeSlots)+freeSlots); i++)
			if(all.size() > i) onpage.add(all.get(i));
		return new Object[] {freeSlots, onpage};
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
