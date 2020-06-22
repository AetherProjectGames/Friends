package de.HyChrod.Friends.Hashing;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class FriendHash {
	
	private static HashMap<UUID, FriendHash> hashes = new HashMap<>();
	private static HashMap<String, UUID> uuidByNames = new HashMap<String, UUID>();
	
	private UUID uuid;
	private String name;
	private Options options;
	private int friendInvSorting = 0;
	private long lastonline;
	
	private LinkedList<Friendship> friends = new LinkedList<>();
	private LinkedList<Friendship> temp_friends = new LinkedList<Friendship>();
	private LinkedList<Request> requests = new LinkedList<>();
	private LinkedList<Blockplayer> blocked = new LinkedList<>();
	private LinkedList<Request> temp_requests = new LinkedList<>();
	private LinkedList<Blockplayer> temp_blocked = new LinkedList<>();
	
	public FriendHash(UUID uuid) {
		this.uuid = uuid;
		this.options = new Options(uuid, false, true, 1, "", 0, true, true);
		hashes.put(uuid, this);
		load();
	}
	
	public static void sendPluginMessage(UUID uuid, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF(Friends.getSMgr().getNameByUUID(uuid));
		out.writeUTF(message);
		Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(Friends.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	public void blockPlayer(UUID toBlock, String message) {
		removeRequest(toBlock);
		addBlocked(new Blockplayer(uuid, toBlock, System.currentTimeMillis(), message));
		if(Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("delete from friends_requests where uuid = '" + toBlock.toString() + "' and uuid2 = '" + uuid.toString() + "';");
			AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid = '" + toBlock.toString() + "' and uuid2 = '" + uuid.toString() + "'");
		}
		if(FriendHash.isHashLoaded(toBlock)) {
			FriendHash bHash = FriendHash.getFriendHash(toBlock);
			bHash.removeRequest(uuid);
			if(isFriend(toBlock)) {
				removeFriend(toBlock);
				bHash.removeFriend(uuid);
				bHash.removeRequest(uuid);
				if(isOnline(toBlock)) {
					if(Configs.BUNGEEMODE.getBoolean()) sendPluginMessage(toBlock, Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage(Bukkit.getPlayer(toBlock)).replace("%NAME%", name));
					else Bukkit.getPlayer(toBlock).sendMessage(Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage(Bukkit.getPlayer(toBlock)).replace("%NAME%", name));
				}
			}
			return;
		}
		removeFriend(toBlock);	
		if(Friends.isMySQL() && !Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("delete from friends_requests where uuid = '" + toBlock.toString() + "' and uuid2 = '" + uuid.toString() + "';");
			AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid = '" + toBlock.toString() + "' and uuid2 = '" + uuid.toString() + "'");
			return;
		}
		FileManager.save(FileManager.REQUESTS.getNewCfg(), FileManager.REQUESTS.getNewFile(), toBlock.toString() + "." + uuid.toString(), null);
		FileManager.save(FileManager.FRIENDS.getNewCfg(), FileManager.FRIENDS.getNewFile(), toBlock.toString() + "." + uuid.toString(), null);
	}
	
	public void sendRequest(UUID toAdd, String message) {
		if(Configs.BUNGEEMODE.getBoolean()) 
			AsyncSQLQueueUpdater.addToQueue("insert into friends_requests(uuid,uuid2,message,timestamp) values ('" + toAdd.toString() + "','" + uuid.toString() + "','" + message + "','" + System.currentTimeMillis() + "');");
		if(FriendHash.isHashLoaded(toAdd)) {
			if(isOnline(toAdd)) {
				Player toAddP = Bukkit.getPlayer(toAdd);
				if(Configs.BUNGEEMODE.getBoolean()) sendPluginMessage(toAdd, Messages.CMD_ADD_RECEIVE_REQUEST.getMessage(toAddP).replace("%NAME%", name));
				else {
					
					TextComponent accept = new TextComponent(Messages.CMD_ADD_CLICK_BUTTON_ACCEPT_TEXT.getMessage(toAddP));
					accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friends accept " + name));
					accept.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.CMD_ADD_CLICK_BUTTON_ACCEPT_HOVER.getMessage(toAddP).replace("%NAME%", name)).create()));
					
					TextComponent deny = new TextComponent(Messages.CMD_ADD_CLICK_BUTTON_DENY_TEXT.getMessage(toAddP));
					deny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friends deny " + name));
					deny.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Messages.CMD_ADD_CLICK_BUTTON_DENY_HOVER.getMessage(toAddP).replace("%NAME%", name)).create()));
					
					String clickable = Messages.CMD_ADD_CLICK.getMessage(toAddP).replace("%NAME%", name);
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
					Bukkit.getPlayer(toAdd).sendMessage(Messages.CMD_ADD_RECEIVE_REQUEST.getMessage(toAddP).replace("%NAME%", name));
					if(Configs.CLICKABLE_MESSAGES.getBoolean()) Bukkit.getPlayer(toAdd).spigot().sendMessage(toSendClick);	
				}
				if(Configs.ADDMESSAGE_ENABLE.getBoolean() && message.length() > 0) 
					if(Configs.BUNGEEMODE.getBoolean()) sendPluginMessage(toAdd, Messages.CMD_ADD_RECEIVE_REQUEST_MESSAGE.getMessage(toAddP)
						.replace("%MESSAGE%", message));
					else Bukkit.getPlayer(toAdd).sendMessage(Messages.CMD_ADD_RECEIVE_REQUEST_MESSAGE.getMessage(toAddP)
						.replace("%MESSAGE%", message));
			}
			FriendHash.getFriendHash(toAdd).addRequest(new Request(toAdd, uuid, message, System.currentTimeMillis()));
			return;
		}
		if(Friends.isMySQL() && !Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("insert into friends_requests(uuid,uuid2,message,timestamp) values ('" + toAdd.toString() + "','" + uuid.toString() + "','" + message + "','" + System.currentTimeMillis() + "');");
			return;
		}
		FileManager.save(FileManager.REQUESTS.getNewCfg(), FileManager.REQUESTS.getNewFile(), toAdd.toString() + "." + uuid.toString() + ".Message", message);
		FileManager.save(FileManager.REQUESTS.getNewCfg(), FileManager.REQUESTS.getNewFile(), toAdd.toString() + "." + uuid.toString() + ".Timestamp", System.currentTimeMillis());
	}
	
	public void addBlocked(Blockplayer bl) {
		blocked.add(bl);
		if(Configs.BUNGEEMODE.getBoolean())
			AsyncSQLQueueUpdater.addToQueue("insert into friends_blocked(uuid,uuid2,timestamp,message) values ('" + uuid.toString() + "','" + bl.getBlocked().toString() + "','" + bl.getTimestamp() + "','" + bl.getMessage() + "');");
	}
	
	public void removeBlocked(UUID toRemove) {
		for(Blockplayer bl : getBlockedNew())
			if(bl.getBlocked().equals(toRemove)) {
				blocked.remove(bl);
				if(Configs.BUNGEEMODE.getBoolean())
					AsyncSQLQueueUpdater.addToQueue("delete from friends_blocked where uuid= '" + uuid.toString() + "' and uuid2 = '" + toRemove.toString() + "'");
			}
	}
	
	public LinkedList<Blockplayer> getBlocked() {
		return blocked;
	}
	
	public LinkedList<Blockplayer> getBlockedNew() {
		if(Configs.BUNGEEMODE.getBoolean()) return Friends.getSMgr().getBlocked(uuid);
		return blocked;
	}
	
	public void setStatus(String status) {
		this.options.setStatus(status);
		if(Configs.BUNGEEMODE.getBoolean())
			AsyncSQLQueueUpdater.addToQueue("update friends_options set status='" + status + "' where uuid= '" + uuid.toString() + "'");
	}
	
	public void removeRequest(UUID toRemove) {
		for(Request rq : new LinkedList<>(getRequestsNew()))
			if(rq.getPlayerToAdd().equals(toRemove)) {
				requests.remove(rq);
				if(Configs.BUNGEEMODE.getBoolean())
					AsyncSQLQueueUpdater.addToQueue("delete from friends_requests where uuid='" + uuid.toString() + "' and uuid2 = '" + toRemove.toString() + "'");
			}
	}
	
	public long getLastOnline() {
		return lastonline;
	}
	
	public long getLastOnline(UUID uuid) {
		if(Configs.BUNGEEMODE.getBoolean()) return Friends.getSMgr().getLastOnline(uuid);
		if(isHashLoaded(uuid)) return FriendHash.getFriendHash(uuid).getLastOnline();
		return getFriendship(uuid).getLastOnline();
	}
	
	public void setLastOnline(long lastOnline) {
		if(Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("update friends_playerdata set lastOnline ='" + lastOnline + "' where uuid='" + uuid.toString() +"';");
			return;
		}
		this.lastonline = lastOnline;
	}
	
	public String getWorld() {
		return Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getWorld().getName() : null;
	}
	
	public void removeFriend(UUID toRemove) {
		for(Friendship fs : new LinkedList<>(getFriendsNew()))
			if(fs.getFriend().equals(toRemove)) {
				friends.remove(fs);
				if(Configs.BUNGEEMODE.getBoolean())
					AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid='" + uuid.toString() + "' and uuid2 = '" + toRemove.toString() + "'");
			}
	}
	
	public boolean isFriend(UUID toCheck) {
		for(Friendship fs : new LinkedList<>(getFriendsNew()))
			if(fs.getFriend().equals(toCheck)) return true;
		return false;
	}
	
	public boolean isBlocked(UUID toCheck) {
		for(Blockplayer bp : new LinkedList<>(getBlockedNew()))
			if(bp.getBlocked().equals(toCheck)) return true;
		return false;
	}
	
	public void deleteFriend(UUID toRemove) {
		removeFriend(toRemove);
		if(Configs.BUNGEEMODE.getBoolean())
			AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid='" + toRemove.toString() + "' and uuid2 = '" + uuid.toString() + "'");
			
		if(isOnline(toRemove)) {
			if(Configs.BUNGEEMODE.getBoolean()) sendPluginMessage(toRemove, Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage(Bukkit.getPlayer(toRemove)).replace("%NAME%", name));
			else Bukkit.getPlayer(toRemove).sendMessage(Messages.CMD_REMOVE_FRIEND_REMOVED.getMessage(Bukkit.getPlayer(toRemove)).replace("%NAME%", name));
			if(FriendHash.isHashLoaded(toRemove)) FriendHash.getFriendHash(toRemove).removeFriend(uuid);
			return;
		}
		if(Friends.isMySQL() && !Configs.BUNGEEMODE.getBoolean()) {
			AsyncSQLQueueUpdater.addToQueue("delete from friends_frienddata where uuid='" + toRemove.toString() + "' and uuid2 = '" + uuid.toString() + "'");
			return;
		}
		FileManager.save(FileManager.FRIENDS.getNewCfg(), FileManager.FRIENDS.getNewFile(), toRemove.toString() + "." + uuid.toString(), null);
	}
	
	public void makeFriend(UUID friend) {
		addFriend(new Friendship(uuid, friend, System.currentTimeMillis(), false, true, getStatus(friend), null));
		removeRequest(friend);
		if(isOnline(friend)) {
			if(Configs.BUNGEEMODE.getBoolean()) sendPluginMessage(friend, Messages.CMD_ACCEPT_NEW_FRIEND.getMessage(Bukkit.getPlayer(friend)).replace("%NAME%", name));
			else Bukkit.getPlayer(friend).sendMessage(Messages.CMD_ACCEPT_NEW_FRIEND.getMessage(Bukkit.getPlayer(friend)).replace("%NAME%", name));
			FriendHash.getFriendHash(friend).removeRequest(uuid);
			FriendHash.getFriendHash(friend).addFriend(new Friendship(friend, uuid, System.currentTimeMillis(), false, true, getStatus(), null));
			return;
		}
		if(Friends.isMySQL()) {
			AsyncSQLQueueUpdater.addToQueue("insert into friends_frienddata(uuid,uuid2,favorite,cansendmessages,timestamp) values "
					+ "('" + friend.toString() + "','" + uuid.toString() + "','0','1','" + System.currentTimeMillis() + "');");
			return;
		}
		FileManager.save(FileManager.FRIENDS.getNewCfg(), FileManager.FRIENDS.getNewFile(), friend.toString() + "." + uuid.toString() + ".Timestamp", System.currentTimeMillis());
		FileManager.save(FileManager.FRIENDS.getNewCfg(), FileManager.FRIENDS.getNewFile(), friend.toString() + "." + uuid.toString() + ".Favorite", false);
		FileManager.save(FileManager.FRIENDS.getNewCfg(), FileManager.FRIENDS.getNewFile(), friend.toString() + "." + uuid.toString() + ".CanSendMessages", false);
		FileManager.save(FileManager.REQUESTS.getNewCfg(), FileManager.REQUESTS.getNewFile(), friend.toString() + "." + uuid.toString(), null);
	}
	
	public Options getOptions() {
		return this.options;
	}
	
	public void addRequest(Request request) {
		requests.add(request);
		if(Configs.BUNGEEMODE.getBoolean())
			AsyncSQLQueueUpdater.addToQueue("insert into friends_requests(uuid,uuid2,timestamp,message) values ('" + uuid.toString() + "','" + request.getPlayerToAdd().toString() + "','" + System.currentTimeMillis() + "','" + request.getMessage() + "');");
	}
	
	public void addFriend(Friendship fs) {
		friends.add(fs);
		if(Configs.BUNGEEMODE.getBoolean())
			AsyncSQLQueueUpdater.addToQueue("insert into friends_frienddata(uuid,uuid2,favorite,timestamp,cansendmessages) values "
					+ "('" + uuid.toString() + "','" + fs.getFriend().toString() + "','" + (fs.getFavorite() ? 1 : 0) + "','" + System.currentTimeMillis() + "','" + (fs.getCanSendMessages() ? 1 : 0) + "');");
	}
	
	public LinkedList<Friendship> getFriends() {
		return friends;
	}
	
	public LinkedList<Friendship> getFriendsNew() {
		if(Configs.BUNGEEMODE.getBoolean()) friends = Friends.getSMgr().getFriendships(uuid);
		return friends;
	}
	
	public LinkedList<Request> getRequests() {
		return requests;
	}
	
	public LinkedList<Request> getRequestsNew() {
		if(Configs.BUNGEEMODE.getBoolean()) requests = Friends.getSMgr().getRequests(uuid);
		return requests;
	}
	
	public String getStatus() {
		if(Configs.BUNGEEMODE.getBoolean()) this.options.setStatus(Friends.getSMgr().getOptions(uuid).getStatus());
		return this.options.getStatus();
	}
	
	public int getFriendSorting() {
		return friendInvSorting;
	}
	
	public Friendship getFriendship(UUID uuid) {
		for(Friendship fs : new LinkedList<>(getFriendsNew()))
			if(fs.getFriend().equals(uuid)) return fs;
		return null;
	}
	
	public int getSorting() {
		return this.getOptions().getSorting();
	}
	
	public void setSorting(int sorting) {
		this.getOptions().setSorting(sorting);
		if(Configs.BUNGEEMODE.getBoolean())
			AsyncSQLQueueUpdater.addToQueue("update friends_options set sorting='" + sorting + "' where uuid = '" + uuid.toString() + "'");
	}
	
	public void updateUUID(String name, UUID uuid) {
		this.name = name;
		if(uuidByNames.containsKey(name) && uuidByNames.get(name).equals(uuid)) return;
		
		if(Friends.isMySQL()) {
			AsyncSQLQueueUpdater.addToQueue("insert into friends_playerdata(uuid,name) values ('" + uuid.toString() + "','" + name + "') on duplicate key update uuid=values(uuid), name=values(name)");
			return;
		}
		FileConfiguration config = FileManager.PLAYERDATA.getNewCfg();
		File file = FileManager.PLAYERDATA.getNewFile();
		if(config.get("UUIDs." + uuid.toString()) != null) {
			if(config.getString("UUIDs." + uuid.toString()).equals(name)) return;
			FileManager.save(config, file, "Names." + (config.getString("UUIDs." + uuid.toString())), null);
			FileManager.save(config, file, "UUIDs." + uuid.toString(), name);
			return;
		}
		FileManager.save(config, file, "Names." + name, uuid.toString());
		FileManager.save(config, file, "UUIDs." + uuid.toString(), name);
	}
	
	public static boolean isOnline(UUID uuid) {
		if(Configs.BUNGEEMODE.getBoolean()) {
			return Friends.getSMgr().isOnline(uuid);
		}
		if(Bukkit.getPlayer(uuid) != null) {
			if(FriendHash.getFriendHash(uuid).getOptions().isOffline()) return false;
			return true;
		}
		return false;
	}
	
	public static UUID getUUIDFromName(String name) {
		if(Bukkit.getPlayer(name) != null) return Bukkit.getPlayer(name).getUniqueId();
		if(Friends.isMySQL()) {
			return Friends.getSMgr().getUUIDByName(name);
		}
		if(FileManager.PLAYERDATA.getNewCfg().get("Names." + name) == null) return null;
		return UUID.fromString(FileManager.PLAYERDATA.getNewCfg().getString("Names." + name));
	}
	
	public static String getServer(UUID uuid) {
		if(!Configs.BUNGEEMODE.getBoolean()) return "null";
		String server = Friends.getSMgr().getServer(uuid);
		return server == null ? "null" : server;
	}
	
	public static LinkedList<Request> getRequests(UUID uuid) {
		if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
			return FriendHash.getFriendHash(uuid).getRequestsNew();
		
		if(Friends.isMySQL()) return Friends.getSMgr().getRequests(uuid);
		FileConfiguration cfg = FileManager.REQUESTS.getNewCfg();
		LinkedList<Request> requests = new LinkedList<Request>();
		if(cfg.get(uuid.toString()) != null)
			for(String requestUUIDS : cfg.getConfigurationSection(uuid.toString()).getKeys(false))
				requests.add(new Request(uuid, UUID.fromString(requestUUIDS), cfg.getString(uuid.toString() + "." + requestUUIDS + ".Message"), 
						cfg.getLong(uuid.toString() + "." + requestUUIDS + ".Timestamp")));
		return requests;
	}
	
	public static LinkedList<Blockplayer> getBlocked(UUID uuid) {
		if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
			return FriendHash.getFriendHash(uuid).getBlockedNew();
		
		if(Friends.isMySQL()) return Friends.getSMgr().getBlocked(uuid);
		FileConfiguration cfg = FileManager.BLOCKED.getNewCfg();
		LinkedList<Blockplayer> blocked = new LinkedList<Blockplayer>();
		if(cfg.get(uuid.toString()) != null)
			for(String blockedUUIDs : cfg.getConfigurationSection(uuid.toString()).getKeys(false))
				blocked.add(new Blockplayer(uuid, UUID.fromString(blockedUUIDs), cfg.getLong(uuid.toString() + "." + blockedUUIDs + ".Timestamp"), 
						cfg.getString(uuid.toString() + "."  + blockedUUIDs + ".Message")));
		return blocked;
	}
	
	public static String getName(UUID uuid) {
		if(Configs.BUNGEEMODE.getBoolean()) return Friends.getSMgr().getNameByUUID(uuid);
		if(Bukkit.getOfflinePlayer(uuid).getName() == null) {
			if(Friends.isMySQL()) {
				String name = Friends.getSMgr().getNameByUUID(uuid);
				return name == null ? "unknown" : name;
			}
			FileConfiguration cfg = FileManager.PLAYERDATA.getConfig();
			if(cfg.get("UUIDs." + uuid.toString()) != null) return cfg.getString("UUIDs." + uuid.toString());
			return "unknown";
		}
		return Bukkit.getOfflinePlayer(uuid).getName();
	}
	
	public static Options getOptions(UUID uuid) {
		if(isHashLoaded(uuid)) return FriendHash.getFriendHash(uuid).getOptions();
		if(Friends.isMySQL()) return Friends.getSMgr().getOptions(uuid);
		FileConfiguration cfg = FileManager.OPTIONS.getConfig();
		return new Options(uuid, cfg.getBoolean(uuid.toString() + ".Offlinemode"), cfg.getBoolean(uuid.toString() + ".Requests"), cfg.getInt(uuid.toString() + ".Messages"),
				cfg.getString(uuid.toString() + ".Status"), cfg.getInt(uuid.toString() + ".Sorting"), cfg.getBoolean(uuid.toString() + ".Jumping"), cfg.getBoolean(uuid.toString() + ".Party"));
	}
	
	public static String getStatus(UUID uuid) {
		if(isHashLoaded(uuid)) return FriendHash.getFriendHash(uuid).getStatus();
		if(Friends.isMySQL()) return Friends.getSMgr().getOptions(uuid).getStatus();
		return FileManager.OPTIONS.getConfig().getString(uuid.toString() + ".Status");
	}
	
	public static boolean isHashLoaded(UUID uuid) {
		return hashes.containsKey(uuid);
	}
	
	public static String getWorld(UUID uuid) {
		return Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getWorld().getName() : "null";
	}
	
	public static boolean isPlayerValid(String name) {
		if(Bukkit.getPlayer(name) != null) return true;
		if(Friends.isMySQL()) return Friends.getSMgr().getUUIDByName(name) != null;
		FileConfiguration cfg = FileManager.PLAYERDATA.getConfig();
		return cfg.get("Names." + name) != null;
	}
	
	public static FriendHash getFriendHash(UUID uuid) {
		if( hashes.containsKey(uuid) ) return hashes.get(uuid);
		hashes.put(uuid, new FriendHash(uuid));
		return getFriendHash(uuid);
	}
	
	public static HashMap<UUID, FriendHash> getHashes() {
		return hashes;
	}
	
	public void load() {
		if(name == null) {
			if(Friends.isMySQL()) {
				if(Bukkit.getPlayer(uuid) != null) name = Bukkit.getPlayer(uuid).getName();
				if(name == null) name = Friends.getSMgr().getNameByUUID(uuid);
				
				AsyncSQLQueueUpdater.addToQueue("insert into friends_playerdata(uuid,name) values ('" + uuid.toString() + "','" + name + "') on duplicate key update uuid=values(uuid), name=values(name)");
				uuidByNames.put(name, uuid);
				options = Friends.getSMgr().getOptions(uuid);
				
				lastonline = Friends.getSMgr().getLastOnline(uuid);
				if(lastonline < 0) lastonline = System.currentTimeMillis();
				
				temp_friends = Friends.getSMgr().getFriendships(uuid);
				friends.addAll(temp_friends);
				
				temp_requests = Friends.getSMgr().getRequests(uuid);
				requests.addAll(temp_requests);
				
				temp_blocked = Friends.getSMgr().getBlocked(uuid);
				blocked.addAll(temp_blocked);
				return;
			}
			FileConfiguration pcfg = FileManager.getConfig("/Util","playerdata.dat");
			if(pcfg.get("UUIDs." + uuid.toString()) != null) name = pcfg.getString("UUIDs." + uuid.toString());
			lastonline = System.currentTimeMillis();
			if(pcfg.get("LastOnline." + uuid.toString()) != null) lastonline = pcfg.getLong("LastOnline." + uuid.toString());
			
			uuidByNames.put(name, uuid);
			this.options = new Options(uuid, false, true, 1, "", 0, true, true);
			
			FileConfiguration ocfg = FileManager.OPTIONS.getNewCfg();
			if(ocfg.get(uuid.toString()) != null) {
				this.options = new Options(uuid, ocfg.getBoolean(uuid.toString() + ".Offlinemode"), ocfg.getBoolean(uuid.toString() + ".Requests"), ocfg.getInt(uuid.toString() + ".Messages"), 
						ocfg.getString(uuid.toString() + ".Status"), ocfg.getInt(uuid.toString() + ".Sorting"), ocfg.getBoolean(uuid.toString() + ".Jumping"), ocfg.getBoolean(uuid.toString() + ".Party"));
			}
			
			if(pcfg.get("Sorting." + uuid.toString() + ".FriendInv") != null)
				friendInvSorting = pcfg.getInt("Sorting." + uuid.toString() + ".FriendInv");
			
			FileConfiguration rcfg = FileManager.getConfig("/Util", "requests.dat");
			if(rcfg.getString(uuid.toString()) != null)
				for(String rUUIDs : rcfg.getConfigurationSection(uuid.toString()).getKeys(false))
					addRequest(new Request(uuid, UUID.fromString(rUUIDs), rcfg.getString(uuid.toString() + "." + rUUIDs + ".Message"), 
							rcfg.getLong(uuid.toString() + "." + rUUIDs + ".Timestamp")));
			
			FileConfiguration fcfg = FileManager.getConfig("/Util", "friends.dat");
			if(fcfg.getString(uuid.toString()) != null)
				for(String fUUIDs : fcfg.getConfigurationSection(uuid.toString()).getKeys(false))
					addFriend(new Friendship(uuid, UUID.fromString(fUUIDs), fcfg.getLong(uuid.toString() + "." + fUUIDs + ".Timestamp"), 
							fcfg.getBoolean(uuid.toString() + "." + fUUIDs + ".Favorite"), 
							fcfg.getBoolean(uuid.toString() + "." + fUUIDs + ".CanSendMessages"),
							ocfg.getString(fUUIDs + ".Status"), fcfg.getString(uuid.toString() + "." + fUUIDs + ".Nickname")));
			
			FileConfiguration bcfg = FileManager.getConfig("/Util","blocked.dat");
			if(bcfg.getString(uuid.toString()) != null)
				for(String bUUIDs : bcfg.getConfigurationSection(uuid.toString()).getKeys(false))
					addBlocked(new Blockplayer(uuid, UUID.fromString(bUUIDs), bcfg.getLong(uuid.toString() + "." + bUUIDs + ".Timestamp"),
							bcfg.getString(uuid.toString() + "." + bUUIDs + ".Message")));
			
		}
	}
	
	public void save() {
		if(Friends.isMySQL()) {
			Friends.getSMgr().updateOptions(getOptions());
			if(Configs.BUNGEEMODE.getBoolean()) return;
			if(!temp_friends.equals(friends)) {
				LinkedList<Friendship> toInsert = new LinkedList<Friendship>();
				LinkedList<Friendship> toDelete = new LinkedList<Friendship>();
				for(Friendship fs : temp_friends) {
					toDelete.add(fs);
					for(Friendship ffs : getFriends()) 
						if(ffs.getFriend().equals(fs.getFriend())) toDelete.remove(fs);
				}
				for(Friendship fs : getFriends()) {
					if(toDelete.contains(fs)) continue;
					toInsert.add(fs);
					for(Friendship ffs : temp_friends)
						if(fs.getFriend().equals(ffs.getFriend())) toInsert.remove(fs);
				}
				Friends.getSMgr().insertIntoFriends(toInsert);
				Friends.getSMgr().deleteFromFriends(toDelete);
			}
			LinkedList<Friendship> toUpdate = new LinkedList<Friendship>();
			for(Friendship fs : getFriends())
				if(fs.getUpdated())
					toUpdate.add(fs);
			Friends.getSMgr().updateFriends(toUpdate);
			
			if(!temp_requests.equals(requests)) {
				LinkedList<Request> toInsert = new LinkedList<Request>();
				LinkedList<Request> toDelete = new LinkedList<Request>();
				for(Request fs : temp_requests) {
					toDelete.add(fs);
					for(Request ffs : getRequests()) 
						if(ffs.getPlayerToAdd().equals(fs.getPlayerToAdd())) toDelete.remove(fs);
				}
				for(Request fs : getRequests()) {
					if(toDelete.contains(fs)) continue;
					toInsert.add(fs);
					for(Request ffs : temp_requests)
						if(fs.getPlayerToAdd().equals(ffs.getPlayerToAdd())) toInsert.remove(fs);
				}
				Friends.getSMgr().insertIntoRequests(toInsert);
				Friends.getSMgr().deleteFromRequests(toDelete);
			}
			if(!temp_blocked.equals(blocked)) {
				LinkedList<Blockplayer> toInsert = new LinkedList<Blockplayer>();
				LinkedList<Blockplayer> toDelete = new LinkedList<Blockplayer>();
				for(Blockplayer fs : temp_blocked) {
					toDelete.add(fs);
					for(Blockplayer ffs : getBlocked()) 
						if(ffs.getBlocked().equals(fs.getBlocked())) toDelete.remove(fs);
				}
				for(Blockplayer fs : getBlocked()) {
					if(toDelete.contains(fs)) continue;
					toInsert.add(fs);
					for(Blockplayer ffs : temp_blocked)
						if(fs.getBlocked().equals(ffs.getBlocked())) toInsert.remove(fs);
				}
				Friends.getSMgr().insertIntoBlocked(toInsert);
				Friends.getSMgr().deleteFromBlocked(toDelete);
			}
			Friends.getSMgr().perform("update friends_playerdata set lastOnline='" + System.currentTimeMillis() + "' where uuid='" + uuid.toString() + "'");
			return;
		}
		if(this.options != null) {
			File ofile = FileManager.OPTIONS.getNewFile();
			FileConfiguration ocfg = FileManager.OPTIONS.getNewCfg();
			FileManager.save(ocfg, ofile, uuid.toString() + ".Offlinemode", this.options.isOffline());
			FileManager.save(ocfg, ofile, uuid.toString() + ".Requests", this.options.getRequests());
			FileManager.save(ocfg, ofile, uuid.toString() + ".Messages", this.options.getMessages() ? 1 : this.options.getFavMessages() ? 2 : 0);
			FileManager.save(ocfg, ofile, uuid.toString() + ".Status", this.options.getStatus());
			FileManager.save(ocfg, ofile, uuid.toString() + ".Sorting", this.options.getSorting());
			FileManager.save(ocfg, ofile, uuid.toString() + ".Jumping", this.options.getJumping());
			FileManager.save(ocfg, ofile, uuid.toString() + ".Party", this.options.getPartyInvites());
		}
		
		File pfile = FileManager.PLAYERDATA.getNewFile();
		FileConfiguration pcfg = FileManager.PLAYERDATA.getNewCfg();
		FileManager.save(pcfg, pfile, "LastOnline." + uuid.toString(), lastonline);
		
		File rfile = FileManager.getFile("/Util", "requests.dat");
		FileConfiguration rcfg = FileManager.getConfig(rfile);
		FileManager.save(rcfg, rfile, uuid.toString(), null);
		if(!requests.isEmpty())
			for(Request rq : getRequests()) {
				FileManager.save(rcfg, rfile, uuid.toString() + "." + rq.getPlayerToAdd().toString() + ".Message", rq.getMessage());
				FileManager.save(rcfg, rfile, uuid.toString() + "." + rq.getPlayerToAdd().toString() + ".Timestamp", rq.getTimestamp());
			}
		File ffile = FileManager.getFile("/Util", "friends.dat");
		FileConfiguration fcfg = FileManager.getConfig(ffile);
		FileManager.save(fcfg, ffile, uuid.toString(), null);
		if(!friends.isEmpty())
			for(Friendship fs : getFriends()) {
				FileManager.save(fcfg, ffile, uuid.toString() + "." + fs.getFriend().toString() + ".Timestamp", fs.getTimestamp());
				FileManager.save(fcfg, ffile, uuid.toString() + "." + fs.getFriend().toString() + ".CanSendMessages", fs.getCanSendMessages());
				FileManager.save(fcfg, ffile, uuid.toString() + "." + fs.getFriend().toString() + ".Favorite", fs.getFavorite());
				FileManager.save(fcfg, ffile, uuid.toString() + "." + fs.getFriend().toString() + ".Nickname", fs.getNickname());
			}
		File bfile = FileManager.getFile("/Util", "blocked.dat");
		FileConfiguration bcfg = FileManager.getConfig(bfile);
		FileManager.save(bcfg, bfile, uuid.toString(), null);
		if(!blocked.isEmpty())
			for(Blockplayer bl : getBlocked()) {
				FileManager.save(bcfg, bfile, uuid.toString() + "." + bl.getBlocked().toString() + ".Timestamp", bl.getTimestamp());
				FileManager.save(bcfg, bfile, uuid.toString() + "." + bl.getBlocked().toString() + ".Message", bl.getMessage());
			}
	}

}
