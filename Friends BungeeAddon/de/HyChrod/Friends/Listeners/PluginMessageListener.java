package de.HyChrod.Friends.Listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Party.Commands.SubCommands.Invite_Command;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageListener implements Listener {

	@EventHandler
	public void onMessage(PluginMessageEvent e) {
		if(e.getTag().equals("party:invite")) {
			ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
			DataInputStream in = new DataInputStream(stream);
			try {
				UUID inviter = UUID.fromString(in.readUTF());
				UUID invited = UUID.fromString(in.readUTF());
				new Invite_Command(Friends.getInstance(), BungeeCord.getInstance().getPlayer(inviter), new String[] {"invite",FriendHash.getName(invited)});
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
		if(e.getTag().equals("party:create")) {
			ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
			DataInputStream in = new DataInputStream(stream);
			try {
				UUID inviter = UUID.fromString(in.readUTF());
				if(BungeeCord.getInstance().getPlayer(inviter) == null) return;
				int id = in.readInt();
				AsyncSQLQueueUpdater.addToQueue("insert into party(id,prvt,server) values ('" + id + "','1','" + BungeeCord.getInstance().getPlayer(inviter).getServer().getInfo().getName() 
						+ "') on duplicate key update server=values(server)");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
		if(e.getTag().equals("friends:connect")) {
			
	        ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
	        DataInputStream in = new DataInputStream(stream);
	        try {
	        	
	        	UUID jumper = UUID.fromString(in.readUTF());
	        	UUID toJump = UUID.fromString(in.readUTF());
	        	
	        	ProxiedPlayer jump = BungeeCord.getInstance().getPlayer(jumper);
				String server = BungeeCord.getInstance().getPlayer(toJump).getServer().getInfo().getName();
				if(Configs.getForbiddenServers().contains(server)) {
					jump.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_SERVER_DISABLED.getMessage().replace("%SERVER%", server)));
					return;
				}
				jump.sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_JUMPTOFRIEND.getMessage().replace("%NAME%", BungeeCord.getInstance().getPlayer(toJump).getName())));
				jump.connect(BungeeCord.getInstance().getPlayer(toJump).getServer().getInfo());
				BungeeCord.getInstance().getPlayer(toJump).sendMessage(TextComponent.fromLegacyText(Messages.CMD_JUMP_JUMPTOYOU.getMessage().replace("%NAME%", jump.getName())));
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		return;
	}
	
}
