package com.github.orelii.chestshopextras.Commands.Shopshare;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ShopshareCommand implements CommandExecutor {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Only a player may use this command!</red>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Usage: /shopshare [add/remove/list/help]"));
            return true;
        }
        // /shopshare add command
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Usage: /shopshare add <player></red>"));
                return true;
            }
            try {
                AddCommand.addCommand((Player) sender, args[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // /shopshare remove command
        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Usage: /shopshare remove <player></red>"));
                return true;
            }
            try {
                RemoveCommand.removeCommand(miniMessage, (Player) sender, args[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // /shopshare list command
        else if (args[0].equalsIgnoreCase("list")) {
            ListCommand.listCommand(miniMessage, (Player) sender);
        }

        // /shopshare help command
        else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(miniMessage.deserialize("<dark_aqua>========</dark_aqua><aqua>ChestShop Extras</aqua><dark_aqua>========</dark_aqua>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/shopshare add <player></gold><gray> - Adds a player to your shopshare list.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/shopshare remove <player></gold><gray> - Removes a player from your shopshare list.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/shopshare list</gold><gray> - Lists all the players on your shopshare list.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/shopshare help</gold><gray> - Display this help message.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<dark_aqua>========</dark_aqua><aqua>ChestShop Extras</aqua><dark_aqua>========</dark_aqua>"));
        }

        // /shopshare version command
      //  else if (args[0].equalsIgnoreCase("version")||args[0].equalsIgnoreCase("v")) {
       //     sender.sendMessage(miniMessage.deserialize("<dark_aqua>Shopshare version 1.0.3 - By Oreli</dark_aqua>"));
        //    sender.sendMessage(miniMessage.deserialize("<dark_aqua>https://github.com/Orelii/Shopshare</dark_aqua>"));
        //}

        else {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Unknown command!</red>"));
            return true;
        }

        return true;
    }
}