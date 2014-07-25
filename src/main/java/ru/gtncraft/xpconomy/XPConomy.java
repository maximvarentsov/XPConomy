package ru.gtncraft.xpconomy;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class XPConomy extends JavaPlugin implements Listener {

    /*private ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }*/

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
            //updateBar(player, player.getTotalExperience());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*void updateBar(final Player player, float value) {
        PacketContainer fakeLVLBar = protocolManager.createPacket(PacketType.Play.Server.EXPERIENCE);
        //fakeLVLBar.getFloat().write(0, 0F);
        fakeLVLBar.getIntegers().write(0, 100);
        //fakeLVLBar.getShorts().write(0, (short) 0).write(1, (short) 0);
        try {
            protocolManager.sendServerPacket(player, fakeLVLBar);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Cannot send packet " + fakeLVLBar, ex);
        }
    }*/

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onEnchantItem(final EnchantItemEvent event) {
        int newLevel = event.getEnchanter().getLevel() - event.getExpLevelCost();
        int newTotal = SetExpFix.getExpToLevel( newLevel );
        newTotal += Math.floor(event.getEnchanter().getExp() * SetExpFix.getExpAtLevel( newLevel ));
        SetExpFix.setTotalExperience(event.getEnchanter(), newTotal);
        event.setExpLevelCost(0);
    }

    @Deprecated
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
