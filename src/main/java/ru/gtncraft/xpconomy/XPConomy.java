package ru.gtncraft.xpconomy;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class XPConomy extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            int exp = get(player);
            if (exp != player.getTotalExperience()) {
                SetExpFix.setTotalExperience(player, exp);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    void onPlayerDeath(final PlayerDeathEvent event) {
        event.setKeepLevel(true);
        event.setDroppedExp(0);
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void set(final String playername, int value) throws IOException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        set(player, value);
    }

    public void set(final OfflinePlayer player, int value) throws IOException {
        if (player.isOnline()) {
            SetExpFix.setTotalExperience(((Player) player), value);
        } else {
            write(player, value);
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    public int get(final String playername) throws IOException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        return get(player);
    }

    public int get(final OfflinePlayer player) throws IOException {
        if (player.isOnline()) {
            return SetExpFix.getTotalExperience((Player) player);
        }

        return read(player);
    }

    int read(final OfflinePlayer player) throws IOException {
        return new NBTHelper(player.getUniqueId()).get().getInteger("XpTotal");
    }

    void write(final OfflinePlayer player, int value) throws IOException {
        NBTHelper offlinePlayers = new NBTHelper(player.getUniqueId());
        NbtCompound nbt = offlinePlayers.get();
        nbt.put("XpTotal", value);
        offlinePlayers.save(nbt);
    }
}
