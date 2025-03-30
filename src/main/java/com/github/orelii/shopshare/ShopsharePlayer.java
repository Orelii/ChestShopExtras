package com.github.orelii.shopshare;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopsharePlayer{

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    DataStore ds = GriefPrevention.instance.dataStore;
    Shopshare plugin = Shopshare.getPlugin(Shopshare.class);
    String _Name = "";
    String _UUID = "";
    Location _Location;

    public ShopsharePlayer(String name, String uuid, Location location) {
        _Name = name;
        _UUID = uuid;
        _Location = location;
    }

    public ShopsharePlayer(Player player) {
        _Name = player.getName();
        _UUID = player.getUniqueId().toString();
        _Location = player.getLocation();
    }

    public ShopsharePlayer(String uuid, Location location) {
        _UUID = uuid;
        _Location = location;
    }

    public void makeFile(){
        File data = new File(plugin.getDataFolder(), File.separator + _UUID + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        if (!data.exists()) {
            try{
                playerData.createSection("PlayerData");
                playerData.set("PlayerData.name", _Name);
                playerData.set("PlayerData.UUID", _UUID);

                playerData.createSection("GlobalTrust");
                playerData.set("GlobalTrust.list", new ArrayList<String>());

                playerData.save(data);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public File getFile(){
        File data = new File(plugin.getDataFolder(), File.separator + _UUID + ".yml");
        if (!data.exists()) {
            return null;
        }
        return data;
    }


    public List<String> getLocalTrustList(){
        File data = getFile();

        if (data == null) { return null; }

        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);
        Claim claim = getClaimAtLocation();

        if (claim == null) { return null; }

        List<String> trusted = (List<String>) playerData.getList(claim.getID().toString()+".list", null);
        if (trusted == null) { trusted = new ArrayList<String>(); }

        return trusted;
    }


    public List<String> getGlobalTrustList(){
        File data = getFile();

        if (data == null) { return null; }

        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        List<String> trusted = (List<String>) playerData.getList("globalTrust.list", null);
        if (trusted == null) { trusted = new ArrayList<String>(); }

        return trusted;
    }


    public Claim getClaimAtLocation(){
        return ds.getClaimAt(_Location, false, ds.getPlayerData(java.util.UUID.fromString(_UUID)).lastClaim);
    }

    public String getName(){
        return _Name;
    }

    public UUID getUUID(){
        return UUID.fromString(_UUID);
    }

    public Location getLocation() {
        return _Location;
    }
}
