package de.HyChrod.Friends.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.Blockplayer;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.Hashing.Request;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.Messages;

public class Add_Command {

	public Add_Command(Friends friends, Player p, String[] args) {
		if(!p.hasPermission("Friends.Commands.Add")) {
			p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(args.length == 1) {
			p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends add <Name> (Message)"));
			return;
		}
		
		String playerToAdd = args[1];
		if(playerToAdd.equals(p.getName())) {
			p.sendMessage(Messages.CMD_ADD_SEND_SELF.getMessage(p));
			return;
		}
		
		if(!FriendHash.isPlayerValid(playerToAdd)) {
			p.sendMessage(Messages.PLAYER_DOES_NOT_EXIST.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		
		UUID toAdd = FriendHash.getUUIDFromName(playerToAdd);
		FriendHash pHash = FriendHash.getFriendHash(p.getUniqueId());
		for(Blockplayer bl : FriendHash.getBlocked(toAdd))
			if(bl.getBlocked().equals(p.getUniqueId())) {
				p.sendMessage(Messages.CMD_ADD_BLOCKED.getMessage(p).replace("%NAME%", playerToAdd));
				return;
			}
		
		for(Blockplayer bl : FriendHash.getBlocked(p.getUniqueId()))
			if(bl.getBlocked().equals(toAdd)) {
				p.sendMessage(Messages.CMD_ADD_SELF_BLOCKED.getMessage(p).replace("%NAME%", playerToAdd));
				return;
			}
		
		String msg = "";
		if(args.length > 2) {
			if(!Configs.ADDMESSAGE_ENABLE.getBoolean()) {
				p.sendMessage(Messages.CMD_WRONG_USAGE.getMessage(p).replace("%USAGE%", "/friends add <Name>"));
				return;
			}
			
			for(int arg = 2; arg < args.length; arg++)
				msg = msg + " " + args[arg];
			msg = msg.substring(1);
			
			if(msg.length() > Configs.ADDMESSAGE_CHARLIMIT.getNumber()) {
				p.sendMessage(Messages.CMD_ADD_MESSAGE_CHAR_LIMIT.getMessage(p).replace("%LIMIT%", String.valueOf(Configs.ADDMESSAGE_CHARLIMIT.getNumber())));
				return;
			}
		}
		
		for(Friendship fs : pHash.getFriendsNew())
			if(fs.getFriend().equals(toAdd)) {
				p.sendMessage(Messages.CMD_ADD_ALREADY_FRIENDS.getMessage(p).replace("%NAME%", playerToAdd));
				return;
			}
		
		
		for(Request rs : FriendHash.getRequests(toAdd))
			if(rs.getPlayerToAdd().equals(p.getUniqueId())) {
				p.sendMessage(Messages.CMD_ADD_ALREADY_REQUESTED.getMessage(p).replace("%NAME%", playerToAdd));
				return;
			}
		
		if(!FriendHash.getOptions(toAdd).getRequests()) {
			p.sendMessage(Messages.CMD_ADD_NO_REQUEST_WANTED.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		pHash.sendRequest(toAdd, msg);
		p.sendMessage(Messages.CMD_ADD_REQUEST_SEND.getMessage(p).replace("%NAME%", playerToAdd));
		if(Configs.ADDMESSAGE_ENABLE.getBoolean() && msg.length() > 0)
			p.sendMessage(Messages.CMD_ADD_REQUEST_MESSAGE_SEND.getMessage(p).replace("%MESSAGE%", msg));
		return;
		
	}
	
}
