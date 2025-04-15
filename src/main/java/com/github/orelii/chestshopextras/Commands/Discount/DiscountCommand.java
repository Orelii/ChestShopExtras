package com.github.orelii.chestshopextras.Commands.Discount;

import com.github.orelii.chestshopextras.Commands.Discount.AddCommand;
import com.github.orelii.chestshopextras.Commands.Discount.RemoveCommand;
import com.github.orelii.chestshopextras.Commands.Discount.ListCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DiscountCommand implements CommandExecutor {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Only a player may use this command!</red>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Usage: /discount [add/remove/list]"));
            return true;
        }

        // /discount add command
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 3) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Usage: /discount add <player> <percentage></red>"));
                return true;
            }
            try {
                AddCommand.addCommand((Player) sender, args[1], args[2]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // /discount remove command
        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Usage: /discount remove <player></red>"));
                return true;
            }
            try {
                RemoveCommand.removeCommand(miniMessage, (Player) sender, args[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // /discount list command
        else if (args[0].equalsIgnoreCase("list")) {
            ListCommand.listCommand(miniMessage, (Player) sender);
        }

        // /discount help command
        else if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(miniMessage.deserialize("<dark_aqua>========</dark_aqua><aqua>ChestShop Extras</aqua><dark_aqua>========</dark_aqua>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/discount add <player> <percentage></gold><gray> - Gives the player a discount at the shop you are looking at. Negative discounts increase the price.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/discount remove <player></gold><gray> - Removes a player's discount from the shop you are looking at.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/discount list</gold><gray> - Lists all the players who have discounts for the shop you are looking at.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<gold>/discount help</gold><gray> - Display this help message.</gray>"));
            sender.sendMessage(miniMessage.deserialize("<dark_aqua>========</dark_aqua><aqua>ChestShop Extras</aqua><dark_aqua>========</dark_aqua>"));
        }

        else {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>Unknown command!</red>"));
            return true;
        }

        return true;
    }
}
