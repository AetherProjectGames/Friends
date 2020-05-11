package de.HyChrod.Friends.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.Messages;

public class PluginMessageListeners implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player sender, byte[] data) {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
		if(channel.equalsIgnoreCase("friends:version")) {
			UUID uuid = UUID.fromString(in.readUTF());
			if(Bukkit.getPlayer(uuid) == null) return;
			Bukkit.getPlayer(uuid).sendMessage(Messages.CMD_VERSION.getMessage(Bukkit.getPlayer(uuid)).replace("%VERSION%", Friends.getInstance().getDescription().getVersion() + (Friends.isUpdateNeeded() ? " §c(Outdated)" : "")));
			return;
		}
		if(channel.startsWith("friends:openinv")) {
			if(!Configs.GUI_WITH_COMMAND.getBoolean()) {
				sender.sendMessage(Messages.CMD_UNKNOWN_COMMAND.getMessage(null));
				return;
			}
			UUID uuid = UUID.fromString(in.readUTF());
			if(Bukkit.getPlayer(uuid) == null) return;
			InventoryBuilder.openFriendInventory(Bukkit.getPlayer(uuid), uuid, FriendInventoryListener.getPage(Bukkit.getPlayer(uuid).getUniqueId()), true);
		}
	}
	
}
