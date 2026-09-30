package ru.izizadira.cfancestor.command.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import ru.izizadira.cfancestor.Config;
import ru.izizadira.cfancestor.command.SubCommand;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ReloadCommand implements SubCommand {

    @Getter
    private final List<String> aliases = List.of("reload");

    @Getter
    private final Permission permission = new Permission("cfancestor.reload");

    private final Config config;

    @Override
    public void onCommand(@NonNull CommandSender sender, @NonNull String[] args) {
        config.reload();

        sender.sendMessage(Component.text("✔", TextColor.color(0x07FF00), TextDecoration.BOLD));
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull String[] args) {
        return Collections.emptyList();
    }
}
