package de.HyChrod.Friends.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public enum FileManager {
	
	CONFIG("", "config.yml"),
	MESSAGES("","Messages.yml"),
	MYSQL("","MySQL.yml"),
	FORBIDDEN_PHRASES("","forbidden_phrases.txt"),
	PLAYERDATA("/Util","playerdata.dat"),
	REQUESTS("/Util","requests.dat"),
	FRIENDS("/Util","friends.dat"),
	BLOCKED("/Util","blocked.dat"),
	OPTIONS("/Util","options.dat");
	
	private String path, name;
	private FileConfiguration config;
	private File file;
	
	private FileManager(String path, String name) {
		this.path = path;
		this.name = name;
	}
	
	private void load() {
		this.file = getFile(path, name);
		this.config = getConfig(file);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public FileConfiguration getNewCfg() {
		return getConfig(path, name);
	}
	
	public File getNewFile() {
		return getFile(path, name);
	}
	
	public File getFile() {
		return file;
	}
	
    public static File getFile(final String path, final String name) {
        return new File("plugins/FriendsRELOADED" + path, name);
    }
    
    public static File getFile(final Plugin plugin, final String name) {
        return new File(plugin.getDataFolder(), name);
    }
    
    public static FileConfiguration getConfig(final String path, final String name) {
        return (FileConfiguration)new UTF8YamlConfiguration(getFile(path, name));
    }
    
    public static FileConfiguration getConfig(final Plugin plugin, final String name) {
        return (FileConfiguration)new UTF8YamlConfiguration(getFile(plugin, name));
    }
    
    public static FileConfiguration getConfig(final File file) {
        return (FileConfiguration) new UTF8YamlConfiguration(file);
    }
    
    public static void save(final FileConfiguration ccfg, final File file, final String path, final Object obj) {
        FileConfiguration cfg = (ccfg != null) ? ccfg : getConfig(file);
        cfg.set(path, obj);
        saveFile(cfg, file);
    }
    
    public static void updateFiles() {
    	if(CONFIG.getNewCfg().get("Friends.FriendInventory.HidePageItemsWhenNotNeeded") == null) {
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.HidePageItemsWhenNotNeeded", false);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.HidePageItemsWhenNotNeeded", false);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.HidePageItemsWhenNotNeeded", false);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Commands.EnableClickableMessages", true);
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.Message", "%PREFIX% &6%ACCEPT_BUTTON% &aor &c%DENY_BUTTON% &a%NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.AcceptButton.Text", "[Accept]");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.AcceptButton.Hover", "&aClick here to accept %NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.DenyButton.Text", "[Deny]");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.DenyButton.Hover", "&cClick here to deny %NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.DenyCommand.NoRequest", "%PREFIX% &cYou do not have a request from %NAME%!");	
    	   	if(CONFIG.getNewCfg().get("Friends.BlockedEditInventory.NoteItem.ShowItem") == null) {
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedEditInventory.NoteItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.PreviousPageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.NextPageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.UnblockAllItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.MessageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.DenyAllItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.AcceptAllItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.PreviousPageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.NextPageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.NicknameItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.FavoriteItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.CanSendMessagesItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PreviousPageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.NextPageItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.SortItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.JumpItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.JumpItem.Name", "&9Jumping");
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.JumpItem.ItemID", "401");
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.JumpItem.Lore", "&7Currently: %OPTION_JUMPING_STATUS%// //&7If enabled, friends can jump to//&7your current location!");
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.JumpItem.InventorySlot", 4);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.JumpItem.ShowItem", true);
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.JumpItem.Name", "&9Jump to %NAME%");
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.JumpItem.ItemID", "401");
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.JumpItem.Lore", "");
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.JumpItem.InventorySlot", 4);
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.NoFriends", "%PREFIX% &c%NAME% has to be your friend in order to jump to his location!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.PlayerOffline", "%PREFIX% &c%NAME% seems to be offline!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.JumpingDisabled", "%PREFIX% &c%NAME% does not want players to jump to his location!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.JumpToFriend", "%PREFIX% &aYou jumped to %NAME%'s location!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.JumpedToYou", "%PREFIX% &3%NAME% jumped to your location!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsMessagesCommand.OnlyFavorites", "%PREFIX% &6Only your favorite players are able to send you messages now!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsJumpingCommand.Enable", "%PREFIX% &aYou enabled jumping to your location!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsJumpingCommand.Disable", "%PREFIX% &7You disbaled jumping to your location!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsRequestsCommand.Enable", "%PREFIX% &aPlayers are now able to send your friendrequests!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsRequestsCommand.Disable", "%PREFIX% &7Players are no longer able to send you requests!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsMessagesCommand.Enable", "%PREFIX% &7You can now receive messages from your friends!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsMessagesCommand.Disable", "%PREFIX% &aYou won't get any further messages from your friends!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsOfflinemodeCommand.Enable", "%PREFIX% &7You are now offline!");
        		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.OptionsOfflinemodeCommand.Disable", "%PREFIX% &aYou are now online!");
        		if(CONFIG.getNewCfg().get("Friends.Status.AllowFarbcodes") == null) {
            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Status.Farbcodes", false);
                	if(CONFIG.getNewCfg().get("Friends.CommandAliases") == null) {
                		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.CommandAliases", Arrays.asList("f","freunde","freund","friend"));
                		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendMSG.UseMSGCommand", true);
                		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendItem.DelayedInventorySet", false);
                    	if(CONFIG.getNewCfg().get("Friends.OpenGUIWithCommand") == null) {
                    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OpenGUIWithCommand", true);
                        	if(CONFIG.getNewCfg().get("Friends.FriendInventory.FriendsItem.LoreOnline") == null) {
                        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.Lore", null);
                        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.LoreOnline", "&7Status: //&r&o%STATUS%// //&7Friends since: %DATE%");
                        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.LoreOffline", "&7Status: //&r&o%STATUS%// //&7Last online: %LAST_ONLINE% o'clock//&7Friends since: %DATE%");
                        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.LastOnlineFormat", "MM/dd/yyyy - hh:mm");
                            	if(CONFIG.getNewCfg().get("Friends.BungeeMode") == null) {
                            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BungeeMode", false);
                            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.OnlineStatus", "&a(Online)");
                            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.OfflineStatus", "&7(Offline)");
                            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.NameOnline", null);
                            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.NameOffline", null);
                            		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.FriendsItem.Name", "&a%NAME% %ONLINE_STATUS%");
                                	if(CONFIG.getNewCfg().get("Friends.CheckForUpdates") == null)
                                		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.CheckForUpdates", true);
                            	}
                        	}
                    	}
                	}
            	}
        	}
    	}
    	CONFIG.load();
    }
    
    private static void saveFile(final FileConfiguration cfg, final File file) {
        try {
            cfg.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void saveRessource(final Plugin plugin, final String name) {
        final File FILE = getFile(plugin, name);
        if (FILE.exists()) {
            return;
        }
        try {
            final InputStream STREAM = plugin.getResource(name);
            final InputStreamReader READER = new InputStreamReader(STREAM);
            if (STREAM != null) {
                YamlConfiguration.loadConfiguration((Reader)READER).save(FILE);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static String[] grapSQLData() {
    	FileConfiguration cfg = MYSQL.getConfig();
    	String[] data = new String[5];
    	data[0] = cfg.getString("Data.Host");
    	data[1] = cfg.getString("Data.Port");
    	data[2] = cfg.getString("Data.Database");
    	data[3] = cfg.getString("Data.Username");
    	data[4] = cfg.getString("Data.Password");
    	return data;
    }
    
    public static void loadFiles(final Plugin plugin) {
        plugin.saveDefaultConfig();
        saveRessource(plugin, "Messages.yml");
        saveRessource(plugin, "MySQL.yml");
        saveRessource(plugin, "forbidden_phrases.txt");
        
        for(FileManager mgrs : FileManager.values())
        	mgrs.load();
    }
}

class UTF8YamlConfiguration extends YamlConfiguration {
	
    public UTF8YamlConfiguration(final File file) {
        try {
            this.load(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void save(final File file) throws IOException {
        Validate.notNull((Object)file, "File cannot be null");
        Files.createParentDirs(file);
        final String data = this.saveToString();
        final Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
        try {
            writer.write(data);
        }
        finally {
            writer.close();
        }
        writer.close();
    }
    
    public void load(final File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        Validate.notNull((Object)file, "File cannot be null");
        if (file.exists()) {
            this.load((Reader)new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
        }
    }
	
}
