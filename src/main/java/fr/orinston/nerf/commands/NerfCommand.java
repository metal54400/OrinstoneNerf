package fr.orinston.nerf.commands;

import fr.orinston.nerf.OrinstonNerf;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class NerfCommand implements CommandExecutor, TabCompleter {
    private final OrinstonNerf plugin;
    public static final String GUI_HOLDER_NAME = "ORINSTON_NERF_GUI";

    public NerfCommand(OrinstonNerf plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("orinstonnerf.admin")) { sender.sendMessage(plugin.msg("no-permission")); return true; }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(plugin.msg("reloaded"));
            return true;
        }
        if (!(sender instanceof Player player)) { sender.sendMessage("/orinstonnerf reload"); return true; }
        openGui(player);
        return true;
    }

    public void openGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, plugin.getConfig().getString("settings.gui-title", "§cᴏʀɪɴꜱᴛᴏɴɴᴇʀꜰ"));
        set(inv, 10, "raid-totem-limit", Material.TOTEM_OF_UNDYING, "§e1 totem par raid");
        set(inv, 11, "raid-player-cooldown", Material.OMINOUS_BOTTLE, "§e1 raid / joueur / 24h");
        set(inv, 12, "villager-trade-hours", Material.EMERALD, "§eHoraires villageois");
        set(inv, 13, "enchanted-book-trade-cooldown", Material.ENCHANTED_BOOK, "§eCooldown livres enchantés");
        set(inv, 14, "mace-damage-reduction", Material.MACE, "§eRéduction dégâts mace");
        set(inv, 15, "shulker-farm-nerf", Material.SHULKER_SHELL, "§eFarm à shulker");
        set(inv, 16, "chestshop-min-prices", Material.CHEST, "§ePrix minimum ChestShop");
        player.openInventory(inv);
    }

    private void set(Inventory inv, int slot, String feature, Material mat, String name) {
        boolean enabled = plugin.feature(feature);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of("", enabled ? "§aActivé" : "§cDésactivé", "§7Clique pour changer."));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public static void toggle(OrinstonNerf plugin, String feature) {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("features." + feature, !cfg.getBoolean("features." + feature, true));
        plugin.saveConfig();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? List.of("gui", "reload") : List.of();
    }
}
