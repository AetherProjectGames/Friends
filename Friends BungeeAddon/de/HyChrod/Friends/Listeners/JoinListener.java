package de.HyChrod.Friends.Listeners;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener {

	@EventHandler
	public void onJoin(PostLoginEvent e) {
		ProxiedPlayer p = (ProxiedPlayer) e.getPlayer();
		AsyncSQLQueueUpdater.addToQueue("insert into friends_playerdata(uuid,name,online,lastOnline) values ('" + p.getUniqueId().toString() + "','" + p.getName() +"','1','" + System.currentTimeMillis() + "')"
				+ " on duplicate key update online=values(online), name=values(name);");
		
		if(Friends.needUpdate() && Configs.CHECK_FOR_UPDATES.getBoolean() && (p.hasPermission("Friends.Commands.Version") || p.hasPermission("Friends.Commands.Reload")))
			p.sendMessage(TextComponent.fromLegacyText(Friends.getPrefix() + " §cYou are running an outdated version of Friends-BungeeAddon!"));
		
		BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
				if(hash.getOptions().isOffline()) return;
				for(Friendship fs : hash.getFriends())
					if(BungeeCord.getInstance().getPlayer(fs.getFriend()) != null) {
						FriendHash fHash = FriendHash.getFriendHash(fs.getFriend());
						Friendship ffs = fHash.getFriendship(p.getUniqueId());
						try {
							if(!fHash.getOptions().getMessages() && !fHash.getOptions().getFavMessages() || !ffs.getCanSendMessages()) continue;
							if(fHash.getOptions().getFavMessages() && !ffs.getFavorite()) continue;
							BungeeCord.getInstance().getPlayer(fs.getFriend()).sendMessage(TextComponent.fromLegacyText(Messages.JOIN_MESSAGE.getMessage().replace("%NAME%", p.getName())));
						} catch (Exception ex) {}
					}
			}
		});
	}
	
}
