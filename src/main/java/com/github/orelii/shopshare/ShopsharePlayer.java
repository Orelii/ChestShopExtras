package com.github.orelii.shopshare;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ShopsharePlayer{

    Shopshare plugin = Shopshare.getPlugin(Shopshare.class);
    String name = "";
    String UUID = "";

    public ShopsharePlayer(String pname, String pUUID) {
        name = pname;
        UUID = pUUID;
    }

    public void makeFile(){
        File data = new File(plugin.getDataFolder(), File.separator + UUID + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);
        
        if (!data.exists()) {
            try{
                playerData.createSection("PlayerData");
                playerData.set("PlayerData.name", name);
                playerData.set("PlayerData.UUID", UUID);

                playerData.createSection("Trusted");
                playerData.set("Trusted.list", new String[]{});

                playerData.save(data);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile(){
        File data = new File(plugin.getDataFolder(), File.separator + UUID + ".yml");
        if (!data.exists()) {
            return null;
        }
        return data;
    }
}
