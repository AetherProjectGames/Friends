package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Join_Command {

	public Join_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Join") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 2) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party join <Name>")));
			return;
		}
		
		String toJoin = args[1];
		if(!FriendHash.isPlayerValid(toJoin)) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_JOIN_NO_PARTY.getMessage().replace("%NAME%", toJoin)));
			return;
		}
		
		UUID join = FriendHash.getUUIDFromName(toJoin);
		if(Parties.getParty(join) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_JOIN_NO_PARTY.getMessage().replace("%NAME%", toJoin)));
			return;
		}
		
		Parties party = Parties.getParty(join);
		if(!party.isPublic()) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_JOIN_PRIVATE.getMessage().replace("%NAME%", toJoin)));
			return;
		}
		
		if(party.getMembers().size() >= Configs.PARTY_MAX_SIZE.getNumber()) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_JOIN_PARTY_LIMIT.getMessage()));
			return;
		}
		
		party.addParticipant(p.getUniqueId());
		Parties.removeInvite(p.getUniqueId(), join);
		
		AsyncSQLQueueUpdater.addToQueue("insert into party_players(uuid,id) values ('" + p.getUniqueId().toString() + "','" + party.getID() + "') on duplicate key update id=values(id)");
		AsyncSQLQueueUpdater.addToQueue("insert into party_members(id,uuid) values ('" + party.getID() + "','" + p.getUniqueId().toString() + "') on duplicate key update id=values(id)");
		
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_PARTY_JOIN.getMessage().replace("%NAME%", toJoin)));
		for(UUID member : party.getMembers())
			if(BungeeCord.getInstance().getPlayer(member) != null && !member.equals(p.getUniqueId()))
				BungeeCord.getInstance().getPlayer(member).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_ACCEPT_NEW_MEMBER.getMessage().replace("%NAME%", p.getName())));
		return;
		
	}
	
}
