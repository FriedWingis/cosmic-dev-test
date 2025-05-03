package me.friedwingis.plugin.starforging;

import lombok.AllArgsConstructor;
import me.friedwingis.plugin.starforging.utils.Chat;
import me.friedwingis.plugin.starforging.utils.Constants;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class PlayerListeners implements Listener {

    /**
     * Triggered when a player breaks a block, handling the chance of Stardust drop for specific crops.
     *
     * @param event The BlockBreakEvent that contains the player and block information.
     */
    @EventHandler
    private void onCropFarm(final BlockBreakEvent event) {
        final Player player = event.getPlayer(); // Get the player who broke the block
        final Block block = event.getBlock(); // Get the block that was broken

        // If the block is air, we ignore it (no crop to harvest)
        if (block.getType().isAir())
            return;

        // Fetch the drop chance for the broken block type, defaulting to 0.0 if not found
        final double dropChance = Constants.CROP_RARITY.getOrDefault(block.getType(), 0.0);

        // If dropChance is 0.0 or the random chance fails, we return (no Stardust)
        if (dropChance == 0.0 || Math.random() > dropChance)
            return;

        // Otherwise, add Stardust to the player's inventory
        addDustToPlayer(player);
    }

    /**
     * Adds Stardust to the player's inventory, notifying them if successful.
     * If the inventory is full, a warning message is sent.
     *
     * @param player The player to give Stardust to.
     */
    private void addDustToPlayer(final Player player) {
        // Check if the player has space in their inventory
        if (player.getInventory().firstEmpty() == -1) {
            // If inventory is full, notify the player
            player.sendMessage(Chat.warn("Unable to collect Stardust! There is no space within your inventory."));
            return;
        }

        // Create an ItemStack for Stardust (1 unit)
        final ItemStack dust = Constants.STARDUST.asOne();

        // Add the Stardust item to the player's inventory
        player.getInventory().addItem(dust);

        // Notify the player that they have discovered Stardust
        player.sendMessage(Chat.praise("You discovered.. 1x <gradient:#e0e0e0:#ffffff><b>Star Dust"));
    }
}
