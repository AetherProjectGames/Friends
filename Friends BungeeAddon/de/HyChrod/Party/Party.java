package de.HyChrod.Party;

import java.util.List;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.SQLManager;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Party.Commands.CommandManager;
import de.HyChrod.Party.Listeners.ChatListener;
import de.HyChrod.Party.Listeners.ServerChangeListener;
import de.HyChrod.Party.Utilities.PMessages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;

public class Party {
	
	private static SQLManager SMgr;
	private static Party instance;
	private static String prefix;
	private static boolean hasFriends = true;

	public void enable(Friends friends) {
		instance = this;
		prefix = ChatColor.translateAlternateColorCodes('&', FileManager.CONFIG.getConfig().getString("Party.Prefix"));
		PMessages.loadAll(prefix);
		
		friends.getProxy().registerChannel("party:invite");
		friends.getProxy().registerChannel("party:create");
		friends.getProxy().registerChannel("party:openinv");
		
		List<String> calias = FileManager.CONFIG.getConfig().getStringList("Party.CommandAliases");
		String[] aliases = new String[calias.size()];
		for(int i = 0; i < calias.size(); i++)
			aliases[i] = calias.get(i);
		BungeeCord.getInstance().getPluginManager().registerCommand(friends, new CommandManager("party", aliases));
		BungeeCord.getInstance().getPluginManager().registerListener(friends, new ChatListener());
		BungeeCord.getInstance().getPluginManager().registerListener(friends, new ServerChangeListener());
		
		System.out.println("Party | The plugin was successfully enabled!");
	}
	
	public void disable() {
		Friends.getSMgr().perform("delete from party");
		Friends.getSMgr().perform("delete from party_leaders");
		Friends.getSMgr().perform("delete from party_members");
		Friends.getSMgr().perform("delete from party_players");
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
