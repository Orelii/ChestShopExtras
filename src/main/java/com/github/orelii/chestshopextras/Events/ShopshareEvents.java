package com.github.orelii.chestshopextras.Events;

import com.Acrobot.ChestShop.Events.*;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.github.orelii.chestshopextras.ChestShopExtras;
import com.github.orelii.chestshopextras.ShopsharePlayer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        // Find the sign attached to this chest (if present) and prefer sign-location as key.
        Sign attachedSign = findAttachedSign(block);
        String ownerUUID = null;
        if (attachedSign != null) {
            ownerUUID = shops.getString(locationKey(attachedSign.getLocation()));
        }

        // Fallback: older versions used container location as key. Keep backward compatibility.
        if (ownerUUID == null) {
            ownerUUID = shops.getString(locationKey(chest.getLocation()));
        }

        if (ownerUUID == null) return;
        if (ownerUUID.isEmpty()) return;

        Location ownerSignLoc = null;
        if (attachedSign != null) ownerSignLoc = attachedSign.getLocation();
        else ownerSignLoc = chest.getLocation();

        ShopsharePlayer owner = new ShopsharePlayer(ownerUUID, ownerSignLoc);
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

        // Prefer sign location as canonical storage key. Fall back to container if sign is missing.
        Location keyLoc = null;
        if (e.getSign() != null) keyLoc = e.getSign().getLocation();
        else if (e.getContainer() != null) keyLoc = e.getContainer().getLocation();

        if (keyLoc != null) {
            shops.set(locationKey(keyLoc), e.getPlayer().getUniqueId().toString());
        }

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

        // Prefer sign location, but gracefully handle null container (physics clears container before event on some breaks).
        Location keyLoc = null;
        if (e.getSign() != null) keyLoc = e.getSign().getLocation();
        else if (e.getContainer() != null) keyLoc = e.getContainer().getLocation();

        if (keyLoc != null) {
            shops.set(locationKey(keyLoc), null);
        }

        // Cleanup: remove any empty configuration sections / leftover empty maps caused by prior serialization.
        // Collect keys first to avoid concurrent modification.
        List<String> keys = new ArrayList<>(shops.getKeys(true));
        for (String k : keys) {
            if (shops.isConfigurationSection(k)) {
                if (shops.getConfigurationSection(k).getKeys(false).isEmpty()) shops.set(k, null);
            } else {
                Object val = shops.get(k);
                if (val == null) shops.set(k, null);
                if (val instanceof String && ((String) val).isEmpty()) shops.set(k, null);
            }
        }

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

    // Helper: produce a stable, YAML-safe key for location lookups.
    private String locationKey(Location loc) {
        if (loc == null) return null;
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + (int) loc.getPitch() + ";" + (int) loc.getYaw();
    }

    // Helper: try to find a sign attached to the chest. This is deliberately permissive to cover wall signs and signs above.
    private Sign findAttachedSign(Block chest) {
        Block[] relatives = new Block[] {
                chest.getRelative(BlockFace.UP),
                chest.getRelative(BlockFace.NORTH),
                chest.getRelative(BlockFace.SOUTH),
                chest.getRelative(BlockFace.EAST),
                chest.getRelative(BlockFace.WEST)
        };

        for (Block b : relatives) {
            if (b.getState() instanceof Sign) return (Sign) b.getState();
        }

        // If nothing found, try a slightly wider scan (one block radius) to catch some odd placements.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block b = chest.getWorld().getBlockAt(chest.getX() + dx, chest.getY() + dy, chest.getZ() + dz);
                    if (b.getState() instanceof Sign) return (Sign) b.getState();
                }
            }
        }

        return null;
    }
}