package fr.orinston.nerf.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownStore {
    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, Long> raidCooldowns = new HashMap<>();
    private final Map<UUID, Long> bookCooldowns = new HashMap<>();

    public CooldownStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cooldowns.yml");
    }

    public void load() {
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String key : cfg.getConfigurationSection("raid") == null ? java.util.List.<String>of() : cfg.getConfigurationSection("raid").getKeys(false)) {
            raidCooldowns.put(UUID.fromString(key), cfg.getLong("raid." + key));
        }
        for (String key : cfg.getConfigurationSection("books") == null ? java.util.List.<String>of() : cfg.getConfigurationSection("books").getKeys(false)) {
            bookCooldowns.put(UUID.fromString(key), cfg.getLong("books." + key));
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        raidCooldowns.forEach((uuid, time) -> cfg.set("raid." + uuid, time));
        bookCooldowns.forEach((uuid, time) -> cfg.set("books." + uuid, time));
        try { cfg.save(file); } catch (IOException e) { plugin.getLogger().warning("Impossible de sauvegarder cooldowns.yml: " + e.getMessage()); }
    }

    public long getRaid(UUID uuid) { return raidCooldowns.getOrDefault(uuid, 0L); }
    public void setRaid(UUID uuid, long end) { raidCooldowns.put(uuid, end); save(); }
    public long getBook(UUID uuid) { return bookCooldowns.getOrDefault(uuid, 0L); }
    public void setBook(UUID uuid, long end) { bookCooldowns.put(uuid, end); save(); }
}
