package de.HyChrod.Friends.Commands;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;

public class SubCommandSerializer {
	
    private static ConcurrentHashMap<String, Constructor<?>> SUB_COMMANDS = null;
    
    public SubCommandSerializer() {
        SubCommandSerializer.SUB_COMMANDS = new ConcurrentHashMap<String, Constructor<?>>();
        this.register("Accept", "Accept_Command");
        this.register("Add", "Add_Command");
        this.register("Block", "Block_Command");
        this.register("Deny", "Deny_Command");
        this.register("List", "List_Command");
        this.register("Remove", "Remove_Command");
        this.register("Unblock", "Unblock_Command");
        this.register("Status", "Status_Command");
        this.register("Acceptall", "AcceptAll_Command");
        this.register("Denyall", "DenyAll_Command");
        this.register("Unblockall", "UnblockAll_Command");
        this.register("msg", "MSG_Command");
        this.register("jump", "Jump_Command");
        this.register("jumping", "OptJumping_Command");
        this.register("messages", "OptMessages_Command");
        this.register("requests", "OptRequests_Command");
        this.register("offlinemode", "OptOffline_Command");
        this.register("nickname", "Nickname_Command");
    }
    
    private void register(final String cmd, final String clazz) {
        try {
            final Class<?> clz = Class.forName("de.HyChrod.Friends.Commands.SubCommands." + clazz);
            final Constructor<?> construct = clz.getConstructor(Friends.class, Player.class, String[].class);
            SubCommandSerializer.SUB_COMMANDS.put(cmd.toLowerCase(), construct);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Constructor<?> get(final String name) {
        if (SubCommandSerializer.SUB_COMMANDS == null) {
            new SubCommandSerializer();
        }
        return SubCommandSerializer.SUB_COMMANDS.get(name.toLowerCase());
    }
}
