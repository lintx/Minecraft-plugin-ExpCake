package org.lintx.mincraft.plugins.expcake.Helper;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.lintx.mincraft.plugins.expcake.ExpCakePlugin;

public class CoreProtectHelper {

    private static CoreProtectAPI getCoreProtect(){
        Plugin plugin = ExpCakePlugin.getPlugin().getServer().getPluginManager().getPlugin("CoreProtect");
        if (plugin==null || !(plugin instanceof CoreProtect)) return null;
        CoreProtectAPI co = ((CoreProtect) plugin).getAPI();
        if (!co.isEnabled()) return null;
        if (co.APIVersion()<6) return null;
        return co;
    }

    public static void logUseCake(Player player, Location location){
        CoreProtectAPI co = getCoreProtect();
        if (co==null) return;
        co.logInteraction(player.getName(),location);
    }
}
