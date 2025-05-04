package me.friedwingis.plugin.starforging.struct;

import me.friedwingis.plugin.starforging.utils.Chat;
import me.friedwingis.plugin.starforging.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 *
 * The ForgeGUI class handles the Celestial Forge interface and logic.
 * It allows players to fuse StarDust with Diamond or Netherite armor to apply powerful Starbound Traits.
 */
public class ForgeGUI implements InventoryHolder {
    private final Inventory inventory;
    private boolean forged;  // Indicates whether the item has been successfully forged

    /**
     * Initializes the Forge GUI with empty slots and placeholders.
     */
    public ForgeGUI() {
        this.inventory = Bukkit.createInventory(this, 27, Chat.format("<gradient:#7a00cc:#cc00ff>Celestial Forge"));

        // Set up the empty slots (black stained glass) for the player to interact with
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 11 || i == 12) continue;  // Skip the slots for armor and Stardust
            this.inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("<red>").build());
        }

        // Set the output placeholder in slot 15
        this.inventory.setItem(15, createPlaceholderOutput());
    }

    /**
     * Opens the forge interface for the player.
     * Plays the enchantment table sound to indicate the forge is ready.
     */
    public void open(final Player player) {
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
    }

    /**
     * Handles the event when the player closes the inventory.
     * If the item was not forged, it returns the items to the player.
     */
    public void handleClose(final InventoryCloseEvent event) {
        if (forged) return;  // Do nothing if the item was forged

        final Player player = (Player) event.getPlayer();

        // Return any items the player had in the forge slots
        final ItemStack inputItem = inventory.getItem(11);
        final ItemStack dustItem = inventory.getItem(12);

        if (inputItem != null && !inputItem.getType().isAir())
            giveOrDropItem(player, inputItem);
        if (dustItem != null && !dustItem.getType().isAir())
            giveOrDropItem(player, dustItem);
    }

    /**
     * Tries to give the specified item to the player. If the player's inventory is full,
     * the item will be dropped at the player's location.
     *
     * @param target The player to receive or drop the item.
     * @param item   The item to be given or dropped.
     */
    private void giveOrDropItem(final Player target, final ItemStack item) {
        // Check if there's an empty slot in the player's inventory
        final int firstEmptySlot = target.getInventory().firstEmpty();

        if (firstEmptySlot != -1) {
            // If there is an empty slot, add the item to the inventory
            target.getInventory().addItem(item);
        } else {
            // If the inventory is full, drop the item at the player's location
            target.getWorld().dropItem(target.getLocation().add(0, 0.5, 0), item);
        }
    }

    /**
     * Handles click events in the forge interface.
     * Handles both input validation and the forging process.
     */
    public void handleTopClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final int slot = event.getSlot();

        // If player clicked on the output slot (15), start the forging process
        if (slot == 15) {
            final ItemStack inputItem = event.getInventory().getItem(11);
            final ItemStack dustItem = event.getInventory().getItem(12);

            // Cancel if items are missing or invalid
            if (inputItem == null || inputItem.getType().isAir() || dustItem == null || dustItem.getType().isAir()) {
                player.sendMessage(Chat.severe("You do not have the correct combination of items!"));
                return;
            }

            // Ensure the item doesn't already have a trait applied
            if (StarboundTrait.containsTrait(inputItem)) {
                player.sendMessage(Chat.severe("You may only apply one trait per item."));
                return;
            }

            final int dustReq = getDustRequirement(inputItem);
            if (dustItem.getAmount() < dustReq) {
                player.sendMessage(Chat.severe("You do not have the required amount of StarDust for this material!"));
                player.sendMessage(Chat.format("<gray>Required Amount: <u>" + dustReq));
                return;
            }

            // Forge the item and give it to the player
            player.getInventory().addItem(createForgedResult(inputItem));

            // Return any leftover StarDust
            final int remainingDust = dustItem.getAmount() - dustReq;
            if (remainingDust > 0) {
                final ItemStack remainder = dustItem.clone();
                remainder.setAmount(remainingDust);
                player.getInventory().addItem(remainder);
            }

            // Mark the item as forged and close the inventory
            forged = true;
            player.closeInventory();
            player.sendMessage(Chat.praise("Your item has been successfully Starforged!"));

            if (remainingDust > 0) {
                player.sendMessage(Chat.format("<gray>Returned " + remainingDust + " left over StarDust to you."));
            }
        }

        // Handle item insertion into the forge slots (11 and 12)
        if (slot == 11 || slot == 12) {
            final ItemStack current = inventory.getItem(slot);
            final ItemStack cursor = event.getCursor();

            if (cursor.getType() != Material.AIR) {
                // If cursor is not air, validate and insert the item
                if (slot == 11) {
                    if (!isValidArmorPiece(cursor.getType())) {
                        player.sendMessage(Chat.warn("Only a diamond or netherite armor piece can be inserted here."));
                        return;
                    }
                    if (StarboundTrait.containsTrait(cursor)) {
                        player.sendMessage(Chat.severe("This item already has a trait applied! You may only apply one trait per item."));
                        return;
                    }
                }

                // Validate Stardust for slot 12
                if (slot == 12) {
                    if (!cursor.hasItemMeta() || !cursor.getItemMeta().getPersistentDataContainer().has(Constants.STARDUST_KEY)) {
                        player.sendMessage(Chat.warn("Only Stardust can be inserted here."));
                        return;
                    }
                }

                // Insert item if slot is empty
                if (current == null || current.getType().isAir()) {
                    inventory.setItem(slot, cursor.clone());
                    player.setItemOnCursor(null);
                    updateOutputSlot();
                }
                return;
            }

            // Handle removing items from the forge slots
            if (cursor.getType() == Material.AIR && current != null && current.getType() != Material.AIR) {
                player.setItemOnCursor(current.clone());
                inventory.setItem(slot, null);
                updateOutputSlot();
            }
        }
    }

    /**
     * Updates the output slot based on the items in the input and Stardust slots.
     */
    private void updateOutputSlot() {
        final ItemStack input = inventory.getItem(11);
        final ItemStack dust = inventory.getItem(12);

        final ItemStack result = (input != null && input.getType() != Material.AIR
                && dust != null && dust.getType() != Material.AIR && isValidArmorPiece(input.getType()) && dust.getAmount() >= getDustRequirement(input))
                ? createReadyOutputPlaceholder()
                : createPlaceholderOutput();

        inventory.setItem(15, result);
    }

    /**
     * Creates the final forged item by applying a random Starbound trait.
     */
    private ItemStack createForgedResult(final ItemStack input) {
        final ItemStack clone = input.clone();
        final double r = Math.random();

        StarboundTrait trait;
        if (r < 0.05) {
            trait = StarboundTrait.GALACTIC_REINFORCEMENT;
        } else if (r < 0.25) {
            trait = StarboundTrait.VOID_STEP;
        } else {
            trait = StarboundTrait.SOLAR_WRATH;
        }

        trait.applyToItem(clone);
        return clone;
    }

    /**
     * Creates a placeholder item for the output slot when conditions aren't met.
     */
    private ItemStack createPlaceholderOutput() {
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("<red><b>Locked")
                .setLore(
                        "<gray>Your final forged item will appear here",
                        "<gray>once you've inserted both a valid armor piece",
                        "<gray>and the required amount of Stardust.",
                        "",
                        "<yellow>- <gray>Place the piece you want to forge in <gold>Slot 1",
                        "<yellow>- <gray>Insert <gradient:#e0e0e0:#ffffff><b>StarDust</b> <gray>into <gold>Slot 2",
                        "",
                        "<light_purple><b>Stardust Cost:",
                        "<white>- <gray>Diamond Armor: 48",
                        "<white>- <gray>Netherite Armor: 64",
                        "",
                        "<gray>The imbued item will appear here once both are valid."
                )
                .build();
    }

    /**
     * Creates a ready-to-forge placeholder when both input and Stardust are valid.
     */
    private ItemStack createReadyOutputPlaceholder() {
        return new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("<green><b>Ready to Forge")
                .setLore(
                        "<gray>Your item and Stardust are valid.",
                        "<gray>Click to complete the forging process.",
                        "",
                        "<yellow>- <gray>Forging Piece: <gold>✔",
                        "<yellow>- <gray>Req Stardust Amount: <gold>✔",
                        "",
                        "<green>Click to Starforge your gear!"
                )
                .build();
    }

    /**
     * Gets the Stardust requirement based on the material of the armor piece.
     */
    private int getDustRequirement(final ItemStack item) {
        final Material material = item.getType();
        return switch (material) {
            case DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS -> 48;
            case NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS -> 64;
            default -> -1;
        };
    }

    /**
     * Checks if the given material is a valid armor piece (Diamond or Netherite).
     */
    private boolean isValidArmorPiece(final Material material) {
        return EnchantmentTarget.ARMOR.includes(material) && (material.name().contains("DIAMOND") || material.name().contains("NETHERITE"));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}