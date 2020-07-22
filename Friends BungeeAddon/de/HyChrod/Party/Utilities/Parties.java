package de.HyChrod.Party.Utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Parties {
	
	private LinkedList<UUID> leader = new LinkedList<>();
	private LinkedList<UUID> participants = new LinkedList<>();
	
	private boolean publicParty = false;
	private String info;
	
	private int id = -1;
	
	public Parties(UUID leader) {
		this.leader.add(leader);
		this.info = BungeeCord.getInstance().getPlayer(leader).getServer().getInfo().getName();
		BungeeCord.getInstance().getPlayer(leader).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_CREATE_PARTY.getMessage()));
		partyByPlayer.put(leader, this);
		
		id = new Random().nextInt(Integer.MAX_VALUE);
		
		AsyncSQLQueueUpdater.addToQueue("insert into party_players(uuid,id) values ('" + leader.toString() + "','" + id + "') on duplicate key update id=values(id);");
		AsyncSQLQueueUpdater.addToQueue("insert into party(id,prvt,server) values ('" + id + "', '" + 1 + "','" + BungeeCord.getInstance().getPlayer(leader).getServer().getInfo().getName() + "');");
		AsyncSQLQueueUpdater.addToQueue("insert into party_leaders(id,uuid) values ('" + id + "','" + leader.toString() + "') on duplicate key update id=values(id);");
	}
	
	public Parties() {}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public LinkedList<UUID> getParticipants() {
		return participants;
	}
	
	public boolean isPublic() {
		return publicParty;
	}
	
	public void setInfo(String info) {
		this.info = info;
		setServer(getID(), info);
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setPublic(boolean bool) {
		this.publicParty = bool;
	}
	
	public int getSize() {
		return leader.size() + participants.size();
	}
	
	public LinkedList<UUID> getMembers() {
		LinkedList<UUID> members = new LinkedList<UUID>(getParticipants());
		members.addAll(getLeader());
		return members;
	}
	
	public LinkedList<UUID> getLeader() {
		return leader;
	}
	
	public boolean isParticipant(UUID uuid) {
		return participants.contains(uuid);
	}
	
	public boolean isLeader(UUID uuid) {
		return leader.contains(uuid);
	}
	
	public void addParticipant(UUID uuid) {
		if(!this.participants.contains(uuid)) this.participants.add(uuid);
		updatePlayerParty(uuid, this);
	}
	
	public void removeParticipant(UUID uuid) {
		if(this.participants.contains(uuid)) this.participants.remove(uuid);
		partyByPlayer.remove(uuid);
	}
	
	public void removeLeader(UUID uuid) {
		if(this.leader.contains(uuid)) this.leader.remove(uuid);
		partyByPlayer.remove(uuid);
	}
	
	public void makeLeader(UUID uuid) {
		if(!this.leader.contains(uuid)) this.leader.add(uuid);
		updatePlayerParty(uuid, this);
	}
	
	private static HashMap<UUID, Parties> partyByPlayer = new HashMap<UUID, Parties>();
	private static HashMap<UUID, LinkedList<Invite>> pendingInvites = new HashMap<UUID, LinkedList<Invite>>();
	
	public static Parties getParty(UUID uuid) {
		return Friends.getSMgr().getParty(uuid);
	}
	
	private static void updatePlayerParty(UUID uuid, Parties party) {
		partyByPlayer.put(uuid, party);
	}
	
	public static void invitePlayer(UUID toInvite, UUID sender, Parties party) {
		LinkedList<Invite> invites = pendingInvites.containsKey(toInvite) ? pendingInvites.get(toInvite) : new LinkedList<Invite>();
		invites.add(new Invite(sender, toInvite, party));
		pendingInvites.put(toInvite, invites);
		if(BungeeCord.getInstance().getPlayer(toInvite) == null) return;
		String nameSender = FriendHash.getName(sender);
		ProxiedPlayer invited = BungeeCord.getInstance().getPlayer(toInvite);
		invited.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_RECEIVED.getMessage().replace("%NAME%", nameSender)));
		if(!Configs.PARTY_CLICKABLE_MESSAGES.getBoolean()) return;
		
		TextComponent accept = new TextComponent(PMessages.CMD_INVITE_CLICK_BUTTON_ACCEPT_TEXT.getMessage());
		accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/party accept " + nameSender));
		accept.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PMessages.CMD_INVITE_CLICK_BUTTON_ACCEPT_HOVER.getMessage().replace("%NAME%", nameSender)).create()));
		
		TextComponent deny = new TextComponent(PMessages.CMD_INVITE_CLICK_BUTTON_DENY_TEXT.getMessage());
		deny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/party deny " + nameSender));
		deny.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PMessages.CMD_INVITE_CLICK_BUTTON_DENY_HOVER.getMessage().replace("%NAME%", nameSender)).create()));
		
		String clickable = PMessages.CMD_INVITE_CLICK.getMessage().replace("%NAME%", nameSender);
		TextComponent toSendClick = new TextComponent("");
		String[] accept_replaced = clickable.split("%ACCEPT_BUTTON%");
		
		for(int i = 0; i < accept_replaced.length; i++) {
			String v = accept_replaced[i];
			if(v.contains("%DENY_BUTTON%")) {
				String[] split = v.split("%DENY_BUTTON%");
				for(int b = 0; b < split.length; b++) {
					String x = split[b];
					if(x.length() > 0) toSendClick.addExtra(x);
					if(b < (split.length-1)) toSendClick.addExtra(deny);
				}
				continue;
			}
			if(v.length() > 0) toSendClick.addExtra(v);
			if(i <( accept_replaced.length-1)) toSendClick.addExtra(accept);
		}
		invited.sendMessage(toSendClick);
	}
	
	public static void removeInvite(UUID uuid, UUID toRemove) {
		for(Invite inv : getInvites(uuid))
			if(inv.getSender().equals(toRemove)) pendingInvites.get(uuid).remove(inv);
	}
	
	private static HashMap<Integer, String> partyServer = new HashMap<Integer, String>();
	
	public static void setServer(int id, String server) {
		partyServer.put(id, server);
	}
	
	public static String getServer(int id) {
		return partyServer.containsKey(id) ? partyServer.get(id) : "null";
	}
	
	public static boolean hasInvite(UUID uuid, UUID uuid2) {
		if(pendingInvites.containsKey(uuid))
			for(Invite inv : pendingInvites.get(uuid))
				if(inv.getSender().equals(uuid2) && (inv.getTimestamp() + (Configs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000) > System.currentTimeMillis())) return true;
		return false;
	}
	
	public static LinkedList<Invite> getInvites(UUID uuid) {
		return pendingInvites.containsKey(uuid) ? pendingInvites.get(uuid) : new LinkedList<Invite>();
	}
	
	public static boolean hasInvite(UUID uuid, Parties party) {
		if(pendingInvites.containsKey(uuid))
			for(Invite inv : pendingInvites.get(uuid))
				if(inv.getParty().getID() == party.getID()) return true;
		return false;
	}
	
}
