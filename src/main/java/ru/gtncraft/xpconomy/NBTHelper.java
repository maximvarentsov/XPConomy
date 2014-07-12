package ru.gtncraft.xpconomy;


import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.*;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

final class NBTHelper {

    final File file;

    /**
     * Retrieve the path to the file containing information about the given player name,-
     * @param world - the world where the player information is located. Cannot be NULL.
     * @param uuid - the player uuid. Cannot be NULL.
     *
     */
    public NBTHelper(final World world, final UUID uuid) {
        Preconditions.checkNotNull("world", "world cannot be NULL");
        Preconditions.checkNotNull("uuid", "uuid cannot be NULL");
        File worldDirectory = world.getWorldFolder();

        if (!worldDirectory.exists()) {
            throw new IllegalArgumentException(String.format("Cannot find world %s: Directory %s doesn't exist.", world.getName(), worldDirectory));
        }

        file = new File(new File(worldDirectory, "playerdata"), uuid + ".dat");
    }

    public NBTHelper(final UUID uuid) {
        this(Bukkit.getServer().getWorlds().get(0), uuid);
    }

    /**
     * Load a NBT compound from a compressed file.
     */
    public NbtCompound get() throws IOException {
        try (FileInputStream stream = new FileInputStream(file)) {
            try (DataInputStream input = new DataInputStream(new GZIPInputStream(stream))) {
                return NbtBinarySerializer.DEFAULT.deserializeCompound(input);
            }
        }
    }

    /**
     * Save a NBT compound from a compressed file.
     */
    public void save(final NbtCompound compound) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            try (DataOutputStream output = new DataOutputStream(new GZIPOutputStream(stream))) {
                NbtBinarySerializer.DEFAULT.serialize(compound, output);
            }
        }
    }
}
