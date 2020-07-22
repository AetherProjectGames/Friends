package de.HyChrod.Friends.Commands;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.MSG_Command;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MSGManager extends Command {

	public MSGManager(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PLAYER.getMessage()));
			return;
		}
		String[] arguments = new String[args.length+1];
		for(int i = 0; i < args.length; i++)
			arguments[i+1] = args[i];
		arguments[0] = "fill";
		
		new MSG_Command(Friends.getInstance(), ((ProxiedPlayer)sender), arguments);
		return;
	}

}
