package de.HyChrod.Friends.Commands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.MSG_Command;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command {

	public ReplyCommand(String name) {
		super(name, "", new String[] {"r"});
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PLAYER.getMessage()));
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;
		if(MSG_Command.getReply(p.getUniqueId()) == null) {
			p.sendMessage(TextComponent.fromLegacyText(Messages.CMD_REPLY_NO_REPLY.getMessage()));
			return;
		}
		
		UUID uuid = MSG_Command.getReply(p.getUniqueId());
		String name = FriendHash.getName(uuid);
		
		String[] newArgs = new String[args.length+2];
		for(int i = 0; i < args.length; i++)
			newArgs[i+2] = args[i];
		newArgs[0] = "fill";
		newArgs[1] = name;
		new MSG_Command(Friends.getInstance(), p, newArgs);
		return;
	}

}
