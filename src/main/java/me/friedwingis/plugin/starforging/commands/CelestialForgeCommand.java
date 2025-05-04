package me.friedwingis.plugin.starforging.commands;

import me.friedwingis.plugin.starforging.struct.ForgeGUI;
import me.friedwingis.plugin.starforging.utils.Chat;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;

/**
 * Copyright Fried - 2025
 * All code is private and not to be used by any
 * other entity unless explicitly stated otherwise.
 *
 * Handles the /celestialforge (or /cforge) command and related help output.
 * Players use this to access the Starbound trait forging interface.
 */
@Command({"celestialforge", "cforge"})
public class CelestialForgeCommand {

    /**
     * Opens the Celestial Forge GUI when a player runs /celestialforge or /cforge with no arguments.
     */
    @DefaultFor({"celestialforge", "cforge"})
    private void onDefaultCommand(final Player player) {
        new ForgeGUI().open(player);
    }

    /**
     * Displays help information about the Celestial Forge system.
     */
    @Subcommand("help")
    private void onHelpSubcommand(final BukkitCommandActor actor) {
        actor.reply(Chat.EMPTY_STRING);
        actor.reply(Chat.format("<gradient:#7a00cc:#cc00ff><b>Celestial Forge Help</b>"));
        actor.reply(Chat.format("<gray>The Celestial Forge is where you fuse your precious StarDust with <white>Diamond</white> or <white>Netherite</white> armor. "
                + "Once the forging is complete, your gear will be infused with extraordinary power—unlocking potent <yellow>Starbound Traits</yellow> "
                + "that set you apart from the rest."));
        actor.reply(Chat.format("<gray> * Use <white>/celestialforge</white> to get started."));
    }
}

