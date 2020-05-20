package de.HyChrod.Party.Utilities;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Utilities.FileManager;
import net.md_5.bungee.api.ChatColor;

public enum PMessages {
	
	NO_PERMISSIONS("PartyMessages.NoPermissions"),
	VERSION("PartyMessages.Version"),
	NO_PLAYER("PartyMessages.NoPlayer"),
	UNKNOWN_COMMAND("PartyMessages.UnknownCommand"),
	WRONG_USAGE("PartyMessages.WrongUsage"),
	INVALID_PLAYER("PartyMessages.Commands.PlayerInvalid"),
	CMD_INVITE_NOFRIEND("PartyMessages.Commands.InviteCommand.NoFriends"),
	CMD_INVITE_NOINVITES("PartyMessages.Commands.InviteCommand.NoInvites"),
	CMD_INVITE_ALREADY_IN_PARTY("PartyMessages.Commands.InviteCommand.AlreadyInParty"),
	CMD_INVITE_NOT_LEADER("PartyMessages.Commands.InviteCommand.NotLeader"),
	CMD_INVITE_ALREADY_INVITED("PartyMessages.Commands.InviteCommand.AlreadyInvited"),
	CMD_INVITE_CLICK("PartyMessages.Commands.InviteCommand.ClickableMessage.Message"),
	CMD_INVITE_CLICK_BUTTON_ACCEPT_TEXT("PartyMessages.Commands.InviteCommand.ClickableMessage.AcceptButton.Text"),
	CMD_INVITE_CLICK_BUTTON_ACCEPT_HOVER("PartyMessages.Commands.InviteCommand.ClickableMessage.AcceptButton.Hover"),
	CMD_INVITE_CLICK_BUTTON_DENY_TEXT("PartyMessages.Commands.InviteCommand.ClickableMessage.DenyButton.Text"),
	CMD_INVITE_CLICK_BUTTON_DENY_HOVER("PartyMessages.Commands.InviteCommand.ClickableMessage.DenyButton.Hover"),
	CMD_INVITE_OFFLINE("PartyMessages.Commands.InviteCommand.PlayerOffline"),
	CMD_INVITE_SEND("PartyMessages.Commands.InviteCommand.InviteSent"),
	CMD_INVITE_RECEIVED("PartyMessages.Commands.InviteCommand.InviteReceived"),
	CMD_DENY_NO_INVITE("PartyMessages.Commands.DenyCommand.NoInvite"),
	CMD_DENY_DENY("PartyMessages.Commands.DenyCommand.Deny"),
	CMD_DENY_DENIED("PartyMessages.Commands.DenyCommand.Denied"),
	CMD_ACCEPT_NO_INVITE("PartyMessages.Commands.AcceptCommand.NoInvite"),
	CMD_ACCEPT_PARTY_CLOSED("PartyMessages.Commands.AcceptCommand.PartyClosed"),
	CMD_ACCEPT_EXPIRED("PartyMessages.Commands.AcceptCommand.InviteExpired"),
	CMD_ACCEPT_NO_NEW_INVITE("PartyMessages.Commands.AcceptCommand.NoNewInvite"),
	CMD_DENY_NO_NEW_INVITE("PartyMessages.Commands.DenyCommand.NoNewInvite"),
	CMD_ACCEPT_LIMIT_REACHED("PartyMessages.Commands.AcceptCommand.PartyLimitReached"),
	CMD_ACCEPT_PARTY_JOIN("PartyMessages.Commands.AcceptCommand.Join"),
	CMD_ACCEPT_NEW_MEMBER("PartyMessages.Commands.AcceptCommand.NewMember"),
	CMD_LEAVE_NO_PARTY("PartyMessages.Commands.LeaveCommand.NoParty"),
	CMD_LEAVE_LEAVE("PartyMessages.Commands.LeaveCommand.Leave"),
	CMD_LEAVE_MEMBER_LEAVE("PartyMessages.Commands.LeaveCommand.MemberLeave"),
	CMD_PROMOTE_NOPARTY("PartyMessages.Commands.PromoteCommand.NoParty"),
	CMD_PROMOTE_NO_LEADER("PartyMessages.Commands.PromoteCommand.NoLeader"),
	CMD_PROMOTE_PROMOTED("PartyMessages.Commands.PromoteCommand.Promoted"),
	CMD_PROMOTE_NEW_LEADER("PartyMessages.Commands.PromoteCommand.NewLeader"),
	CMD_PROMOTE_ALREADY_LEADER("PartyMessages.Commands.PromoteCommand.AlreadyLeader"),
	CMD_DEMOTE_NO_PARTY("PartyMessages.Commands.DemoteCommand.NoParty"),
	CMD_DEMOTE_NO_LEADER("PartyMessages.Commands.DemoteCommand.NoLeader"),
	CMD_DEMOTE_ALREADY_MEMBER("PartyMessages.Commands.DemoteCommand.AlreadyMember"),
	CMD_DEMOTE_DEMOTED("PartyMessages.Commands.DemoteCommand.Demoted"),
	CMD_DEMOTE_NEW_MEMBER("PartyMessages.Commands.DemoteCommand.NewMember"),
	CMD_PROMOTE_NOT_IN_PARTY("PartyMessages.Commands.PromoteCommand.NotInParty"),
	CMD_DEMOTE_NOT_IN_PARTY("PartyMessages.Commands.DemoteCommand.NotInParty"),
	CMD_LIST_NO_PARTY("PartyMessages.Commands.ListCommand.NoParty"),
	CMD_LIST_LIST("PartyMessages.Commands.ListCommand.List"),
	CMD_JOIN_NO_PARTY("PartyMessages.Commands.JoinCommand.NoParty"),
	CMD_JOIN_PRIVATE("PartyMessages.Commands.JoinCommand.Private"),
	CMD_JOIN_IN_PARTY("PartyMessages.Commands.JoinCommand.InParty"),
	CMD_JOIN_PARTY_LIMIT("PartyMessages.Commands.JoinCommand.PartyLimitReached"),
	CHAT_NO_PARTY("PartyMessages.PartyChat.NoParty"),
	CHAT_MESSAGE("PartyMessages.PartyChat.Message"),
	SWITCH_SERVER_NO_LEADER("PartyMessages.SwitchServer.NoLeader"),
	SWTICH_SERVER_SWITCH("PartyMessages.SwitchServer.JoinServer"),
	CMD_KICK_NO_PARTY("PartyMessages.Commands.KickCommand.NoParty"),
	CMD_KICK_NO_LEADER("PartyMessages.Commands.KickCommand.NoLeader"),
	CMD_KICK_KICK("PartyMessages.Commands.KickCommand.Kick"),
	CMD_KICK_KICKED("PartyMessages.Commands.KickCommand.Kicked"),
	CMD_KICK_NOT_IN_PARTY("PartyMessages.Commands.KickCommand.NotInParty"),
	CMD_CREATE_IN_PARTY("PartyMessages.Commands.CreateCommand.InParty"),
	CMD_CREATE_CREATE("PartyMessages.Commands.CreateCommand.Create");
	
	private String path;
	private String message;
	
	private PMessages(String path) {
		this.path = path;
	}
	
	private void loadMessage(FileConfiguration cfg, String prefix) {
		this.message = ChatColor.translateAlternateColorCodes('&', cfg.getString(path).replace("%PREFIX%", prefix));
	}
	
	public String getMessage(Player player) {
		String msg = this.message;
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && player != null) {
			try {
				msg = (String) Class.forName("me.clip.placeholderapi.PlaceholderAPI").getMethod("setPlaceholders", Player.class, String.class).invoke(null, player, msg);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}
	
	public static void loadAll(String prefix) {
		FileConfiguration cfg = FileManager.getConfig("","Messages.yml");
		for(PMessages msg : PMessages.values()) {
			System.out.println(msg.name());
			msg.loadMessage(cfg, prefix);
		}
	}

}
