package de.HyChrod.Friends.Utilities;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public enum Messages {
	
	NO_PERMISSIONS("Messages.NoPermissions"),
	NO_PLAYER("Messages.NoPlayer"),
	PLAYER_DOES_NOT_EXIST("Messages.PlayerDoesNotExist"),
	QUIT_MESSAGE("Messages.QuitMessage"),
	JOIN_MESSAGE("Messages.JoinMessage"),
	CMD_HELP_UNKNOWNPAGE("Messages.Commands.HelpCommand.UnknownPage"),
	CMD_VERSION("Messages.Commands.Version"),
	CMD_RELOAD("Messages.Commands.Reload"),
	CMD_WRONG_USAGE("Messages.Commands.WrongUsage"),
	CMD_UNKNOWN_COMMAND("Messages.Commands.UnknownCommand"),
	CMD_ADD_MESSAGE_CHAR_LIMIT("Messages.Commands.AddCommand.MessageCharLimit"),
	CMD_ADD_SEND_SELF("Messages.Commands.AddCommand.SendSelf"),
	CMD_ADD_ALREADY_FRIENDS("Messages.Commands.AddCommand.AlreadyFriends"),
	CMD_ADD_ALREADY_REQUESTED("Messages.Commands.AddCommand.AlreadyRequested"),
	CMD_ADD_REQUEST_SEND("Messages.Commands.AddCommand.RequestSend"),
	CMD_ADD_REQUEST_MESSAGE_SEND("Messages.Commands.AddCommand.RequestMessageSend"),
	CMD_ADD_RECEIVE_REQUEST("Messages.Commands.AddCommand.ReceiveRequest"),
	CMD_ADD_RECEIVE_REQUEST_MESSAGE("Messages.Commands.AddCommand.ReceiveRequestMessage"),
	CMD_ADD_BLOCKED("Messages.Commands.AddCommand.Blocked"),
	CMD_ADD_SELF_BLOCKED("Messages.Commands.AddCommand.SelfBlocked"),
	CMD_ACCEPT_NO_REQUEST("Messages.Commands.AcceptCommand.NoRequest"),
	CMD_ACCEPT_NO_NEW_REQUEST("Messages.Commands.AcceptCommand.NoNewRequest"),
	CMD_ACCEPT_LIMIT_REACHED("Messages.Commands.AcceptCommand.LimitReached"),
	CMD_ACCEPT_NEW_FRIEND("Messages.Commands.AcceptCommand.NewFriend"),
	CMD_DENY_NO_NEW_REQUEST("Messages.Commands.AcceptCommand.NoNewRequest"),
	CMD_DENY_DENY_REQUEST("Messages.Commands.DenyCommand.DenyRequest"),
	CMD_DENY_DENIED_REQUEST("Messages.Commands.DenyCommand.DeniedRequest"),
	CMD_REMOVE_NO_FRIENDS("Messages.Commands.RemoveCommand.NoFriends"),
	CMD_REMOVE_FRIEND_REMOVED("Messages.Commands.RemoveCommand.FriendRemoved"),
	CMD_BLOCK_BLOCK_SELF("Messages.Commands.BlockCommand.BlockSelf"),
	CMD_BLOCK_NOTE_LIMIT("Messages.Commands.BlockCommand.NoteLimit"),
	CMD_BLOCK_BLOCK_PLAYER("Messages.Commands.BlockCommand.BlockPlayer"),
	CMD_BLOCK_BLOCK_NOTE("Messages.Commands.BlockCommand.BlockNote"),
	CMD_BLOCK_ALREADY_BLOCKED("Messages.Commands.BlockCommand.AlreadyBlocked"),
	CMD_UNBLOCK_PLAYER_NOT_BLOCKED("Messages.Commands.UnblockCommand.PlayerNotBlocked"),
	CMD_UNBLOCK_UNBLOCKED("Messages.Commands.UnblockCommand.Unblocked"),
	CMD_STATUS_STATUS_LENGHT("Messages.Commands.StatusCommand.StatusLenght"),
	CMD_STATUS_ABUSIVE_PHRASE("Messages.Commands.StatusCommand.AbusivePhrase"),
	CMD_STATUS_STATUS_SET("Messages.Commands.StatusCommand.StatusSet"),
	CMD_STATUS_CANT_CHANGE_YET("Messages.Commands.StatusCommand.CantChangeYet"),
	CMD_STATUS_NO_STATUS("Messages.Commands.StatusCommand.NoStatus"),
	CMD_STATUS_CURRENT_STATUS("Messages.Commands.StatusCommand.CurrentStatus"),
	CMD_STATUS_FRIENDCHECK_NO_FRIENDS("Messages.Commands.StatusCommand.FriendCheck.NoFriends"),
	CMD_STATUS_FRIENDCHECK_NO_STATUS("Messages.Commands.StatusCommand.FriendCheck.NoStatus"),
	CMD_STATUS_FRIENDCHECK_SHOW_STATUS("Messages.Commands.StatusCommand.FriendCheck.ShowStatus"),
	CMD_LIST_NO_FRIENDS("Messages.Commands.ListCommand.NoFriends"),
	CMD_LIST_LIST("Messages.Commands.ListCommand.List"),
	CMD_ACCEPTALL("Messages.Commands.AcceptAllCommand"),
	CMD_DENYALL("Messages.Commands.DenyAllCommand"),
	FRIENDCHAT_FORMAT("Messages.FriendChat.Format"),
	FRIENDCHAT_ABUSIVE_PHRASE("Messages.FriendChat.AbusivePhrase"),
	CMD_UNBLOCKALL_NOPLAYER("Messages.Commands.UnblockAllCommand.NoPlayerBlocked"),
	CMD_UNBLOCKALL_UNBLOCK("Messages.Commands.UnblockAllCommand.Unblock"),
	CMD_ADD_NO_REQUEST_WANTED("Messages.Commands.AddCommand.NoRequestsWanted"),
	FRIENDCHAT_DISABLED("Messages.FriendChat.DisabledOption"),
	CMD_MSG_OFFLINE("Messages.Commands.MSGCommand.PlayerOffline"),
	CMD_MSG_NOFRIENDS("Messages.Commands.MSGCommand.NoFriends"),
	CMD_MSG_MSG_DISABLED("Messages.Commands.MSGCommand.MessagesDisabled"),
	CMD_MSG_NOMSG("Messages.Commands.MSGCommand.NoMSGWanted"),
	CMD_MSG_MSG("Messages.Commands.MSGCommand.MSG"),
	CMD_MSG_ABUSIVE_PHRASE("Messages.Commands.MSGCommand.AbusivePhrase"),
	CMD_JUMP_NOFRIENDS("Messages.Commands.JumpCommand.NoFriends"),
	CMD_JUMP_OFFLINE("Messages.Commands.JumpCommand.PlayerOffline"),
	CMD_JUMP_DISABLED("Messages.Commands.JumpCommand.JumpingDisabled"),
	CMD_JUMP_JUMPTOFRIEND("Messages.Commands.JumpCommand.JumpToFriend"),
	CMD_JUMP_JUMPTOYOU("Messages.Commands.JumpCommand.JumpedToYou"),
	CMD_OPT_JUMP_ENABLE("Messages.Commands.OptionsJumpingCommand.Enable"),
	CMD_OPT_JUMP_DISABLE("Messages.Commands.OptionsJumpingCommand.Disable"),
	CMD_OPT_OFFLINE_ENABLE("Messages.Commands.OptionsOfflinemodeCommand.Enable"),
	CMD_OPT_OFFLINE_DISABLE("Messages.Commands.OptionsOfflinemodeCommand.Disable"),
	CMD_OPT_REQUEST_ENABLE("Messages.Commands.OptionsRequestsCommand.Enable"),
	CMD_OPT_REQUEST_DISABLE("Messages.Commands.OptionsRequestsCommand.Disable"),
	CMD_OPT_MESSAGES_ENABLE("Messages.Commands.OptionsMessagesCommand.Enable"),
	CMD_OPT_MESSAGES_DISABLE("Messages.Commands.OptionsMessagesCommand.Disable"),
	CMD_OPT_MESSAGES_FAVORITES("Messages.Commands.OptionsMessagesCommand.OnlyFavorites"),
	CMD_ADD_CLICK("Messages.Commands.AddCommand.ClickableMessage.Message"),
	CMD_ADD_CLICK_BUTTON_ACCEPT_TEXT("Messages.Commands.AddCommand.ClickableMessage.AcceptButton.Text"),
	CMD_ADD_CLICK_BUTTON_ACCEPT_HOVER("Messages.Commands.AddCommand.ClickableMessage.AcceptButton.Hover"),
	CMD_ADD_CLICK_BUTTON_DENY_TEXT("Messages.Commands.AddCommand.ClickableMessage.DenyButton.Text"),
	CMD_ADD_CLICK_BUTTON_DENY_HOVER("Messages.Commands.AddCommand.ClickableMessage.DenyButton.Hover"),
	CMD_DENY_NO_REQUEST("Messages.Commands.DenyCommand.NoRequest"),
	CMD_NICKNAME_NOFRIENDS("Messages.Commands.NicknameCommand.NoFriends"),
	CMD_NICKNAME_SAME_NICK("Messages.Commands.NicknameCommand.SameNickname"),
	CMD_NICKNAME_SET_NICK("Messages.Commands.NicknameCommand.SetNick"),
	CMD_NICKNAME_ABUSIVE_PHRASE("Messages.Commands.NicknameCommand.AbusivePhrase"),
	CMD_REPLY_NO_REPLY("Messages.Commands.ReplyCommand.NoReply");
	
	private String path;
	private String message;
	
	private Messages(String path) {
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
		for(Messages msg : Messages.values())
			msg.loadMessage(cfg, prefix);
	}

}
