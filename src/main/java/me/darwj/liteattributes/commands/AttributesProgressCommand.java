package me.darwj.liteattributes.commands;

import me.darwj.liteattributes.events.StaticAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AttributesProgressCommand implements CommandExecutor, TabCompleter {


    public AttributesProgressCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do this.");
            return true;
        }

        Player player = (Player) sender;

        sender.sendMessage(ChatColor.GRAY + "- - - - - - - - - -");
        sender.sendMessage(ChatColor.GOLD + "Attribute Progress");
        sender.sendMessage(ChatColor.GRAY + "- - - - - - - - - -");
        {
            double speed;
            if (StaticAttributes.getPlayerWalkedDistance(player) > 10000000) {
                speed = 100;
            } else {
                speed = StaticAttributes.getPlayerWalkedDistance(player) / 10000000d;
                speed = Math.floor(speed * 1000) / 10;
            }
            sender.sendMessage(ChatColor.YELLOW + "Speed        " + speed + "%");
        }
        {
            double jump;
            if (player.getStatistic(Statistic.JUMP) > 500000) {
                jump = 100;
            } else {
                jump = player.getStatistic(Statistic.JUMP) / 500000d;
                jump = Math.floor(jump * 1000) / 10;
            }
            sender.sendMessage(ChatColor.YELLOW + "Jump         " + jump + "%");
        }
        {
            double endurance;
            if (player.getStatistic(Statistic.DAMAGE_TAKEN) > 100000) {
                endurance = 100;
            } else {
                endurance = player.getStatistic(Statistic.DAMAGE_TAKEN) / 100000d;
                endurance = Math.floor(endurance * 1000) / 10;
            }
            sender.sendMessage(ChatColor.YELLOW + "Endurance   " + endurance + "%");
        }
        sender.sendMessage(ChatColor.GRAY + "- - - - - - - - - -");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        return new ArrayList<>();
    }
}
