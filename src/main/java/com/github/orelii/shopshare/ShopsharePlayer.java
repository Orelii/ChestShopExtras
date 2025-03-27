package com.github.orelii.shopshare;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopsharePlayer{

    DataStore ds = GriefPrevention.instance.dataStore;
    Shopshare plugin = Shopshare.getPlugin(Shopshare.class);
    String name = "";
    String UUID = "";
    Location location;

    public ShopsharePlayer(String pname, String pUUID, Location plocation) {
        name = pname;
        UUID = pUUID;
        location = plocation;
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


    public List<String> getLocalTrustList(){
        File data = getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        List<String> trusted = (List<String>) playerData.getList(getClaimAtLocation().getID().toString()+".list", null);
        if (trusted == null) { trusted = new ArrayList<String>(); }

        return trusted;
    }


    public Claim getClaimAtLocation(){
        return ds.getClaimAt(location, false, ds.getPlayerData(java.util.UUID.fromString(UUID)).lastClaim);
    }
}
