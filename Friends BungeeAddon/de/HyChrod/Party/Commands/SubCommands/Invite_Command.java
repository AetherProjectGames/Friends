package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Invite_Command {
	
	public Invite_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Invite") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party invite <Name>")));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) != null && !Parties.getParty(p.getUniqueId()).getLeader().contains(p.getUniqueId())) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_NOT_LEADER.getMessage()));
			return;
		}
		
		String playerToAdd = args[1];
		if(BungeeCord.getInstance().getPlayer(playerToAdd) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.INVALID_PLAYER.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		
		UUID uuid = FriendHash.getUUIDFromName(playerToAdd);
		FriendHash hash = FriendHash.getFriendHash(uuid);
		if(!FriendHash.isOnline(uuid)) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_OFFLINE.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		if(Configs.PARTY_INVITE_ONLY_FRIENDS.getBoolean()) {
			if(hash.getFriendship(p.getUniqueId()) == null) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_NOFRIEND.getMessage().replace("%NAME%", playerToAdd)));
				return;
			}
		}
		if(!hash.getOptions().getPartyInvites()) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_NOINVITES.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		if(Parties.getParty(uuid) != null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_ALREADY_IN_PARTY.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		
		Parties prty = Parties.getParty(p.getUniqueId()) == null ? new Parties(p.getUniqueId()) : Parties.getParty(p.getUniqueId());
		if(Parties.hasInvite(uuid, prty)) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_ALREADY_INVITED.getMessage().replace("%NAME%", playerToAdd)));
			return;
		}
		
		Parties.invitePlayer(uuid, p.getUniqueId(), prty);
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_INVITE_SEND.getMessage().replace("%NAME%", playerToAdd)));
		return;
	}

}
