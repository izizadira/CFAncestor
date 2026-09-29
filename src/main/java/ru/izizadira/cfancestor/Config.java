package ru.izizadira.cfancestor;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import ru.izizadira.cfancestor.util.MobTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Config {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final CFAncestor plugin;

    @Getter
    private ItemStack ancestorItem;

    private final Map<EntityType, Integer> mobWeights = new LinkedHashMap<>();

    @Getter
    private boolean spawnerLimitEnabled;

    @Getter
    private int spawnerMaxUses;

    @Getter
    private long cooldownMillis;

    @Getter
    private final Set<String> disabledWorlds = new HashSet<>();

    @Getter
    private final Set<String> disabledRegions = new HashSet<>();

    @Getter
    private boolean requireBuildAccess;

    @Getter
    private Sound sound;

    @Getter
    private ParticleEffect helixParticle;

    @Getter
    private int helixStrands;

    @Getter
    private double helixRadius;

    @Getter
    private int helixDuration;

    @Getter
    private double helixRotationSpeed;

    @Getter
    private ParticleEffect burstParticle;

    @Getter
    private int burstCount;

    @Getter
    private double burstSpeed;

    private TagResolver prefix;

    @Getter
    private String noPermissionMessage;

    @Getter
    private String disabledHereMessage;

    @Getter
    private String noBuildAccessMessage;

    @Getter
    private String cooldownMessage;

    @Getter
    private String limitReachedMessage;

    @Getter
    private String noMobsMessage;

    @Getter
    private String spawnerChangedMessage;

    public Config(CFAncestor plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();

        final FileConfiguration config = plugin.getConfig();
        ancestorItem = this.createItem(section(config, "item"));
        this.loadMobs(section(config, "mobs"));

        spawnerLimitEnabled = config.getBoolean("spawner-limit.enabled");
        spawnerMaxUses = config.getInt("spawner-limit.max-uses");

        final double cooldownSeconds = config.getDouble("cooldown");
        cooldownMillis = (long) (Math.max(0, cooldownSeconds) * 1000);

        disabledWorlds.clear();
        config.getStringList("restrictions.disabled-worlds").forEach(world -> disabledWorlds.add(world.toLowerCase(Locale.ROOT)));
        disabledRegions.clear();
        config.getStringList("restrictions.disabled-regions").forEach(region -> disabledRegions.add(region.toLowerCase(Locale.ROOT)));
        requireBuildAccess = config.getBoolean("restrictions.require-build-access", true);

        sound = this.loadSound(section(config, "effects.sound"));

        final ConfigurationSection helix = section(config, "effects.helix");
        helixParticle = this.loadParticle(helix, "effects.helix");
        helixStrands = Math.max(1, helix.getInt("strands", 3));
        helixRadius = Math.max(0.1, helix.getDouble("radius", 0.9));
        helixDuration = Math.max(1, helix.getInt("duration", 30));
        helixRotationSpeed = helix.getDouble("rotation-speed", 0.3);

        final ConfigurationSection burst = section(config, "effects.burst");
        burstParticle = this.loadParticle(burst, "effects.burst");
        burstCount = Math.max(1, burst.getInt("count", 40));
        burstSpeed = Math.max(0, burst.getDouble("speed", 0.12));

        prefix = Placeholder.parsed("prefix", config.getString("messages.prefix", ""));
        noPermissionMessage = config.getString("messages.no-permission", "");
        disabledHereMessage = config.getString("messages.disabled-here", "");
        noBuildAccessMessage = config.getString("messages.no-build-access", "");
        cooldownMessage = config.getString("messages.cooldown", "");
        limitReachedMessage = config.getString("messages.limit-reached", "");
        noMobsMessage = config.getString("messages.no-mobs", "");
        spawnerChangedMessage = config.getString("messages.spawner-changed", "");
    }

    public void sendMessage(Audience audience, String message, TagResolver... placeholders) {
        if (message.isEmpty()) return;
        audience.sendMessage(MINI_MESSAGE.deserialize(message, prefix, TagResolver.resolver(placeholders)));
    }

    public void sendActionBar(Audience audience, String message, TagResolver... placeholders) {
        if (message.isEmpty()) return;
        audience.sendActionBar(MINI_MESSAGE.deserialize(message, prefix, TagResolver.resolver(placeholders)));
    }

    @Nullable
    public EntityType getRandomMobType(@Nullable EntityType exclude) {
        int totalWeight = 0;
        for (Map.Entry<EntityType, Integer> entry : mobWeights.entrySet()) {
            if (entry.getKey() != exclude) totalWeight += entry.getValue();
        }
        if (totalWeight == 0) return null;

        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        for (Map.Entry<EntityType, Integer> entry : mobWeights.entrySet()) {
            if (entry.getKey() == exclude) continue;
            roll -= entry.getValue();
            if (roll < 0) return entry.getKey();
        }
        return null;
    }

    public List<ItemStack> createItems(int amount) {
        final int maxStackSize = ancestorItem.getMaxStackSize();
        final List<ItemStack> stacks = new ArrayList<>();
        for (int left = amount; left > 0; left -= maxStackSize) {
            stacks.add(ancestorItem.asQuantity(Math.min(left, maxStackSize)));
        }
        return stacks;
    }

    private ItemStack createItem(ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", ""));
        if (material == null || !material.isItem() || material.isAir()) {
            material = Material.BREEZE_ROD;
        }

        final ItemStack stack = new ItemStack(material);
        stack.editMeta(meta -> {
            meta.displayName(parseItemText(section.getString("display-name", "")));
            meta.lore(section.getStringList("lore").stream().map(Config::parseItemText).toList());
            if (section.getBoolean("glow")) meta.setEnchantmentGlintOverride(true);
            meta.getPersistentDataContainer().set(plugin.getAncestorKey(), PersistentDataType.BYTE, (byte) 1);
        });
        return stack;
    }

    private void loadMobs(ConfigurationSection section) {
        mobWeights.clear();
        for (String key : section.getKeys(false)) {
            final EntityType type = MobTypes.parse(key);
            final int weight = section.getInt(key);
            mobWeights.merge(type, weight, Integer::sum);
        }
    }

    @Nullable
    private Sound loadSound(ConfigurationSection section) {
        if (!section.getBoolean("enabled")) return null;

        final String rawKey = section.getString("key", "");
        final NamespacedKey key = NamespacedKey.fromString(rawKey.toLowerCase(Locale.ROOT));

        return Sound.sound(key, Sound.Source.BLOCK, (float) section.getDouble("volume", 1), (float) section.getDouble("pitch", 1));
    }

    @Nullable
    private ParticleEffect loadParticle(ConfigurationSection section, String path) {
        if (!section.getBoolean("enabled")) return null;

        final String name = section.getString("particle", "");
        final NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));
        final Particle particle = key != null ? Registry.PARTICLE_TYPE.get(key) : null;
        if (particle == null) {
            return null;
        }

        final Class<?> dataType = particle.getDataType();
        if (dataType == Void.class) return new ParticleEffect(particle, null);

        final Color color = this.parseColor(section.getString("color", "#FFFFFF"), path);
        if (dataType == Particle.DustOptions.class) {
            final float size = (float) Math.clamp(section.getDouble("size", 1), 0.01, 4.0);
            return new ParticleEffect(particle, new Particle.DustOptions(color, size));
        }
        if (dataType == Color.class) return new ParticleEffect(particle, color);
        return null;
    }

    private Color parseColor(String hex, String path) {
        final String digits = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            if (digits.length() == 6) return Color.fromRGB(Integer.parseInt(digits, 16));
        } catch (NumberFormatException ignored) {}
        return Color.WHITE;
    }

    private static ConfigurationSection section(ConfigurationSection parent, String path) {
        final ConfigurationSection section = parent.getConfigurationSection(path);
        return section != null ? section : new MemoryConfiguration();
    }

    private static Component parseItemText(String text) {
        return Component.empty()
                .decoration(TextDecoration.ITALIC, false)
                .append(MINI_MESSAGE.deserialize(text));
    }

    public record ParticleEffect(Particle particle, @Nullable Object data) { }
}
