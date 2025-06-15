package net.phantix.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TeleportBow implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof Player)) return;

        Player player = (Player) arrow.getShooter();

        // Check if the arrow was shot by the teleport bow
        ItemStack bow = player.getInventory().getItem(0);
        if (bow == null || bow.getType() != Material.BOW) return;
        if (!bow.hasItemMeta() || !bow.getItemMeta().hasDisplayName()) return;
        if (!bow.getItemMeta().getDisplayName().equals("§dTeleport Bow")) return;

        // Get the arrow's location
        Location hitLocation = arrow.getLocation();

        // Add slight offset to prevent getting stuck in blocks
        hitLocation.add(0, 1, 0);

        // Teleport the player
        player.teleport(hitLocation);

        // Remove the arrow
        arrow.remove();

        // Give player another arrow if they don't have any
        if (!player.getInventory().contains(Material.ARROW)) {
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }
    }

    // Method to create the teleport bow
    public static ItemStack createTeleportBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§dTeleport Bow");
            meta.setLore(Arrays.asList(
                    "§7Shoot to teleport!"
            ));

            // Add infinity enchantment to prevent consuming arrows
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addEnchant(Enchantment.UNBREAKING, 10, true);

            // Hide enchantment glint
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            bow.setItemMeta(meta);
        }

        return bow;
    }
}