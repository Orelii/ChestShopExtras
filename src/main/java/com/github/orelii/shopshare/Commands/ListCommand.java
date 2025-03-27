package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ListCommand {

    public static void listCommand(MiniMessage miniMessage, Player sender) {

        ShopsharePlayer player = new ShopsharePlayer(sender.getName(), sender.getUniqueId().toString(), sender.getLocation());


        if (player.getFile() == null) {
            sender.sendMessage(miniMessage.deserialize("<red>You have no trusted players!</red>"));
            return;
        }


        List<String> trusted = player.getLocalTrustList();

        if (trusted == null) {
            trusted = player.getGlobalTrustList();
        }

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
                name = offlinePlayer.getName();

                if (name == "") {
                    continue;
                }
            }
            else { name = target.getName(); }
            sender.sendMessage(miniMessage.deserialize("<blue>    ○ " + name + "</blue>"));
        }
    }
}
