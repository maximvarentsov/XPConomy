package ru.gtncraft.xpconomy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener {
	
	final OfflineManager offlineManager;
	
	public Listeners(final XPConomy plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        offlineManager = plugin.getManager();
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.giveExp(offlineManager.get(player));
    }
}
