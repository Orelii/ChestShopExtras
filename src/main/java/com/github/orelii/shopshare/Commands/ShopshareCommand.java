package com.github.orelii.shopshare.Commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopshareCommand implements CommandExecutor {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(miniMessage.deserialize("<red>Only a player may use this command!</red>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: /shopshare [add/remove/list]"));
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<red>Usage: /shopshare add <player></red>"));
                return true;
            }
            AddCommand.addCommand(miniMessage, (Player) sender, args[1]);
        }
        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<red>Usage: /shopshare remove <player></red>"));
                return true;
            }
            RemoveCommand.removeCommand(miniMessage, (Player) sender, args[1]);
        }
        else if (args[0].equalsIgnoreCase("list")) {
            ListCommand.listCommand(miniMessage, (Player) sender);
        }
        else {
            sender.sendMessage(miniMessage.deserialize("<red>Unknown command!</red>"));
            return true;
        }

        return true;
    }
}