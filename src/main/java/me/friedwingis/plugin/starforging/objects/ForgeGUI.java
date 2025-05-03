package me.friedwingis.plugin.starforging.objects;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import me.friedwingis.plugin.starforging.utils.Constants;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
public class ForgeGUI extends ChestGui {

    // Main pane that holds all interactive GUI items
    private final StaticPane pane;

    /**
     * Initializes the Celestial Forge GUI with custom layout and controls.
     */
    public ForgeGUI() {
        super(3, "Celestial Forge"); // Create a 3-row GUI titled "Celestial Forge"

        // Cancel all click events to prevent unintended behavior
        setOnGlobalClick(event -> event.setCancelled(true));

        // Optional handlers for interacting with GUI or player inventory
        setOnTopClick(this::handleTopClick);
        setOnBottomClick(this::handleBottomClick);

        this.pane = new StaticPane(9, 3); // Define a 9x3 layout grid

        // Add a help/info book in the bottom-right corner
        this.pane.addItem(new GuiItem(createInfoBook()), Slot.fromIndex(26));

        // Add pink and blue decorative pane elements for visual structure
        addPaneItems(Constants.PINK_PANES, Material.PURPLE_STAINED_GLASS_PANE);
        addPaneItems(Constants.BLUE_PANES, Material.CYAN_STAINED_GLASS_PANE);

        // Attach the pane to the GUI
        addPane(this.pane);
    }

    /**
     * Handles clicks within the top inventory (the GUI itself).
     *
     * @param event Inventory click event
     */
    private void handleTopClick(final InventoryClickEvent event) {
        // To be implemented — could handle item insertion, validation, etc.
    }

    /**
     * Handles clicks in the player's bottom inventory.
     *
     * @param event Inventory click event
     */
    private void handleBottomClick(final InventoryClickEvent event) {
        // To be implemented — could restrict taking items while forging, etc.
    }

    /**
     * Fills the GUI with decorative glass pane items at specified slot positions.
     *
     * @param slots    Array of GUI slot indices
     * @param material The material type for the glass pane
     */
    private void addPaneItems(final int[] slots, final Material material) {
        final ItemStack item = createBlankItem(material);
        for (final int slot : slots) {
            this.pane.addItem(new GuiItem(item), Slot.fromIndex(slot));
        }
    }

    /**
     * Creates a blank glass pane item with no visible display name.
     *
     * @param material The glass pane material
     * @return The blank ItemStack
     */
    private ItemStack createBlankItem(final Material material) {
        return new ItemBuilder(material)
                .setDisplayName("<red>") // Red placeholder color tag (likely renders as blank)
                .build();
    }

    /**
     * Creates the "Forge Help" info book shown in the GUI.
     *
     * @return ItemStack representing the help book
     */
    private ItemStack createInfoBook() {
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("<#cc00ff><b>Forge Help") // Gradient and bold style
                .setLore("") // Placeholder, can be expanded with usage tips
                .build();
    }
}
