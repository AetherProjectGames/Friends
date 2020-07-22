package de.HyChrod.Friends.Commands.SubCommands;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Options;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class OptJumping_Command {
	
	public OptJumping_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Options.Jumping") && !p.hasPermission("Friends.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 1) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends jumping")));
			return;
		}
		
		FriendHash hash = FriendHash.getFriendHash(p.getUniqueId());
		Options opt = hash.getOptions();
		if(opt.getJumping()) {
			opt.setJumping(false);
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_OPT_JUMP_DISABLE.getMessage()));
			updateOptionsOnBungee(opt);
			return;
		}
		opt.setJumping(true);
		p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_OPT_JUMP_ENABLE.getMessage()));
		updateOptionsOnBungee(opt);
		return;
	}
	
	private void updateOptionsOnBungee(Options opt) {
		AsyncSQLQueueUpdater.addToQueue("insert into friends_options(uuid, offline,receivemsg,receiverequests,sorting,status,jumping) "
				+ "values ('" + opt.getUuid().toString() + "','" + (opt.isOffline() ? 1 : 0) + "','" + (opt.getMessages() ? 1 : opt.getFavMessages() ? 2 : 0) + "','" + (opt.getRequests() ? 1 : 0) + "',"
						+ "'" + opt.getSorting() + "','" + opt.getStatus() + "', '" + (opt.getJumping() ? 1 : 0) + "') on duplicate key update "
				+ "offline=values(offline),receivemsg=values(receivemsg),receiverequests=values(receiverequests),sorting=values(sorting),status=values(status),jumping=values(jumping);");
	}

}
