package com.github.orelii.shopshare;

import com.github.orelii.shopshare.Commands.ShopshareCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Shopshare extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("shopshare").setExecutor(new ShopshareCommand());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getLogger().info("Shopshare data folder has been created");
        }

        getLogger().info("Shopshare is enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
