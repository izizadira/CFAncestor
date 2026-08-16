package ru.izizadira.cfancestor;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@AllArgsConstructor
public class BukkitListener implements Listener {

    private final CFAncestor plugin;
    private final Config config;

    @EventHandler
    private void on(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SPAWNER) return;
        if (!(block.getState() instanceof CreatureSpawner spawner)) return;

        final ItemStack stack = event.getItem();
        if (stack == null || !stack.getPersistentDataContainer().has(plugin.getRegeneratorKey(), PersistentDataType.BYTE)) return;

        final Player player = event.getPlayer();
        final EntityType resultType = config.getRandomMobType();
        if (resultType == null) {
            player.sendMessage("Поменять моба не получилось. Обратитесь в техническую поддержку");
            return;
        }

        spawner.setSpawnedType(resultType);
        spawner.update();

        stack.setAmount(stack.getAmount() - 1);
    }
}
