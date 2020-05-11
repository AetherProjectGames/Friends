package de.HyChrod.Friends.Commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.Messages;

public class CommandManager extends BukkitCommand {
	
	public CommandManager(String name) {
		super(name);
		this.description = "Manage all your friends! -> /friends help";
		this.usageMessage = "/friends";
		this.setAliases(new ArrayList<String>(FileManager.CONFIG.getConfig().getStringList("Friends.CommandAliases")));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args ) {
		if(args.length == 0) {
			if(Configs.GUI_WITH_COMMAND.getBoolean()) {
				if(sender instanceof Player) {
					Player p = (Player) sender;
					InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), true);
					return true;
				}
			}
			sender.sendMessage(Messages.CMD_UNKNOWN_COMMAND.getMessage(null));
			return false;
		}
		
		if(args[0].equalsIgnoreCase("version")) {
			if(!sender.hasPermission("Friends.Commands.Version")) {
				sender.sendMessage(Messages.NO_PERMISSIONS.getMessage(null));
				return false;
			}
			if(args.length != 1) {
				sender.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(null).replace("%USAGE%", "/friends version"));
				return false;
			}
			sender.sendMessage(Messages.CMD_VERSION.getMessage(null).replace("%VERSION%", Friends.getInstance().getDescription().getVersion() + (Friends.isUpdateNeeded() ? " §c(Outdated)" : "")));
			if(Friends.isUpdateNeeded())
				sender.sendMessage(Friends.getPrefix() + " §cPlease update to a newer version of friends!");
			return true;
		}
		if(args[0].equalsIgnoreCase("reload")) {
			if(!sender.hasPermission("Friends.Commands.Reload")) {
				sender.sendMessage(Messages.NO_PERMISSIONS.getMessage(null));
				return false;
			}
			if(args.length != 1) {
				sender.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(null).replace("%USAGE%", "/friends reload"));
			}
			Friends.reload();
			sender.sendMessage(Messages.CMD_RELOAD.getMessage(null));
			return true;
		}
		if(args[0].equalsIgnoreCase("help")) {
			if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("1"))) {
				sendHelp(sender, "1");
				return true;
			}
			sendHelp(sender, args[1]);
			return true;
		}
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.NO_PLAYER.getMessage(null));
			return false;
		}
		
		Player p = (Player) sender;
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
		        Constructor<?> construct = SubCommandSerializer.get(args[0]);
		        if (construct == null) {
		            sender.sendMessage(Messages.CMD_UNKNOWN_COMMAND.getMessage(p));
		            return;
		        }
		        try {
		            construct.newInstance(Friends.getInstance(), p, args);
		        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		            e.printStackTrace();
		        }
			}
		});
        return true;
	}
	
	private void sendHelp(CommandSender sender, String page) {
		FileConfiguration cfg = FileManager.MESSAGES.getConfig();
		if(cfg.get("Messages.Commands.HelpCommand.Page" + page) == null) {
			sender.sendMessage(Messages.CMD_HELP_UNKNOWNPAGE.getMessage(null).replace("%PAGE%", page));
			return;
		}
		
		for(String msg : cfg.getStringList("Messages.Commands.HelpCommand.Page" + page))
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg.replace("%PREFIX%", Friends.getPrefix())));
	}

}
 