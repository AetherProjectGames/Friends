package de.HyChrod.Party.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Party.Commands.SubCommands.Leave_Command;
import de.HyChrod.Party.Utilities.PConfigs;
import de.HyChrod.Party.Utilities.PInventoryBuilder;
import de.HyChrod.Party.Utilities.PItemStacks;
import de.HyChrod.Party.Utilities.Parties;

public class PartyInventoryListener implements Listener {

	private static HashMap<UUID, HashMap<String, UUID>> cachedPositions = new HashMap<>();
	
	public static void setPositions(UUID uuid, HashMap<String, UUID> positions) {
		cachedPositions.put(uuid, positions);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(Parties.getParty(p.getUniqueId()) != null) {
				if(e.getView() != null && e.getView().getTitle().equals(PInventoryBuilder.PARTY_PARTY.getTitle(p).replace("%NAME%", p.getName()))) {
					e.setCancelled(true);
					if(e.getCurrentItem() != null)
						if(e.getCurrentItem().hasItemMeta())
							if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
								
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.INV_PARTY_BACK.getItem(p).getItemMeta().getDisplayName())) {
									FriendInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), false));
									return;
								}
								
								Parties party = Parties.getParty(p.getUniqueId());
								String visibility = party.isPublic() ? PConfigs.PARTY_VISIBILITY_PUBLIC.getColoredText() : PConfigs.PARTY_VISIBILITY_PRIVATE.getColoredText();
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.replace(PItemStacks.INV_PARTY_VISIBILITY.getItem(p), "%PARTY_STATUS%", visibility)
										.getItemMeta().getDisplayName())) {
									party.setPublic(party.isPublic() ? false : true);
									if(Configs.BUNGEEMODE.getBoolean()) 
										AsyncSQLQueueUpdater.addToQueue("update party set prvt='" + (party.isPublic() ? 0 : 1) + "' where id='" + party.getID() + "'");
									setPositions(p.getUniqueId(), PInventoryBuilder.openPartyInventory(p, party));
									return;
								}
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.INV_PARTY_LEAVE.getItem(p).getItemMeta().getDisplayName())) {
									new Leave_Command(Friends.getInstance(), p, new String[] {"leave"});
									PInventoryBuilder.openCreateInventory(p);
									return;
								}
								
								if(!party.isLeader(p.getUniqueId())) return;
								HashMap<String, UUID> positions = cachedPositions.get(p.getUniqueId());
								for(String identifies : positions.keySet()) {
									if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith(identifies)) {
										PartyEditInventoryListener.setEdit(p.getUniqueId(), positions.get(identifies));
										PInventoryBuilder.openEditInventory(p, positions.get(identifies));
										return;
									}
								}
								
							}
				}
			}
		}
	}
	
}
