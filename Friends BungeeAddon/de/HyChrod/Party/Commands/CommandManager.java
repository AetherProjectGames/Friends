package de.HyChrod.Party.Commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.Utilities.PMessages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class CommandManager extends Command {

	public CommandManager(String name, String[] aliases) {
		super(name, "", aliases);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof ProxiedPlayer)) {
				sender.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PLAYER.getMessage()));
				return;
			}
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if(p.hasPermission("Party.Commands.OpenGUI") || p.hasPermission("Party.Commands.*")) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF(p.getUniqueId().toString());
				p.getServer().getInfo().sendData("party:openinv", out.toByteArray());
				return;
			}
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		
		if(args[0].equalsIgnoreCase("version")) {
			if(!sender.hasPermission("Party.Commands.Version") && !sender.hasPermission("Party.Commands.*")) {
				sender.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
				return;
			}
			sender.sendMessage(TextComponent.fromLegacyText(PMessages.VERSION.getMessage().replace("%VERSION%", Friends.getInstance().getDescription().getVersion())));
			return;
		}
		if(args[0].equalsIgnoreCase("help")) {
			if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("1"))) {
				sendHelp(sender, "1");
				return;
			}
			sendHelp(sender, args[1]);
			return;
		}
		
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PLAYER.getMessage()));
			return;
		}
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		
		BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
		        Constructor<?> construct = SubCommandSerializer.get(args[0]);
		        if (construct == null) {
		            p.sendMessage(TextComponent.fromLegacyText(PMessages.UNKNOWN_COMMAND.getMessage()));
		            return;
		        }
		        try {
		            construct.newInstance(Friends.getInstance(), p, args);
		        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		            e.printStackTrace();
		        }
			}
		});
		return;
	}
	
	private void sendHelp(CommandSender sender, String page) {
		Configuration cfg = FileManager.MESSAGES.getConfig();
		if(cfg.get("PartyMessages.Commands.HelpCommand.Page" + page) == null) {
			sender.sendMessage(TextComponent.fromLegacyText(Messages.CMD_HELP_UNKNOWNPAGE.getMessage().replace("%PAGE%", page)));
			return;
		}
		
		for(String msg : cfg.getStringList("PartyMessages.Commands.HelpCommand.Page" + page))
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg.replace("%PREFIX%", Party.getPrefix()))));
	}

}
