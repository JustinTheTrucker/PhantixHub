package net.phantix.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class Weather implements Listener {

    public Weather() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : org.bukkit.Bukkit.getWorlds()) {
                    if (world.getTime() > 12300 || world.getTime() < 0) {
                        world.setTime(1000);
                    }

                    if (world.hasStorm() || world.isThundering()) {
                        world.setStorm(false);
                        world.setThundering(false);
                    }
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PhantixHub")), 0, 100);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        world.setTime(1000);
        world.setStorm(false);
        world.setThundering(false);
    }
}
