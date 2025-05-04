package me.friedwingis.plugin.starforging.struct;

import lombok.Getter;
import me.friedwingis.plugin.starforging.utils.Chat;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class ItemBuilder {

    private final ItemStack item;

    @Getter private String displayName;
    @Getter private List<String> lore;

    // ────────────────────────────────
    // Constructors
    // ────────────────────────────────

    /**
     * Constructs an ItemBuilder with a given material and amount.
     */
    public ItemBuilder(@NotNull final Material material, final int amount) {
        this.item = new ItemStack(material, amount);
    }

    /**
     * Constructs an ItemBuilder with a default amount of 1.
     * Applies all item flags by default to hide visual clutter.
     */
    public ItemBuilder(@NotNull final Material material) {
        this(material, 1);
        applyItemFlags(ItemFlag.values());
    }

    /**
     * Constructs an ItemBuilder from an existing item, cloning its state.
     * Captures display name and lore if present.
     */
    public ItemBuilder(@NotNull final ItemStack clone) {
        this.item = new ItemStack(clone);

        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            this.displayName = meta.hasDisplayName() ? Objects.requireNonNull(meta.displayName()).toString() : null;
            this.lore = meta.hasLore() ? Objects.requireNonNull(meta.lore()).stream().map(Component::toString).toList() : null;
        }
    }

    // ────────────────────────────────
    // Fluent Setters
    // ────────────────────────────────

    public ItemBuilder setAmount(final int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(final String name) {
        this.displayName = name;
        return applyMeta(meta -> meta.displayName(Chat.format(name)));
    }

    public ItemBuilder setLore(final List<String> lore) {
        this.lore = lore;
        return applyMeta(meta -> {
            if (lore != null)
                meta.lore(lore.stream().map(Chat::format).toList());
        });
    }

    public ItemBuilder setLore(final String... lore) {
        return setLore(lore != null ? Arrays.asList(lore) : null);
    }

    public ItemBuilder setLoreC(final List<Component> lore) {
        return applyMeta(meta -> {
            if (lore != null)
                meta.lore(lore);
        });
    }

    public ItemBuilder setLoreC(final Component... lore) {
        return setLoreC(lore != null ? Arrays.asList(lore) : null);
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        return applyMeta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder addEnchantmentIf(final boolean condition, final Enchantment enchantment, final int level) {
        return condition ? addEnchantment(enchantment, level) : this;
    }

    public ItemBuilder addPersistentData(@NotNull NamespacedKey key, @NotNull PersistentDataType type, @NotNull Object value) {
        return applyMeta(meta -> meta.getPersistentDataContainer().set(key, type, value));
    }

    public ItemBuilder removePersistentData(@NotNull NamespacedKey key) {
        return applyMeta(meta -> meta.getPersistentDataContainer().remove(key));
    }

    public ItemBuilder setCustomModelData(final int data) {
        return applyMeta(meta -> meta.setCustomModelData(data));
    }

    public ItemBuilder applyItemFlags(final ItemFlag... flags) {
        return applyMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder removeItemFlags(final ItemFlag... flags) {
        return applyMeta(meta -> meta.removeItemFlags(flags));
    }

    // ────────────────────────────────
    // Metadata Getters
    // ────────────────────────────────

    /**
     * Returns the component-based lore from the item.
     */
    public List<Component> getLoreC() {
        return Optional.ofNullable(item.getItemMeta()).map(ItemMeta::lore).orElse(List.of());
    }

    /**
     * Returns the component-based display name of the item.
     */
    public Component getDisplayC() {
        return Optional.ofNullable(item.getItemMeta()).map(ItemMeta::displayName).orElse(null);
    }

    // ────────────────────────────────
    // Utility Methods
    // ────────────────────────────────

    /**
     * Applies a mutation to the item's metadata.
     *
     * @param consumer A consumer that modifies the {@link ItemMeta}
     * @return The current builder instance
     */
    private ItemBuilder applyMeta(@NotNull final Consumer<ItemMeta> consumer) {
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            consumer.accept(meta);
            item.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Clones this builder and the internal item.
     */
    @Override
    public ItemBuilder clone() {
        var builder = new ItemBuilder(this.item.clone());

        if (this.displayName != null)
            builder.setDisplayName(this.displayName);
        if (this.lore != null)
            builder.setLore(this.lore);

        return builder;
    }

    /**
     * Builds and returns the finalized {@link ItemStack}.
     */
    public ItemStack build() {
        return item.clone(); // Defensive copy
    }
}