package ru.izizadira.cfancestor.command.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import ru.izizadira.cfancestor.Config;
import ru.izizadira.cfancestor.command.SubCommand;
import ru.izizadira.cfancestor.util.MobTypes;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class SetCommand implements SubCommand {

    private static final int MAX_TARGET_DISTANCE = 6;

    @Getter
    private final List<String> aliases = List.of("set");

    @Getter
    private final Permission permission = new Permission("cfancestor.set");

    @Override
    public void onCommand(@NonNull CommandSender sender, @NonNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Команда доступна только игроку");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage("/ancestor set <моб>");
            return;
        }

        final EntityType type = MobTypes.parse(args[1]);
        if (type == null) {
            sender.sendMessage("Моб не найден");
            return;
        }

        final Block block = player.getTargetBlockExact(MAX_TARGET_DISTANCE);
        if (block == null || !(block.getState() instanceof CreatureSpawner spawner)) {
            sender.sendMessage("Посмотри на спавнер");
            return;
        }

        spawner.setSpawnedType(type);
        spawner.update();
        sender.sendMessage(Component.text("Спавнер изменён: ").append(MobTypes.displayName(type)));
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull String[] args) {
        return args.length == 2 ? MobTypes.names() : Collections.emptyList();
    }
}
