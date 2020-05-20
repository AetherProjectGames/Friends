package de.HyChrod.Party.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.FileManager;

public enum PInventoryBuilder {
	
	PARTY_CREATE("Party.CreateInventory.InventoryTitle","Party.CreateInventory.InventorySize"),
	PARTY_PARTY("Party.PartyInventory.InventoryTitle","Party.PartyInventory.InventorySize"),
	PARTY_EDIT_MEMBER("Party.EditMemberInventory.InventoryTitle","Party.EditMemberInventory.InventorySize");
	
	private String title, title_path, size_path;
	private int size;
	
	private PInventoryBuilder(String title, String size) {
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
		FileConfiguration cfg = FileManager.PARTY.getConfig();
		for(PInventoryBuilder builders : PInventoryBuilder.values()) {
			System.out.println(builders.name());
			builders.load(cfg);
		}
	}
	
	public static void openCreateInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, PARTY_CREATE.getSize(), PARTY_CREATE.getTitle(p));
		
		for(String slots : FileManager.PARTY.getConfig().getStringList("Party.CreateInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, PItemStacks.INV_CREATE_PLACEHOLDER.getItem(p));
		
		inv.setItem(PItemStacks.INV_CREATE_CREATE.getInventorySlot(), PItemStacks.INV_CREATE_CREATE.getItem(p));
		inv.setItem(PItemStacks.INV_CREATE_BACK.getInventorySlot(), PItemStacks.INV_CREATE_BACK.getItem(p));
		
		p.openInventory(inv);
	}
	
	public static HashMap<String, UUID> openPartyInventory(Player p, Parties party) {
		Inventory inv = Bukkit.createInventory(null, PARTY_PARTY.getSize(), PARTY_PARTY.getTitle(p));
		
		for(String slots : FileManager.PARTY.getConfig().getStringList("Party.PartyInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slots)-1, PItemStacks.INV_PARTY_PLACEHOLDER.getItem(p));
		
		
		inv.setItem(PItemStacks.INV_PARTY_BACK.getInventorySlot(), PItemStacks.INV_PARTY_BACK.getItem(p));
		String visibility = party.isPublic() ? PConfigs.PARTY_VISIBILITY_PUBLIC.getColoredText() : PConfigs.PARTY_VISIBILITY_PRIVATE.getColoredText();
		inv.setItem(PItemStacks.INV_PARTY_LEAVE.getInventorySlot(), PItemStacks.INV_PARTY_LEAVE.getItem(p));
		if(party.isLeader(p.getUniqueId()))
			inv.setItem(PItemStacks.INV_PARTY_VISIBILITY.getInventorySlot(), PItemStacks.replace(PItemStacks.INV_PARTY_VISIBILITY.getItem(p), "%PARTY_STATUS%", visibility));
		
		HashMap<String, UUID> cachedPositions = new HashMap<String, UUID>();
		for(UUID members : party.getMembers()) {
			String name = FriendHash.getName(members);
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			
			String identifier = createUniqueIdentifier();
			
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(members));
			meta.setDisplayName(identifier + (party.isLeader(members) ? PConfigs.LEADER_NAME.getColoredText() : PConfigs.MEMBER_NAME.getColoredText()).replace("%NAME%", name));
			if(party.isLeader(p.getUniqueId()))
				meta.setLore(Arrays.asList((party.isLeader(members) ? PConfigs.LEADER_LORE.getColoredText() : PConfigs.MEMBER_LORE.getColoredText()).split("//")));
			
			head.setItemMeta(meta);
			inv.addItem(head);
			cachedPositions.put(identifier, members);
		}
		
		p.openInventory(inv);
		return cachedPositions;
	}
	
	public static void openEditInventory(Player p, UUID toEdit) {
		String name = FriendHash.getName(toEdit);
		Inventory inv = Bukkit.createInventory(null, PARTY_EDIT_MEMBER.getSize(), PARTY_EDIT_MEMBER.getTitle(p).replace("%NAME%", name));
		
		for(String slot : FileManager.PARTY.getConfig().getStringList("Party.EditMemberInventory.Placeholders.InventorySlots"))
			inv.setItem(Integer.parseInt(slot)-1, PItemStacks.INV_EDIT_PLACEHOLDERS.getItem(p));
		
		inv.setItem(PItemStacks.INV_EDIT_BACK.getInventorySlot(), PItemStacks.INV_EDIT_BACK.getItem(p));
		inv.setItem(PItemStacks.INV_EDIT_DEMOTE.getInventorySlot(), PItemStacks.replace(PItemStacks.INV_EDIT_DEMOTE.getItem(p), "%NAME%", name));
		inv.setItem(PItemStacks.INV_EDIT_PROMOTE.getInventorySlot(), PItemStacks.replace(PItemStacks.INV_EDIT_PROMOTE.getItem(p), "%NAME%", name));
		inv.setItem(PItemStacks.INV_EDIT_REMOVE.getInventorySlot(), PItemStacks.replace(PItemStacks.INV_EDIT_REMOVE.getItem(p), "%NAME%", name));
		
		p.openInventory(inv);
	}
	
	private static String[] keys = new String[] {"§a","§b","§c","§d","§e","§f","§1","§2","§3","§4","§5","§6","§7","§8","§9","§o"};
	
	private static String createUniqueIdentifier() {
		String identifier = "";
		for(int i = 0; i < 15; i++)
			identifier = identifier + keys[new Random().nextInt(keys.length)];
		return identifier+"§r";
	}

}
