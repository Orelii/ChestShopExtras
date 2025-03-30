package com.github.orelii.shopshare;

import com.github.orelii.shopshare.Commands.ShopshareCommand;
import com.github.orelii.shopshare.Commands.TabAutocomplete;
import com.github.orelii.shopshare.Events.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Shopshare extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        getCommand("shopshare").setExecutor(new ShopshareCommand());
        getCommand("shopshare").setTabCompleter(new TabAutocomplete());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getLogger().info("Shopshare data folder has been created");

            File data = new File(getDataFolder(), "shops.yml");
            getLogger().info("Shopshare shops file has been created");
        }

        getLogger().info("Shopshare is enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
