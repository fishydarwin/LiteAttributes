package me.darwj.liteattributes.events;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class StaticAttributes implements Listener {

    LiteAttributes plugin;
    public StaticAttributes(LiteAttributes plugin) {
        this.plugin = plugin;
    }

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
            updateStatistic(event.getPlayer(), Statistic.JUMP,
                    getPlayerWalkedDistance(event.getPlayer()));
        }
    }

    // set attributes if they respawn just to be sure
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawns(PlayerPostRespawnEvent event) {
        updateStatistic(event.getPlayer(), Statistic.DAMAGE_TAKEN,
                event.getPlayer().getStatistic(Statistic.DAMAGE_TAKEN));
        updateStatistic(event.getPlayer(), Statistic.WALK_ONE_CM,
                event.getPlayer().getStatistic(Statistic.WALK_ONE_CM));
        updateStatistic(event.getPlayer(), Statistic.JUMP,
                getPlayerWalkedDistance(event.getPlayer()));
    }

    private void updateStatistic(Player player, @NotNull Statistic statistic, int newValue) {
        switch (statistic) {
            // max health <-> damage taken
            case DAMAGE_TAKEN -> setAttribute(player, Attribute.GENERIC_MAX_HEALTH,
                    LiteAttributes.proportionalLevelValue(newValue,
                            0, 100000, -5, 15));
            // more speed <-> distance walked
            case WALK_ONE_CM -> setAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED,
                    LiteAttributes.proportionalLevelValue(newValue,
                            0, 10000000, 0, 0.05));
            // jump boost <-> jumps
            case JUMP -> setPotionEffect(player, PotionEffectType.JUMP, newValue, 500000);
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

    /**
     * Sets a potion effect conveniently, only for internal use.
     * @param player - the player
     * @param potionEffectType - the potion effect
     * @param level - the level the player is at
     * @param max - the max level
     */
    private void setPotionEffect(Player player, PotionEffectType potionEffectType, double level, double max) {
        // clear if already has
        if (player.hasPotionEffect(potionEffectType)) { player.removePotionEffect(potionEffectType); }
        // decide amplifier
        if (level < max / 2) { return; } // below half, ignore...
        int amplifier = 0; if (level >= max) { amplifier = 1; } // max level...
        // give potion effect
        PotionEffect potionEffect = new PotionEffect(potionEffectType, 999999, amplifier,
                false, false, false);
        player.addPotionEffect(potionEffect);
    }

    // update attributes on correct statistic increment
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        updateStatistic(event.getPlayer(), event.getStatistic(), event.getNewValue());
    }

    /**
     * Required for distance walked - otherwise will not count all cases.
     * @param player - the player to look for
     * @return - the proper level
     */
    public static int getPlayerWalkedDistance(Player player) {
        return player.getStatistic(Statistic.WALK_ONE_CM)
                + player.getStatistic(Statistic.SPRINT_ONE_CM)
                + player.getStatistic(Statistic.CROUCH_ONE_CM);
    }

    // special high frequency statistics that require alternate events
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        int newValue = getPlayerWalkedDistance(event.getPlayer());
        if (newValue % 500 != 0) { // avoid overkilling
            return;
        }
        updateStatistic(event.getPlayer(), Statistic.WALK_ONE_CM, newValue);
    }

}
