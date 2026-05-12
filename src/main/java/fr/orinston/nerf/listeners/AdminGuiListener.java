package fr.orinston.nerf.listeners;

import fr.orinston.nerf.OrinstonNerf;
import fr.orinston.nerf.commands.NerfCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class AdminGuiListener implements Listener {
    private final OrinstonNerf plugin;
    public AdminGuiListener(OrinstonNerf plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(plugin.getConfig().getString("settings.gui-title", "§cᴏʀɪɴꜱᴛᴏɴɴᴇʀꜰ"))) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null) return;
        String feature = switch (event.getSlot()) {
            case 10 -> "raid-totem-limit";
            case 11 -> "raid-player-cooldown";
            case 12 -> "villager-trade-hours";
            case 13 -> "enchanted-book-trade-cooldown";
            case 14 -> "mace-damage-reduction";
            case 15 -> "shulker-farm-nerf";
            case 16 -> "chestshop-min-prices";
            default -> null;
        };
        if (feature == null) return;
        NerfCommand.toggle(plugin, feature);
        player.sendMessage(plugin.prefix() + "§aOption modifiée: §e" + feature);
        new NerfCommand(plugin).openGui(player);
    }
}
