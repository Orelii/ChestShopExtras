package com.github.orelii.chestshopextras.Events;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.github.orelii.chestshopextras.ChestShopExtras;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscountEvents implements Listener {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @EventHandler
    public void ApplyDiscount(PreTransactionEvent e) {
        Container chest = uBlock.findConnectedContainer(e.getSign());

        File data = new File(ChestShopExtras.getPlugin(ChestShopExtras.class).getDataFolder(), File.separator + "discounts.yml");
        FileConfiguration discountData = YamlConfiguration.loadConfiguration(data);
        @NotNull List<Map<?, ?>> discounts = discountData.getMapList(chest.getLocation().toString());

        boolean broken = false;
        double percentage = 0;
        for (int i = 0; i < discounts.size(); i++) {
            Map<?, ?> discount = discounts.get(i);
            if (discount.containsKey(e.getClient().getUniqueId().toString())) {
                broken = true;
                percentage = (double) (int) discount.get(e.getClient().getUniqueId().toString()) / 100.;
                break;
            }
        }
        if (!broken) return;

        if (percentage < 0) {
            if (!e.getClient().hasMetadata("upcharges")) {
                e.getClient().setMetadata("upcharges", new FixedMetadataValue(ChestShopExtras.getPlugin(ChestShopExtras.class), new ArrayList<Location>()));
            }

            List<MetadataValue> upcharges = e.getClient().getMetadata("upcharges");
            List<Location> upcharge = (List<Location>) upcharges.get(0).value();

            if (upcharge != null) {
                if (!upcharge.contains(chest.getLocation())) {
                    upcharge.add(chest.getLocation());
                    e.getClient().sendMessage(miniMessage.deserialize("<aqua>[CSE]</aqua> <red>The owner of this shop has placed a "
                            + -Math.round(percentage * 100)
                            + "% upcharge on you for this shop. This purchase has been blocked. If you still want to buy from this shop, right click again."));
                    e.setCancelled(true);
                    return;
                }
            }
        }
        e.setExactPrice(e.getExactPrice().subtract(e.getExactPrice().multiply(BigDecimal.valueOf(percentage))));
    }

    @EventHandler
    public void Join(PlayerJoinEvent e) {
        e.getPlayer().removeMetadata("upcharges", ChestShopExtras.getPlugin(ChestShopExtras.class));
    }
}
