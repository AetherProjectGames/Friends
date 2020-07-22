package de.HyChrod.Party.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Listeners.FriendInventoryListener;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Party.Utilities.PInventoryBuilder;
import de.HyChrod.Party.Utilities.PItemStacks;
import de.HyChrod.Party.Utilities.Parties;

public class CreateInventoryListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(Parties.getParty(p.getUniqueId()) != null) return;
			if(e.getView() != null && e.getView().getTitle().equals(PInventoryBuilder.PARTY_CREATE.getTitle(p).replace("%NAME%", p.getName()))) {
				e.setCancelled(true);
				if(e.getCurrentItem() != null)
					if(e.getCurrentItem().hasItemMeta())
						if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
							if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.INV_CREATE_BACK.getItem(p).getItemMeta().getDisplayName())) {
								FriendInventoryListener.setPositions(p.getUniqueId(), InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), true));
								return;
							}
							if(e.getCurrentItem().getItemMeta().getDisplayName().equals(PItemStacks.INV_CREATE_CREATE.getItem(p).getItemMeta().getDisplayName())) {
								Parties party = new Parties(p.getUniqueId());
								if(Configs.BUNGEEMODE.getBoolean()) {
									ByteArrayDataOutput out = ByteStreams.newDataOutput();
									out.writeUTF(p.getUniqueId().toString());
									out.writeInt(party.getID());
									p.sendPluginMessage(Friends.getInstance(), "party:create", out.toByteArray());		
								}
								
								PartyInventoryListener.setPositions(p.getUniqueId(), PInventoryBuilder.openPartyInventory(p, party));
								return;
							}
						}
			}
		}
	}
	
}
