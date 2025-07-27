package me.marc3308.koSystem_Alkusia.commands;

import me.marc3308.koSystem_Alkusia.KoSystem_Alkusia;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class picuptime implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(sender instanceof Player p && p.isOp()){
            if(args.length==1){
                try {
                    int time = Integer.parseInt(args[0]);
                    if(time>0){
                        KoSystem_Alkusia.pickuptime = time;
                        p.sendMessage("§aPickup time set to: " + time + " seconds");
                    } else {
                        p.sendMessage("§cTime must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage("§cInvalid number format. Please enter a valid integer.");
                }
            } else {
                p.sendMessage("§cUsage: /picuptime <time_in_seconds>");
            }
        } else {
            sender.sendMessage("§cYou do not have permission to use this command.");
        }
        return false;
    }
}
