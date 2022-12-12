package me.darwj.liteattributes;

import me.darwj.liteattributes.commands.AttributesProgressCommand;
import me.darwj.liteattributes.events.EventAttributes;
import me.darwj.liteattributes.events.StaticAttributes;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LiteAttributes extends JavaPlugin {

    @Override
    public void onEnable() {

        this.getServer().getPluginManager().registerEvents(new StaticAttributes(), this);
        this.getServer().getPluginManager().registerEvents(new EventAttributes(this), this);

        {
            PluginCommand attributesProgressCommand = this.getCommand("attributes");
            AttributesProgressCommand attributesProgressCommandHandler = new AttributesProgressCommand();
            attributesProgressCommand.setExecutor(attributesProgressCommandHandler);
            attributesProgressCommand.setTabCompleter(attributesProgressCommandHandler);
        }

    }

    /**
     * Uses proportions to find how far a level value is up between min-max.
     * @param who For who to do this
     * @param min The minimum statistic level
     * @param max The maximum statistic level
     * @param start Attribute level at beginning
     * @param end Attribute level at maximum
     * @return
     */
    public static double proportionalLevelValue(int who, int min, int max, double start, double end) {
        if (min + who > max) { return end; }
        double proportion = (min + who) / ((double) max);
        return start + (end - start) * proportion;
    }

}
