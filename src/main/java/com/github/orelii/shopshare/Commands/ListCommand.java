package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ListCommand {
    public static void listCommand(MiniMessage miniMessage, Player sender) {
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
            Player target = Bukkit.getPlayer(s);
            String name = "";
            if (target == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(s));
                Bukkit.getLogger().info(offlinePlayer.getName());
                name = offlinePlayer.getName();
                if (name == "") {
                    continue;
                }
            }
            else{
                name = target.getName();
            }
            sender.sendMessage(miniMessage.deserialize("<blue>    ○ " + name + "</blue>"));
        }
    }
}
