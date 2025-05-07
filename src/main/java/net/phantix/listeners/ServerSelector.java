package net.phantix.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerSelector implements Listener {

    private JavaPlugin plugin;

    // You need a constructor that accepts the plugin parameter
    public ServerSelector(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Handle player interacting with the compass to open the server selector
    @EventHandler
    public void onPlayerUseCompass(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check for right-click actions specifically
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
            event.setCancelled(true);
            openServerSelector(player);
        }
    }

    // Open the server selector GUI
    private void openServerSelector(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "§bServer Selector");

        // Adding server items to the inventory
        inv.setItem(2, createServerItem("§aSurvival", Material.GRASS_BLOCK));
        inv.setItem(4, createServerItem("§bCreative", Material.BRICKS));
        inv.setItem(6, createServerItem("§cSkyblock", Material.SAND));

        player.openInventory(inv);
    }

    // Utility method to create server items
    private ItemStack createServerItem(String displayName, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    // Handle inventory clicks (server selection)
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals("§bServer Selector")) {
            event.setCancelled(true); // Prevent items from being moved

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                    String itemName = clickedItem.getItemMeta().getDisplayName();

                    // Close inventory
                    player.closeInventory();

                    // Use Runnable to delay the command by 1 tick to avoid issues
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (itemName.equals("§aSurvival")) {
                            player.performCommand("server survival");
                        } else if (itemName.equals("§bCreative")) {
                            player.performCommand("server creative");
                        } else if (itemName.equals("§cSkyblock")) {
                            player.performCommand("server skyblock");
                        }
                    }, 1L);
                }
            }
        }
    }
}