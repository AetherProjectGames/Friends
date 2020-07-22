package de.HyChrod.Friends.Utilities;

import java.util.LinkedList;

import net.md_5.bungee.config.Configuration;

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
	FRIEND_MSG_ENABLE("Friends.FriendMSG.Enable"),
	FIENED_MSG_FLAG("Friends.FriendMSG.CheckForAbusiveWords"),
	CHECK_FOR_UPDATES("Friends.CheckForUpdates"),
	MSG_COMMAND("Friends.FriendMSG.UseMSGCommand"),
	CLICKABLE_MESSAGES("Friends.Commands.EnableClickableMessages"),
	NICK_ENABLE("Friends.Nicknames.Enable"),
	NICK_CHECK_FOR_ABUSIVE_WORDS("Friends.Nicknames.CheckForAbusiveWords"),
	PARTY_ENABLE("Party.Enable"),
	PARTY_CLICKABLE_MESSAGES("Party.ClickableMessages"),
	PARTY_INVITE_ONLY_FRIENDS("Party.InviteFriendsOnly"),
	PARTY_MAX_SIZE("Party.MaxPartySize"),
	PARTY_INVITE_EXPIRE_TIME("Party.InviteExpireTime"),
	PARTY_CHAT_ENABLE("Party.PartyChat.Enable"),
	PARTY_CHAT_FLAG_FOR_ABUSIVE_FORS("Party.PartyChat.CheckForAbusiveWords"),
	PARTY_CHAT_FORMAT("Party.PartyChat.Format"),
	JUMPING_ENABLE("Friends.Commands.Jumping.Enable");
	
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
		return (String) value;
	}
	
	private static LinkedList<String> forbidden_phrases = new LinkedList<String>();
	private static LinkedList<String> forbidden_servers = new LinkedList<String>();
	private static LinkedList<String> forbidden_party_servers = new LinkedList<String>();
	
	public static LinkedList<String> getForbiddenPhrases() {
		return forbidden_phrases;
	}
	
	public static LinkedList<String> getForbiddenServers() {
		return forbidden_servers;
	}
	
	public static LinkedList<String> getForbiddenPartyServers() {
		return forbidden_party_servers;
	}
	
	public static void loadConfigs() {
		forbidden_phrases.clear();
		Configuration cfg = FileManager.getConfig("","forbidden_phrases.txt");
		for(String phrase : cfg.getStringList("phrases"))
			forbidden_phrases.add(phrase);
		
		forbidden_servers.clear();
		if(FileManager.CONFIG.getConfig().getString("Friends.DisabledServers") != null)
			for(String sev : FileManager.CONFIG.getConfig().getStringList("Friends.DisabledServers"))
				forbidden_servers.add(sev);
		
		forbidden_party_servers.clear();
		if(FileManager.CONFIG.getConfig().getString("Party.DisabledServers") != null)
			for(String sev : FileManager.CONFIG.getConfig().getStringList("Party.DisabledServers"))
				forbidden_party_servers.add(sev);
		
		for(Configs cf : Configs.values())
			cf.load();
	}

}
