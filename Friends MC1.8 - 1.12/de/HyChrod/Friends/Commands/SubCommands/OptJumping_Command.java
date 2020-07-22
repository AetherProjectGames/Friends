package de.HyChrod.Friends.Commands.SubCommands;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Options;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class OptJumping_Command {
	
	public OptJumping_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Options.Jumping") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length != 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends jumping"));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		if(hash.getOptions().getJumping()) {
			hash.getOptions().setJumping(false);
			p.sendMessage(Messages.CMD_OPT_JUMP_DISABLE.getMessage(p));
			updateOptionsOnBungee(hash.getOptions());
			return;
		}
		hash.getOptions().setJumping(true);
		p.sendMessage(Messages.CMD_OPT_JUMP_ENABLE.getMessage(p));
		updateOptionsOnBungee(hash.getOptions());
		return;
	}
	
	private void updateOptionsOnBungee(Options opt) {
		if(!Configs.BUNGEEMODE.getBoolean()) return;
		AsyncSQLQueueUpdater.addToQueue("insert into friends_options(uuid, offline,receivemsg,receiverequests,sorting,status,jumping) "
				+ "values ('" + opt.getUuid().toString() + "','" + (opt.isOffline() ? 1 : 0) + "','" + (opt.getMessages() ? 1 : opt.getFavMessages() ? 2 : 0) + "','" + (opt.getRequests() ? 1 : 0) + "',"
						+ "'" + opt.getSorting() + "','" + opt.getStatus() + "', '" + (opt.getJumping() ? 1 : 0) + "') on duplicate key update "
				+ "offline=values(offline),receivemsg=values(receivemsg),receiverequests=values(receiverequests),sorting=values(sorting),status=values(status),jumping=values(jumping);");
	}

}
