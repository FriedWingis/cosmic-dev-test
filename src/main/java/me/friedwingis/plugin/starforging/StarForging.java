package me.friedwingis.plugin.starforging;

import me.friedwingis.plugin.starforging.commands.StarDustCommand;
import me.friedwingis.plugin.starforging.commands.CelestialForgeCommand;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class StarForging extends JavaPlugin {

    private BukkitCommandHandler commandHandler; // Handler to manage commands for the plugin

    /**
     * Called when the plugin is enabled. Registers event listeners and commands.
     */
    @Override
    public void onEnable() {
        // Register player-related event listeners (such as handling crop farming)
        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        // Initialize the command handler and register our custom commands
        this.commandHandler = BukkitCommandHandler.create(this);
        this.commandHandler.register(new CelestialForgeCommand()); // Register the "starforge" command
        this.commandHandler.register(new StarDustCommand()); // Register the "stardust" command
    }

    /**
     * Called when the plugin is disabled. This is where any cleanup logic would go.
     */
    @Override
    public void onDisable() {
        // Placeholder for disabling actions if needed (e.g., saving data or stopping tasks).
    }
}
