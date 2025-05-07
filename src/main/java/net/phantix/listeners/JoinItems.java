package net.phantix.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class JoinItems implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getInventory().clear();

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        if (compassMeta != null) {
            compassMeta.setDisplayName("§bServer Selector");
            compassMeta.setLore(Arrays.asList("§7Right click to select a server"));
            compass.setItemMeta(compassMeta);
        }
        player.getInventory().setItem(4, compass);

        player.getInventory().setHeldItemSlot(4);

        ItemStack hider = new ItemStack(Material.LIME_DYE);
        ItemMeta hiderMeta = hider.getItemMeta();
        if (hiderMeta != null) {
            hiderMeta.setDisplayName("§aPlayers: Visible");
            hiderMeta.setLore(Arrays.asList("§7Right click to toggle visibility"));
            hider.setItemMeta(hiderMeta);
        }
        player.getInventory().setItem(8, hider);
    }
}
