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

public class Deny_Command {

	public Deny_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Deny") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length > 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party deny <Name>")));
			return;
		}
		String toDeny = null;
		UUID denyTo = null;
		if(args.length == 2) {
			toDeny = args[1];
			if(!FriendHash.isPlayerValid(toDeny)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DENY_NO_INVITE.getMessage().replace("%NAME%", toDeny)));
				return;
			}
			denyTo = FriendHash.getUUIDFromName(toDeny);
			if(!Parties.hasInvite(p.getUniqueId(), denyTo)) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DENY_NO_INVITE.getMessage().replace("%NAME%", toDeny)));
				return;
			}
		}
		if(args.length == 1) {
			if(Parties.getInvites(p.getUniqueId()).isEmpty() || ((Parties.getInvites(p.getUniqueId()).getFirst().getTimestamp() + (Configs.PARTY_INVITE_EXPIRE_TIME.getNumber()*1000)) < System.currentTimeMillis())) {
				p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DENY_NO_NEW_INVITE.getMessage()));
				return;
			}
			toDeny = FriendHash.getName(Parties.getInvites(p.getUniqueId()).getFirst().getSender());
			denyTo = FriendHash.getUUIDFromName(toDeny);
		}
		
		Parties.removeInvite(p.getUniqueId(), denyTo);
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DENY_DENY.getMessage().replace("%NAME%", toDeny)));
		if(BungeeCord.getInstance().getPlayer(denyTo) != null)
			BungeeCord.getInstance().getPlayer(denyTo).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_DENY_DENIED.getMessage().replace("%NAME%", p.getName())));
		return;
	}
	
}
