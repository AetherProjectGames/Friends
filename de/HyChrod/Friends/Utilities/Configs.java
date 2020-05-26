package de.HyChrod.Friends.Utilities;

import java.util.LinkedList;

import org.bukkit.configuration.file.FileConfiguration;

public enum Configs {
	
	ADDMESSAGE_ENABLE("Friends.Commands.AddMessage.Enable"),
	ADDMESSAGE_CHARLIMIT("Friends.Commands.AddMessage.CharacterLimit"),
	FRIEND_LIMIT("Friends.FriendLimit"),
	FRIEND_LIMIT_EXT("Friends.FriendLimitExtended"),
	BLOCKCOMMENT_ENABLE("Friends.Commands.BlockNote.Enable"),
	BLOCKCOMMENT_CHARLIMIT("Friends.Commands.BlockNote.CharacterLimit"),
	STATUS_FILTER("Friends.Status.EnableFilter"),
	STATUS_LENGHT("Friends.Status.MaxLenght"),
	STATUS_CHANGEDURATION("Friends.Status.ChangeDuration"),
	FRIENDCHAT_ENABLE("Friends.FriendChat.Enable"),
	FRIENDCHAT_FLAG("Friends.FriendChat.CheckForAbusiveWords"),
	FRIENDCHAT_FORMAT("Friends.FriendChat.Format"),
	INV_FRIENDS_FRIENDS_CHANGESKULL("Friends.FriendInventory.FriendsItem.ChangeSkullWhenOffline"),
	INV_REQUEST_NO_MSG_REPLACEMENT("Friends.RequestsInventory.RequestItem.NoMessageReplacement"),
	INV_BLOCKED_NO_NOTE_REPLACEMENT("Friends.BlockedInventory.BlockedItem.NoNoteReplacement"),
	INV_FRIENDS_NO_STATUS_REPLACEMENT("Friends.FriendInventory.FriendsItem.NoStatusReplacement"),
	INV_RQ_EDIT_NO_MSG_REPLACEMENT("Friends.RequestEditInventory.MessageItem.NoMessageReplacement"),
	INV_BL_EDIT_NO_NOTE_REPLACEMENT("Friends.BlockedEditInventory.NoteItem.NoNoteReplacement"),
	ITEM_REQUEST_PLAYER_LORE("Friends.RequestsInventory.RequestItem.Lore"),
	ITEM_BLOCKED_PLAYER_LORE("Friends.BlockedInventory.BlockedItem.Lore"),
	ITEM_REQUEST_PLAYER_NAME("Friends.RequestsInventory.RequestItem.Name"),
	ITEM_BLOCKED_PLAYER_NAME("Friends.BlockedInventory.BlockedItem.Name"),
	DATE_FORMAT("Friends.DateFormat"),
	LASTONLINE_DATE_FORMAT("Friends.FriendInventory.FriendsItem.LastOnlineFormat"),
	ITEM_FRIENDS_ONLINE_STAT_ON("Friends.FriendInventory.FriendsItem.OnlineStatus"),
	ITEM_FRIENDS_ONLINE_STAT_OFF("Friends.FriendInventory.FriendsItem.OfflineStatus"),
	ITEM_FRIENDS_LORE_ONLINE("Friends.FriendInventory.FriendsItem.LoreOnline"),
	ITEM_FRIENDS_LORE_OFFLINE("Friends.FriendInventory.FriendsItem.LoreOffline"),
	ITEM_FRIENDS_NAME("Friends.FriendInventory.FriendsItem.Name"),
	ITEM_FRIEND_FAV_STATUS_ON("Friends.FriendEditInventory.FavoriteItem.FavoriteStatusOn"),
	ITEM_FRIEND_FAV_STATUS_OFF("Friends.FriendEditInventory.FavoriteItem.FavoriteStatusOff"),
	ITEM_FRIEND_SENDMSG_STATUS_ON("Friends.FriendEditInventory.CanSendMessagesItem.SendMessagesStatusOn"),
	ITEM_FRIEND_SENDMSG_STATUS_OFF("Friends.FriendEditInventory.CanSendMessagesItem.SendMessagesStatusOff"),
	ITEM_FRIEND_NO_NICK_REPLACEMENT("Friends.FriendEditInventory.NicknameItem.NoNicknameReplacement"),
	OPTIONS_ON("Friends.OptionsInventory.OptionStatusOn"),
	OPTIONS_OFF("Friends.OptionsInventory.OptionStatusOff"),
	OPTIONS_OFFLINEMODE_SHOW("Friends.OptionsInventory.OfflinemodeItem.ShowItem"),
	OPTIONS_REQUESTS_SHOW("Friends.OptionsInventory.ReceiveRequestsItem.ShowItem"),
	OPTIONS_MESSAGES_SHOW("Friends.OptionsInventory.ReceiveMessagesItem.ShowItem"),
	OPTIONS_STATUS_SHOW("Friends.OptionsInventory.StatusItem.ShowItem"),
	OPTIONS_MESSAGES_ONLY_FAV_STATUS("Friends.OptionsInventory.ReceiveMessagesItem.OnlyFavoritesStatus"),
	OPTIONS_JUMP_SHOW("Friends.OptionsInventory.JumpItem.ShowItem"),
	OPTIONS_PARTY_SHOW("Friends.OptionsInventory.PartyItem.ShowItem"),
	FRIEND_MSG_ENABLE("Friends.FriendMSG.Enable"),
	FIENED_MSG_FLAG("Friends.FriendMSG.CheckForAbusiveWords"),
	SORTING_ONOFF("Friends.FriendInventory.SortItem.OnOffSorting"),
	SORTING_FAVORITE("Friends.FriendInventory.SortItem.FavoriteSorting"),
	SORTING_ALPHABETIC("Friends.FriendInventory.SortItem.Alphabetic"),
	SORTING_LONGFRIEND("Friends.FriendInventory.SortItem.LongFriendSorting"),
	FRIEND_ITEM_ENABLE("Friends.FriendItem.Enable"),
	CHECK_FOR_UPDATES("Friends.CheckForUpdates"),
	BUNGEEMODE("Friends.BungeeMode"),
	GUI_WITH_COMMAND("Friends.OpenGUIWithCommand"),
	MSG_COMMAND("Friends.FriendMSG.UseMSGCommand"),
	DELAYED_INV_SET("Friends.FriendItem.DelayedInventorySet"),
	INV_PARTY_ENABLE("Friends.FriendInventory.PartyItem.ShowItem"),
	ALLOW_STATUS_COLOR("Friends.Status.AllowFarbcodes"),
	INV_JUMPING_ENABLE("Friends.FriendEditInventory.JumpItem.ShowItem"),
	INV_FRIEND_SORT_ENABLE("Friends.FriendInventory.SortItem.ShowItem"),
	INV_FRIEND_NEXTPAGE_ENABLE("Friends.FriendInventory.NextPageItem.ShowItem"),
	INV_FRIEND_PREVIOUSPAGE_ENABLE("Friends.FriendInventory.PreviousPageItem.ShowItem"),
	INV_FRIENDEDIT_CSM_ENABLE("Friends.FriendEditInventory.CanSendMessagesItem.ShowItem"),
	INV_FRIENDEDIT_FAVORITE_ENABLE("Friends.FriendEditInventory.FavoriteItem.ShowItem"),
	INV_FRIENDEDIT_NICK_ENABLE("Friends.FriendEditInventory.NicknameItem.ShowItem"),
	INV_REQUEST_NEXTPAGE_ENABLE("Friends.RequestsInventory.NextPageItem.ShowItem"),
	INV_REQUEST_PREVIOUSPAGE_ENABLE("Friends.RequestsInventory.PreviousPageItem.ShowItem"),
	INV_REQUEST_ACCEPTALL_ENABLE("Friends.RequestsInventory.AcceptAllItem.ShowItem"),
	INV_REQUEST_DENYALL_ENABLE("Friends.RequestsInventory.DenyAllItem.ShowItem"),
	INV_REQUESTEDIT_MESSAGE_ENABLE("Friends.RequestEditInventory.MessageItem.ShowItem"),
	INV_BLOCKED_UNBLOCKALL_ENABLE("Friends.BlockedInventory.UnblockAllItem.ShowItem"),
	INV_BLOCKED_NEXTPAGE_ENABLE("Friends.BlockedInventory.NextPageItem.ShowItem"),
	INV_BLOCKED_PREVIOUSPAGE_ENABLE("Friends.BlockedInventory.PreviousPageItem.ShowItem"),
	INV_BLOCKEDIT_NOTE_ENABLE("Friends.BlockedEditInventory.NoteItem.ShowItem"),
	INV_FRIENDEDIT_PARTY_ENABLE("Friends.FriendEditInventory.PartyItem.ShowItem"),
	CLICKABLE_MESSAGES("Friends.Commands.EnableClickableMessages"),
	INV_FRIEND_HIDEPAGES("Friends.FriendInventory.HidePageItemsWhenNotNeeded"),
	INV_REQUEST_HIDEPAGES("Friends.RequestsInventory.HidePageItemsWhenNotNeeded"),
	INV_BLOCKED_HIDEPAGES("Friends.BlockedInventory.HidePageItemsWhenNotNeeded"),
	NICK_ENABLE("Friends.Nicknames.Enable"),
	NICK_CHECK_FOR_ABUSIVE_WORDS("Friends.Nicknames.CheckForAbusiveWords"),
	INV_FRIEND_REQUESTS_ENABLE("Friends.FriendInventory.RequestsItem.ShowItem"),
	INV_FRIEND_BLOCKED_ENABLE("Friends.FriendInventory.BlockedItem.ShowItem"),
	INV_FRIEND_OPTIONS_ENABLE("Friends.FriendInventory.OptionsItem.ShowItem");
	
	private Object value;
	private String path;
	
	private Configs(String path) {
		this.path = path;
	}
	
	private void load() {
		this.value = FileManager.CONFIG.getConfig().get(path);
	}
	
	public boolean getBoolean() {
		return (boolean) value;
	}
	
	public int getNumber() {
		return (int) value;
	}
	
	public String getText() {
		String text = (String) value;
		return text;
	}
	
	private static LinkedList<String> forbidden_phrases = new LinkedList<String>();
	
	public static LinkedList<String> getForbiddenPhrases() {
		return forbidden_phrases;
	}
	
	public static void loadConfigs() {
		forbidden_phrases.clear();
		FileConfiguration cfg = FileManager.getConfig("","forbidden_phrases.txt");
		for(String phrase : cfg.getStringList("phrases"))
			forbidden_phrases.add(phrase);
		
		for(Configs cf : Configs.values())
			cf.load();
	}

}
