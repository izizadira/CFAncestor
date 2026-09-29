package ru.izizadira.cfancestor.util;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.izizadira.cfancestor.Config;

@AllArgsConstructor
public class SpawnerEffects {

    private static final int POINTS_PER_TICK = 3;
    private static final double HELIX_HEIGHT = 1.4;
    private static final double BURST_SPREAD = 0.35;

    private final Plugin plugin;
    private final Config config;

    public void play(Block block) {
        final Location center = block.getLocation().toCenterLocation();
        final Config.ParticleEffect helix = config.getHelixParticle();
        if (helix == null) {
            this.finish(center);
            return;
        }

        final int duration = config.getHelixDuration();
        new BukkitRunnable() {
            private int tick;

            @Override
            public void run() {
                if (tick >= duration) {
                    this.cancel();
                    finish(center);
                    return;
                }
                for (int point = 0; point < POINTS_PER_TICK; point++) {
                    drawHelix(center, helix, (tick + (double) point / POINTS_PER_TICK) / duration);
                }
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void drawHelix(Location center, Config.ParticleEffect helix, double progress) {
        final double y = center.getY() - 0.5 + progress * HELIX_HEIGHT;
        final double radius = config.getHelixRadius() * (1.0 - progress / 2);
        final double baseAngle = progress * config.getHelixDuration() * config.getHelixRotationSpeed();
        final int strands = config.getHelixStrands();

        for (int strand = 0; strand < strands; strand++) {
            final double angle = baseAngle + 2 * Math.PI * strand / strands;
            center.getWorld().spawnParticle(helix.particle(),
                    center.getX() + Math.cos(angle) * radius, y, center.getZ() + Math.sin(angle) * radius,
                    1, 0, 0, 0, 0, helix.data());
        }
    }

    private void finish(Location center) {
        final Config.ParticleEffect burst = config.getBurstParticle();
        if (burst != null) {
            center.getWorld().spawnParticle(burst.particle(), center, config.getBurstCount(),
                    BURST_SPREAD, BURST_SPREAD, BURST_SPREAD, config.getBurstSpeed(), burst.data());
        }
        if (config.getSound() != null) center.getWorld().playSound(config.getSound(), center.getX(), center.getY(), center.getZ());
    }
}
