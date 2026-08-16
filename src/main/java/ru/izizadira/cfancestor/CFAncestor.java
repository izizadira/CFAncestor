package ru.izizadira.cfancestor;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import ru.izizadira.cfancestor.command.AncestorCommand;

public final class CFAncestor extends JavaPlugin {

    @Getter
    private final NamespacedKey regeneratorKey = new NamespacedKey(this, "ancestor");

    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        final Config config = new Config(this);
        super.getCommand("ancestor").setExecutor(new AncestorCommand(config));
        super.getServer().getPluginManager().registerEvents(new BukkitListener(this, config), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
