package ru.gtncraft.xpconomy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class OfflineManager {
	
	final File dataDir;
	final XPConomy plugin;
    final Logger logger;

    public OfflineManager(final XPConomy plugin) {
    	this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataDir = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "playerdata");
    }

    @SuppressWarnings("unused")
    public boolean set(final OfflinePlayer player, int value) {

        if (player.isOnline()) {
            ((Player) player).giveExp(value);
            return true;
        }

        try {
            return writeExperience(player.getUniqueId(), value);
        } catch (IOException ex) {
            logger.severe("Error writing experience for " + player.getUniqueId() + "[" + ex.getMessage() + "]");
        }

        return false;
    }

    @SuppressWarnings("unused")
    public int get(final OfflinePlayer player) {

        if (player.isOnline()) {
            return ((Player) player).getTotalExperience();
        }

        try {
            return readExperience(player.getUniqueId());
        } catch (IOException ex) {
            logger.severe("Error reading experience for " + player.getUniqueId() + "[" + ex.getMessage() + "]");
        }

        return -1;
    }

    @SuppressWarnings("unused")
    public int remove(final OfflinePlayer p, int value) {
        int remain = get(p);

        // Remove the exp from the player.
        if (remain >= 0) {
            remain -= value;
        }

        // If there's some exp left over, set it to the remaining.
        if (remain >= 0) {
            // Set exp to remaining.
            set(p, remain);
            // Set remaining to amount removed (in this case, it's value).
            remain = value;
        } else {
            // Set exp to zero.
            set(p, 0);
            // If remain < 0, adding it to value will give the true amount removed.
            remain = value + remain;
        }

        // Return the amount that was removed.
        return remain;
    }

    public int add(final OfflinePlayer p, int value) {
        int exp = get(p);

        // If it is less than 0 (ie. -1), it means there was a problem.
        if ( exp < 0 ) {
            return 0;
        }

        // Check the value.
        if ( value < 0 ) {
            return 0;
        }

        // Add in the experience.
        set(p, exp + value);

        // Return the value added.
        return value;
    }

    @SuppressWarnings("unused")
    public boolean has(final OfflinePlayer p, int value) {
        return get(p) >= value;
    }

    int readExperience(final UUID name) throws IOException {
        try (NBTInputStream in = new NBTInputStream( new FileInputStream(new File(dataDir, name + ".dat")) )) {
            CompoundTag tag = (CompoundTag) in.readTag();
            IntTag exp = (IntTag) tag.getValue().get("XpTotal");
            return exp.getValue();
        }
    }
	
	boolean writeExperience(final UUID name, int value) throws IOException {
        try (NBTOutputStream out = new NBTOutputStream(new FileOutputStream(new File(dataDir, name + ".dat")))) {
            out.writeTag(new IntTag("XpTotal", value));
            return true;
        }
    }
}
