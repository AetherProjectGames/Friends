package de.HyChrod.Friends;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitWorker;

import de.HyChrod.Friends.Commands.CommandManager;
import de.HyChrod.Friends.Commands.FriendsGUICommand;
import de.HyChrod.Friends.Commands.MSGManager;
import de.HyChrod.Friends.Commands.ReplyCommand;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Listeners.BlockedInventoryListener;
import de.HyChrod.Friends.Listeners.BlockeditInventoryListener;
import de.HyChrod.Friends.Listeners.ChatListener;
import de.HyChrod.Friends.Listeners.FriendEditInventoryListener;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.Listeners.FriendItemListener;
import de.HyChrod.Friends.Listeners.JoinListener;
import de.HyChrod.Friends.Listeners.OptionsInventoryListener;
import de.HyChrod.Friends.Listeners.PluginMessageListeners;
import de.HyChrod.Friends.Listeners.QuitListener;
import de.HyChrod.Friends.Listeners.RequestEditInventoryListener;
import de.HyChrod.Friends.Listeners.RequestsInventoryListener;
import de.HyChrod.Friends.SQL.AsyncMySQlReconnctor;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.SQL.SQLManager;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Friends.Utilities.Metrics;
import de.HyChrod.Friends.Utilities.UpdateChecker;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.Utilities.PConfigs;

/**
 * 
 * Permissions:
 * - Friends.Commands.Basic
 * - Friends.Commands.Status.Show
 * - Friends.Commands.Status.Set
 * - Friends.Commands.Version
 * - Friends.Commands.Reload
 * - Friends.FriendLimit.Extended
 * - Friends.Status.ChangeLimit.ByPass
 * - Friends.Commands.Msg
 * - Friends.Commands.Msg.Bypass
 *
 */

public class Friends extends JavaPlugin {
	
	private static String prefix;
	private static Friends instance;
	private static boolean update = false;
	
	private static SQLManager smgr;
	
	@Override
	public void onEnable() {
		FileManager.loadFiles(this);
		FileManager.updateFiles();
		Configs.loadConfigs();
		PConfigs.loadConfigs();
		prefix = ChatColor.translateAlternateColorCodes('&', FileManager.CONFIG.getConfig().getString("Friends.Prefix"));
		instance = this;
		
		if(FileManager.MYSQL.getConfig().getBoolean("Enable") || Configs.BUNGEEMODE.getBoolean()) {
			smgr = new SQLManager(FileManager.grapSQLData());
			if(!smgr.connect()) {
				Bukkit.getConsoleSender().sendMessage(prefix + " §cCannot connect to MySQL!");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return;
			}
			new AsyncSQLQueueUpdater();
			new AsyncMySQlReconnctor();
			Bukkit.getConsoleSender().sendMessage(prefix + " §aConnected to MySQL!");
		}
		this.getCommand("friendsgui").setExecutor(new FriendsGUICommand());
		if(!Configs.BUNGEEMODE.getBoolean()) registerCommands();
		registerListeners();
		loadHashes();
		Messages.loadAll(prefix);
		ItemStacks.loadItems();
		InventoryBuilder.loadInventorys();
		
		if(Configs.CHECK_FOR_UPDATES.getBoolean()) checkForUpdates();
		Bukkit.getConsoleSender().sendMessage(prefix + " §aPlugin was successfully loaded!");
		if(PConfigs.PARTY_ENABLE.getBoolean()) new Party().enable(this);
		return;
	}
	
	private void registerCommands() {
		try {
			Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap cMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			cMap.register("friends", new CommandManager("friends"));
			if(Configs.MSG_COMMAND.getBoolean()) {
				cMap.register("msg", new MSGManager("msg"));
				cMap.register("r", new ReplyCommand("r"));
				cMap.register("reply", new ReplyCommand("reply"));
			}
		} catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void checkForUpdates() {
		new UpdateChecker(this).getVersion(version -> {
			if(!this.getDescription().getVersion().equalsIgnoreCase(version)) {
				update = true;
				Bukkit.getConsoleSender().sendMessage(prefix + " §cYou are running an outdated build. Please update to the newer version of friends!");
			}
		});
	}
	
	private void registerListeners() {
		new Metrics(getInstance(), 7397);
		
		if(Configs.BUNGEEMODE.getBoolean()) {
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "friends:openinv", new PluginMessageListeners());
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "friends:version", new PluginMessageListeners());
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "friends:reload", new PluginMessageListeners());
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "friends:connect");
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		}
		this.getServer().getPluginManager().registerEvents(new JoinListener(), this);
		this.getServer().getPluginManager().registerEvents(new QuitListener(), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		this.getServer().getPluginManager().registerEvents(new FriendInventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new FriendItemListener(), this);
		this.getServer().getPluginManager().registerEvents(new RequestsInventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new BlockedInventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new RequestEditInventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new BlockeditInventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new FriendEditInventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new OptionsInventoryListener(), this);
	}
	
	private void loadHashes() {
		for(Player p : Bukkit.getOnlinePlayers())
			FriendHash.getFriendHash(p.getUniqueId()).load();
	}
	
	private void saveHashes() {
		for(FriendHash hashes : FriendHash.getHashes().values())
			hashes.save();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(getPrefix() + " §7Saving data..");
		saveHashes();
		Bukkit.getServer().getScheduler().cancelTasks(this);
		AsyncSQLQueueUpdater.kill();
		if(smgr != null) smgr.closeConnection();
		synchronized (this) {
			try {
				wait(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			for(BukkitWorker bw : Bukkit.getScheduler().getActiveWorkers()) bw.getThread().interrupt();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Bukkit.getConsoleSender().sendMessage(getPrefix() + " §7..finished");
		Bukkit.getConsoleSender().sendMessage(getPrefix() + " §7Friends was disabled");
		return;
	}
	
	public static String getMessage(String path) {
		return ChatColor.translateAlternateColorCodes('&', FileManager.getConfig("", "Messages.yml").getString(path).replace("%PREFIX%", prefix));
	}
	
	public static Friends getInstance() {
		return instance;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static SQLManager getSMgr() {
		return smgr;
	}
	
	public static boolean isUpdateNeeded() {
		return update;
	}
	
	public static void reload() {
		FileManager.loadFiles(getInstance());
		Configs.loadConfigs();
		prefix = ChatColor.translateAlternateColorCodes('&', FileManager.CONFIG.getConfig().getString("Friends.Prefix"));
		Messages.loadAll(getPrefix());
		ItemStacks.loadItems();
		InventoryBuilder.loadInventorys();
	}
	
	public static boolean isMySQL() {
		return smgr != null && smgr.isConnected();
	}
	
	public String getNMS() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}
	
}
