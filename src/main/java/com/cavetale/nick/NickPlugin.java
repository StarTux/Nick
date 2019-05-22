package com.cavetale.nick;

import com.winthier.sql.SQLDatabase;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class NickPlugin extends JavaPlugin implements Listener {
    private SQLDatabase db = new SQLDatabase(this);

    @Override
    public void onEnable() {
        db.registerTable(SQLNick.class);
        db.createAllTables();
        getServer().getPluginManager().registerEvents(this, this);
        for (final Player player : getServer().getOnlinePlayers()) {
            final SQLNick nick = db.find(SQLNick.class)
                .eq("uuid", player.getUniqueId())
                .findUnique();
            if (nick == null) continue;
            applyNick(player, nick);
        }
    }

    @Override
    public void onDisable() {
        for (final Player player : getServer().getOnlinePlayers()) {
            player.setDisplayName(null);
            player.setPlayerListName(null);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender,
                             final Command command,
                             final String alias,
                             final String[] args) {
        if (!(sender instanceof Player)) return false;
        final Player player = (Player) sender;
        final String nickname = args.length > 0
            ? Arrays.stream(args).collect(Collectors.joining(" "))
            : null;
        final SQLNick nick = new SQLNick(player.getUniqueId(),
                                         nickname);
        db.save(nick);
        final String format = applyNick(player, nick);
        player.sendMessage(ChatColor.GOLD + "Nickname set to \""
                           + format
                           + ChatColor.GOLD + "\".");
        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final SQLNick nick = db.find(SQLNick.class)
            .eq("uuid", player.getUniqueId())
            .findUnique();
        if (nick == null) return;
        applyNick(player, nick);
    }

    /**
     * Set or reset player's nickname from database row.
     * @return the new name, {@code player.getName()}.
     */
    private static String applyNick(final Player player,
                                  final SQLNick nick) {
        if (nick.getNickname() == null) {
            player.setDisplayName(null);
            player.setPlayerListName(null);
            return player.getName();
        }
        final String format = ChatColor
            .translateAlternateColorCodes('&', nick.getNickname());
        player.setDisplayName(format);
        player.setPlayerListName(format);
        return format;
    }
}
