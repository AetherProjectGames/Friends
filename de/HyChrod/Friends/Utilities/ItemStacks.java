package de.HyChrod.Friends.Utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.HyChrod.Friends.Hashing.FriendHash;

public enum ItemStacks {
	
	FRIEND_ITEM("Friends.FriendItem"),
	INV_FRIEND_REQUESTS("Friends.FriendInventory.RequestsItem"),
	INV_FRIEND_BLOCKED("Friends.FriendInventory.BlockedItem"),
	INV_FRIEND_OPTIONS("Friends.FriendInventory.OptionsItem"),
	INV_FRIEND_PREVIOUSPAGE("Friends.FriendInventory.PreviousPageItem"),
	INV_FRIEND_NEXTPAGE("Friends.FriendInventory.NextPageItem"),
	INV_FRIEND_PLACEHOLDERS("Friends.FriendInventory.Placeholders"),
	INV_FRIEND_SORTING("Friends.FriendInventory.SortItem"),
	INV_REQUESTS_DENYALL("Friends.RequestsInventory.DenyAllItem"),
	INV_REQUESTS_ACCEPTALL("Friends.RequestsInventory.AcceptAllItem"),
	INV_REQUESTS_BACK("Friends.RequestsInventory.BackItem"),
	INV_REQUESTS_PLACEHOLDERS("Friends.RequestsInventory.Placeholders"),
	INV_REQUESTS_PREVIOUSPAGE("Friends.RequestsInventory.PreviousPageItem"),
	INV_REQUESTS_NEXTPAGE("Friends.RequestsInventory.NextPageItem"),
	INV_BLOCKED_UNBLOCKALL("Friends.BlockedInventory.UnblockAllItem"),
	INV_BLOCKED_NEXTPAGE("Friends.BlockedInventory.NextPageItem"),
	INV_BLOCKED_PREVIOUSPAGE("Friends.BlockedInventory.PreviousPageItem"),
	INV_BLOCKED_BACK("Friends.BlockedInventory.BackItem"),
	INV_BLOCKED_PLACEHOLDERS("Friends.BlockedInventory.Placeholders"),
	INV_REQUESTEDIT_ACCEPT("Friends.RequestEditInventory.AcceptItem"),
	INV_REQUESTEDIT_DENY("Friends.RequestEditInventory.DenyItem"),
	INV_REQUESTEDIT_MESSAGE("Friends.RequestEditInventory.MessageItem"),
	INV_REQUESTEDIT_BACK("Friends.RequestEditInventory.BackItem"),
	INV_REQUESTEDIT_PLACEHOLDERS("Friends.RequestEditInventory.Placeholders"),
	INV_BLOCKEDIT_BACK("Friends.BlockedEditInventory.BackItem"),
	INV_BLOCKEDIT_UNBLOCK("Friends.BlockedEditInventory.UnblockItem"),
	INV_BLOCKEDIT_NOTE("Friends.BlockedEditInventory.NoteItem"),
	INV_BLOCKEDIT_PLACEHOLDERS("Friends.BlockedEditInventory.Placeholders"),
	INV_FRIENDEDIT_BACK("Friends.FriendEditInventory.BackItem"),
	INV_FRIENDEDIT_NICKNAME("Friends.FriendEditInventory.NicknameItem"),
	INV_FRIENDEDIT_CANSENDMESSAGES("Friends.FriendEditInventory.CanSendMessagesItem"),
	INV_FRIENDEDIT_FAVORITE("Friends.FriendEditInventory.FavoriteItem"),
	INV_FRIENDEDIT_REMOVE("Friends.FriendEditInventory.RemoveItem"),
	INV_FRIENDEDIT_PLACEHOLDERS("Friends.FriendEditInventory.Placeholders"),
	INV_OPTIONS_BACK("Friends.OptionsInventory.BackItem"),
	INV_OPTIONS_MESSAGES("Friends.OptionsInventory.ReceiveMessagesItem"),
	INV_OPTIONS_REQUESTS("Friends.OptionsInventory.ReceiveRequestsItem"),
	INV_OPTIONS_OFFLINEMODE("Friends.OptionsInventory.OfflinemodeItem"),
	INV_OPTIONS_STATUS("Friends.OptionsInventory.StatusItem"),
	INV_OPTIONS_PLACEHOLDERS("Friends.OptionsInventory.Placeholders"),
	INV_FRIENDEDIT_JUMP("Friends.FriendEditInventory.JumpItem"),
	INV_OPTIONS_JUMP("Friends.OptionsInventory.JumpItem"),
	INV_OPTIONS_PARTY("Friends.OptionsInventory.PartyItem"),
	INV_FRIENDEDIT_PARTY("Friends.FriendEditInventory.PartyItem"),
	INV_FRIEND_PARTY("Friends.FriendInventory.PartyItem");
	
	private String path;
	private String name = " ";
	private List<String> lore;
	private String material;
	private int inventoryslot;
	private boolean showItem = true;
	private String base64value = "";
	
	private ItemStacks(String path) {
		this.path = path;
	}
	
	private void load(FileConfiguration cfg) {
		if(cfg.get(path + ".Name") != null) this.name = ChatColor.translateAlternateColorCodes('&', cfg.getString(path + ".Name"));
		this.lore = new ArrayList<String>();
		if(cfg.getString(path + ".Lore") != null)
			for(String lore : cfg.getString(path + ".Lore").split("//"))
				if(lore.length() > 0) this.lore.add(ChatColor.translateAlternateColorCodes('&', lore));
		this.material = cfg.getString(path + ".Material");
		if(cfg.get(path + ".InventorySlot") != null) this.inventoryslot = cfg.getInt(path + ".InventorySlot");
		if(cfg.get(path + ".ShowItem") != null) showItem = cfg.getBoolean(path + ".ShowItem");
		if(cfg.get(path + ".Base64Value") != null) base64value = cfg.getString(path + ".Base64Value");
	}
	
	public ItemStack getItem(OfflinePlayer player) {
		return getItemStack(name, material, lore, player, base64value.length() > 20, base64value);
	}
	
	public int getInventorySlot() {
		return inventoryslot-1;
	}
	
	public boolean show() {
		return showItem;
	}
	
	private static HashMap<String, LinkedList<String[]>> customItems = new HashMap<String, LinkedList<String[]>>();
	
	private static void generateCustomItems(FileConfiguration cfg) {
		String[] inventorys = new String[] {"FriendInventory","FriendEditInventory","RequestsInventory","RequestEditInventory","BlockedInventory","BlockedEditInventory","OptionsInventory"};
		for(String inv : inventorys) {
			if(cfg.getConfigurationSection("Friends." + inv + ".CustomItems") == null) continue;
			for(String cFItems : cfg.getConfigurationSection("Friends." + inv + ".CustomItems").getKeys(false)) {
				if(!cFItems.startsWith("CUSTOM_ITEM")) continue;
				String[] values = new String[6];
				values[0] = createUniqueIdentifier() + ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends." + inv + ".CustomItems." + cFItems + ".Name"));
				values[1] = cfg.getString("Friends." + inv + ".CustomItems." + cFItems + ".Material");
				values[2] = ChatColor.translateAlternateColorCodes('&', cfg.getString("Friends." + inv + ".CustomItems." + cFItems + ".Lore"));
				values[3] = cfg.getString("Friends." + inv + ".CustomItems." + cFItems + ".InventorySlot");
				values[4] = cfg.getString("Friends." + inv + ".CustomItems." + cFItems + ".PerformCommand");
				values[5] = cfg.get("Friends." + inv + ".CustomItems." + cFItems + ".Base64Value") == null ? "" : cfg.getString("Friends." + inv + ".CustomItems." + cFItems + ".Base64Value");
				addToHash(inv, values);
			}
		}
	}
	
	public static int getItemCount(String inv) {
		return customItems.containsKey(inv) ? customItems.get(inv).size() : 0;
	}
	
	public static int getCustomInventorySlot(String inv, int index) {
		return customItems.containsKey(inv) ? Integer.valueOf(customItems.get(inv).get(index)[3]) : 0;
	}
	
	public static String getCustomCommand(String inv, int index) {
		return customItems.containsKey(inv) ? customItems.get(inv).get(index)[4] : "";
	}
	
	public static ItemStack getCutomItem(String inv, int index, OfflinePlayer p) {
		if(!customItems.containsKey(inv) || (customItems.containsKey(inv) && customItems.get(inv).size() <= index)) return null;
		String[] data = customItems.get(inv).get(index);
		String base64 = data[5];
		return getItemStack(p == null ? data[0] : data[0].replace("%NAME%", p.getName()), data[1], data[2].length() == 0 ? new ArrayList<String>() : Arrays.asList((p == null ? data[2] : data[2].replace("%NAME%", p.getName())).split("//")), p, base64.length() > 20, base64);
	}
	
	private static void addToHash(String key, String[] item) {
		LinkedList<String[]> cItems = customItems.containsKey(key) ? customItems.get(key) : new LinkedList<String[]>();
		cItems.add(item);
		customItems.put(key, cItems);
	}
	
	public static ItemStack setSkin(ItemStack item, String name, boolean base64, String signature) {
		if(!item.getType().name().equals("PLAYER_HEAD")) return item;
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		if(base64) {
			GameProfile profile = new GameProfile(UUID.randomUUID(), "");
			profile.getProperties().put("textures", new Property("textures", signature));
			Field profileField = null;
			try {
				profileField = meta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(meta, profile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else meta.setOwningPlayer(Bukkit.getOfflinePlayer(FriendHash.getUUIDFromName(name)));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack replace(ItemStack item, String toReplace, String replacement) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(meta.getDisplayName().replace(toReplace, replacement));
		ArrayList<String> lore = new ArrayList<String>();
		if(meta.hasLore())
			for(String s : meta.getLore())
				lore.add(s.replace(toReplace, replacement));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack replaceMulti(ItemStack item, String[] toReplace, String[] replacements) {
		ItemMeta meta = item.getItemMeta();
		for(int i = 0; i < toReplace.length; i++) {
			meta.setDisplayName(meta.getDisplayName().replace(toReplace[i], replacements[i]));
			ArrayList<String> lore = new ArrayList<String>();
			if(meta.hasLore())
				for(String s : meta.getLore())
					lore.add(s.replace(toReplace[i], replacements[i]));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack getItemStack(String name, String material, List<String> lore, OfflinePlayer player, boolean base64, String signature) {
		ItemStack item = new ItemStack(Material.getMaterial(material.toUpperCase()));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if(!lore.isEmpty()) meta.setLore(lore);
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && player != null) {
			try {
				meta.setDisplayName((String) Class.forName("me.clip.placeholderapi.PlaceholderAPI").getMethod("setPlaceholders", OfflinePlayer.class, String.class).invoke(null, player, name));
				List<String> l = new ArrayList<String>();
				for(String v : lore)
					l.add((String) Class.forName("me.clip.placeholderapi.PlaceholderAPI").getMethod("setPlaceholders", OfflinePlayer.class, String.class).invoke(null, player, v));
				if(!l.isEmpty()) meta.setLore(l);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		return base64 ? setSkin(getItemStack(name, "PLAYER_HEAD", lore, player, false, null), name, base64, signature) : item;
	}
	
	public static void loadItems() {
		FileConfiguration cfg = FileManager.getConfig("","config.yml");
		for(ItemStacks items : ItemStacks.values())
			items.load(cfg);
		generateCustomItems(cfg);
	}
	
	private static String[] keys = new String[] {"�a","�b","�c","�d","�e","�f","�1","�2","�3","�4","�5","�6","�7","�8","�9","�o"};
	private static String createUniqueIdentifier() {
		String identifier = "";
		for(int i = 0; i < 15; i++)
			identifier = identifier + keys[new Random().nextInt(keys.length)];
		return identifier+"�r";
	}

}
