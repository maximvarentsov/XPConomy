package ca.agnate.EconXP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EconXPListener implements Listener {
	
	final private EconXP plugin;
	
	public EconXPListener(EconXP plugin) {
		this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Set the experience so that levels are calculated properly.
		plugin.setExp(event.getPlayer(), plugin.getExp(event.getPlayer()));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerRespawn (final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		
		// Delay the setting so that it actually works.
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
            	// Set the experience so that levels are calculated properly.
        		plugin.setExp(player, plugin.getExp(player));
            }
        }, 1);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityDeath (final EntityDeathEvent evt) {
        if (evt instanceof PlayerDeathEvent) {
            PlayerDeathEvent event = (PlayerDeathEvent) evt;
            if (event.getEntity() instanceof Player) {
                Player player = event.getEntity();
                event.setDroppedExp( plugin.calcDroppedExp(player));
                event.setNewExp( plugin.calcRemainingExp(player));
            }
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantItem(final EnchantItemEvent event) {
		// Calculate the cost based on level.
		int newLevel = event.getEnchanter().getLevel() - event.getExpLevelCost();
		int newTotal = plugin.convertLevelToExp( newLevel );
		
		// Add in what's remaining in the bar.
		newTotal += Math.floor(event.getEnchanter().getExp() * plugin.getExpToLevel( newLevel ));
		
		// Set the experience so that levels are calculated properly.
		plugin.setExp(event.getEnchanter(), newTotal);
		
		// Fake a cost of no levels so that Bukkit doesn't alter the level.
		event.setExpLevelCost( 0 );
	}
}
