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

    private static final int MAX_AMOUNT = 64 * 36;

    @Getter
    private final List<String> aliases = List.of("give");

    @Getter
    private final Permission permission = new Permission("cfancestor.give");

    private final Config config;

    @Override
    public void onCommand(@NonNull CommandSender sender, @NonNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Использование: /ancestor give <игрок> [кол-во]");
            return;
        }

        final Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("Игрок не найден");
            return;
        }

        final int amount = args.length > 2 ? parseAmount(args[2]) : 1;
        if (amount < 1 || amount > MAX_AMOUNT) {
            sender.sendMessage("Количество должно быть от 1 до " + MAX_AMOUNT);
            return;
        }

        target.give(config.createItems(amount));
        sender.sendMessage("Выдано " + amount + " шт. игроку " + target.getName());
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull String[] args) {
        return switch (args.length) {
            case 2 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            case 3 -> List.of("1", "16", "64");
            default -> Collections.emptyList();
        };
    }

    private static int parseAmount(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
