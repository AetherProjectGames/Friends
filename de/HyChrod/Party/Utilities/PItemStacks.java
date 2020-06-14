package de.HyChrod.Party.Utilities;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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
import de.HyChrod.Friends.Utilities.FileManager;

public enum PItemStacks {
	
	INV_CREATE_CREATE("Party.CreateInventory.CreateItem"),
	INV_CREATE_BACK("Party.CreateInventory.BackItem"),
	INV_CREATE_PLACEHOLDER("Party.CreateInventory.Placeholders"),
	INV_PARTY_BACK("Party.PartyInventory.BackItem"),
	INV_PARTY_PLACEHOLDER("Party.PartyInventory.Placeholders"),
	INV_PARTY_VISIBILITY("Party.PartyInventory.VisibilityItem"),
	INV_PARTY_LEAVE("Party.PartyInventory.LeaveItem"),
	INV_EDIT_PROMOTE("Party.EditMemberInventory.PromoteItem"),
	INV_EDIT_DEMOTE("Party.EditMemberInventory.DemoteItem"),
	INV_EDIT_REMOVE("Party.EditMemberInventory.RemoveItem"),
	INV_EDIT_BACK("Party.EditMemberInventory.BackItem"),
	INV_EDIT_PLACEHOLDERS("Party.EditMemberInventory.Placeholders");
	
	private String path;
	private String name = " ";
	private List<String> lore;
	private String material;
	private int inventoryslot;
	private String base64value;
	
	private PItemStacks(String path) {
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
		if(cfg.get(path + ".Base64Value") != null) this.base64value = cfg.getString(path + ".Base64Value");
	}
	
	public ItemStack getItem(OfflinePlayer player) {
		return getItemStack(name, material, lore, player, base64value.length() > 20, base64value);
	}
	
	public int getInventorySlot() {
		return inventoryslot-1;
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
		FileConfiguration cfg = FileManager.getConfig("","Party.yml");
		for(PItemStacks items : PItemStacks.values()) {
			items.load(cfg);
		}
	}

}
