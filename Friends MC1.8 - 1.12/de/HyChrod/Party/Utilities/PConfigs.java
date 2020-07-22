package de.HyChrod.Party.Utilities;

import java.util.LinkedList;

import org.bukkit.ChatColor;

import de.HyChrod.Friends.Utilities.FileManager;

public enum PConfigs {
	
	PARTY_ENABLE("Party.Enable"),
	PARTY_CLICKABLE_MESSAGES("Party.ClickableMessages"),
	PARTY_INVITE_ONLY_FRIENDS("Party.InviteFriendsOnly"),
	PARTY_MAX_SIZE("Party.MaxPartySize"),
	PARTY_INVITE_EXPIRE_TIME("Party.InviteExpireTime"),
	PARTY_CHAT_ENABLE("Party.PartyChat.Enable"),
	PARTY_CHAT_FLAG_FOR_ABUSIVE_FORS("Party.PartyChat.CheckForAbusiveWords"),
	PARTY_CHAT_FORMAT("Party.PartyChat.Format"),
	MEMBER_NAME("Party.PartyInventory.MemberItem.Name"),
	MEMBER_LORE("Party.PartyInventory.MemberItem.Lore"),
	LEADER_NAME("Party.PartyInventory.LeaderItem.Name"),
	LEADER_LORE("Party.PartyInventory.LeaderItem.Lore"),
	PARTY_VISIBILITY_PRIVATE("Party.PartyInventory.VisibilityItem.StatusPrivate"),
	PARTY_VISIBILITY_PUBLIC("Party.PartyInventory.VisibilityItem.StatusPublic"),
	PARTY_LORE_PUBLIC("Party.PartyInventory.VisibilityItem.LorePublic"),
	PARTY_LORE_PRIVATE("Party.PartyInventory.VisibilityItem.LorePrivate"),
	PARTY_LORE_FRIENDS("Party.PartyInventory.VisibilityItem.LoreFriends");
	
	private Object value;
	private String path;
	
	private PConfigs(String path) {
		this.path = path;
	}
	
	private void load() {
		this.value = FileManager.PARTY.getConfig().get(path);
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
	
	public String getColoredText() {
		String text = (String) value;
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	private static LinkedList<String> forbidden_worlds = new LinkedList<String>();
	
	public static LinkedList<String> getForbiddenWorlds() {
		return forbidden_worlds;
	}
	
	public static void loadConfigs() {
		forbidden_worlds.clear();
		if(FileManager.PARTY.getNewCfg().getString("Party.DisabledWorlds") != null)
			for(String forb : FileManager.PARTY.getNewCfg().getStringList("Party.DisabledWorlds"))
				forbidden_worlds.add(forb);
		
		for(PConfigs cf : PConfigs.values()) {
			cf.load();
		}
	}

}
