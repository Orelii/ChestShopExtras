package com.github.orelii.shopshare.Commands;

import com.github.orelii.shopshare.ShopsharePlayer;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddCommand {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void addCommand(Player sender, String argument) throws IOException {

        // Is the player trying to add themselves to their own claim?
        if (sender.getName().equalsIgnoreCase(argument)) {
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>You can already open your shop chests!</red>"));
            return;
        }

        // Find the target of the command. If they are not online, return.
        Player target = getTarget(sender, argument);
        if (target == null) { return; }


        ShopsharePlayer player = new ShopsharePlayer(sender.getName(), sender.getUniqueId().toString(), sender.getLocation());
        if (player.getFile() == null) {
            player.makeFile();
        }
        File data = player.getFile();
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(data);

        if (!insideTrustedClaim(sender, player)) {
            List<String> trusted = player.getGlobalTrustList();
            if (trusted == null) {
                return;
            }

            if (!trusted.contains(target.getUniqueId().toString())) {
                trusted.add(target.getUniqueId().toString());
                playerData.set("globalTrust.list", trusted);
            } else {
                sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>That player is already globally trusted!</red>"));
                return;
            }

            try {
                playerData.save(data);
            } catch (IOException e) {
                throw new IOException(e);
            }
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <blue>" + target.getName() + " has been added to your global trust list.</blue>"));
            target.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <blue>" + sender.getName() + " has added you to their global trust list.</blue>"));
        }
        else {
            List<String> trusted = player.getLocalTrustList();
            if (trusted == null) {
                sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>You are not within a claim!</red>"));
                return;
            }

            if (!trusted.contains(target.getUniqueId().toString())) {
                trusted.add(target.getUniqueId().toString());
                playerData.set(player.getClaimAtLocation().getID().toString() + ".list", trusted);
            } else {
                sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>That player is already trusted!</red>"));
                return;
            }

            try {
                playerData.save(data);
            } catch (IOException e) {
                throw new IOException(e);
            }
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <blue>" + target.getName() + " has been added to your trusted list.</blue>"));
            target.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <blue>" + sender.getName() + " has added you to their trusted list.</blue>"));
        }
    }


    private static Player getTarget(Player sender, String argument) {
        Player target = null;

        for (Player p : sender.getServer().getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(argument)) { target = p; break; }
        }

        if (target == null){
            sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>That player is not online!</red>"));
            return null;
        }

        return target;
    }

    private static boolean insideTrustedClaim(Player sender, ShopsharePlayer player) {
        Claim claim = player.getClaimAtLocation();

        if (claim == null) {
            return false;
        }

        if (claim.getOwnerID() == sender.getUniqueId()) { return true; }
        if (claim.getOwnerName().equals(sender.getName())) { return true; }
        if (claim.getPermission(sender.getUniqueId().toString()) == ClaimPermission.Build
        || claim.getPermission(sender.getUniqueId().toString()) == ClaimPermission.Inventory) return true;

        sender.sendMessage(miniMessage.deserialize("<aqua>[Shopshare]</aqua> <red>You do not have chest permissions in this claim!</red>"));
        return false;
    }
}
