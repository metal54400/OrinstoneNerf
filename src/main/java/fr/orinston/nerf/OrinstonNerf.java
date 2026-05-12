package fr.orinston.nerf;

import fr.orinston.nerf.commands.NerfCommand;
import fr.orinston.nerf.listeners.*;
import fr.orinston.nerf.storage.CooldownStore;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class OrinstonNerf extends JavaPlugin {
    private CooldownStore cooldownStore;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cooldownStore = new CooldownStore(this);
        cooldownStore.load();

        getServer().getPluginManager().registerEvents(new fr.orinston.nerf.listeners.AdminGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new fr.orinston.nerf.listeners.RaidNerfListener(this), this);
        getServer().getPluginManager().registerEvents(new fr.orinston.nerf.listeners.VillagerTradeListener(this), this);
        getServer().getPluginManager().registerEvents(new fr.orinston.nerf.listeners.MaceNerfListener(this), this);
        getServer().getPluginManager().registerEvents(new fr.orinston.nerf.listeners.ShulkerNerfListener(this), this);

        if (getServer().getPluginManager().getPlugin("ChestShop") != null) {
            getServer().getPluginManager().registerEvents(new fr.orinston.nerf.listeners.ShopPriceListener(this), this);
        } else {
            getLogger().warning("ChestShop non trouvé: nerf des shops désactivé.");
        }

        NerfCommand command = new NerfCommand(this);
        PluginCommand pluginCommand = getCommand("orinstonnerf");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        } else {
            getLogger().severe("Commande orinstonnerf introuvable dans plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        if (cooldownStore != null) cooldownStore.save();
    }

    public CooldownStore cooldowns() {
        return cooldownStore;
    }

    public boolean feature(String path) {
        return getConfig().getBoolean("features." + path, true);
    }

    public String prefix() {
        return getConfig().getString("settings.prefix", "§8[§cᴏʀɪɴꜱᴛᴏɴɴᴇʀꜰ§8] §r");
    }

    public String msg(String key) {
        return prefix() + getConfig().getString("messages." + key, key);
    }

    public boolean bypass(org.bukkit.entity.Player player) {
        return player.hasPermission(getConfig().getString("settings.bypass-permission", "orinstonnerf.bypass"));
    }
}
