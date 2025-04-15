package com.github.orelii.chestshopextras;

import com.github.orelii.chestshopextras.Commands.Discount.DiscountCommand;
import com.github.orelii.chestshopextras.Commands.Shopshare.ShopshareCommand;
import com.github.orelii.chestshopextras.Commands.Shopshare.ShopshareAutocomplete;
import com.github.orelii.chestshopextras.Events.DiscountEvents;
import com.github.orelii.chestshopextras.Events.ShopshareEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ChestShopExtras extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ShopshareEvents(), this);
        Bukkit.getPluginManager().registerEvents(new DiscountEvents(), this);

        getCommand("shopshare").setExecutor(new ShopshareCommand());
        getCommand("shopshare").setTabCompleter(new ShopshareAutocomplete());

        getCommand("discount").setExecutor(new DiscountCommand());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getLogger().info("ChestShop Extras data folder has been created");

            File shopdata = new File(getDataFolder(), "shops.yml");
            getLogger().info("Shops file has been created");

            File discountdata = new File(getDataFolder(), "discounts.yml");
            getLogger().info("Discounts file has been created");
        }

        getLogger().info("ChestShop Extras is enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
