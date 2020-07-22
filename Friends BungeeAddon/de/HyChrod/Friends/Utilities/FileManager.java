package de.HyChrod.Friends.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public enum FileManager {
	
	CONFIG("", "config.yml"),
	MESSAGES("","Messages.yml"),
	MYSQL("","MySQL.yml"),
	FORBIDDEN_PHRASES("","forbidden_phrases.txt");
	
	private String path, name;
	private Configuration config;
	private File file;
	
	private FileManager(String path, String name) {
		this.path = path;
		this.name = name;
	}
	
	private void load() {
		this.file = getFile(path, name);
		this.config = getConfig(file);
	}
	
	public Configuration getConfig() {
		return config;
	}
	
	public Configuration getNewCfg() {
		return getConfig(path, name);
	}
	
	public File getNewFile() {
		return getFile(path, name);
	}
	
	public File getFile() {
		return file;
	}
	
    public static File getFile(final String path, final String name) {
        return new File("plugins/Friends BungeeAddon" + path, name);
    }
    
    public static File getFile(final Plugin plugin, final String name) {
        return new File(plugin.getDataFolder(), name);
    }
    
    public static Configuration getConfig(final String path, final String name) {
    	try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(path, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static Configuration getConfig(final Plugin plugin, final String name) {
    	try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile(plugin, name));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static Configuration getConfig(final File file) {
        try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public static void save(final Configuration ccfg, final File file, final String path, final Object obj) {
    	Configuration cfg = (ccfg != null) ? ccfg : getConfig(file);
        cfg.set(path, obj);
        try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void updateFiles() {
    	if(MESSAGES.getNewCfg().get("PartyMessages.SwitchServer.ServerBlocked") == null) {
    		// v1.2.6
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.DisabledServers", Arrays.asList("dev","build","lobby"));
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Party.DisabledServers", Arrays.asList("build","lobby"));
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.ServerBlocked", "%PREFIX% &cYou cannot jump to your friends current server!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.SwitchServer.ServerBlocked", "%PREFIX% &cYou cannot enter this server while beeing in a party!");
    	}
    	if(CONFIG.getNewCfg().getString("Party.CommandAliases") == null) {
    		// v1.2.5
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Party.CommandAliases", Arrays.asList("parties","p"));
        	save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Commands.Jumping.Enable", true);
    	}
		if(MESSAGES.getNewCfg().getString("Messages.Commands.ReplyCommand.NoReply") == null) {
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.ReplyCommand.NoReply", "%PREFIX% &cYou do not have any message to reply to!");
    	}
       	if(MESSAGES.getNewCfg().getString("PartyMessages.Commands.HelpCommand.UnknownPage") == null) {
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.NoPermissions", "%PREFIX% &cYou do not have permissions to do that!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Version", "%PREFIX% &aYou are running Party-Version: %VERSION%");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.NoPlayer", "%PREFIX% &cOnly players are able to perform this command!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.UnknownCommand", "%PREFIX% &cUnknown command! -> /party help");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.WrongUsage", "%PREFIX% &cWrong Usage! -> %USAGE%");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PlayerInvalid", "%PREFIX% &c%NAME% seems to be offline!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.NoFriends", "%PREFIX% &c%NAME% has to be your friend in order to invite him to your party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.NoInvites", "%PREFIX% &c%NAME% does not want to receive any party-invites!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.AlreadyInParty", "%PREFIX% &c%NAME% is already in a party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.NotLeader", "%PREFIX% &cOnly party leaders are able to invite new members!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.AlreadyInvited", "%PREFIX% &cYou have already sent an invite to %NAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.PlayerOffline", "%PREFIX% &c%NAME% seems to be offline!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.InviteSent", "%PREFIX% &aYou have successfully send an invite to %NAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.InviteReceived", "%PREFIX% &3%NAME% &awants you to join his party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.CreateParty", "%PREFIX% &aYou successfully created a party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.ClickableMessage.Message", "%PREFIX% %ACCEPT_BUTTON% &aor %DENY_BUTTON% &a%NAME%'s party-invite!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.ClickableMessage.AcceptButton.Text", "&6[Accept]");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.ClickableMessage.AcceptButton.Hover", "&aClick here to accept %NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.ClickableMessage.DenyButton.Text", "&c[Deny]");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.InviteCommand.ClickableMessage.DenyButton.Hover", "&cClick here to deny %NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DenyCommand.NoInvite", "%PREFIX% &cYou do not have an invite from %NAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DenyCommand.Deny", "%PREFIX% &7You denied the invite from %NAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DenyCommand.NoNewInvite", "%PREFIX% &cYou do not have any new invite!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DenyCommand.Denied", "%PREFIX% &7%NAME% denied your party-invite!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.NoInvite", "%PREFIX% &cYou do not have an invite from %NAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.PartyClosed", "%PREFIX% &cThe party you want to join does not exist anymore!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.NoNewInvite", "%PREFIX% &cYou do not have any new invite!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.InviteExpired", "%PREFIX% &cThis party-invite has expired!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.PartyLimitReached", "%PREFIX% &cThe party you want to join has already reached the maximum amount of players!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.Join", "%PREFIX% &aYou joined %NAME%'s party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.AcceptCommand.NewMember", "%PREFIX% &3%NAME% has joined the party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.LeaveCommand.NoParty", "%PREFIX% &cYou are currently in no party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.LeaveCommand.Leave", "%PREFIX% &7You left the party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.LeaveCommand.MemberLeave", "%PREFIX% &3%NAME% &7has left the party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PromoteCommand.NotInParty", "%PREFIX% &c%NAME% is not a member of your party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PromoteCommand.NoParty", "%PREFIX% &cYou are currently in no party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PromoteCommand.NoLeader", "%PREFIX% &cOnly partyleaders are able to promote other members!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PromoteCommand.Promoted", "%PREFIX% &a%NAME% is now a party-leader!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PromoteCommand.NewLeader", "%PREFIX% &aYou are now a party-leader!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.PromoteCommand.AlreadyLeader", "%PREFIX% &c%NAME% is already a party-leader!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DemoteCommand.NotInParty", "%PREFIX% &c%NAME% is not a member of your party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DemoteCommand.NoParty", "%PREFIX% &cYou are currently in no party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DemoteCommand.AlreadyMember", "%PREFIX% &c%NAME% is already a normal party-member!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DemoteCommand.NoLeader", "%PREFIX% &cOnly partyleaders are able to demote other members!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DemoteCommand.Demoted", "%PREFIX% &7%NAME% is no longer a leader!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.DemoteCommand.NewMember", "%PREFIX% &7You are no longer a party-leader!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.ListCommand.NoParty", "%PREFIX% &cYou are currently in no party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.ListCommand.List", "%PREFIX% &aLeaders (&7%LEADER_COUNT%&a):\\n%PREFIX% &7%PARTY_LEADERS%\\n%PREFIX% &aMembers (&7%MEMBER_COUNT%&a):\\n%PREFIX% &7%PARTY_MEMBERS%");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.JoinCommand.NoParty", "%PREFIX% &c%NAME% has no party to join!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.JoinCommand.Private", "%PREFIX% &c%NAME%'s party is private!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.JoinCommand.InParty", "%PREFIX% &cYou are already in a party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.JoinCommand.PartyLimitReached", "%PREFIX% &cThe party you want to join has already reached the maximum amount of players!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.PartyChat.NoParty", "%PREFIX% &cYou are currently in no party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.PartyChat.Message", "%PREFIX% &3%NAME% | &r%MESSAGE%");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.SwitchServer.NoLeader", "%PREFIX% &cOnly party-leaders can switch servers!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.SwitchServer.JoinServer", "%PREFIX% &aYour party joins server %SERVER%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.KickCommand.NoParty", "%PREFIX% &cYou are currently in no party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.KickCommand.NoLeader", "%PREFIX% &cYou are not allowed to kick other members!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.KickCommand.Kick", "%PREFIX% &7Y%NAME% was kicked out of the party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.KickCommand.Kicked", "%PREFIX% &cYou were kicked out of the party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.KickCommand.NotInParty", "%PREFIX% &c%NAME% is not in your party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.CreateCommand.InParty", "%PREFIX% &cYou are already in a party!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.CreateCommand.Create", "%PREFIX% &aYou successfully created a party!");	
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.HelpCommand.UnknownPage", "%PREFIX% &c%PAGE% &c is not a valid helppage!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.HelpCommand.Page1", Arrays.asList("%PREFIX% ------------------------------------------",
    				"%PREFIX% &b/party invite <Name>",
    				"%PREFIX% &b/party accept <Name>",
    				"%PREFIX% &b/party deny <Name>",
    				"%PREFIX% &b/party leave",
    				"%PREFIX%",
    				"%PREFIX% &7More commands -> /party help 2",
    				"%PREFIX% ------------------------------------------"));
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.Commands.HelpCommand.Page2", Arrays.asList("%PREFIX% ------------------------------------------",
    				"%PREFIX% &b/party kick <Name>",
    				"%PREFIX% &b/party promote <Name>",
    				"%PREFIX% &b/party demote <Name>",
    				"%PREFIX% &b@party <Message>",
    				"%PREFIX% &b/party join <Name>",
    				"%PREFIX% &b/party list",
    				"%PREFIX% ------------------------------------------"));
       	}
    	if(MESSAGES.getNewCfg().get("Messages.Commands.DenyCommand.NoRequest") == null) {
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Commands.EnableClickableMessages", true);
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.Message", "%PREFIX% &6%ACCEPT_BUTTON% &aor &c%DENY_BUTTON% &a%NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.AcceptButton.Text", "[Accept]");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.AcceptButton.Hover", "&aClick here to accept %NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.DenyButton.Text", "[Deny]");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.AddCommand.ClickableMessage.DenyButton.Hover", "&cClick here to deny %NAME%'s request!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.DenyCommand.NoRequest", "%PREFIX% &cYou do not have a request from %NAME%!");	
    	}
    	if(MESSAGES.getNewCfg().get("Messages.Commands.OptionsOfflinemodeCommand") == null) {
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
    	}
    	if(CONFIG.getNewCfg().get("Friends.CommandAliases") == null) {
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.CommandAliases", Arrays.asList("f","freund","freunde","friend"));
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendMSG.UseMSGCommand", true);
        	if(CONFIG.getNewCfg().get("Friends.CheckForUpdates") == null)
        		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.CheckForUpdates", true);
    	}
    	CONFIG.load();
    }
    
    private static void saveRessource(final Plugin plugin, final String name) throws IOException {
    	File file = new File(plugin.getDataFolder(), name);
    	if(!file.exists())
            try (InputStream in = plugin.getResourceAsStream(name)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    public static String[] grapSQLData() {
    	Configuration cfg = MYSQL.getConfig();
    	String[] data = new String[5];
    	data[0] = cfg.getString("Data.Host");
    	data[1] = cfg.getString("Data.Port");
    	data[2] = cfg.getString("Data.Database");
    	data[3] = cfg.getString("Data.Username");
    	data[4] = cfg.getString("Data.Password");
    	return data;
    }
    
    public static void loadFiles(final Plugin plugin) {
    	if(!plugin.getDataFolder().exists())
    		plugin.getDataFolder().mkdir();
    	
    	try {
        	saveRessource(plugin, "config.yml");
            saveRessource(plugin, "Messages.yml");
            saveRessource(plugin, "MySQL.yml");
            saveRessource(plugin, "forbidden_phrases.txt");
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
        
        for(FileManager mgrs : FileManager.values())
        	mgrs.load();
        
        updateFiles();
    }
}
