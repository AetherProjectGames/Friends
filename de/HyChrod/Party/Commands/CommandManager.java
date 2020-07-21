package de.HyChrod.Party.Commands;

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
import de.HyChrod.Friends.Utilities.FileManager;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Party.Party;
import de.HyChrod.Party.Listeners.PartyInventoryListener;
import de.HyChrod.Party.Utilities.PInventoryBuilder;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class CommandManager extends BukkitCommand {

	public CommandManager(String name) {
		super(name);
		this.description = "Party! -> /party help";
		this.usageMessage = "/party";
		this.setAliases(new ArrayList<String>(FileManager.PARTY.getConfig().getStringList("Party.CommandAliases")));
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args ) {
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(PMessages.NO_PLAYER.getMessage(null));
				return false;
			}
			Player p = (Player) sender;
			if(p.hasPermission("Party.Commands.OpenGUI") || p.hasPermission("Party.Commands.*")) {
				if(Parties.getParty(p.getUniqueId()) == null) PInventoryBuilder.openCreateInventory(p);
				else PartyInventoryListener.setPositions(p.getUniqueId(), PInventoryBuilder.openPartyInventory(p, Parties.getParty(p.getUniqueId())));
				return true;
			}
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return false;
		}
		
		if(args[0].equalsIgnoreCase("version")) {
			if(!sender.hasPermission("Party.Commands.Version") && !sender.hasPermission("Party.Commands.*")) {
				sender.sendMessage(PMessages.NO_PERMISSIONS.getMessage(null));
				return false;
			}
			sender.sendMessage(PMessages.VERSION.getMessage(null).replace("%VERSION%", Friends.getInstance().getDescription().getVersion()));
			return false;
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
			sender.sendMessage(PMessages.NO_PLAYER.getMessage(null));
			return false;
		}
		
		Player p = (Player) sender;
		
		Bukkit.getScheduler().runTaskAsynchronously(Friends.getInstance(), new Runnable() {
			
			@Override
			public void run() {
		        Constructor<?> construct = SubCommandSerializer.get(args[0]);
		        if (construct == null) {
		            p.sendMessage(PMessages.UNKNOWN_COMMAND.getMessage(null));
		            return;
		        }
		        try {
		            construct.newInstance(Friends.getInstance(), p, args);
		        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		            e.printStackTrace();
		        }
			}
		});
		return false;
	}
	
	private void sendHelp(CommandSender sender, String page) {
		FileConfiguration cfg = FileManager.MESSAGES.getConfig();
		if(cfg.get("PartyMessages.Commands.HelpCommand.Page" + page) == null) {
			sender.sendMessage(Messages.CMD_HELP_UNKNOWNPAGE.getMessage(null).replace("%PAGE%", page));
			return;
		}
		
		for(String msg : cfg.getStringList("PartyMessages.Commands.HelpCommand.Page" + page))
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg.replace("%PREFIX%", Party.getPrefix())));
	}

}
