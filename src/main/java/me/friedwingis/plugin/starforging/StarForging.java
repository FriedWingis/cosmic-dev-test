package me.friedwingis.plugin.starforging;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarForging extends JavaPlugin {

    // Main class instance accessible globally
    public static StarForging _I;

    // Plugin enable logic
    @Override
    public void onEnable() {
        _I = this;
    }

    // Plugin disable logic
    @Override
    public void onDisable() {
        _I = null;
    }
}
