package me.friedwingis.plugin.starforging;

import me.friedwingis.plugin.starforging.struct.ForgeGUI;
import me.friedwingis.plugin.starforging.struct.StarboundTrait;
import me.friedwingis.plugin.starforging.utils.Chat;
import me.friedwingis.plugin.starforging.utils.Constants;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 *
 * Listener class for player interactions, including events like block breaking, inventory actions,
 * and entity damage, all integrated with the Starbound traits system.
 */
public class PlayerListeners implements Listener {

    /**
     * Handles the event when a player breaks a crop.
     * Checks if the block has a chance to drop Stardust based on the crop type.
     *
     * @param event The BlockBreakEvent triggered when a player breaks a block.
     */
    @EventHandler
    private void onCropFarm(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (block.getType().isAir())
            return; // Ignore if the block is air.

        final double dropChance = Constants.CROP_RARITY.getOrDefault(block.getType(), 0.0);

        if (dropChance == 0.0 || Math.random() > dropChance)
            return; // If no drop chance or the random chance fails, do nothing.

        addDustToPlayer(player); // Add Stardust to the player.
    }

    /**
     * Adds Stardust to the player's inventory.
     * If the inventory is full, notifies the player.
     *
     * @param player The player to receive Stardust.
     */
    private void addDustToPlayer(final Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Chat.warn("Unable to collect Stardust! There is no space within your inventory."));
            return; // Stop if the inventory is full.
        }

        final ItemStack dust = Constants.STARDUST.asOne(); // Get Stardust item.

        player.getInventory().addItem(dust); // Add the Stardust to the player's inventory.
        player.sendMessage(Chat.praise("You discovered.. 1x <gradient:#e0e0e0:#ffffff><b>Star Dust"));
    }

    /**
     * Handles a click event in the inventory.
     * Specifically processes interactions within the Forge GUI.
     *
     * @param event The InventoryClickEvent triggered when a player clicks in their inventory.
     */
    @EventHandler
    private void onClick(final InventoryClickEvent event) {
        final Inventory top = event.getView().getTopInventory();
        final InventoryHolder holder = top.getHolder();

        if (holder instanceof ForgeGUI gui && event.getRawSlot() < top.getSize()) {
            event.setCancelled(true); // Prevent default behavior.
            gui.handleTopClick(event); // Process the Forge GUI click.
        }
    }

    /**
     * Handles the event when a player closes their inventory.
     * Specifically handles closing the Forge GUI.
     *
     * @param event The InventoryCloseEvent triggered when a player closes their inventory.
     */
    @EventHandler
    private void onClose(final InventoryCloseEvent event) {
        final InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder instanceof ForgeGUI gui)
            gui.handleClose(event); // Handle Forge GUI close event.
    }

    /**
     * Handles damage events for players, including fall damage and trait-based effects.
     *
     * @param event The EntityDamageEvent triggered when an entity takes damage.
     */
    @EventHandler
    private void onEntityDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (!(entity instanceof Player player))
            return; // Ignore non-player entities.

        // Handle fall damage if the player has the VOID_STEP trait
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && hasTrait(player, StarboundTrait.VOID_STEP)) {
            if (Math.random() < 0.20) {
                event.setCancelled(true); // Cancel fall damage.
                player.sendMessage(Chat.format("<#6A0DAD><b>Void Step</b> - You avoided fall damage."));
            }
        }

        // Handle Galactic Reinforcement trait
        if (hasTrait(player, StarboundTrait.GALACTIC_REINFORCEMENT)) {
            // Get the player's current and max health
            final double currentHealth = player.getHealth();
            final double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue();

            // Check if the player's health is at or below 25% of their maximum
            if (currentHealth <= maxHealth * 0.25) {
                // Get the current system time
                final long now = System.currentTimeMillis();

                // Retrieve the cooldown timestamp from metadata, or default to 0 if not present
                final long cooldown = player.hasMetadata("trait_cd")
                        ? player.getMetadata("trait_cd").getFirst().asLong()
                        : 0L;

                // If the cooldown has expired, apply the trait effect
                if (now >= cooldown) {
                    // Give the player the Absorption effect for 3 seconds (60 ticks) at level 2
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 1));

                    // Set a new cooldown (20 seconds from now)
                    player.setMetadata("trait_cd", new FixedMetadataValue(
                            JavaPlugin.getPlugin(StarForging.class),
                            now + 20_000
                    ));

                    player.sendMessage(Chat.format("<#D8B4F8><b>Galactic Reinforcement</b> - Absorption activated!"));
                }
            }
        }
    }

    /**
     * Handles the event when a player damages another entity (player).
     * Includes checking for Solar Wrath trait effects.
     *
     * @param event The EntityDamageByEntityEvent triggered when a player damages another entity.
     */
    @EventHandler
    private void onEntityDamageByPlayer(final EntityDamageByEntityEvent event) {
        final Entity v = event.getEntity(), d = event.getDamager();
        if (!(v instanceof Player victim))
            return;
        if (!(d instanceof Player damager))
            return;

        // Handle Void Step trait - Teleport behind attacker when damaged.
        if (Math.random() < 0.03 && hasTrait(victim, StarboundTrait.VOID_STEP)) {
            final Location attackerLocation = damager.getLocation();
            final Vector direction = attackerLocation.getDirection().normalize().multiply(-1);
            final Location behindAttacker = attackerLocation.add(direction.setY(0)).add(0, 0.5, 0);

            // Check if the block at the teleport location is empty
            if (behindAttacker.getBlock().getType().isAir()) {
                victim.teleport(behindAttacker); // Teleport victim behind attacker.
                victim.sendMessage(Chat.format("<#6A0DAD><b>Void Step</b> - You teleported behind your attacker!"));
            }
        }

        // Handle Solar Wrath trait - Increase damage in sunlight and blind at high noon.
        if (hasTrait(damager, StarboundTrait.SOLAR_WRATH)) {
            final World world = damager.getWorld();
            final long time = world.getTime();
            final boolean isDay = time >= 0 && time < 12300,
                    isHighNoon = time >= 6000 && time <= 7000,
                    isClear = world.getEnvironment() == World.Environment.NORMAL && !world.hasStorm();

            if (isDay && isClear) {
                final double damage = event.getDamage();
                event.setDamage(damage * 1.15); // Increase damage by 15%.

                if (isHighNoon && Math.random() < 0.25) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1)); // Apply blindness effect.
                    victim.sendMessage(Chat.format("<#FFD700><b>Solar Wrath</b> - You are blinded by the sun!"));
                }
            }
        }
    }

    /**
     * Checks if a player has a specific StarboundTrait equipped on their armor.
     *
     * @param player The player to check.
     * @param trait  The trait to check for.
     * @return True if the player has the trait, false otherwise.
     */
    private boolean hasTrait(final Player player, final StarboundTrait trait) {
        return Arrays.stream(player.getInventory().getArmorContents())
                .filter(Objects::nonNull)
                .filter(item -> !item.getType().isAir())
                .filter(EnchantmentTarget.ARMOR::includes)
                .filter(item -> {
                    String name = item.getType().name();
                    return name.contains("DIAMOND") || name.contains("NETHERITE");
                })
                .filter(StarboundTrait::containsTrait)
                .anyMatch(item -> StarboundTrait.getTrait(item) == trait); // Return true if any armor piece has the trait.
    }
}
