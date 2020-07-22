package de.HyChrod.Friends.Hashing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FriendHash {
	
	private static HashMap<UUID, FriendHash> hashes = new HashMap<>();
	private static HashMap<String, UUID> uuidByNames = new HashMap<String, UUID>();
	
	private UUID uuid;
	private String name;
	private Options options;
	private int friendInvSorting = 0;
	
	private LinkedList<Friendship> friends = new LinkedList<>();
	private LinkedList<Request> requests = new LinkedList<>();
	private LinkedList<Blockplayer> blocked = new LinkedList<>();
	
	public FriendHash(UUID uuid) {
		this.uuid = uuid;
		this.name = BungeeCord.getInstance().getPlayer(uuid) != null ? BungeeCord.getInstance().getPlayer(uuid).getName() : null;
		this.options = new Options(uuid, false, true, 1, "", 0, true, true);
		hashes.put(uuid, this);
		AsyncSQLQueueUpdater.addToQueue("insert into friends_playerdata(uuid,name) values ('" + uuid.toString() + "','" + name + "') on duplicate key update uuid=values(uuid), name=values(name)");
	}
	
	public void blockPlayer(UUID toBlock, String message) {
		removeRequest(toBlock);
		addBlocked(new Blockplayer(uuid, toBlock, System.currentTimeMillis(), message));
		if(FriendHash.isHashLoaded(toBlock)) {
			FriendHash bHash = FriendHash.getFriendHash(toBlock);
			bHash.removeRequest(uuid);
			if(isFriend(toBlock)) {
				removeFriend(toBlock);
				bHash.removeFriend(uuid);
				bHash.removeRequest(uuid);
				if(BungeeCord.getInstance().getPlayer(toBlock) != null) BungeeCord.getInstance().getPlayer(toBlock).sendMessage(TextComponent.fromLegacyText(Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage().replace("%NAME%", name)));
			}
			return;
		}
		removeFriend(toBlock);	
		AsyncSQLQueueUpdater.addToQueue("delete from friends_requests where uuid = '" + toBlock.toString() + "' and uuid2 = '" + uuid.toString() + "';");
		AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid = '" + toBlock.toString() + "' and uuid2 = '" + uuid.toString() + "'");
		return;
	}
	
	public void sendRequest(UUID toAdd, String message) {
		if(FriendHash.isHashLoaded(toAdd)) {
			if(BungeeCord.getInstance().getPlayer(toAdd) != null) {
				ProxiedPlayer p = BungeeCord.getInstance().getPlayer(toAdd);
				p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ADD_RECEIVE_REQUEST.getMessage().replace("%NAME%", name)));
				
				TextComponent accept = new TextComponent(Messages.CMD_ADD_CLICK_BUTTON_ACCEPT_TEXT.getMessage());
				accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friends accept " + name));	
				accept.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.CMD_ADD_CLICK_BUTTON_ACCEPT_HOVER.getMessage().replace("%NAME%", name)).create()));
				
				TextComponent deny = new TextComponent(Messages.CMD_ADD_CLICK_BUTTON_DENY_TEXT.getMessage());
				deny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friends deny " + name));
				deny.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.CMD_ADD_CLICK_BUTTON_DENY_HOVER.getMessage().replace("%NAME%", name)).create()));
				
				String clickable = Messages.CMD_ADD_CLICK.getMessage().replace("%NAME%", name);
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
				if(Configs.CLICKABLE_MESSAGES.getBoolean()) p.sendMessage(toSendClick);
				
				if(Configs.ADDMESSAGE_ENABLE.getBoolean() && message.length() > 0) p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_ADD_RECEIVE_REQUEST_MESSAGE.getMessage()
						.replace("%MESSAGE%", message)));
			}
			FriendHash.getFriendHash(toAdd).addRequest(new Request(toAdd, uuid, message, System.currentTimeMillis()));
			return;
		}
		AsyncSQLQueueUpdater.addToQueue("insert into friends_requests(uuid,uuid2,message,timestamp) values ('" + toAdd.toString() + "','" + uuid.toString() + "','" + message + "','" + System.currentTimeMillis() + "');");
		return;
	}
	
	public void addBlocked(Blockplayer bl) {
		blocked.add(bl);
		AsyncSQLQueueUpdater.addToQueue("insert into friends_blocked(uuid,uuid2,timestamp,message) values ('" + uuid.toString() + "','" + bl.getBlocked().toString() + "','" + bl.getTimestamp() + "','" + bl.getMessage() + "');");
	}
	
	public void removeBlocked(UUID toRemove) {
		for(Blockplayer bl : getBlocked())
			if(bl.getBlocked().equals(toRemove)) {
				blocked.remove(bl);
				AsyncSQLQueueUpdater.addToQueue("delete from friends_blocked where uuid= '" + uuid.toString() + "' and uuid2 = '" + toRemove.toString() + "'");
			}
	}
	
	public LinkedList<Blockplayer> getBlocked() {
		return Friends.getSMgr().getBlocked(uuid);
	}
	
	public void setStatus(String status) {
		this.options.setStatus(status);
		AsyncSQLQueueUpdater.addToQueue("update friends_options set status='" + status + "' where uuid= '" + uuid.toString() + "'");
	}
	
	public void removeRequest(UUID toRemove) {
		for(Request rq : getRequests())
			if(rq.getPlayerToAdd().equals(toRemove)) {
				requests.remove(rq);
				AsyncSQLQueueUpdater.addToQueue("delete from friends_requests where uuid='" + uuid.toString() + "' and uuid2 = '" + toRemove.toString() + "'");
			}
	}
	
	public void removeFriend(UUID toRemove) {
		for(Friendship fs : getFriends())
			if(fs.getFriend().equals(toRemove)) {
				friends.remove(fs);
				AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid='" + uuid.toString() + "' and uuid2 = '" + toRemove.toString() + "'");
			}
	}
	
	public boolean isFriend(UUID toCheck) {
		for(Friendship fs : getFriends())
			if(fs.getFriend().equals(toCheck)) return true;
		return false;
	}
	
	public boolean isBlocked(UUID toCheck) {
		for(Blockplayer bp : getBlocked())
			if(bp.getBlocked().equals(toCheck)) return true;
		return false;
	}
	
	public void deleteFriend(UUID toRemove) {
		removeFriend(toRemove);
		if(FriendHash.isHashLoaded(toRemove)) {
			if(BungeeCord.getInstance().getPlayer(toRemove) != null) BungeeCord.getInstance().getPlayer(toRemove).sendMessage(TextComponent.fromLegacyText(Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage().replace("%NAME%", name)));
			FriendHash.getFriendHash(toRemove).removeFriend(uuid);
			return;
		}
		AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid='" + toRemove.toString() + "' and uuid2 = '" + uuid.toString() + "'");
		return;
	}
	
	public void makeFriend(UUID friend) {
		addFriend(new Friendship(uuid, friend, System.currentTimeMillis(), false, true, getStatus(friend), null));
		removeRequest(friend);
		if(BungeeCord.getInstance().getPlayer(friend) != null) {
			BungeeCord.getInstance().getPlayer(friend).sendMessage(TextComponent.fromLegacyText(Messages.CMD_ACCEPT_NEW_FRIEND.getMessage().replace("%NAME%", name)));
			FriendHash.getFriendHash(friend).removeRequest(uuid);
			FriendHash.getFriendHash(friend).addFriend(new Friendship(friend, uuid, System.currentTimeMillis(), false, true, getStatus(), null));
			return;
		}
		AsyncSQLQueueUpdater.addToQueue("insert into friends_frienddata(uuid,uuid2,favorite,cansendmessages,timestamp) values "
				+ "('" + friend.toString() + "','" + uuid.toString() + "','0','1','" + System.currentTimeMillis() + "');");
		return;
	}
	
	public Options getOptions() {
		return Friends.getSMgr().getOptions(uuid);
	}
	
	public void addRequest(Request request) {
		requests.add(request);
		AsyncSQLQueueUpdater.addToQueue("insert into friends_requests(uuid,uuid2,timestamp,message) values ('" + uuid.toString() + "','" + request.getPlayerToAdd().toString() + "','" + System.currentTimeMillis() + "','" + request.getMessage() + "');");
	}
	
	public void addFriend(Friendship fs) {
		friends.add(fs);
		AsyncSQLQueueUpdater.addToQueue("insert into friends_frienddata(uuid,uuid2,favorite,timestamp,cansendmessages) values "
				+ "('" + uuid.toString() + "','" + fs.getFriend().toString() + "','" + (fs.getFavorite() ? 1 : 0) + "','" + System.currentTimeMillis() + "','" + (fs.getCanSendMessages() ? 1 : 0) + "');");
	}
	
	public LinkedList<Friendship> getFriends() {
		return Friends.getSMgr().getFriendships(uuid);
	}
	
	public LinkedList<Request> getRequests() {
		return Friends.getSMgr().getRequests(uuid);
	}
	
	public String getStatus() {
		return Friends.getSMgr().getOptions(uuid).getStatus();
	}
	
	public int getFriendSorting() {
		return friendInvSorting;
	}
	
	public Friendship getFriendship(UUID uuid) {
		for(Friendship fs : getFriends())
			if(fs.getFriend().equals(uuid)) return fs;
		return null;
	}
	
	public void updateUUID(String name, UUID uuid) {
		this.name = name;
		if(uuidByNames.containsKey(name) && uuidByNames.get(name).equals(uuid)) return;
		
		AsyncSQLQueueUpdater.addToQueue("insert into friends_playerdata(uuid,name) values ('" + uuid.toString() + "','" + name + "') on duplicate key update uuid=values(uuid), name=values(name)");
		return;
	}
	
	public static boolean isOnline(UUID uuid) {
		if(BungeeCord.getInstance().getPlayer(uuid) != null) {
			if(FriendHash.getFriendHash(uuid).getOptions().isOffline()) return false;
			return true;
		}
		return false;
	}
	
	public static UUID getUUIDFromName(String name) {
		if(BungeeCord.getInstance().getPlayer(name) != null) return BungeeCord.getInstance().getPlayer(name).getUniqueId();
		return Friends.getSMgr().getUUIDByName(name);
	}
	
	public static LinkedList<Request> getRequests(UUID uuid) {
		if(BungeeCord.getInstance().getPlayer(uuid) != null)
			return FriendHash.getFriendHash(uuid).getRequests();
		return Friends.getSMgr().getRequests(uuid);
	}
	
	public static LinkedList<Blockplayer> getBlocked(UUID uuid) {
		if(BungeeCord.getInstance().getPlayer(uuid) != null)
			return FriendHash.getFriendHash(uuid).getBlocked();
		return Friends.getSMgr().getBlocked(uuid);
	}
	
	public static String getName(UUID uuid) {
		if(BungeeCord.getInstance().getPlayer(uuid) != null) return BungeeCord.getInstance().getPlayer(uuid).getName();
		return Friends.getSMgr().getNameByUUID(uuid);
	}

	public static String getServer(UUID uuid) {
		return Friends.getSMgr().getServer(uuid);
	}
	
	public static Options getOptions(UUID uuid) {
		if(isHashLoaded(uuid)) return FriendHash.getFriendHash(uuid).getOptions();
		return Friends.getSMgr().getOptions(uuid);
	}
	
	public static String getStatus(UUID uuid) {
		if(isHashLoaded(uuid)) return FriendHash.getFriendHash(uuid).getStatus();
		return Friends.getSMgr().getOptions(uuid).getStatus();
	}
	
	public static boolean isHashLoaded(UUID uuid) {
		return hashes.containsKey(uuid);
	}
	
	public static boolean isPlayerValid(String name) {
		if(BungeeCord.getInstance().getPlayer(name) != null) return true;
		return Friends.getSMgr().getUUIDByName(name) != null;
	}
	
	public static boolean isPlayerValid(UUID uuid) {
		if(BungeeCord.getInstance().getPlayer(uuid) != null) return true;
		return Friends.getSMgr().getNameByUUID(uuid) != null;
	}
	
	public static FriendHash getFriendHash(UUID uuid) {
		if( hashes.containsKey(uuid) ) return hashes.get(uuid);
		hashes.put(uuid, new FriendHash(uuid));
		return getFriendHash(uuid);
	}
	
	public static HashMap<UUID, FriendHash> getHashes() {
		return hashes;
	}

}
