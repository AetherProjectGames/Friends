package de.HyChrod.Party.Commands.SubCommands;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class Create_Command {

	public Create_Command(Friends friends, Player p, String[] args) {
		if(args.length != 1) {
			p.sendMessage(PMessages.WRONG_USAGE.getMessage(p).replace("%USAGE%", "/party create"));
			return;
		}
		if(Parties.getParty(p.getUniqueId()) == null) {
			new Parties(p.getUniqueId());
			p.sendMessage(PMessages.CMD_CREATE_CREATE.getMessage(p));
			return;
		}
		p.sendMessage(PMessages.CMD_CREATE_IN_PARTY.getMessage(p));
		return;
	}
	
}
