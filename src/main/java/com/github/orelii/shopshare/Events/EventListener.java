package com.github.orelii.shopshare.Events;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.github.orelii.shopshare.Shopshare;
import com.github.orelii.shopshare.ShopsharePlayer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class EventListener implements Listener {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @EventHandler
    public void OpenChestEvent(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!ChestShopSign.isShopBlock(block)) return;

        Chest chest = (Chest) block.getState();
        ShopsharePlayer player = new ShopsharePlayer(e.getPlayer());
        Claim claim = player.getClaimAtLocation();

        if (claim != null){
            if (claim.getPermission(player.getUUID().toString()) != ClaimPermission.Inventory
                    && claim.getPermission(player.getUUID().toString()) != ClaimPermission.Build) {
                if (claim.getOwnerID() != player.getUUID()) return;
                if (claim.getOwnerName() != player.getName()) return;
                e.getPlayer().sendMessage(miniMessage.deserialize("<red>You do not have permission to open this shop. Ask the claim owner to trust you.</red>"));
                return;
            }
        }

        File data = new File(Shopshare.getPlugin(Shopshare.class).getDataFolder(), File.separator + "shops.yml");
        FileConfiguration shops = YamlConfiguration.loadConfiguration(data);
        String ownerUUID = (String) shops.get(chest.getLocation().toString());

        if (ownerUUID == null) return;
        if (ownerUUID.isEmpty()) return;

        ShopsharePlayer owner = new ShopsharePlayer(ownerUUID, chest.getLocation());
        List<String> trusted = owner.getLocalTrustList();
        List<String> globalTrusted = owner.getGlobalTrustList();

        if (trusted == null) {
            if (globalTrusted == null) return;
            else trusted = globalTrusted;
        }


        if (!globalTrusted.contains(player.getUUID().toString())) {
            if (!trusted.contains(player.getUUID().toString())) {
                return;
            }
        }

        Inventory shopInv = chest.getInventory();
        e.getPlayer().openInventory(shopInv);
    }


    @EventHandler
    public void CreateShopEvent(ShopCreatedEvent e){
        File data = new File(Shopshare.getPlugin(Shopshare.class).getDataFolder(), File.separator + "shops.yml");
        FileConfiguration shops = YamlConfiguration.loadConfiguration(data);

        shops.set(e.getContainer().getLocation().toString(), e.getPlayer().getUniqueId().toString());

        try {
            shops.save(data);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save shops.yml after new shop creation", ex);
        }
    }


    @EventHandler
    public void DeleteShopEvent(ShopDestroyedEvent e){
        File data = new File(Shopshare.getPlugin(Shopshare.class).getDataFolder(), File.separator + "shops.yml");
        FileConfiguration shops = YamlConfiguration.loadConfiguration(data);

        shops.set(e.getContainer().getLocation().toString(), null);

        try {
            shops.save(data);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save shops.yml after shop destruction", ex);
        }
    }


    @EventHandler
    public void PreTransactionEvent(PreTransactionEvent e){
        ShopsharePlayer owner = new ShopsharePlayer(e.getOwnerAccount().getUuid().toString(), e.getSign().getLocation());
        List<String> trusted = owner.getLocalTrustList();
        List<String> globalTrusted = owner.getGlobalTrustList();

        if (trusted == null) {
            if (globalTrusted == null) return;
            else trusted = globalTrusted;
        }
        ;
        if (globalTrusted.contains(e.getClient().getUniqueId().toString()) || trusted.contains(e.getClient().getUniqueId().toString())) {
            e.getClient().sendMessage(miniMessage.deserialize("<red>You cannot buy or sell from a shop you have access to!</red>"));
            e.setCancelled(true);
        }
    }
}
