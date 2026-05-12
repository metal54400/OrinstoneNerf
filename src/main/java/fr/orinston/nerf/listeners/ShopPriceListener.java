package fr.orinston.nerf.listeners;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import fr.orinston.nerf.OrinstonNerf;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ShopPriceListener implements Listener {
    private final OrinstonNerf plugin;
    public ShopPriceListener(OrinstonNerf plugin) { this.plugin = plugin; }

    @EventHandler
    public void onShopCreate(PreShopCreationEvent event) {
        Player player = event.getPlayer();
        if (player != null && plugin.bypass(player)) return;
        String[] lines = event.getSignLines();
        if (lines.length < 4) return;
        Material material = Material.matchMaterial(lines[3]);
        if (material == null) return;

        if (plugin.feature("blocked-shop-items") && plugin.getConfig().getStringList("shop.blocked-items").contains(material.name())) {
            event.setOutcome(PreShopCreationEvent.CreationOutcome.BUY_PRICE_BELOW_MIN);
            if (player != null) player.sendMessage(plugin.msg("shop-blocked").replace("%item%", material.name()));
            return;
        }

        if (!plugin.feature("chestshop-min-prices")) return;
        double buyPrice = extractBuyPrice(lines[2]);
        double minPrice = plugin.getConfig().getDouble("shop.min-prices." + material.name(), 0);
        if (buyPrice > 0 && minPrice > 0 && buyPrice < minPrice) {
            event.setOutcome(PreShopCreationEvent.CreationOutcome.BUY_PRICE_BELOW_MIN);
            if (player != null) player.sendMessage(plugin.msg("min-price").replace("%item%", material.name()).replace("%price%", String.valueOf(minPrice)));
        }
    }

    private double extractBuyPrice(String line) {
        if (line == null) return -1;
        line = line.replace(" ", "").toUpperCase();
        if (!line.contains("B")) return -1;
        try {
            String pricePart = line.split("B", 2)[1];
            if (pricePart.contains(":")) return Double.parseDouble(pricePart.split(":")[0]);
            return Double.parseDouble(pricePart);
        } catch (Exception e) { return -1; }
    }
}
