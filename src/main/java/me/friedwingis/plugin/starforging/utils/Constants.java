package me.friedwingis.plugin.starforging.utils;

import com.google.common.collect.Maps;
import me.friedwingis.plugin.starforging.struct.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 *
 * Utility class to hold all constants mainly used for
 * inventory related operations.
 **/
public class Constants {

    public static final Map<Material, Double> CROP_RARITY;
    public static final NamespacedKey STARDUST_KEY;
    public static final ItemStack STARDUST;

    static {
        CROP_RARITY = Maps.newHashMap();
        CROP_RARITY.put(Material.WHEAT, 0.30);
        CROP_RARITY.put(Material.SWEET_BERRIES, 0.29);
        CROP_RARITY.put(Material.GLOW_BERRIES, 0.28);
        CROP_RARITY.put(Material.POTATO, 0.27);
        CROP_RARITY.put(Material.PITCHER_PLANT, 0.26);
        CROP_RARITY.put(Material.CARROT, 0.25);
        CROP_RARITY.put(Material.TORCHFLOWER, 0.24);
        CROP_RARITY.put(Material.SUGAR_CANE, 0.23);
        CROP_RARITY.put(Material.BAMBOO, 0.22);
        CROP_RARITY.put(Material.MELON, 0.21);
        CROP_RARITY.put(Material.BEETROOT, 0.20);
        CROP_RARITY.put(Material.TWISTING_VINES, 0.19);
        CROP_RARITY.put(Material.PUMPKIN, 0.18);
        CROP_RARITY.put(Material.NETHER_WART, 0.17);
        CROP_RARITY.put(Material.COCOA_BEANS, 0.16);
        CROP_RARITY.put(Material.WEEPING_VINES, 0.15);
        CROP_RARITY.put(Material.CHORUS_FRUIT, 0.14);

        STARDUST_KEY = new NamespacedKey("starforging", "sf_istardust");

        STARDUST = new ItemBuilder(Material.BONE_MEAL)
                .setDisplayName("<gradient:#e0e0e0:#ffffff><b>StarDust")
                .setLore(
                        "<gray>Essence of fallen stars, shimmering with cosmic energy.",
                        "<gray>Used to imbue items with powerful Starbound traits.",
                        "",
                        "<dark_gray>Can be refined in the <gradient:#7a00cc:#cc00ff>Celestial Forge</gradient>."
                )
                .addEnchantment(Enchantment.LURE, 1)
                .applyItemFlags(ItemFlag.values())
                .addPersistentData(STARDUST_KEY, PersistentDataType.STRING, "jungleplanetog")
                .build();
    }
}
