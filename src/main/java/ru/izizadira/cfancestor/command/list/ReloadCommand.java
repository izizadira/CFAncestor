package ru.izizadira.cfancestor.command.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import ru.izizadira.cfancestor.Config;
import ru.izizadira.cfancestor.command.SubCommand;
import ru.izizadira.cfancestor.util.Colorizer;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ReloadCommand implements SubCommand {

    @Getter
    private final List<String> aliases = List.of("reload");

    @Getter
    private final Permission permission = new Permission("cfancestor.reload");

    @Getter
    private final Config config;

    @Override
    public void onCommand(@NonNull CommandSender sender, @NotNull @NonNull String[] args) {
        config.reload();
        sender.sendMessage(Colorizer.use("&#07FF00&l✔"));
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NotNull @NonNull String[] args) {
        return Collections.emptyList();
    }
}