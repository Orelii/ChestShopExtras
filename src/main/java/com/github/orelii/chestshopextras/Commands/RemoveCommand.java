package com.github.orelii.chestshopextras.Commands;

import com.github.orelii.chestshopextras.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RemoveCommand {

    public static void removeCommand(MiniMessage miniMessage, Player sender, String argument) throws IOException {

        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>You can't remove yourself!</red>"));
            return;
        }

        ShopsharePlayer player = new ShopsharePlayer(sender.getName(), sender.getUniqueId().toString(), sender.getLocation());
        if (player.getFile() == null) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>That player is not trusted!</red>"));
            return;
        }


        Player target = Bukkit.getPlayerExact(argument);
        String name;
        String uuid;
        File data = player.getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);
        List<String> trusted = player.getLocalTrustList();
        boolean global = false;

        if (trusted == null) {
            trusted = player.getGlobalTrustList();
            global = true;
        }

        if (target == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(argument);
            name = offlinePlayer.getName();
            uuid = offlinePlayer.getUniqueId().toString();

            // OfflinePlayers will always return a value even if no account associated exists.
            // Thus, we need to check if the name is empty instead of checking if offlinePlayer is null.
            if (name == null || name.isEmpty()) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>That player does not exist!</red>"));
                return;
            }
        } else {
            name = target.getName();
            uuid = target.getUniqueId().toString();
        }


        if (trusted.contains(uuid)) {
            trusted.remove(uuid);
            if (global) {
                playerData.set("globalTrust.list", trusted);
            } else {
                playerData.set(player.getClaimAtLocation().getID().toString() + ".list", trusted);
            }
        } else {
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>That player is not trusted!</red>"));
            return;
        }


        try {
            playerData.save(data);
        } catch (IOException e) {
            throw new IOException(e);
        }

        sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <blue>" + name + " has been removed from your" + (global ? " global" : "") + " trust list.</blue>"));
    }
}
