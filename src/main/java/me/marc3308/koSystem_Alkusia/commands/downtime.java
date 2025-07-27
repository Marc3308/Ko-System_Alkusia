package me.marc3308.koSystem_Alkusia.commands;

import me.marc3308.koSystem_Alkusia.KoSystem_Alkusia;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class downtime implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender.isOp()) {
            if (args.length == 1) {
                try {
                    int time = Integer.parseInt(args[0]);
                    if (time > 0) {
                        KoSystem_Alkusia.koZeit = time;
                        sender.sendMessage("§aDowntime set to: " + time + " seconds");
                    } else {
                        sender.sendMessage("§cTime must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number format. Please enter a valid integer.");
                }
            } else {
                sender.sendMessage("§cUsage: /koZeit <time_in_seconds>");
            }
        } else {
            sender.sendMessage("§cYou do not have permission to use this command.");
        }
        return true;
    }
}
