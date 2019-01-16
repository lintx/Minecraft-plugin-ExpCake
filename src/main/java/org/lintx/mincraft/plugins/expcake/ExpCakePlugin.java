package org.lintx.mincraft.plugins.expcake;

import org.bukkit.plugin.java.JavaPlugin;
import org.lintx.mincraft.plugins.expcake.config.Config;

public class ExpCakePlugin extends JavaPlugin {
    private static ExpCakePlugin plugin;

    public static ExpCakePlugin getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("ConfigureCore")){
            getLogger().info("ExpCake depends on ConfigureCore, you must install ConfigureCore");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        plugin = this;
        Config.getInstance().load();

        getServer().getPluginManager().registerEvents(new ExpCakeEvent(),plugin);
    }

    @Override
    public void onDisable() {
        Util.unload();
        Config.getInstance().unload();
    }
}
