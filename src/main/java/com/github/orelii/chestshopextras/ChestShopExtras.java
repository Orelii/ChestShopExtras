package com.github.orelii.chestshopextras;

import com.github.orelii.chestshopextras.Commands.ShopshareCommand;
import com.github.orelii.chestshopextras.Commands.TabAutocomplete;
import com.github.orelii.chestshopextras.Events.ShopshareEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ChestShopExtras extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ShopshareEventListener(), this);
        getCommand("shopshare").setExecutor(new ShopshareCommand());
        getCommand("shopshare").setTabCompleter(new TabAutocomplete());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getLogger().info("ChestShop Extras data folder has been created");

            File shopdata = new File(getDataFolder(), "shops.yml");
            getLogger().info("Shopshare shops file has been created");

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
