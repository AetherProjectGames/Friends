package de.HyChrod.Friends.Commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.MSG_Command;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Messages;

public class ReplyCommand extends BukkitCommand {

	public ReplyCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String cmd, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.NO_PLAYER.getMessage(null));
			return false;
		}
		Player p = (Player) sender;
		if(MSG_Command.getReply(p.getUniqueId()) == null) {
			p.sendMessage(Messages.CMD_REPLY_NO_REPLY.getMessage(p));
			return false;
		}
		
		UUID uuid = MSG_Command.getReply(p.getUniqueId());
		String name = FriendHash.getName(uuid);
		
		String[] newArgs = new String[args.length+1];
		for(int i = 0; i < args.length; i++)
			newArgs[i+1] = args[i];
		newArgs[0] = name;
		new MSG_Command(Friends.getInstance(), p, newArgs);
		return false;
	}

}
