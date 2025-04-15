package com.github.orelii.chestshopextras.Commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VersionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        commandSender.sendMessage(miniMessage.deserialize("<dark_aqua>ChestShop Extras version 1.1.0 - By Oreli</dark_aqua>"));
        commandSender.sendMessage(miniMessage.deserialize("<dark_aqua>https://github.com/Orelii/ChestShopExtras</dark_aqua>"));
        return true;
    }
}
