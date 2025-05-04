package me.friedwingis.plugin.starforging.commands;

import me.friedwingis.plugin.starforging.utils.Chat;
import me.friedwingis.plugin.starforging.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 *
 * Handles the /stardust (or /sdust) command and its subcommands.
 * Players can view info about StarDust or admins can give it to others.
 */
@Command({"stardust", "sdust"})
public class StarDustCommand {

    /**
     * Displays help and information about the StarDust system to the command sender.
     * Triggered when a player runs /stardust or /sdust with no arguments.
     */
    @DefaultFor({"stardust", "sdust"})
    private void onDefault(final BukkitCommandActor actor) {
        actor.reply(Chat.EMPTY_STRING);
        actor.reply(Chat.format("<gradient:#e0e0e0:#ffffff><b>StarDust Help</b>"));
        actor.reply(Chat.format("<gray>StarDust is a rare and valuable item obtained by harvesting crops. "
                + "The higher the crop tier, the greater the chance to find one—but it's never guaranteed. "
                + "Once collected, bring your StarDust to the <white>/celestialforge</white> to imbue your armor with "
                + "powerful <yellow>Starbound Traits</yellow> that can turn the tide of any battle."));
    }

    /**
     * Gives the specified amount of StarDust to a target player.
     * Requires the sender to have the 'starforging.givedust' permission.
     */
    @Subcommand("givedust")
    @CommandPermission("starforging.givedust")
    private void onGiveSubcommand(final BukkitCommandActor actor, final Player target, final int amount) {
        final ItemStack stardust = Constants.STARDUST.asQuantity(amount);

        // Give the item directly or drop it near the player if their inventory is full
        giveOrDropItem(target, stardust);

        actor.reply(Chat.praise("Gave %s %dx StarDust!".formatted(target.getName(), amount)));
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
}