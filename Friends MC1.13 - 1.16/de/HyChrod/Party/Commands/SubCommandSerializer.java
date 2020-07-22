package de.HyChrod.Party.Commands;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import de.HyChrod.Friends.Friends;

public class SubCommandSerializer {
	
    private static ConcurrentHashMap<String, Constructor<?>> SUB_COMMANDS = null;
    
    public SubCommandSerializer() {
        SubCommandSerializer.SUB_COMMANDS = new ConcurrentHashMap<String, Constructor<?>>();
        this.register("invite", "Invite_Command");
        this.register("deny", "Deny_Command");
        this.register("accept", "Accept_Command");
        this.register("demote", "Demote_Command");
        this.register("promote", "Promote_Command");
        this.register("list", "List_Command");
        this.register("leave", "Leave_Command");
        this.register("join", "Join_Command");
        this.register("kick", "Kick_Command");
    }
    
    private void register(final String cmd, final String clazz) {
        try {
            final Class<?> clz = Class.forName("de.HyChrod.Party.Commands.SubCommands." + clazz);
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
