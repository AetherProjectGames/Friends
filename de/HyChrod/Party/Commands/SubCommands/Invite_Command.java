package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Party.Utilities.PConfigs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Invite_Command {
	
	public Invite_Command(Friends friends, Player p, String[] args) {
		if(args.length != 2) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party invite <Name>"));
			return;
		}
		if(!p.hasPermission("Party.Commands.Invite")) {
			p.sendMessage(PMessages.NO_PERMISSIONS.getMessage(p));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) != null && !Parties.getParty(p.getUniqueId()).getLeader().contains(p.getUniqueId())) {
			p.sendMessage(PMessages.CMD_INVITE_NOT_LEADER.getMessage(p));
			return;
		}
		
		String playerToAdd = args[1];
		if(Bukkit.getPlayer(playerToAdd) == null) {
			p.sendMessage(PMessages.INVALID_PLAYER.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		
		UUID uuid = FriendHash.getUUIDFromName(playerToAdd);
		FriendHash hash = FriendHash.getFriendHash(uuid);
		if(!FriendHash.isOnline(uuid)) {
			p.sendMessage(PMessages.CMD_INVITE_OFFLINE.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		if(PConfigs.PARTY_INVITE_ONLY_FRIENDS.getBoolean()) {
			if(hash.getFriendship(p.getUniqueId()) == null) {
				p.sendMessage(PMessages.CMD_INVITE_NOFRIEND.getMessage(p).replace("%NAME%", playerToAdd));
				return;
			}
		}
		if(!hash.getOptions().getPartyInvites()) {
			p.sendMessage(PMessages.CMD_INVITE_NOINVITES.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		if(Parties.getParty(uuid) != null) {
			p.sendMessage(PMessages.CMD_INVITE_ALREADY_IN_PARTY.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		
		Parties prty = Parties.getParty(p.getUniqueId()) == null ? new Parties(p.getUniqueId()) : Parties.getParty(p.getUniqueId());
		if(Parties.hasInvite(uuid, prty)) {
			p.sendMessage(PMessages.CMD_INVITE_ALREADY_INVITED.getMessage(p).replace("%NAME%", playerToAdd));
			return;
		}
		
		Parties.invitePlayer(uuid, p.getUniqueId(), prty);
		p.sendMessage(PMessages.CMD_INVITE_SEND.getMessage(p).replace("%NAME%", playerToAdd));
		return;
	}

}
