package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
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

    public static void removeCommand(MiniMessage miniMessage, Player sender, String argument) {

        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<red>You can't remove yourself!</red>"));
            return;
        }

        ShopsharePlayer player = new ShopsharePlayer(sender.getName(), sender.getUniqueId().toString(), sender.getLocation());
        if (player.getFile() == null) {
            sender.sendMessage(miniMessage.deserialize("<red>That player is not trusted!</red>"));
            return;
        }


        Player target = Bukkit.getPlayerExact(argument);
        String name = "";
        String uuid = "";
        File data = player.getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);
        List<String> trusted = player.getLocalTrustList();

        if (trusted == null) {
            sender.sendMessage(miniMessage.deserialize("<red>You are not within a claim!</red>"));
            return;
        }

        if (target == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(argument);
            name = offlinePlayer.getName();
            uuid = offlinePlayer.getUniqueId().toString();

            // OfflinePlayers will always return a value even if no account associated exists.
            // Thus, we need to check if the name is empty instead of checking if offlinePlayer is null.
            if (name == "") {
                sender.sendMessage(miniMessage.deserialize("<red>That player does not exist!</red>"));
                return;
            }
        }
        else
        {
            name = target.getName();
            uuid = target.getUniqueId().toString();
        }


        if (trusted.contains(uuid)) {
            trusted.remove(uuid);
            playerData.set(player.getClaimAtLocation().getID().toString()+".list", trusted);
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


        sender.sendMessage(miniMessage.deserialize("<blue>" + name + " has been removed from your trusted list.</blue>"));
    }

}
