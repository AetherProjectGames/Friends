package de.HyChrod.Friends.Commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.Messages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class CommandManager extends Command {

	public CommandManager(String name, String[] aliases) {
		super(name, "", aliases);
	}
	
	private void sendData(ProxiedPlayer player, String channel, String info) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(info);
		out.writeUTF(player.getUniqueId().toString());
		player.getServer().getInfo().sendData(channel, out.toByteArray());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof ProxiedPlayer)) {
				sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PLAYER.getMessage()));
				return;
			}
			sendData(((ProxiedPlayer)sender), "friends:openinv", ((ProxiedPlayer)sender).getUniqueId().toString());
			return;
		}
		if(args[0].equalsIgnoreCase("version")) {
			if(!sender.hasPermission("Friends.Commands.Version") && !sender.hasPermission("Friends.Commands.*")) {
				sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
				return;
			}
			if(args.length != 1) {
				sender.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends version")));
				return;
			}
			sender.sendMessage(TextComponent.fromLegacyText(Messages.CMD_VERSION.getMessage().replace("%VERSION%", Friends.getInstance().getDescription().getVersion())));
			if(sender instanceof ProxiedPlayer)
				sendData(((ProxiedPlayer)sender), "friends:version", ((ProxiedPlayer)sender).getUniqueId().toString());
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
		
		if(args[0].equalsIgnoreCase("reload")) {
			if(!sender.hasPermission("Friends.Commands.Reload") && !sender.hasPermission("Friends.Commands.*")) {
				sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS.getMessage()));
				return;
			}
			if(args.length != 1) {
				sender.sendMessage(TextComponent.fromLegacyText(Messages.CMD_WRONG_USAGE.getMessage().replace("%USAGE%", "/friends reload")));
				return;
			}
			
			for(ServerInfo s : BungeeCord.getInstance().getServers().values()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("reload");
				s.sendData("friends:reload", out.toByteArray());
			}
			Friends.reload();
			sender.sendMessage(TextComponent.fromLegacyText(Messages.CMD_RELOAD.getMessage()));
			return;
		}
		
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			
			BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
				
				@Override
				public void run() {
			        Constructor<?> construct = SubCommandSerializer.get(args[0]);
			        if (construct == null) {
			            p		.sendMessage(TextComponent.fromLegacyText(Messages.CMD_UNKNOWN_COMMAND.getMessage()));
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
		sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PLAYER.getMessage()));
		return;
	}
	
	private void sendHelp(CommandSender sender, String page) {
		Configuration cfg = FileManager.MESSAGES.getConfig();
		if(cfg.get("Messages.Commands.HelpCommand.Page" + page) == null) {
			sender.sendMessage(TextComponent.fromLegacyText(Messages.CMD_HELP_UNKNOWNPAGE.getMessage().replace("%PAGE%", page)));
			return;
		}
		
		for(String msg : cfg.getStringList("Messages.Commands.HelpCommand.Page" + page))
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg.replace("%PREFIX%", Friends.getPrefix()))));
	}

}
