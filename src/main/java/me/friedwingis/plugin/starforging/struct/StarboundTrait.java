package me.friedwingis.plugin.starforging.struct;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.friedwingis.plugin.starforging.utils.Chat;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
@AllArgsConstructor
public enum StarboundTrait {
    VOID_STEP("<#6A0DAD>", new String[]{
            "20% chance to ignore fall damage.",
            "3% chance to teleport behind your attacker when struck."
    }),
    SOLAR_WRATH("<#FFD700>", new String[]{
            "Deal +15% melee damage in sunlight.",
            "Chance to blind enemies briefly when attacking at high noon."
    }),
    GALACTIC_REINFORCEMENT("<#D8B4F8>", new String[]{
            "Gain Absorption II for 3s when below 25% HP.",
            "20s cooldown between activations."
    });

    // The color associated with this trait (for display purposes)
    final String color;
    // The array of perks that this trait provides
    final String[] perks;

    /**
     * Checks if the given item has a Starbound trait applied to it.
     *
     * @param item The item to check
     * @return True if the item has a Starbound trait, false otherwise
     */
    public static boolean containsTrait(final ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta())
            return false;

        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("starforging", "starbound_trait"));
    }

    /**
     * Retrieves the Starbound trait applied to the given item.
     *
     * @param item The item to retrieve the trait from
     * @return The Starbound trait, or null if none is found
     */
    public static StarboundTrait getTrait(final ItemStack item) {
        if (!containsTrait(item))
            return null;

        // Retrieve and return the trait name from the persistent data container
        return StarboundTrait.valueOf(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("starforging", "starbound_trait"), PersistentDataType.STRING));
    }

    /**
     * Applies this Starbound trait to the given item, adding its perks and setting
     * the appropriate metadata.
     *
     * @param input The item to apply the trait to
     */
    public void applyToItem(final ItemStack input) {
        final ItemMeta meta = input.getItemMeta();

        // Initialize lore with the item's current lore or an empty list if none exists
        final List<Component> lore = meta.hasLore() ? Lists.newArrayList(Objects.requireNonNull(meta.lore())) : Lists.newArrayList();

        // Add a separator and the Starbound trait's name and color
        lore.add(Chat.EMPTY_STRING);
        lore.add(Chat.format("<light_purple><b>Starbound Trait (" + color + WordUtils.capitalizeFully(name().toLowerCase().replace("_", " ")) + "<light_purple>)"));

        // Add each perk of the trait to the lore
        for (final String s : perks) {
            lore.add(Chat.format(" <white><b>*</b> <light_purple>" + s));
        }

        // Set the new lore on the item
        meta.lore(lore);

        // Store the trait in the item's persistent data container so we can retrieve it later
        meta.getPersistentDataContainer().set(new NamespacedKey("starforging", "starbound_trait"), PersistentDataType.STRING, name());

        // Apply the updated metadata to the item
        input.setItemMeta(meta);
    }
}