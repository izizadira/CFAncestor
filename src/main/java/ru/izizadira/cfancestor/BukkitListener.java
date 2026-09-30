package ru.izizadira.cfancestor;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import ru.izizadira.cfancestor.hook.WorldGuardHook;
import ru.izizadira.cfancestor.util.MobTypes;
import ru.izizadira.cfancestor.util.SpawnerEffects;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class BukkitListener implements Listener {

    private final CFAncestor plugin;
    private final Config config;
    private final SpawnerEffects effects;

    @Nullable
    private final WorldGuardHook worldGuard;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    private void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack stack = event.getItem();
        if (stack == null || !stack.getPersistentDataContainer().has(plugin.getAncestorKey(), PersistentDataType.BYTE)) return;

        final Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof CreatureSpawner spawner)) return;

        event.setCancelled(true);

        final Player player = event.getPlayer();
        final String denial = this.checkRestrictions(player, block.getLocation());
        if (denial != null) {
            config.sendMessage(player, denial);
            return;
        }

        final boolean bypassCooldown = player.hasPermission("cfancestor.bypass.cooldown");
        final long cooldownLeft = bypassCooldown ? 0 : this.getCooldownLeft(player.getUniqueId());
        if (cooldownLeft > 0) {
            config.sendMessage(player, config.getCooldownMessage(), Placeholder.unparsed("time", String.valueOf((cooldownLeft + 999) / 1000)));
            return;
        }

        final int uses = spawner.getPersistentDataContainer().getOrDefault(plugin.getUsesKey(), PersistentDataType.INTEGER, 0);
        if (config.isSpawnerLimitEnabled() && uses >= config.getSpawnerMaxUses() && !player.hasPermission("cfancestor.bypass.limit")) {
            config.sendMessage(player, config.getLimitReachedMessage(), Placeholder.unparsed("max", String.valueOf(config.getSpawnerMaxUses())));
            return;
        }

        final EntityType resultType = config.getRandomMobType(spawner.getSpawnedType());
        if (resultType == null) {
            config.sendMessage(player, config.getNoMobsMessage());
            return;
        }

        spawner.setSpawnedType(resultType);
        spawner.getPersistentDataContainer().set(plugin.getUsesKey(), PersistentDataType.INTEGER, uses + 1);
        spawner.update();

        stack.setAmount(stack.getAmount() - 1);
        if (!bypassCooldown && config.getCooldownMillis() > 0) cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + config.getCooldownMillis());

        effects.play(block);
        config.sendActionBar(player, config.getSpawnerChangedMessage(), Placeholder.component("mob", MobTypes.displayName(resultType)));
    }

    @Nullable
    private String checkRestrictions(Player player, Location location) {
        if (config.getDisabledWorlds().contains(location.getWorld().getName().toLowerCase(Locale.ROOT))) return config.getDisabledHereMessage();
        if (worldGuard == null) return null;
        if (worldGuard.isInAnyRegion(location, config.getDisabledRegions())) return config.getDisabledHereMessage();
        if (config.isRequireBuildAccess() && !worldGuard.canBuild(player, location)) return config.getNoBuildAccessMessage();

        return null;
    }

    private long getCooldownLeft(UUID playerId) {
        final Long expiresAt = cooldowns.get(playerId);
        if (expiresAt == null) return 0;

        final long left = expiresAt - System.currentTimeMillis();
        if (left <= 0) {
            cooldowns.remove(playerId);
            return 0;
        }

        return left;
    }
}
