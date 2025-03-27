package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class ListCommand {
    public static void listCommand(MiniMessage miniMessage, CommandSender sender) {
        Player commandPlayer = (Player) sender;
        ShopsharePlayer player = new ShopsharePlayer(commandPlayer.getName(), commandPlayer.getUniqueId().toString());
        if (player.getFile() == null) {
            sender.sendMessage(miniMessage.deserialize("<red>You have no trusted players!</red>"));
            return;
        }
        File data = player.getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        List<String> trusted = (List<String>) playerData.getList("Trusted.list", null);
        if (trusted.isEmpty()) {
            sender.sendMessage(miniMessage.deserialize("<red>You have no trusted players!</red>"));
            return;
        }
        sender.sendMessage(miniMessage.deserialize("<aqua>Trusted players:</aqua>"));
        for (String s : trusted) {
            sender.sendMessage(miniMessage.deserialize("<blue>    ○ " + s + "</blue>"));
        }
    }
}
