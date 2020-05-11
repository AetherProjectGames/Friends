package de.HyChrod.Friends.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.MSG_Command;
import de.HyChrod.Friends.Utilities.Messages;

public class MSGManager extends BukkitCommand {

	public MSGManager(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.NO_PLAYER.getMessage(null));
			return false;
		}
		String[] arguments = new String[args.length+1];
		for(int i = 0; i < args.length; i++)
			arguments[i+1] = args[i];
		arguments[0] = "fill";
		
		new MSG_Command(Friends.getInstance(), ((Player)sender), arguments);
		return false;
	}

}
