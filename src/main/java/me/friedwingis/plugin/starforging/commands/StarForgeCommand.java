package me.friedwingis.plugin.starforging.commands;

import me.friedwingis.plugin.starforging.objects.ForgeGUI;
import me.friedwingis.plugin.starforging.utils.Chat;
import me.friedwingis.plugin.starforging.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Optional;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 **/
@Command({"starforge", "sf", "sforge"}) // Command aliases for the StarForge command
public class StarForgeCommand {

    /**
     * Default command that opens the Forge GUI for the player.
     * Triggered when the player uses /starforge or any of its aliases without subcommands.
     */
    @DefaultFor({"starforge", "sf", "sforge"})
    private void onDefaultCommand(final Player player) {
        new ForgeGUI().show(player); // Show the Forge GUI
    }

    /**
     * Subcommand that gives a specified amount of StarDust to a target player.
     * Usage: /starforge givedust [player] <amount>
     * Requires the "starforging.givedust" permission.
     */
    @Subcommand("givedust")
    @CommandPermission("starforging.givedust")
    private void onGiveSubcommand(final BukkitCommandActor actor,final Player target, final int amount) {
        final ItemStack item = Constants.STARDUST.asQuantity(amount); // Create the item with given quantity

        // Try adding the item to the player's inventory; if full, drop it at their location
        Optional.of(target.getInventory().firstEmpty())
                .filter(slot -> slot != -1)
                .ifPresentOrElse(
                        slot -> target.getInventory().addItem(item),
                        () -> target.getWorld().dropItem(target.getLocation().add(0, 0.5, 0), item)
                );

        // Confirm action to the command sender
        actor.reply(Chat.praise("Gave %s %dx StarDust!".formatted(target.getName(), amount)));
    }
}
