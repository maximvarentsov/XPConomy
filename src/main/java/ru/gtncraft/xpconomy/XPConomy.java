package ru.gtncraft.xpconomy;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class XPConomy extends JavaPlugin implements Listener {

    OfflineManager offline;

    @Override
    public void onLoad() {
        offline = new OfflineManager(this);
    }

    @Override
    public void onEnable() {
        new Listeners(this);
    }

    public OfflineManager getManager() {
        return offline;
    }
}
