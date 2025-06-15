package net.phantix.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DisableDamage implements Listener {

    private PvPMode pvpMode;

    public DisableDamage(PvPMode pvpMode) {
        this.pvpMode = pvpMode;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            // If it's PvP damage, handle it separately
            if (event instanceof EntityDamageByEntityEvent) {
                handlePvPDamage((EntityDamageByEntityEvent) event);
            } else {
                // Cancel all non-PvP damage to players
                event.setCancelled(true);
            }
        }
    }

    private void handlePvPDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Allow damage only if both players are in PvP mode
        if (pvpMode.isInPvPMode(victim) && pvpMode.isInPvPMode(attacker)) {
            // Allow the damage
            return;
        } else {
            // Cancel the damage
            event.setCancelled(true);
        }
    }
}