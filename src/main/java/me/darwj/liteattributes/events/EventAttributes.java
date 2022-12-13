package me.darwj.liteattributes.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.darwj.liteattributes.LiteAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class EventAttributes implements Listener {

    LiteAttributes plugin;
    public EventAttributes(LiteAttributes plugin) {
        this.plugin = plugin;
    }

    /*
     * This is a place for attributes which can't be statically loaded using Minecraft's
     * attribute system. Instead, we must handle each event individually, looking at the
     * statistic, and performing some calculations.
     *
     * The prime example of this is -jumping-
     */

    private final double JumpOffset2Ticks = 0.33319999363422365;
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJumps(PlayerJumpEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            double jumpHeightBonus =
                    LiteAttributes.proportionalLevelValue(event.getPlayer().getStatistic(Statistic.JUMP),
                    0, 100000, 0, 0.125);
            Vector target = event.getFrom().subtract(event.getTo()).toVector();

            Vector velocity = event.getPlayer().getVelocity();
            velocity.setY(JumpOffset2Ticks + jumpHeightBonus);

            velocity.setX(-target.getX() / 2);
            velocity.setZ(-target.getZ() / 2);

            event.getPlayer().setVelocity(velocity);
        });
    }


}
