package de.HyChrod.Friends.Listeners;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Party.Commands.SubCommands.Leave_Command;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class QuitListener implements Listener {

	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		AsyncSQLQueueUpdater.addToQueue("update friends_playerdata set online='0', lastOnline='" + System.currentTimeMillis() + "' where uuid='" + e.getPlayer().getUniqueId().toString() + "'");
		if(Parties.getParty(e.getPlayer().getUniqueId()) != null) new Leave_Command(Friends.getInstance(), e.getPlayer(), new String[] {"leave"}); 
		if(!FriendHash.isOnline(e.getPlayer().getUniqueId())) return;
		BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				FriendHash hash = FriendHash.getFriendHash(e.getPlayer().getUniqueId());
				for(Friendship fs : hash.getFriends())
					if(BungeeCord.getInstance().getPlayer(fs.getFriend()) != null) {
						if(fs == null || fs.getFriend() == null) continue;
						FriendHash fHash = FriendHash.getFriendHash(fs.getFriend());
						Friendship ffs = fHash.getFriendship(e.getPlayer().getUniqueId());
						if(fHash.getOptions() != null && ffs != null) {
							if(!fHash.getOptions().getMessages() && !fHash.getOptions().getFavMessages() || !ffs.getCanSendMessages()) continue;
							if(fHash.getOptions().getFavMessages() && !ffs.getFavorite()) continue;
						}
						if(BungeeCord.getInstance().getPlayer(fs.getFriend()) != null)
							BungeeCord.getInstance().getPlayer(fs.getFriend()).sendMessage(TextComponent.fromLegacyText(Messages.QUIT_MESSAGE.getMessage().replace("%NAME%", e.getPlayer().getName())));
					}
			}
			
		});
	}
	
}
