package ru.izizadira.cfancestor.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public class WorldGuardHook {

    public boolean isInAnyRegion(Location location, Set<String> regionIds) {
        if (regionIds.isEmpty()) return false;

        for (ProtectedRegion region : this.query().getApplicableRegions(BukkitAdapter.adapt(location))) {
            if (regionIds.contains(region.getId().toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    public boolean canBuild(Player player, Location location) {
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) return true;

        return this.query().testBuild(BukkitAdapter.adapt(location), localPlayer);
    }

    private RegionQuery query() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
    }
}
