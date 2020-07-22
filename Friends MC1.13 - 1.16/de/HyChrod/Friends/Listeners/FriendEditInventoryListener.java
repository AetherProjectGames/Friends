package de.HyChrod.Friends.Listeners;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.HyChrod.Friends.Friends;
import de.HyChrod.Friends.Commands.SubCommands.Jump_Command;
import de.HyChrod.Friends.Commands.SubCommands.Remove_Command;
import de.HyChrod.Friends.Hashing.FriendHash;
import de.HyChrod.Friends.Hashing.Friendship;
import de.HyChrod.Friends.SQL.AsyncSQLQueueUpdater;
import de.HyChrod.Friends.Utilities.Configs;
import de.HyChrod.Friends.Utilities.InventoryBuilder;
import de.HyChrod.Friends.Utilities.ItemStacks;
import de.HyChrod.Friends.Utilities.Messages;
import de.HyChrod.Party.Commands.SubCommands.Invite_Command;
import net.wesjd.anvilgui.AnvilGUI;

public class FriendEditInventoryListener implements Listener {
	
	private static HashMap<UUID, Friendship> currentlyEditing = new HashMap<>();
	
	public static void setEditing(UUID uuid, Friendship fs) {
		currentlyEditing.put(uuid, fs);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(!Configs.BUNGEEMODE.getBoolean()) return;
		Player p = (Player) e.getPlayer();
		if(currentlyEditing.containsKey(p.getUniqueId())) {
			Friendship fs = currentlyEditing.get(p.getUniqueId());
			if(e.getView() != null && e.getView().getTitle() != null)
				if(e.getView().getTitle().equals(InventoryBuilder.FRIENDEDIT_INVENTORY.getTitle(p,0).replace("%NAME%", FriendHash.getName(fs.getFriend())))) {
					AsyncSQLQueueUpdater.addToQueue("update friends_frienddata set favorite='" + (fs.getFavorite() ? 1 : 0) + "',cansendmessages='" + (fs.getCanSendMessages() ? 1 : 0) + "' where uuid='" + p.getUniqueId().toString() + "' and uuid2='" + fs.getFriend().toString() + "';");
				}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(currentlyEditing.containsKey(p.getUniqueId())) {
				Friendship fs = currentlyEditing.get(p.getUniqueId());
				if(e.getView() != null)
					if(e.getView().getTitle() != null && e.getView().getTitle().equals(InventoryBuilder.FRIENDEDIT_INVENTORY.getTitle(p,0).replace("%NAME%", FriendHash.getName(fs.getFriend())))) {
						e.setCancelled(true);
						
						OfflinePlayer inEdit = Bukkit.getOfflinePlayer(fs.getFriend());
						if(e.getCurrentItem() != null)
							if(e.getCurrentItem().hasItemMeta())
								if(e.getCurrentItem().getItemMeta().hasDisplayName()) {
									String orig_name = FriendHash.getName(fs.getFriend());
									String name = orig_name;
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_BACK.getItem(inEdit).getItemMeta().getDisplayName())) {
										InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), false);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_REMOVE.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										new Remove_Command(Friends.getInstance(), p, new String[] {"remove",orig_name});
										InventoryBuilder.openFriendInventory(p, p.getUniqueId(), FriendInventoryListener.getPage(p.getUniqueId()), false);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_NICKNAME.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										if(!p.hasPermission("Friends.Commands.Nickname") && !p.hasPermission("Friends.Commands.*")) {
											p.sendMessage(Messages.NO_PERMISSIONS.getMessage(p));
											return;
										}
										
										ItemStack item = new ItemStack(ItemStacks.INV_OPTIONS_STATUS.getItem(p).getType());
										ItemMeta meta = item.getItemMeta();
										String current = (!fs.hasNickname() ? Configs.ITEM_FRIEND_NO_NICK_REPLACEMENT.getText() : fs.getNickname());
										meta.setDisplayName(current);
										item.setItemMeta(meta);
										
										float expt = p.getExp();
										int lvl = p.getLevel();
										new AnvilGUI.Builder().onComplete((BiFunction<Player, String, AnvilGUI.Response>)(player,text) -> {
											if(Configs.NICK_CHECK_FOR_ABUSIVE_WORDS.getBoolean()) {
												for(String phrases : Configs.getForbiddenPhrases())
													if(text.toLowerCase().contains(phrases.toLowerCase())) {
														p.sendMessage(Messages.CMD_NICKNAME_ABUSIVE_PHRASE.getMessage(p).replace("%PHRASE%", phrases));
														return AnvilGUI.Response.close();
													}
												
											}
											
											fs.setNickname(text);
											p.sendMessage(Messages.CMD_NICKNAME_SET_NICK.getMessage(p).replace("%NAME%", orig_name).replace("%NICKNAME%", text));
											if(Configs.BUNGEEMODE.getBoolean())
												AsyncSQLQueueUpdater.addToQueue("update friends_frienddata set nickname='" + text + "' where uuid='" + p.getUniqueId().toString() + "' and uuid2 = '" + fs.getFriend().toString() + "'");
											
											if(p.getExp() != expt || p.getLevel() != lvl) {
												p.setExp(expt);
												p.setLevel(lvl);
											}
											InventoryBuilder.openFriendEditInventory(p, fs);
											return AnvilGUI.Response.close();
											
										}).title("§aNickname:").item(item).text(current).plugin(Friends.getInstance()).open(p);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_PARTY.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										if(Configs.BUNGEEMODE.getBoolean()) {
											ByteArrayDataOutput out = ByteStreams.newDataOutput();
											out.writeUTF(p.getUniqueId().toString());
											out.writeUTF(fs.getFriend().toString());
											p.sendPluginMessage(Friends.getInstance(), "party:invite", out.toByteArray());		
											return;
										}
										new Invite_Command(Friends.getInstance(), p, new String[] {"invite",name});
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_CANSENDMESSAGES.getItem(inEdit).getItemMeta().getDisplayName()
											.replace("%NAME%", name))) {
										fs.setCanSendMessages(fs.getCanSendMessages() ? false : true);
										InventoryBuilder.openFriendEditInventory(p, fs);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_FAVORITE.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										fs.setFavorite(fs.getFavorite() ? false : true);
										InventoryBuilder.openFriendEditInventory(p, fs);
										return;
									}
									if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ItemStacks.INV_FRIENDEDIT_JUMP.getItem(inEdit).getItemMeta().getDisplayName().replace("%NAME%", name))) {
										if(!Configs.JUMPING_ENABLE.getBoolean()) {
											p.sendMessage(Messages.CMD_JUMP_NOT_ALLOWED.getMessage(p));
											return;
										}
										new Jump_Command(Friends.getInstance(), p, new String[] {"jump",orig_name});
										return;
									}
									
									String invName = "FriendEditInventory";
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
