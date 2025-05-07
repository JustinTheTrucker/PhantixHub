package net.phantix.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TabListManager implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String header = ChatColor.translateAlternateColorCodes('&', "&b&lPhantix Network\n");
        String footer = ChatColor.translateAlternateColorCodes('&', "\n&7play.phantix.net");

        player.setPlayerListHeaderFooter(header, footer);
    }
}
