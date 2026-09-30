package ru.izizadira.cfancestor.command.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import ru.izizadira.cfancestor.Config;
import ru.izizadira.cfancestor.command.SubCommand;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class GiveCommand implements SubCommand {

    @Getter
    private final List<String> aliases = List.of("give");

    @Getter
    private final Permission permission = new Permission("cfancestor.give");

    private final Config config;

    @Override
    public void onCommand(@NonNull CommandSender sender, @NonNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Использование: /ancestor give <игрок>");
            return;
        }

        final Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("Игрок не найден");
            return;
        }

        target.give(config.createItem());
        sender.sendMessage("Выдано игроку " + target.getName());
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull String[] args) {
        return switch (args.length) {
            case 2 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            default -> Collections.emptyList();
        };
    }
}
