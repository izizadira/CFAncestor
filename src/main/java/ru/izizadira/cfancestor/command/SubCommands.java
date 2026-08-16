package ru.izizadira.cfancestor.command;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import ru.izizadira.cfancestor.Config;
import ru.izizadira.cfancestor.command.list.GiveCommand;
import ru.izizadira.cfancestor.command.list.ReloadCommand;

import javax.annotation.Nullable;
import java.util.Collection;

public class SubCommands {
    private final ClassToInstanceMap<SubCommand> commands;

    public SubCommands(Config config) {
        commands = new ImmutableClassToInstanceMap.Builder<SubCommand>()
                .put(GiveCommand.class, new GiveCommand(config))
                .put(ReloadCommand.class, new ReloadCommand(config))
                .build();
    }

    @Nullable
    public SubCommand getCommand(Class<? extends SubCommand> targetClass) {
        return commands.get(targetClass);
    }

    public Collection<SubCommand> getCommands() {
        return commands.values();
    }
}
