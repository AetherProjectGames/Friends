package de.HyChrod.Party.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Party.Commands.SubCommands.Demote_Command;
import de.HyChrod.Party.Commands.SubCommands.Kick_Command;
import de.HyChrod.Party.Commands.SubCommands.Promote_Command;
import de.HyChrod.Party.Utilities.PInventoryBuilder;
import de.HyChrod.Party.Utilities.PItemStacks;
import de.HyChrod.Party.Utilities.PMessages;
import de.HyChrod.Party.Utilities.Parties;

public class PartyEditInventoryListener implements Listener {

	private static HashMap<UUID, UUID> editing = new HashMap<>();
	
	public static void setEdit(UUID uuid, UUID toEdit) {
		editing.put(uuid, toEdit);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(editing.containsKey(p.getUniqueId())) {
				String name = FriendHash.getName(editing.get(p.getUniqueId()));
				if(e.getView() != null && e.getView().getTitle().equals(PInventoryBuilder.PARTY_EDIT_MEMBER.getTitle(p).replace("%NAME%", name))) {
					e.setCancelled(true);
					
					if(e.getCurrentItem() != null)
						if(e.getCurrentItem().hasItemMeta())
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								if(Parties.getParty(p.getUniqueId()) == null) {
									FriendInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), true));
									p.sendMessage(PMessages.CMD_KICK_NO_PARTY.getMessage(p));
									return;
								}
								
								Parties party = Parties.getParty(p.getUniqueId());
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.INV_EDIT_BACK.getItem(p).getItemMeta().getDisplayName())) {
									PartyInventoryListener.setPositions(p.getUniqueId(), PInventoryBuilder.openPartyInventory(p, party));
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.replace(PItemStacks.INV_EDIT_PROMOTE.getItem(p), "%NAME%", name).getItemMeta().getDisplayName())) {
									new Promote_Command(Friends.getInstance(), p, new String[] {"promote",name});
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.replace(PItemStacks.INV_EDIT_DEMOTE.getItem(p), "%NAME%", name).getItemMeta().getDisplayName())) {
									new Demote_Command(Friends.getInstance(), p, new String[] {"demote",name});
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.replace(PItemStacks.INV_EDIT_REMOVE.getItem(p), "%NAME%", name).getItemMeta().getDisplayName())) {
									new Kick_Command(Friends.getInstance(), p, new String[] {"kick",name});
									party.removeLeader(editing.get(p.getUniqueId()));
									party.removeParticipant(editing.get(p.getUniqueId()));
									if(name.equalsIgnoreCase(p.getName())) PInventoryBuilder.openCreateInventory(p);
									else PartyInventoryListener.setPositions(p.getUniqueId(), PInventoryBuilder.openPartyInventory(p, party));
									return;
								}
							}
				}
			}
		}
	}
	
}
