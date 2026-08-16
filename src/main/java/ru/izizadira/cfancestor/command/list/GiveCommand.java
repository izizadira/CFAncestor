package ru.izizadira.cfancestor.command.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
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

    @Getter
    private final Config config;

    @Override
    public void onCommand(@NonNull CommandSender sender, @NotNull @NonNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Укажи игрока");
            return;
        }

        final Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("Игрок не найден");
            return;
        }

        target.give(config.getAncestorItem());
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NotNull @NonNull String[] args) {
        return Collections.emptyList();
    }
}