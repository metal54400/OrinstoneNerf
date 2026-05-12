package fr.orinston.nerf.listeners;

import fr.orinston.nerf.OrinstonNerf;
import fr.orinston.nerf.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.inventory.InventoryType;

public final class VillagerTradeListener implements Listener {
    private final OrinstonNerf plugin;
    public VillagerTradeListener(OrinstonNerf plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onVillagerOpen(PlayerInteractEntityEvent event) {
        if (!plugin.feature("villager-trade-hours")) return;
        if (!(event.getRightClicked() instanceof AbstractVillager)) return;
        Player player = event.getPlayer();
        if (plugin.bypass(player)) return;
        long time = player.getWorld().getTime();
        int startHour = plugin.getConfig().getInt("villagers.trade-start-hour", 6);
        int endHour = plugin.getConfig().getInt("villagers.trade-end-hour", 19);
        long start = TimeUtil.hourToMinecraftTick(startHour);
        long end = TimeUtil.hourToMinecraftTick(endHour);
        boolean allowed = start <= end ? time >= start && time <= end : time >= start || time <= end;
        if (!allowed) {
            event.setCancelled(true);
            player.sendMessage(plugin.msg("villager-hours").replace("%start%", String.valueOf(startHour)).replace("%end%", String.valueOf(endHour)));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBookTrade(InventoryClickEvent event) {
        if (!plugin.feature("enchanted-book-trade-cooldown")) return;
        if (!(event.getWhoClicked() instanceof Player player) || plugin.bypass(player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.MERCHANT) return;
        if (event.getRawSlot() != 2 || event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.ENCHANTED_BOOK) return;
        long until = plugin.cooldowns().getBook(player.getUniqueId());
        if (until > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(plugin.msg("book-cooldown").replace("%time%", TimeUtil.remaining(until)));
            return;
        }
        long minutes = plugin.getConfig().getLong("villagers.enchanted-book-cooldown-minutes", 60);
        plugin.cooldowns().setBook(player.getUniqueId(), System.currentTimeMillis() + minutes * 60L * 1000L);
    }
}
