package net.phantix.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.Player;

public class DisableHunger implements Listener {

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true); // Prevent hunger change
            event.getEntity().setFoodLevel(20); // Ensure it's always full
            event.getEntity().setSaturation(20); // Max saturation for smoothness
        }
    }
}
