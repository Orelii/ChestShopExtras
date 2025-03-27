package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ShopshareCommand implements CommandExecutor {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(miniMessage.deserialize("<red>Only a player may use this command!</red>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: /shopshare [add/remove/list]"));
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<red>Usage: /shopshare add <player></red>"));
                return true;
            }
            addCommand(sender, args[1]);
        }
        else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(miniMessage.deserialize("<red>Usage: /shopshare remove <player></red>"));
                return true;
            }
            removeCommand(sender, args[1]);
        }
        else if (args[0].equalsIgnoreCase("list")) {
            listCommand(sender);
        }
        else {
            sender.sendMessage(miniMessage.deserialize("<red>Unknown command!</red>"));
            return true;
        }

        return true;
    }

    private void addCommand(CommandSender sender, String argument) {
        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<red>You can already open your shop chests!</red>"));
            return;
        }

        Player target = null;

        for (Player p : sender.getServer().getOnlinePlayers()) {
            Bukkit.getLogger().info(argument + " " + p.getName());
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

        if (!trusted.contains(target.getName())) {
            trusted.add(target.getName().toString());
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

    private void removeCommand(CommandSender sender, String argument) {
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

    private void listCommand(CommandSender sender) {
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