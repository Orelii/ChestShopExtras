package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RemoveCommand {
    public static void removeCommand(MiniMessage miniMessage, CommandSender sender, String argument) {
        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<red>You can already open your shop chests!</red>"));
            return;
        }

        Player commandPlayer = (Player) sender;
        ShopsharePlayer player = new ShopsharePlayer(commandPlayer.getName(), commandPlayer.getUniqueId().toString());
        if (player.getFile() == null) {
            sender.sendMessage(miniMessage.deserialize("<red>That player is not trusted!</red>"));
            return;
        }
        File data = player.getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        List<String> trusted = (List<String>) playerData.getList("Trusted.list", null);

        if (trusted.contains(argument)) {
            trusted.remove(argument);
            playerData.set("Trusted.list", trusted);
        }
        else {
            sender.sendMessage(miniMessage.deserialize("<red>That player is not trusted!</red>"));
            return;
        }

        try {
            playerData.save(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(miniMessage.deserialize("<blue>" + argument + " has been removed from your trusted list.</blue>"));
    }

}
