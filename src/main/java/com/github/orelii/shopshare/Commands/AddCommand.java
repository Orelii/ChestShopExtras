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

public class AddCommand {
    public static void addCommand(MiniMessage miniMessage, Player sender, String argument) {
        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<red>You can already open your shop chests!</red>"));
            return;
        }

        Player target = null;

        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(argument)) { target = p; break; }
        }

        if (target == null){
            sender.sendMessage(miniMessage.deserialize("<red>That player is not online!</red>"));
            return;
        }

        Player commandPlayer = (Player) sender;
        ShopsharePlayer player = new ShopsharePlayer(commandPlayer.getName(), commandPlayer.getUniqueId().toString());
        if (player.getFile() == null) {
            player.makeFile();
        }
        File data = player.getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        List<String> trusted = (List<String>) playerData.getList("Trusted.list", null);

        if (!trusted.contains(target.getUniqueId().toString())) {
            trusted.add(target.getUniqueId().toString());
            playerData.set("Trusted.list", trusted);
        }
        else {
            sender.sendMessage(miniMessage.deserialize("<red>That player is already trusted!</red>"));
            return;
        }

        try {
            playerData.save(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(miniMessage.deserialize("<blue>" + target.getName() + " has been added to your trusted list.</blue>"));
    }
}
