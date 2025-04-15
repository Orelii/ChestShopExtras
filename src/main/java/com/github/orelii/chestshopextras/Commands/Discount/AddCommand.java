package com.github.orelii.chestshopextras.Commands.Discount;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.github.orelii.chestshopextras.ChestShopExtras;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AddCommand {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();


    public static void addCommand(Player sender, String argument, String value) throws IOException {
        Block target = sender.getTargetBlock((Set<Material>) null, 10);

        if (argument.equalsIgnoreCase(sender.getName())){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You cannot give yourself a discount!</red>"));
            return;
        }

        if (!ChestShopSign.isShopBlock(target)){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You must be looking at a shop to add a discount!</red>"));
            return;
        }

        if (!NumberUtils.isCreatable(value)) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You must provide a valid number between -100 and 100.</red>"));
            return;
        }

        if (Float.parseFloat(value) < -100 || Float.parseFloat(value) > 100){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>You must provide a valid number between -100 and 100.</red>"));
            return;
        }

        Player player = Bukkit.getPlayer(argument);
        if (player == null){
            sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>That player is not online!"));
            return;
        }

        int discountValue = Math.round(Float.parseFloat(value));

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
        FileConfiguration discounts = YamlConfiguration.loadConfiguration(data);

        List<Map<?, ?>> playerDiscounts = discounts.getMapList(target.getLocation().toString());
        if (playerDiscounts.isEmpty()) {
            playerDiscounts = new ArrayList<>();
        }

        boolean broken = false;
        for (int i = 0; i < playerDiscounts.size(); i++) {
            Map<?, ?> discount = playerDiscounts.get(i);
            if (discount.containsKey(player.getUniqueId().toString())) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>That player already has a discount! Remove it first to change it.</red>"));
                broken = true;
                break;
            }
        }
        if (broken) return;

        Map<String, Integer> playerDiscount = new HashMap<>();
        playerDiscount.put(Bukkit.getPlayer(argument).getUniqueId().toString(), discountValue);
        playerDiscounts.add(playerDiscount);

        discounts.set(target.getLocation().toString(), playerDiscounts);
        sender.sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <blue>" + player.getName() + " has been given a discount of " + discountValue + "% at this shop.</blue>"));

        try {
            discounts.save(data);
        } catch (IOException ex) {
            throw new IOException("Could not save discounts.yml after new discount added", ex);
        }

    }
}
