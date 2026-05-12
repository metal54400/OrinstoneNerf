package fr.orinston.nerf.listeners;

import fr.orinston.nerf.OrinstonNerf;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class MaceNerfListener implements Listener {
    private final OrinstonNerf plugin;
    public MaceNerfListener(OrinstonNerf plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!plugin.feature("mace-damage-reduction")) return;
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) return;
        Material mace = Material.matchMaterial(plugin.getConfig().getString("mace.material", "MACE"));
        if (mace == null || attacker.getInventory().getItemInMainHand().getType() != mace) return;
        if (plugin.getConfig().getBoolean("mace.only-when-victim-not-full-armor", true) && armorPieces(victim) >= plugin.getConfig().getInt("mace.required-armor-pieces", 4)) return;
        double multiplier = plugin.getConfig().getDouble("mace.damage-multiplier", 0.60);
        event.setDamage(event.getDamage() * multiplier);
    }

    private int armorPieces(Player player) {
        int pieces = 0;
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) pieces++;
        }
        return pieces;
    }
}
