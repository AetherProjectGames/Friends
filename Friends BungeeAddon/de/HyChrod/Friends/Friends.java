package de.HyChrod.Friends;

import java.util.List;

import de.HyChrod.Friends.Commands.CommandManager;
import de.HyChrod.Friends.Commands.MSGManager;
import de.HyChrod.Friends.Commands.ReplyCommand;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Listeners.ChangeServerListener;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.Listeners.JoinListener;
import de.HyChrod.Friends.Listeners.PluginMessageListener;
import de.HyChrod.Friends.Listeners.QuitListener;
import de.HyChrod.Friends.SQL.AsyncMySQlReconnctor;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.SQL.SQLManager;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Friends.Utilities.Metrics;
import de.HyChrod.Friends.Utilities.UpdateChecker;
import de.HyChrod.Party.Party;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class Friends extends Plugin {
	
	private static String prefix;
	private static Friends instance;
	private static SQLManager SMgr;
	private static boolean update = false;

	@Override
	public void onEnable() {
		FileManager.loadFiles(this);
		prefix = ChatColor.translateAlternateColorCodes('&', FileManager.CONFIG.getConfig().getString("Friends.Prefix"));
		instance = this;
		
		SMgr = new SQLManager(FileManager.grapSQLData());
		if(!SMgr.connect()) {
			System.out.print("Friends |");
			System.out.print("Friends |");
			System.out.print("Friends | Cannot connect to MySQL!");
			System.out.print("Friends |");
			System.out.print("Friends |");
			return;
		}
		new AsyncMySQlReconnctor();
		new AsyncSQLQueueUpdater();
		
		Friends.getSMgr().perform("update friends_playerdata set online='0' where online='1';");
		
		for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
			FriendHash.getFriendHash(player.getUniqueId());
		
		Configs.loadConfigs();
		Messages.loadAll(prefix);
		
		new Metrics(this, 7398);
		
		List<String> calias = FileManager.CONFIG.getConfig().getStringList("Friends.CommandAliases");
		String[] aliases = new String[calias.size()];
		for(int i = 0; i < calias.size(); i++)
			aliases[i] = calias.get(i);
		this.getProxy().getPluginManager().registerCommand(this, new CommandManager("friends", aliases));
		if(Configs.MSG_COMMAND.getBoolean()) {
			this.getProxy().getPluginManager().registerCommand(this, new ReplyCommand("reply"));
			this.getProxy().getPluginManager().registerCommand(this, new MSGManager("msg"));
		}
		this.getProxy().getPluginManager().registerListener(this, new JoinListener());
		this.getProxy().getPluginManager().registerListener(this, new QuitListener());
		this.getProxy().getPluginManager().registerListener(this, new ChatListener());
		this.getProxy().getPluginManager().registerListener(this, new PluginMessageListener());
		this.getProxy().getPluginManager().registerListener(this, new ChangeServerListener());
		this.getProxy().registerChannel("friends:openinv");
		this.getProxy().registerChannel("friends:version");
		this.getProxy().registerChannel("friends:connect");
		this.getProxy().registerChannel("friends:reload");
		
		if(Configs.CHECK_FOR_UPDATES.getBoolean()) checkForUpdates();
		
		System.out.println("Friends | Plugin was successfully loaded!");
		if(Configs.PARTY_ENABLE.getBoolean()) new Party().enable(this);
	}
	
	private void checkForUpdates() {
		new UpdateChecker(this).getVersion(version -> {
			if(!this.getDescription().getVersion().equalsIgnoreCase(version)) {
				update = true;
				System.out.println("");
				System.out.println("Friends | You are running an outdated build. Please update to the newer version of friends!");
				System.out.println("");
			}
		});
	}
	
	public static boolean needUpdate() {
		return update;
	}
	
	@Override
	public void onDisable() {
		System.out.println("Friends | Saving data..");
		if(Party.getInstance() != null)Party.getInstance().disable();
		try {
			AsyncMySQlReconnctor.kill();
			AsyncSQLQueueUpdater.kill();
			Thread.sleep(5L);
		} catch (Exception ex) {}
		getSMgr().closeConnection();
		System.out.println("Friends | ..finished");
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static Friends getInstance() {
		return instance;
	}
	
	public static SQLManager getSMgr() {
		return SMgr;
	}
	
	public static void reload() {
		FileManager.loadFiles(getInstance());
		prefix = ChatColor.translateAlternateColorCodes('&', FileManager.CONFIG.getConfig().getString("Friends.Prefix"));
		Messages.loadAll(prefix);
		Configs.loadConfigs();
	}
	
}
