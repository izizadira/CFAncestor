package ru.izizadira.cfancestor;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.izizadira.cfancestor.command.AncestorCommand;
import ru.izizadira.cfancestor.hook.WorldGuardHook;
import ru.izizadira.cfancestor.util.SpawnerEffects;

public final class CFAncestor extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 34398;

    @Getter
    private final NamespacedKey ancestorKey = new NamespacedKey(this, "ancestor");

    @Getter
    private final NamespacedKey usesKey = new NamespacedKey(this, "uses");

    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        final Config config = new Config(this);
        final PluginManager pm = super.getServer().getPluginManager();

        final WorldGuardHook worldGuard = pm.isPluginEnabled("WorldGuard") ? new WorldGuardHook() : null;

        pm.registerEvents(
                new BukkitListener(this, config, new SpawnerEffects(this, config), worldGuard), this
        );

        super.getCommand("ancestor").setExecutor(new AncestorCommand(config));

        new Metrics(this, BSTATS_PLUGIN_ID);
    }
}
