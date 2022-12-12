package me.darwj.liteattributes.events;

import me.darwj.liteattributes.LiteAttributes;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class StaticAttributes implements Listener {
    /*
     * Static attributes can be managed by Minecraft's attribute system. This is helpful
     * because we can stop ignoring attributes like these after they max out. Ideally,
     * this helps us save some event processing power.
     *
     * Likewise, we don't have to load them on player join. Minecraft should handle this
     * for us. Because the saving is so optimized, the statistics will likely persist
     * even after crashes.
     *
     * Additionally, we are allowed to use monitor-only events for statistic increments.
     * This helps because we don't overload other plugins that may care about it.
     */

    // set initial attributes if they are a new player
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoins(PlayerJoinEvent event) {
        if (event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()
                .stream().filter((m) -> { return m.getName().equals("liteattributes"); }).findFirst().isEmpty()) {
            updateStatistic(event.getPlayer(), Statistic.DAMAGE_TAKEN,
                    event.getPlayer().getStatistic(Statistic.DAMAGE_TAKEN));
            updateStatistic(event.getPlayer(), Statistic.WALK_ONE_CM,
                    event.getPlayer().getStatistic(Statistic.WALK_ONE_CM));
        }
    }

    private void updateStatistic(Player player, @NotNull Statistic statistic, int newValue) {
        switch (statistic) {
            // max health <-> damage taken
            case DAMAGE_TAKEN -> setAttribute(player, Attribute.GENERIC_MAX_HEALTH,
                    LiteAttributes.proportionalLevelValue(newValue,
                            0, 500000, -5, 15));
            case WALK_ONE_CM -> setAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED,
                    LiteAttributes.proportionalLevelValue(newValue,
                            0, 10000000, 0, 0.05));
        }
    }

    /**
     * Sets an attribute conveniently, only for internal use.
     * @param player - the player
     * @param attribute - the attribute
     * @param newValue - the new value
     */
    // private static final String BaseUniqueIdString ="9a7660b2-17fd-4087-9e93-242f444132fX";
    private void setAttribute(Player player, Attribute attribute, double newValue) {
        // String appendedUniqueId = BaseUniqueIdString.replace('X', append);
        // UUID baseUniqueId = UUID.fromString(appendedUniqueId);

        AttributeModifier newModifier =
                new AttributeModifier(
                        //baseUniqueId,
                        "liteattributes",
                        newValue,
                        AttributeModifier.Operation.ADD_NUMBER);

        AttributeInstance attributeInstance = player.getAttribute(attribute);

        assert attributeInstance != null;
        Optional<AttributeModifier> attributeModifier = attributeInstance.getModifiers()
                .stream().filter((m) -> { return m.getName().equals("liteattributes"); }).findFirst();

        attributeModifier.ifPresent(attributeInstance::removeModifier);
        attributeInstance.addModifier(newModifier);
    }

    // update attributes on correct statistic increment
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        updateStatistic(event.getPlayer(), event.getStatistic(), event.getNewValue());
    }

    // special high frequency statistics that require alternate events
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        int newValue = event.getPlayer().getStatistic(Statistic.WALK_ONE_CM);
        if (newValue % 500 != 0) { // avoid overkilling
            return;
        }
        updateStatistic(event.getPlayer(), Statistic.WALK_ONE_CM, newValue);
    }

}
