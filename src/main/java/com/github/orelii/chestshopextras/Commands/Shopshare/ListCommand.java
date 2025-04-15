package com.github.orelii.chestshopextras.Commands.Shopshare;

import com.github.orelii.chestshopextras.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ListCommand {

    public static void listCommand(MiniMessage miniMessage, Player sender) {

        ShopsharePlayer player = new ShopsharePlayer(sender.getName(), sender.getUniqueId().toString(), sender.getLocation());
        boolean global = false;

        if (player.getFile() == null) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You have no trusted players!</red>"));
            return;
        }


        List<String> trusted = player.getLocalTrustList();

        if (trusted == null) {
            trusted = player.getGlobalTrustList();
            global = true;
        }

        if (trusted.isEmpty()) {
            if (global) { sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You have no globally trusted players!</red>")); }
            else { sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You have no trusted players!</red>")); }
            return;
        }

        if (global) { sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <aqua>Globally trusted players:</aqua>")); }
        else { sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <aqua>Trusted players:</aqua>")); }
        for (String s : trusted) {

            Player target = Bukkit.getPlayer(s);
            String name;

            if (target == null) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(s));
                name = offlinePlayer.getName();

                if (name == null || name.isEmpty()) {
                    continue;
                }
            } else { name = target.getName(); }
            sender.sendMessage(miniMessage.deserialize("<blue>    ○ " + name + "</blue>"));
        }
    }
}
