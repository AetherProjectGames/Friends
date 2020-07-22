package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Status_Command;
import de.HyChrod.Friends.Hashing.Options;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Friends.Utilities.Messages;
import net.wesjd.anvilgui.AnvilGUI;

public class OptionsInventoryListener implements Listener {
	
	private static HashMap<UUID, Options> currentlyEditing = new HashMap<>();
	
	public static void setEditing(UUID uuid, Options opt) {
		currentlyEditing.put(uuid, opt);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(!Configs.BUNGEEMODE.getBoolean()) return;
		if(e.getView() != null) {
			if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.OPTIONS_INVENTORY.getTitle((Player)e.getPlayer(),0))) {
				if(currentlyEditing.containsKey(e.getPlayer().getUniqueId())) {
					Options opt = currentlyEditing.get(e.getPlayer().getUniqueId());
					AsyncSQLQueueUpdater.addToQueue("insert into friends_options(uuid, offline,receivemsg,receiverequests,sorting,status,jumping,party) "
							+ "values ('" + e.getPlayer().getUniqueId().toString() + "','" + (opt.isOffline() ? 1 : 0) + "','" + (opt.getMessages() ? 1 : opt.getFavMessages() ? 2 : 0) + "','" + (opt.getRequests() ? 1 : 0) + "',"
									+ "'" + opt.getSorting() + "','" + opt.getStatus() + "', '" + (opt.getJumping() ? 1 : 0) + "', '" + (opt.getPartyInvites() ? 1 : 0) + "') on duplicate key update "
							+ "offline=values(offline),receivemsg=values(receivemsg),receiverequests=values(receiverequests),sorting=values(sorting),status=values(status),jumping=values(jumping),party=values(party);");
				}
				
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(currentlyEditing.containsKey(p.getUniqueId())) {
				if(e.getView() != null)
					if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.OPTIONS_INVENTORY.getTitle(p,0))) {
						e.setCancelled(true);
						if(e.getCurrentItem() != null)
							if(e.getCurrentItem().hasItemMeta())
								if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
									Options opt = currentlyEditing.get(p.getUniqueId());
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_BACK.getItem(p).getItemMeta().getDisplayName())) {
										InventoryBuilder.openFriendInventory(p, p.getUniqueId(), 0, false);
										return;
									}
									
									String offline = ChatColor.translateAlternateColorCodes('&', opt.isOffline() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
									String messages = ChatColor.translateAlternateColorCodes('&', opt.getMessages() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
									String requests = ChatColor.translateAlternateColorCodes('&', opt.getRequests() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
									String status = opt.getStatus() == null || opt.getStatus().length() < 1 ? Configs.INV_FRIENDS_NO_STATUS_REPLACEMENT.getText() : opt.getStatus();
									String jumping = ChatColor.translateAlternateColorCodes('&', opt.getJumping() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
									String party = ChatColor.translateAlternateColorCodes('&', opt.getPartyInvites() ? Configs.OPTIONS_ON.getText() : Configs.OPTIONS_OFF.getText());
									
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_MESSAGES.getItem(p).getItemMeta().getDisplayName()
											.replace("%OPTION_MESSAGES_STATUS%", messages))) {
										int msg = (opt.getMessages() ? 1 : opt.getFavMessages() ? 2 : 0) + 1;
										if(msg > 2) msg = 0;
										opt.setReceive_messages(msg);
										InventoryBuilder.openOptionsInventory(p, opt);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_REQUESTS.getItem(p).getItemMeta().getDisplayName()
											.replace("%OPTION_REQUESTS_STATUS%", requests))) {
										opt.setReceive_requests(opt.getRequests() ? false : true);
										InventoryBuilder.openOptionsInventory(p, opt);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_OFFLINEMODE.getItem(p).getItemMeta().getDisplayName()
											.replace("%OPTION_OFFLINEMODE_STATUS%", offline))) {
										opt.setOffline(opt.isOffline() ? false : true);
										InventoryBuilder.openOptionsInventory(p, opt);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_JUMP.getItem(p).getItemMeta().getDisplayName()
											.replace("%OPTION_JUMPING_STATUS%", jumping))) {
										opt.setJumping(opt.getJumping() ? false : true);
										InventoryBuilder.openOptionsInventory(p, opt);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_PARTY.getItem(p).getItemMeta().getDisplayName()
											.replace("%OPTION_PARTY_STATUS%", party))) {
										opt.setPartyInvites(opt.getPartyInvites() ? false : true);
										InventoryBuilder.openOptionsInventory(p, opt);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_OPTIONS_STATUS.getItem(p).getItemMeta().getDisplayName().replace("%STATUS%", status))) {
										if(!p.hasPermission("Friends.Commands.Status.Set") && !p.hasPermission("Friends.Commands.*")) {
											p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
											return;
										}
										
										ItemStack item = new ItemStack(ItemStacks.INV_OPTIONS_STATUS.getItem(p).getType());
										ItemMeta meta = item.getItemMeta();
										String current = (opt.getStatus() == null || opt.getStatus().length() < 1 ? Configs.INV_FRIENDS_NO_STATUS_REPLACEMENT.getText() : opt.getStatus());
										meta.setDisplayName(current.replace("§", "&"));
										item.setItemMeta(meta);
										
										float expt = p.getExp();
										int lvl = p.getLevel();
										new AnvilGUI.Builder().onComplete((BiFunction<Player, String, AnvilGUI.Response>)(player,text) -> {
											if(text.length() > Configs.STATUS_LENGHT.getNumber()) {
												p.sendMessage(Messages.CMD_STATUS_STATUS_LENGHT.getMessage(p).replace("%LIMIT%", String.valueOf(Configs.STATUS_LENGHT.getNumber())));
												return AnvilGUI.Response.close();
											}
											if(Configs.STATUS_FILTER.getBoolean()) {
												for(String phrases : Configs.getForbiddenPhrases())
													if(text.toLowerCase().contains(phrases.toLowerCase())) {
														p.sendMessage(Messages.CMD_STATUS_ABUSIVE_PHRASE.getMessage(p).replace("%PHRASE%", phrases));
														return AnvilGUI.Response.close();
													}
												
											}
											if(!Status_Command.canChangeStatus(p.getUniqueId())) {
												p.sendMessage(Messages.CMD_STATUS_CANT_CHANGE_YET.getMessage(p).replace("%REMAINING_TIME%", String.valueOf((
														Configs.STATUS_CHANGEDURATION.getNumber()-(System.currentTimeMillis()-Status_Command.lastChangedStatus.get(p.getUniqueId()))/1000))));
												return AnvilGUI.Response.close();
											}
											
											opt.setStatus(text);
											if(Configs.ALLOW_STATUS_COLOR.getBoolean()) text = ChatColor.translateAlternateColorCodes('&', text);
											p.sendMessage(Messages.CMD_STATUS_STATUS_SET.getMessage(p).replace("%STATUS%", text));
											if(Configs.BUNGEEMODE.getBoolean())
												AsyncSQLQueueUpdater.addToQueue("update friends_options set status='" + text + "' where uuid='" + p.getUniqueId().toString() + "'");
											
											if(p.getExp() != expt || p.getLevel() != lvl) {
												p.setExp(expt);
												p.setLevel(lvl);
											}
											return AnvilGUI.Response.close();
											
										}).title("§aStatus:").item(item).text(current).plugin(Friends.getInstance()).open(p);
										return;
									}
									
									String invName = "OptionsInventory";
									for(int customIndex = 0; customIndex < ItemStacks.getItemCount(invName); customIndex++)
										if(e.getCurrentItem().getItemMeta().getDisplayName().contentEquals(ItemStacks.getCutomItem(invName, customIndex, p).getItemMeta().getDisplayName())) {
											String cmd = ItemStacks.getCustomCommand(invName, customIndex);
											if(cmd.length() > 0) p.performCommand(cmd.replace("%NAME%", p.getName()));
											return;
										}
									
								}
					}
			}
		}
	}
	
}
