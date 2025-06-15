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
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSelector implements Listener, PluginMessageListener {

    private JavaPlugin plugin;
    private Map<String, Integer> serverPlayerCounts = new HashMap<>();

    public ServerSelector(JavaPlugin plugin) {
        this.plugin = plugin;
        // Register plugin messaging channels
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);

        // Initialize player counts
        serverPlayerCounts.put("survival", 0);
        serverPlayerCounts.put("lifesteal", 0);
        serverPlayerCounts.put("anarchy", 0);
        serverPlayerCounts.put("skyblock", 0);
    }

    // Handle player interacting with the compass to open the server selector
    @EventHandler
    public void onPlayerUseCompass(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check for right-click actions specifically
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
            event.setCancelled(true);

            // Update player counts before opening GUI
            updatePlayerCounts(player);

            // Delay opening the GUI to allow time for player count updates
            Bukkit.getScheduler().runTaskLater(plugin, () -> openServerSelector(player), 5L);
        }
    }

    // Request player counts from all servers
    private void updatePlayerCounts(Player player) {
        String[] servers = {"survival", "lifesteal", "anarchy", "skyblock"};

        for (String server : servers) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }

    // Open the server selector GUI
    private void openServerSelector(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "§bServer Selector");

        // Adding server items to the inventory with dynamic player counts
        // Survival with description
        List<String> survivalLore = Arrays.asList(
                "§7Classic Minecraft survival experience",
                "§7with enhanced features and community.",
                "",
                "§bFeatures:",
                "§7• Land Claims & Protection",
                "§7• Player Economy",
                "§7• Custom Recipes",
                "§7• McMMO Skills",
                "§7• Jobs & Ranks",
                "§7• Auction House",
                "§7• ... & much more!",
                "",
                "§a▶ §7" + serverPlayerCounts.get("survival") + " players currently playing"
        );
        inv.setItem(1, createServerItem("§aSurvival", Material.GRASS_BLOCK, survivalLore));

        // Lifesteal with description
        List<String> lifestealLore = Arrays.asList(
                "§7Steal hearts from other players",
                "§7in this intense PvP experience.",
                "",
                "§bFeatures:",
                "§7• Heart Stealing Mechanics",
                "§7• Custom Recipes",
                "§7• PvP Combat",
                "§7• Death Penalties",
                "§7• Teams & Alliances",
                "§7• Custom Items",
                "§7• ... & much more!",
                "",
                "§a▶ §7" + serverPlayerCounts.get("lifesteal") + " players currently playing"
        );
        inv.setItem(3, createServerItem("§cLifesteal", Material.NETHERITE_SWORD, lifestealLore));

        // Anarchy with description
        List<String> anarchyLore = Arrays.asList(
                "§7No rules, no limits, pure chaos.",
                "§7Survive in the wild west of Minecraft.",
                "",
                "§bFeatures:",
                "§7• No Rules or Restrictions",
                "§7• Unlimited PvP",
                "§7• Griefing Allowed",
                "§7• Hacking Permitted",
                "§7• Economy System",
                "§7• Faction Warfare",
                "§7• ... & much more!",
                "",
                "§a▶ §7" + serverPlayerCounts.get("anarchy") + " players currently playing"
        );
        inv.setItem(5, createServerItem("§4Anarchy", Material.TNT, anarchyLore));

        // Skyblock with description
        List<String> skyblockLore = Arrays.asList(
                "§7Survive on a floating island",
                "§7with minimal resources.",
                "",
                "§bFeatures:",
                "§7• Shop & Quests",
                "§7• Custom Enchantments",
                "§7• Island Upgrades",
                "§7• Crates & Envoys",
                "§7• King of the Hill",
                "§7• Player Shops",
                "§7• ... & much more!",
                "",
                "§a▶ §7" + serverPlayerCounts.get("skyblock") + " players currently playing"
        );
        inv.setItem(7, createServerItem("§cSkyblock", Material.SAND, skyblockLore));

        player.openInventory(inv);
    }

    // Utility method to create server items with optional lore
    private ItemStack createServerItem(String displayName, Material material, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            if (lore != null) {
                meta.setLore(lore);
            }
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

                    // Use Runnable to delay the server switch by 1 tick to avoid issues
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        String serverName = null;

                        if (itemName.equals("§aSurvival")) {
                            serverName = "survival";
                        } else if (itemName.equals("§cLifesteal")) {
                            serverName = "lifesteal";
                        } else if (itemName.equals("§4Anarchy")) {
                            serverName = "anarchy";
                        } else if (itemName.equals("§cSkyblock")) {
                            serverName = "skyblock";
                        }

                        if (serverName != null) {
                            connectToServer(player, serverName);
                        }
                    }, 1L);
                }
            }
        }
    }

    // Method to connect player to server using plugin messaging
    private void connectToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    // Handle incoming plugin messages (player count responses)
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("PlayerCount")) {
            String serverName = in.readUTF();
            int playerCount = in.readInt();

            serverPlayerCounts.put(serverName, playerCount);
        }
    }
}