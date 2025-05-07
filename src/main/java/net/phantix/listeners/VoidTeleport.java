package net.phantix.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class VoidTeleport implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() < 0) {
            Location spawnLocation = player.getWorld().getSpawnLocation();
            spawnLocation.setY(spawnLocation.getY() + 1);
            player.teleport(spawnLocation);
        }
    }
}
