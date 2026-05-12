package fr.orinston.nerf.listeners;

import fr.orinston.nerf.OrinstonNerf;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ShulkerNerfListener implements Listener {
    private final OrinstonNerf plugin;
    public ShulkerNerfListener(OrinstonNerf plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onShulkerSpawn(EntitySpawnEvent event) {
        if (!plugin.feature("shulker-farm-nerf")) return;
        if (!plugin.getConfig().getBoolean("shulker-farm.block-shulker-duplication", true)) return;
        if (event.getEntityType() == EntityType.SHULKER && event.getEntity().getTicksLived() <= 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShulkerDeath(EntityDeathEvent event) {
        if (!plugin.feature("shulker-farm-nerf")) return;
        if (!plugin.getConfig().getBoolean("shulker-farm.block-shulker-drops", false)) return;
        if (event.getEntity() instanceof Shulker) event.getDrops().clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnEgg(PlayerInteractEvent event) {
        if (!plugin.feature("shulker-farm-nerf")) return;
        if (!plugin.getConfig().getBoolean("shulker-farm.disable-shulker-spawn-eggs", true)) return;
        Player player = event.getPlayer();
        if (plugin.bypass(player)) return;
        if (event.getItem() != null && event.getItem().getType() == Material.SHULKER_SPAWN_EGG) {
            event.setCancelled(true);
            player.sendMessage(plugin.msg("shulker-disabled"));
        }
    }
}
