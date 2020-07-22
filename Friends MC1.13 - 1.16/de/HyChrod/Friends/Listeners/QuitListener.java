package de.HyChrod.Friends.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Party.Commands.SubCommands.Leave_Command;
import de.HyChrod.Party.Utilities.Parties;

public class QuitListener implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if((hash.getOptions() != null && hash.getOptions().isOffline()) || Configs.BUNGEEMODE.getBoolean()) return;
		hash.setLastOnline(System.currentTimeMillis());
		if(Parties.getParty(p.getUniqueId()) != null) new Leave_Command(Friends.getInstance(), p, new String[] {"leave"}); 
		if(!FriendHash.isOnline(p.getUniqueId())) return;
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				for(Friendship fs : hash.getFriendsNew())
					if(Bukkit.getPlayer(fs.getFriend()) != null) {
						if(fs == null || fs.getFriend() == null) continue;
						FriendHash fHash = FriendHash.getFriendHash(fs.getFriend());
						Friendship ffs = fHash.getFriendship(p.getUniqueId());
						if(fHash.getOptions() != null && ffs != null) {
							if(!fHash.getOptions().getMessages() && !fHash.getOptions().getFavMessages() || !ffs.getCanSendMessages()) continue;
							if(fHash.getOptions().getFavMessages() && !ffs.getFavorite()) continue;
						}
						Bukkit.getPlayer(fs.getFriend()).sendMessage(Messages.QUIT_MESSAGE.getMessage(Bukkit.getPlayer(fs.getFriend())).replace("%NAME%", p.getName()));
					}
			}
		});
	}

}
