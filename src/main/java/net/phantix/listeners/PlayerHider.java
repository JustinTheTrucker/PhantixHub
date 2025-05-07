package net.phantix.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class PlayerHider implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 1500; // 1.5 seconds

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || (item.getType() != Material.LIME_DYE && item.getType() != Material.RED_DYE)) return;
        event.setCancelled(true);

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid) && (now - cooldowns.get(uuid)) < COOLDOWN_MS) {
            return; // Silent cooldown: do nothing
        }

        cooldowns.put(uuid, now); // Set cooldown

        boolean isHiding = item.getType() == Material.LIME_DYE;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target != player) {
                if (isHiding) {
                    player.hidePlayer(Bukkit.getPluginManager().getPlugin("PhantixHub"), target);
                } else {
                    player.showPlayer(Bukkit.getPluginManager().getPlugin("PhantixHub"), target);
                }
            }
        }

        // Toggle item
        ItemStack newItem = new ItemStack(isHiding ? Material.RED_DYE : Material.LIME_DYE);
        ItemMeta meta = newItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(isHiding ? "§cPlayers: Hidden" : "§aPlayers: Visible");
            meta.setLore(Collections.singletonList("§7Right click to toggle visibility"));
            newItem.setItemMeta(meta);
        }

        player.getInventory().setItem(8, newItem);
    }
}
