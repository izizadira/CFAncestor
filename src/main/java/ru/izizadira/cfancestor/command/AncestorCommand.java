package ru.izizadira.cfancestor.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.izizadira.cfancestor.Config;
import ru.izizadira.cfancestor.command.list.GiveCommand;
import ru.izizadira.cfancestor.command.list.ReloadCommand;
import ru.izizadira.cfancestor.command.list.SetCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AncestorCommand implements TabExecutor {

    private final Config config;
    private final List<SubCommand> subCommands;

    public AncestorCommand(Config config) {
        this.config = config;
        this.subCommands = List.of(
                new GiveCommand(config),
                new SetCommand(config),
                new ReloadCommand(config)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final SubCommand sub = args.length < 1 ? null : this.findSubCommand(args[0]);
        if (sub == null) {
            sender.sendMessage("/ancestor give <игрок> [кол-во] | set <моб> | reload");
            return true;
        }
        if (!this.hasPermission(sender, sub)) {
            config.sendMessage(sender, config.getNoPermissionMessage());
            return true;
        }

        sub.onCommand(sender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            final List<String> tabs = new ArrayList<>();
            for (SubCommand sub : subCommands) {
                if (this.hasPermission(sender, sub)) tabs.add(sub.getAliases().get(0));
            }
            return StringUtil.copyPartialMatches(args[0], tabs, new ArrayList<>());
        }

        final SubCommand sub = this.findSubCommand(args[0]);
        if (sub == null || !this.hasPermission(sender, sub)) return Collections.emptyList();
        return StringUtil.copyPartialMatches(args[args.length - 1], sub.onTabComplete(sender, args), new ArrayList<>());
    }

    @Nullable
    private SubCommand findSubCommand(String alias) {
        for (SubCommand sub : subCommands) {
            for (String subAlias : sub.getAliases()) {
                if (subAlias.equalsIgnoreCase(alias)) return sub;
            }
        }
        return null;
    }

    private boolean hasPermission(CommandSender sender, SubCommand sub) {
        return sub.getPermission() == null || sender.hasPermission(sub.getPermission());
    }
}
