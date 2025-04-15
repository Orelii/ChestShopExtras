package com.github.orelii.chestshopextras.Events;

import com.Acrobot.ChestShop.Events.*;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.github.orelii.chestshopextras.ChestShopExtras;
import com.github.orelii.chestshopextras.ShopsharePlayer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

public class ShopshareEvents implements Listener {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @EventHandler
    public void OpenChestEvent(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) return;
        // Possible to have non-chest shops with admin shenanigans, doubt it'll come up ever in survival but have this here in case anyone messes around with op
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!ChestShopSign.isShopBlock(block)) return;

        Chest chest = (Chest) block.getState();
        ShopsharePlayer player = new ShopsharePlayer(e.getPlayer().getUniqueId().toString(), e.getClickedBlock().getLocation());
        Claim claim = player.getClaimAtLocation();

        if (claim != null){
            if (claim.getPermission(player.getUUID().toString()) != ClaimPermission.Inventory
                    && claim.getPermission(player.getUUID().toString()) != ClaimPermission.Build) {
                if (claim.getOwnerID() != player.getUUID()) return;
                if (!claim.getOwnerName().equals(player.getName())) return;
                e.getPlayer().sendMessage(miniMessage.deserialize("<red>You do not have permission to open this shop. Ask the claim owner to trust you.</red>"));
                return;
            }
        }

        File data = new File(ChestShopExtras.getPlugin(ChestShopExtras.class).getDataFolder(), File.separator + "shops.yml");
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
    public void CreateShopEvent(ShopCreatedEvent e) throws IOException {
        File data = new File(ChestShopExtras.getPlugin(ChestShopExtras.class).getDataFolder(), File.separator + "shops.yml");
        FileConfiguration shops = YamlConfiguration.loadConfiguration(data);

        shops.set(e.getContainer().getLocation().toString(), e.getPlayer().getUniqueId().toString());

        try {
            shops.save(data);
        } catch (IOException ex) {
            throw new IOException("Could not save shops.yml after new shop creation", ex);
        }
    }


    @EventHandler
    public void DeleteShopEvent(ShopDestroyedEvent e) throws IOException {
        File data = new File(ChestShopExtras.getPlugin(ChestShopExtras.class).getDataFolder(), File.separator + "shops.yml");
        FileConfiguration shops = YamlConfiguration.loadConfiguration(data);

        shops.set(e.getContainer().getLocation().toString(), null);

        try {
            shops.save(data);
        } catch (IOException ex) {
            throw new IOException("Could not save shops.yml after shop destruction", ex);
        }
    }


    @EventHandler
    public void PreTransactionEvent(PreTransactionEvent e){
        ShopsharePlayer owner = new ShopsharePlayer(e.getOwnerAccount().getUuid().toString(), e.getSign().getLocation());
        List<String> trusted = owner.getLocalTrustList();
        List<String> globalTrusted = owner.getGlobalTrustList();
        ShopsharePlayer player = new ShopsharePlayer(e.getClient().getUniqueId().toString(), uBlock.findConnectedContainer(e.getSign()).getLocation());
        Claim claim = player.getClaimAtLocation();

        if (trusted == null) {
            if (globalTrusted == null) return;
            else trusted = globalTrusted;
        }

        if (claim != null){
            if (claim.getPermission(player.getUUID().toString()) != ClaimPermission.Inventory
                    && claim.getPermission(player.getUUID().toString()) != ClaimPermission.Build) return;
        }

        if (globalTrusted.contains(e.getClient().getUniqueId().toString()) || trusted.contains(e.getClient().getUniqueId().toString())) {
            e.getClient().sendMessage(miniMessage.deserialize("<red>You cannot buy or sell from a shop you have access to!</red>"));
            e.setCancelled(true);
        }
    }
}
