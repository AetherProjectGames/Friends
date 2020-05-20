package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Party.Utilities.PConfigs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Deny_Command {

	public Deny_Command(Friends friends, Player p, String[] args) {
		if(args.length > 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party deny <Name>"));
			return;
		}
		String toDeny = null;
		UUID denyTo = null;
		if(args.length == 2) {
			toDeny = args[1];
			if(!FriendHash.isPlayerValid(toDeny)) {
				p.sendMessage(PMessages.CMD_DENY_NO_INVITE.getMessage(p).replace("%NAME%", toDeny));
				return;
			}
			denyTo = FriendHash.getUUIDFromName(toDeny);
			if(!Parties.hasInvite(p.getUniqueId(), denyTo)) {
				p.sendMessage(PMessages.CMD_DENY_NO_INVITE.getMessage(p).replace("%NAME%", toDeny));
				return;
			}
		}
		if(args.length == 1) {
			if(Parties.getInvites(p.getUniqueId()).isEmpty() || ((Parties.getInvites(p.getUniqueId()).getFirst().getTimestamp() + (PConfigs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000)) < System.currentTimeMillis())) {
				p.sendMessage(PMessages.CMD_DENY_NO_NEW_INVITE.getMessage(p));
				return;
			}
			toDeny = FriendHash.getName(Parties.getInvites(p.getUniqueId()).getFirst().getSender());
			denyTo = FriendHash.getUUIDFromName(toDeny);
		}
		
		Parties.removeInvite(p.getUniqueId(), denyTo);
		p.sendMessage(PMessages.CMD_DENY_DENY.getMessage(p).replace("%NAME%", toDeny));
		if(Bukkit.getPlayer(denyTo) != null)
			Bukkit.getPlayer(denyTo).sendMessage(PMessages.CMD_DENY_DENIED.getMessage(p).replace("%NAME%", p.getName()));
		return;
	}
	
}
