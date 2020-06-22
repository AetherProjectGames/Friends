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
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.HyChrod.Friends.Friends;

public enum FileManager {
	
	CONFIG("", "config.yml"),
	MESSAGES("","Messages.yml"),
	MYSQL("","MySQL.yml"),
	FORBIDDEN_PHRASES("","forbidden_phrases.txt"),
	PLAYERDATA("/Util","playerdata.dat"),
	REQUESTS("/Util","requests.dat"),
	FRIENDS("/Util","friends.dat"),
	BLOCKED("/Util","blocked.dat"),
	OPTIONS("/Util","options.dat"),
	PARTY("","Party.yml");
	
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
    	if(CONFIG.getNewCfg().get("Friends.FriendInventory.RequestsItem.Base64Value") == null) {
    		// v1.3.0
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.RequestsItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.BlockedItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.OptionsItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.SortItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.NextPageItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PreviousPageItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PartyItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.Placeholders.Base64Value", "");
    		
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.FavoriteItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.CanSendMessagesItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.NicknameItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.RemoveItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.JumpItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.PartyItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.Placeholders.Base64Value", "");
    		
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.AcceptAllItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.DenyAllItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.PreviousPageItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.NextPageItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.Placeholders.Base64Value", "");
    		
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.MessageItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.AcceptItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.DenyItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.Placeholders.Base64Value", "");
    		
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.UnblockAllItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.NextPageItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.PreviousPageItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.Placeholders.Base64Value", "");
    	
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedEditInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedEditInventory.UnblockItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedEditInventory.NoteItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedEditInventory.Placeholders.Base64Value", "");
    		
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.ReceiveMessagesItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.ReceiveRequestsItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.OfflinemodeItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.StatusItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.JumpItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.PartyItem.Base64Value", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.Placeholders.Base64Value", "");
    		
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.CreateInventory.CreateItem.Base64Value", "");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.CreateInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.CreateInventory.Placeholders.Base64Value", "");
    		
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.PartyInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.PartyInventory.VisibilityItem.Base64Value", "");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.PartyInventory.Placeholders.Base64Value", "");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.PartyInventory.LeaveItem.Base64Value", "");
    		
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.EditMemberInventory.PromoteItem.Base64Value", "");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.EditMemberInventory.DemoteItem.Base64Value", "");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.EditMemberInventory.RemoveItem.Base64Value", "");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.EditMemberInventory.BackItem.Base64Value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFmYWIwZTZhZWE4ODc0OGNhM2I1NTEyZWQ1MDJhNmQxOGU3NmQ4YWZjNDc3MGQ5OTUyMzNhYzBkYzUxODYifX19");
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.EditMemberInventory.Placeholders.Base64Value", "");
    		
    	}
    	if(CONFIG.getNewCfg().get("Friends.DisabledWorlds") == null) {
    		// v1.2.8
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.DisabledWorlds", Arrays.asList("world_end","world_nether"));
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.DisabledWorlds", Arrays.asList("world_end","world_nether"));
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.ServerBlocked", "%PREFIX% &cYou cannot jump to your friends current world!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "PartyMessages.SwitchServer.ServerBlocked", "%PREFIX% &cYou cannot enter this world while beeing in a party!");
    	}
    	if(CONFIG.getNewCfg().get("Friends.FriendItemOptions.KeepOnDeath") == null) {
    		// v1.2.7
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendItemOptions.KeepOnDeath", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendItemOptions.MoveItemInInventory", false);
    	}
    	if(MESSAGES.getNewCfg().get("Messages.Commands.JumpCommand.JumpingNotAllowed") == null) {
    		// v1.2.5
    		save(PARTY.getNewCfg(), PARTY.getNewFile(), "Party.CommandAliases", Arrays.asList("parties","p"));
        	save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Commands.Jumping.Enable", true);
        	save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.JumpCommand.JumpingNotAllowed", "%PREFIX% &cJumping cannot be used on this server!");
    	}
		if(CONFIG.getNewCfg().get("Friends.FriendInventory.RequestsItem.ShowItem") == null) {
			// v1.2.4
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.RequestsItem.ShowItem", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.BlockedItem.ShowItem", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.OptionsItem.ShowItem", true);
    	}
    	if(MESSAGES.getNewCfg().get("Messages.Commands.ReplyCommand.NoReply") == null) {
    		// v1.2.3
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.ReplyCommand.NoReply", "%PREFIX% &cYou do not have any message to reply to!");
    	}
       	if(MESSAGES.getNewCfg().get("PartyMessages.Commands.KickCommand.NoParty") == null) {
       		// v1.2.0
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
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PartyItem.ShowItem", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PartyItem.Material", "firework_rocket");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PartyItem.Name", "&9Your Party");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PartyItem.Lore", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.PartyItem.InventorySlot", 50);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.PartyItem.ShowItem", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.PartyItem.Material", "firework_star");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.PartyItem.Name", "&dInvite %NAME% to your Party!");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.PartyItem.Lore", "");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.PartyItem.InventorySlot", 7);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.PartyItem.ShowItem", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.PartyItem.Material", "firework_star");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.PartyItem.Name", "&dParty Invites");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.PartyItem.Lore", "&7Currently: %OPTION_PARTY_STATUS%// //&7Toggle wether you want to receive//&7partyinvites from your friends!\"");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.PartyItem.InventorySlot", 2);        	
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
		if(MESSAGES.getNewCfg().get("Messages.Commands.NicknameCommand.NoFriends") == null) {
			save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.NicknameCommand.NoFriends", "%PREFIX% &c%NAMER% has to be your friend in order to give him a nickname!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.NicknameCommand.SameNickname", "%PREFIX% &c%NAME% is already named %NICKNAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.NicknameCommand.SetNick", "%PREFIX% &a%NAME% is now called %NICKNAME%!");
    		save(MESSAGES.getNewCfg(), MESSAGES.getNewFile(), "Messages.Commands.NicknameCommand.AbusivePhrase", "%PREFIX% &cYour nickname contains forbidden phrases! \\n%PREFIX% &cFlagged: &7%PHRASE%");
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Nicknames.Enable", true);
    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Nicknames.CheckForAbusiveWords", true);
    	}
		if(CONFIG.getNewCfg().get("Friends.FriendInventory.CustomItems") == null) {
			if(MESSAGES.getNewCfg().get("Messages.Commands.NicknameCommand.NoFriends") == null) {
				save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.CustomItems.CUSTOM_ITEM_1.Name", "&aCustomItem");
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.CustomItems.CUSTOM_ITEM_1.Material", "stone");
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.CustomItems.CUSTOM_ITEM_1.Lore", "&7This is a custom item, which can be//&7created and edited in the config.yml// //&7If you do not want to use this item//&7just remove it.");
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.CustomItems.CUSTOM_ITEM_1.InventorySlot", "1");
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.CustomItems.CUSTOM_ITEM_1.PerformCommand", "say HyChrod is the best!");
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendEditInventory.CustomItems.NO_ITEMS", new ArrayList<>());
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestsInventory.CustomItems.NO_ITEMS", new ArrayList<>());
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.RequestEditInventory.CustomItems.NO_ITEMS", new ArrayList<>());
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedInventory.CustomItems.NO_ITEMS", new ArrayList<>());
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.BlockedEditInventory.CustomItems.NO_ITEMS", new ArrayList<>());
	    		save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.OptionsInventory.CustomItems.NO_ITEMS", new ArrayList<>());
			} else save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.FriendInventory.CustomItems", "[]");
    	}
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
    	}
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
    	}
		if(CONFIG.getNewCfg().get("Friends.Status.AllowFarbcodes") == null) {
			save(CONFIG.getNewCfg(), CONFIG.getNewFile(), "Friends.Status.Farbcodes", false);
        	if(CONFIG.getNewCfg().get("Friends.CommandAliases") == null) {
        		CONFIG.getNewFile().delete();
        		loadFiles(Friends.getInstance());
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
        saveRessource(plugin, "Party.yml");
        
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
