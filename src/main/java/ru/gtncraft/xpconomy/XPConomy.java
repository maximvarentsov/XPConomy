package ru.gtncraft.xpconomy;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
            player.setTotalExperience(get(player));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Deprecated
    public void set(final String playername, int value) throws IOException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        set(player, value);
    }

    public void set(final OfflinePlayer player, int value) throws IOException {
        if (player.isOnline()) {
            ((Player) player).setTotalExperience(value);
        } else {
            write(player, value);
        }
    }

    @Deprecated
    public int get(final String playername) throws IOException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        return get(player);
    }

    public int get(final OfflinePlayer player) throws IOException {
        if (player.isOnline()) {
            return ((Player) player).getTotalExperience();
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
