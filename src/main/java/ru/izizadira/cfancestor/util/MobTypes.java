package ru.izizadira.cfancestor.util;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class MobTypes {

    @Nullable
    public static EntityType parse(String input) {
        final NamespacedKey key = NamespacedKey.fromString(input.toLowerCase(Locale.ROOT));
        final EntityType type = key != null ? Registry.ENTITY_TYPE.get(key) : null;

        return type != null && isSpawnerMob(type) ? type : null;
    }

    public static List<String> names() {
        return Registry.ENTITY_TYPE.stream()
                .filter(MobTypes::isSpawnerMob)
                .map(type -> type.getKey().getKey())
                .sorted()
                .toList();
    }

    public static Component displayName(EntityType type) {
        return Component.translatable(type.translationKey());
    }

    private static boolean isSpawnerMob(EntityType type) {
        return type.isSpawnable() && type.isAlive();
    }
}
