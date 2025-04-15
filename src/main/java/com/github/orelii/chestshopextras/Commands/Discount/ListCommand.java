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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ListCommand {

    public static void listCommand(MiniMessage miniMessage, Player sender) {
        Block target = sender.getTargetBlock((Set<Material>) null, 10);

        if (!ChestShopSign.isShopBlock(target)){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You must be looking at a shop to use this command.</red>"));
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
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>This shop has no discounts.</red>"));
            return;
        }

        sender.sendMessage(miniMessage.deserialize("<aqua>[CSE] Discounted players:</aqua>"));
        for (Map<?, ?> discount : discounts) {
            discount.forEach((key, value) -> {
                Player player = Bukkit.getPlayer(key.toString());
                String name;

                if (player == null) {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(key.toString()));
                    name = offlinePlayer.getName();

                    if (name == null || name.isEmpty()) {
                        return;
                    }
                } else { name = player.getName(); }
                sender.sendMessage(miniMessage.deserialize("<blue>    ○ " + name + " - " + value.toString() + "% </blue>"));
            });
        }
    }
}
