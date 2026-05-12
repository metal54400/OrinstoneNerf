package fr.orinston.nerf.listeners;

import fr.orinston.nerf.OrinstonNerf;
import fr.orinston.nerf.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class RaidNerfListener implements Listener {
    private final OrinstonNerf plugin;
    private final Map<Integer, Integer> totemsByRaid = new HashMap<>();

    public RaidNerfListener(OrinstonNerf plugin) { this.plugin = plugin; }

    @EventHandler(ignoreCancelled = true)
    public void onRaidTrigger(RaidTriggerEvent event) {
        if (!plugin.feature("raid-player-cooldown")) return;
        Player player = event.getPlayer();
        if (player == null || plugin.bypass(player)) return;
        long until = plugin.cooldowns().getRaid(player.getUniqueId());
        if (until > System.currentTimeMillis()) {
            event.setCancelled(true);
            player.sendMessage(plugin.msg("raid-cooldown").replace("%time%", TimeUtil.remaining(until)));
            return;
        }
        long hours = plugin.getConfig().getLong("raid.player-cooldown-hours", 24);
        plugin.cooldowns().setRaid(player.getUniqueId(), System.currentTimeMillis() + hours * 60L * 60L * 1000L);
    }

    @EventHandler
    public void onRaiderDeath(EntityDeathEvent event) {
        if (!plugin.feature("raid-totem-limit")) return;
        if (!(event.getEntity() instanceof Raider raider)) return;
        Raid raid = raider.getRaid();
        if (raid == null) return;
        int id = raid.getId();
        int max = plugin.getConfig().getInt("raid.max-totems-per-raid", 1);
        int current = totemsByRaid.getOrDefault(id, 0);
        int found = 0;
        for (ItemStack drop : event.getDrops()) {
            if (drop.getType() == Material.TOTEM_OF_UNDYING) found += drop.getAmount();
        }
        if (found <= 0) return;
        if (current >= max && plugin.getConfig().getBoolean("raid.remove-extra-totem-drops", true)) {
            event.getDrops().removeIf(drop -> drop.getType() == Material.TOTEM_OF_UNDYING);
        } else {
            int allowed = Math.max(0, max - current);
            int left = allowed;
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() != Material.TOTEM_OF_UNDYING) continue;
                if (left <= 0) drop.setAmount(0);
                else if (drop.getAmount() > left) { drop.setAmount(left); left = 0; }
                else left -= drop.getAmount();
            }
            event.getDrops().removeIf(drop -> drop.getAmount() <= 0);
            totemsByRaid.put(id, Math.min(max, current + found));
        }
    }

    @EventHandler public void onRaidFinish(RaidFinishEvent event) { totemsByRaid.remove(event.getRaid().getId()); }
    @EventHandler public void onRaidStop(RaidStopEvent event) { totemsByRaid.remove(event.getRaid().getId()); }
}
