package com.github.orelii.chestshopextras.Commands.Discount;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.github.orelii.chestshopextras.ChestShopExtras;
import com.github.orelii.chestshopextras.ShopsharePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoveCommand {

    public static void removeCommand(MiniMessage miniMessage, Player sender, String argument) throws IOException {
        Block target = sender.getTargetBlock((Set<Material>) null, 10);

        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You can't remove yourself!</red>"));
            return;
        }

        if (!ChestShopSign.isShopBlock(target)){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You must be looking at a shop to remove a discount!</red>"));
            return;
        }

        File shopdata = new File(ChestShopExtras.getPlugin(ChestShopExtras.class).getDataFolder(), File.separator + "shops.yml");
        FileConfiguration shops = YamlConfiguration.loadConfiguration(shopdata);
        String ownerUUID = (String) shops.get(target.getLocation().toString());

        if (ownerUUID == null) return;
        if (ownerUUID.isEmpty()) return;

        if (!ownerUUID.equals(sender.getUniqueId().toString())){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You are not the owner of this shop!</red>"));
            return;
        }

        File data = new File(ChestShopExtras.getPlugin(ChestShopExtras.class).getDataFolder(), File.separator + "discounts.yml");
        FileConfiguration discountData = YamlConfiguration.loadConfiguration(data);

        @NotNull List<Map<?, ?>> discounts = discountData.getMapList(target.getLocation().toString());
        if (discounts.isEmpty()) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>That player does not have a discount at this shop!</red>"));
            return;
        }

        Player player = Bukkit.getPlayerExact(argument);
        String name;
        String uuid;

        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(argument);
            name = offlinePlayer.getName();
            uuid = offlinePlayer.getUniqueId().toString();

            // OfflinePlayers will always return a value even if no account associated exists.
            // Thus, we need to check if the name is empty instead of checking if offlinePlayer is null.
            if (name == null || name.isEmpty()) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>That player does not exist!</red>"));
                return;
            }
        } else {
            name = player.getName();
            uuid = player.getUniqueId().toString();
        }

        // Loop through all discounts and remove the target player's if it exists
        boolean broken = false;
        for (int i = 0; i < discounts.size(); i++) {
            Map<?, ?> discount = discounts.get(i);
            if (discount.keySet().contains(uuid)) {
                discounts.remove(discount);
                broken = true;
                break;
            }
        }

        // If the loop was not broken, the target player does not exist within the discount list
        if (!broken) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>That player does not have a discount at this shop!</red>"));
            return;
        }

        discountData.set(target.getLocation().toString(), discounts);

        try {
            discountData.save(data);
        } catch (IOException e) {
            throw new IOException(e);
        }

        sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <blue>" + name + "'s discount has been removed from this shop.</blue>"));
    }
}
