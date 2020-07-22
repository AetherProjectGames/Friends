package de.HyChrod.Party.Commands.SubCommands;

import java.util.UUID;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Leave_Command {
	
	public Leave_Command(Friends friends, ProxiedPlayer p, String[] args) {
		if(!p.hasPermission("Party.Commands.Leave") && !p.hasPermission("Party.Commands.*")) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.NO_PERMISSIONS.getMessage()));
			return;
		}
		if(args.length != 1) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.WRONG_USAGE.getMessage().replace("%USAGE%", "/party leave")));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_LEAVE_NO_PARTY.getMessage()));
			return;
		}
		Parties party = Parties.getParty(p.getUniqueId());
		party.removeLeader(p.getUniqueId());
		party.removeParticipant(p.getUniqueId());
		
		AsyncSQLQueueUpdater.addToQueue("delete from party_players where uuid='" + p.getUniqueId().toString() + "'");
		AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + p.getUniqueId().toString() + "'");
		AsyncSQLQueueUpdater.addToQueue("delete from party_leaders where uuid='" + p.getUniqueId().toString() + "'");
		
		for(UUID member : party.getMembers())
			if(BungeeCord.getInstance().getPlayer(member) != null)
				BungeeCord.getInstance().getPlayer(member).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_LEAVE_MEMBER_LEAVE.getMessage().replace("%NAME%", p.getName())));
		p.sendMessage(TextComponent.fromLegacyText(PMessages.CMD_LEAVE_LEAVE.getMessage()));
		
		if(party.getMembers().isEmpty()) {
			AsyncSQLQueueUpdater.addToQueue("delete from party where id='" + party.getID() + "'");
			return;
		}
		if(party.getLeader().isEmpty()) {
			if(party.getMembers().isEmpty()) return;
			UUID newLeader = party.getMembers().getFirst();
			party.removeParticipant(newLeader);
			party.makeLeader(newLeader);
			
			AsyncSQLQueueUpdater.addToQueue("delete from party_members where uuid='" + newLeader.toString() + "'");
			AsyncSQLQueueUpdater.addToQueue("insert into party_leaders(id,uuid) values ('" + party.getID() + "','" + newLeader.toString() + "') on duplicate key update id=values(id)");
			
			if(BungeeCord.getInstance().getPlayer(newLeader) != null)
				BungeeCord.getInstance().getPlayer(newLeader).sendMessage(TextComponent.fromLegacyText(PMessages.CMD_PROMOTE_NEW_LEADER.getMessage()));
		}
	}

}
