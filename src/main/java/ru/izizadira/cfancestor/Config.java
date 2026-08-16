package ru.izizadira.cfancestor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.izizadira.cfancestor.util.Colorizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Config {

    private final CFAncestor plugin;
    private final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public Config(CFAncestor plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();

        final FileConfiguration config = plugin.getConfig();
        ancestorItem = this.createItem(config.getConfigurationSection("item"));

        for (String key : config.getStringList("permitted-mobs")) {
            permittedMobs.add(EntityType.fromName(key));
        }
    }

    private ItemStack createItem(ConfigurationSection section) {
        final ItemStack stack = new ItemStack(Material.getMaterial(section.getString("material")));
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Colorizer.use(section.getString("display-name")));

        final List<String> resultLore = new ArrayList<>();
        section.getStringList("lore").forEach(str -> resultLore.add(Colorizer.use(str)));
        meta.setLore(resultLore);

        meta.setEnchantmentGlintOverride(section.getBoolean("glow"));
        meta.getPersistentDataContainer().set(
                plugin.getRegeneratorKey(),
                PersistentDataType.BYTE,
                (byte) 1
        );

        stack.setItemMeta(meta);
        return stack;
    }

    @Getter
    private ItemStack ancestorItem;

    private final List<EntityType> permittedMobs = new ArrayList<>();

    public EntityType getRandomMobType() {
        if (permittedMobs.isEmpty()) return null;
        return permittedMobs.get(RANDOM.nextInt(permittedMobs.size()));
    }

}
