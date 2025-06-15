package net.phantix.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPMode implements Listener {

    private JavaPlugin plugin;
    private Map<UUID, Boolean> pvpModeEnabled = new HashMap<>();
    private Map<UUID, BukkitTask> holdTasks = new HashMap<>();
    private Map<UUID, ItemStack[]> storedArmor = new HashMap<>();

    public PvPMode(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

        // Check if switching TO the PvP sword
        if (isPvPSword(newItem)) {
            startHoldTimer(player);
        }
        // Check if switching FROM the PvP sword
        else if (isPvPSword(oldItem)) {
            cancelHoldTimer(player);
            disablePvPMode(player);
        }
    }

    private void startHoldTimer(Player player) {
        UUID uuid = player.getUniqueId();

        // Cancel any existing timer
        cancelHoldTimer(player);

        // Start countdown messages
        player.sendMessage("§7PvP will be enabled in §e3§7...");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isPvPSword(player.getInventory().getItemInMainHand())) {
                player.sendMessage("§7PvP will be enabled in §e2§7...");
            }
        }, 20L); // 1 second

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isPvPSword(player.getInventory().getItemInMainHand())) {
                player.sendMessage("§7PvP will be enabled in §e1§7...");
            }
        }, 40L); // 2 seconds

        // Start 3-second timer for PvP activation
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isPvPSword(player.getInventory().getItemInMainHand())) {
                enablePvPMode(player);
            }
            holdTasks.remove(uuid);
        }, 60L); // 3 seconds = 60 ticks

        holdTasks.put(uuid, task);
    }

    private void cancelHoldTimer(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = holdTasks.get(uuid);
        if (task != null) {
            task.cancel();
            holdTasks.remove(uuid);
        }
    }

    private void enablePvPMode(Player player) {
        UUID uuid = player.getUniqueId();

        if (pvpModeEnabled.getOrDefault(uuid, false)) {
            return; // Already enabled
        }

        pvpModeEnabled.put(uuid, true);

        // Store current armor
        ItemStack[] currentArmor = player.getInventory().getArmorContents();
        storedArmor.put(uuid, currentArmor);

        // Equip diamond armor
        ItemStack[] diamondArmor = new ItemStack[4];
        diamondArmor[0] = new ItemStack(Material.DIAMOND_BOOTS);   // Boots
        diamondArmor[1] = new ItemStack(Material.DIAMOND_LEGGINGS); // Leggings
        diamondArmor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE); // Chestplate
        diamondArmor[3] = new ItemStack(Material.DIAMOND_HELMET);   // Helmet

        player.getInventory().setArmorContents(diamondArmor);

        player.sendMessage("§c⚔ PvP Mode Enabled!");
    }

    private void disablePvPMode(Player player) {
        UUID uuid = player.getUniqueId();

        if (!pvpModeEnabled.getOrDefault(uuid, false)) {
            return; // Already disabled
        }

        pvpModeEnabled.put(uuid, false);

        // Restore original armor
        ItemStack[] originalArmor = storedArmor.get(uuid);
        if (originalArmor != null) {
            player.getInventory().setArmorContents(originalArmor);
            storedArmor.remove(uuid);
        }

        // Heal player to full health
        player.setHealth(player.getMaxHealth());

        player.sendMessage("§a✓ PvP Mode Disabled!");
    }

    private boolean isPvPSword(ItemStack item) {
        if (item == null || item.getType() != Material.DIAMOND_SWORD) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
        return item.getItemMeta().getDisplayName().equals("§bPvP Mode");
    }

    // Method to create the PvP sword
    public static ItemStack createPvPSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§bPvP Mode");
            meta.setLore(Arrays.asList(
                    "§7Hold this sword to enable PvP mode.",
                    "",
                    "§e➤ Hold to toggle!"
            ));

            meta.setUnbreakable(true);
            sword.setItemMeta(meta);
        }

        return sword;
    }

    // Check if player is in PvP mode
    public boolean isInPvPMode(Player player) {
        return pvpModeEnabled.getOrDefault(player.getUniqueId(), false);
    }
}