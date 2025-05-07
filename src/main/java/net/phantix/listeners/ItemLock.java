package net.phantix.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemLock implements Listener {

    private final List<Integer> lockedHotbarSlots = List.of(4, 8); // Slots for locked items

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // Disallow interacting with locked items
        if (isLockedItem(current) || isLockedItem(cursor)) {
            event.setCancelled(true);
            return;
        }

        // Block shift-clicking or placing locked items in modification inventories
        InventoryType type = event.getInventory().getType();
        if (type == InventoryType.ANVIL || type == InventoryType.SMITHING
                || type == InventoryType.GRINDSTONE || type == InventoryType.ENCHANTING
                || type == InventoryType.CRAFTING || type == InventoryType.WORKBENCH) {
            event.setCancelled(true);
        }

        // Block modifying the result slot in anvils (renaming)
        if (event.getSlotType() == InventoryType.SlotType.RESULT && type == InventoryType.ANVIL) {
            event.setCancelled(true);
        }

        // Prevent moving the item out of its locked hotbar slot
        if (event.getSlot() >= 0 && event.getSlot() < 9) {
            if (lockedHotbarSlots.contains(event.getSlot())) {
                ItemStack slotItem = player.getInventory().getItem(event.getSlot());
                if (isLockedItem(slotItem)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isLockedItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (isLockedItem(event.getMainHandItem()) || isLockedItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (int slot : event.getRawSlots()) {
            if (slot >= 0 && slot < 9 && lockedHotbarSlots.contains(slot)) {
                ItemStack dragged = event.getOldCursor();
                if (isLockedItem(dragged)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    private boolean isLockedItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        String name = item.getItemMeta().getDisplayName();
        return name != null && (
                name.equals("§bServer Selector") ||
                        name.equals("§aPlayers: Visible") ||
                        name.equals("§cPlayers: Hidden")
        );
    }
}
