package de.HyChrod.Friends.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.Messages;

public class FriendsGUICommand implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 0) {
			sender.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(null).replace("%USAGE%", "/friendsgui"));
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.NO_PLAYER.getMessage(null));
			return false;
		}
		
		Player player = (Player) sender;
		if(Configs.getForbiddenWorlds().contains(player.getWorld().getName())) return false;
		FriendInventoryListener.setPositions(player.getUniqueId(), InventoryBuilder.openFriendInventory(player, player.getUniqueId(), FriendInventoryListener.getPage(player.getUniqueId()), true));
		return false;
	}

}
