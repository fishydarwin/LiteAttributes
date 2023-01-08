package me.darwj.liteattributes.events;

import me.darwj.liteattributes.LiteAttributes;
import org.bukkit.event.Listener;

@Deprecated
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

//
//    DEPRECATED IN FAVOR OF JUMP BOOST FOR CLIENT SIDE REASONS
//
//    private final double JumpOffset2Ticks = 0.33319999363422365;
//    private final double DefaultJumpTimeSeconds = 19d / 30d;
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onPlayerJumps(PlayerJumpEvent event) {
//        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
//            double jumpHeightBonus =
//                    LiteAttributes.proportionalLevelValue(event.getPlayer().getStatistic(Statistic.JUMP),
//                            0, 100000, 0, 0.125);
//            Vector target = event.getFrom().subtract(event.getTo()).toVector();
//
//            Vector velocity = event.getPlayer().getVelocity();
//            double predictedPing = event.getPlayer().getPing() / 1000d;
//            double pingPredictedJump = jumpHeightBonus;
//            pingPredictedJump = (DefaultJumpTimeSeconds - predictedPing) / DefaultJumpTimeSeconds * pingPredictedJump;
//            velocity.setY(JumpOffset2Ticks + pingPredictedJump);
//
//            velocity.setX(-target.getX() / 2);
//            velocity.setZ(-target.getZ() / 2);
//
//            event.getPlayer().setVelocity(velocity);
//        });
//    }

}
