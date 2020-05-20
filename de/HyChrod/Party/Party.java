package de.HyChrod.Party;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.SQLManager;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Party.Commands.CommandManager;
import de.HyChrod.Party.Listeners.ChatListener;
import de.HyChrod.Party.Listeners.CreateInventoryListener;
import de.HyChrod.Party.Listeners.PartyEditInventoryListener;
import de.HyChrod.Party.Listeners.PartyInventoryListener;
import de.HyChrod.Party.Listeners.WorldChangeListener;
import de.HyChrod.Party.Utilities.PInventoryBuilder;
import de.HyChrod.Party.Utilities.PItemStacks;
import de.HyChrod.Party.Utilities.PMessages;
import net.md_5.bungee.api.ChatColor;

public class Party {
	
	private static SQLManager SMgr;
	private static Party instance;
	private static String prefix;
	private static boolean hasFriends = true;

	public void enable(Friends friends) {
		instance = this;
		prefix = ChatColor.translateAlternateColorCodes('&', FileManager.PARTY.getConfig().getString("Party.Prefix"));
		PMessages.loadAll(prefix);
		PItemStacks.loadItems();
		PInventoryBuilder.loadInventorys();
		try {
			Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap cMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			cMap.register("party", new CommandManager("party"));
		} catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(Configs.BUNGEEMODE.getBoolean()) {
			friends.getServer().getMessenger().registerOutgoingPluginChannel(friends, "party:invite");
			friends.getServer().getMessenger().registerOutgoingPluginChannel(friends, "party:create");
		}
		
		Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), friends);
		if(!Configs.BUNGEEMODE.getBoolean()) Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(), friends);
		Bukkit.getServer().getPluginManager().registerEvents(new PartyInventoryListener(), friends);
		Bukkit.getServer().getPluginManager().registerEvents(new CreateInventoryListener(), friends);
		Bukkit.getServer().getPluginManager().registerEvents(new PartyEditInventoryListener(), friends);
		
		Bukkit.getConsoleSender().sendMessage(prefix + " §aThe plugin was successfully loaded!");
	}
	
	public void disable() {
		System.out.println("Party | The plugin was successfully disabled!");
		return;
	}
	
	public static SQLManager getSMgr() {
		return SMgr;
	}
	
	public static Party getInstance() {
		return instance;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static boolean hasFriends() {
		return hasFriends;
	}
	
}
