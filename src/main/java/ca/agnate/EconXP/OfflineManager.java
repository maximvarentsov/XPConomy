package ca.agnate.EconXP;

import org.jnbt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class OfflineManager {
	
	private final File dataDir;
	private final EconXP plugin;
    private final Logger logger;

    public OfflineManager(final EconXP plugin) {
    	this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataDir = new File(this.plugin.getServer().getWorlds().get(0).getWorldFolder(), "players");
    }
	
	public boolean dataExists(String name) {
        return new File(dataDir, name + ".dat").exists();
    }
	
	public int getBalance(String name) {
		if (!dataExists(name)) {
            logger.severe(name + " does not exist.");
        	return -1;
        }
        return readExperience( name );
    }
	
	public boolean setBalance(String name, int value) {
		if (!dataExists(name)) {
            logger.severe(name + " does not exist.");
        	return false;
        }
        return writeExperience( name, value );
    }
	
	private int readExperience(String name) {
        try {
        	NBTInputStream in = new NBTInputStream( new FileInputStream(new File(dataDir, name + ".dat")) );
            CompoundTag tag = (CompoundTag) in.readTag();
            in.close();
            
            IntTag exp = (IntTag) tag.getValue().get("XpTotal");
            
            return exp.getValue();
        } catch (IOException ex) {
            logger.severe("Error reading experience for " + name + "[" + ex.getMessage() + "]");
        }
        return -1;
    }
	
	private boolean writeExperience(String name, int value) {
        try {
        	NBTInputStream in = new NBTInputStream( new FileInputStream(new File(dataDir, name + ".dat")) );
            CompoundTag tag = (CompoundTag) in.readTag();
            in.close();
            
            // Uncomment this to see the NBT tag list.
            //for (String aTag : tag.getValue().keySet() ) {
            //	System.out.println("TEST: "+aTag); 
            //}
            
            // Dirty hacks from Bukkit bugs.
            if ( value < 0 ) {
            	value = 0;
            }
            else if (value > Integer.MAX_VALUE) {
            	value = Integer.MAX_VALUE;
            }
            
            int level = 0;
        	int total = value;
        	int tnl = plugin.getExpToLevel(level);
        	float exp = (float) total / (float) tnl;
            
            while (exp >= 1.0F) {
                level++;
                total -= tnl;
                tnl = plugin.getExpToLevel(level);
                exp = (float) total / (float) tnl;
            }
            
            // Create tags for new data here.
            FloatTag newExpCur = new FloatTag("XpP", exp);
            IntTag newExpLevel = new IntTag("XpLevel", level);
            IntTag newExp = new IntTag("XpTotal", value);
            
            HashMap<String, Tag> tagCompound = new HashMap<>(tag.getValue());
            
            // Add tags here for merging of data.
            tagCompound.put("XpTotal", newExp);
            tagCompound.put("XpLevel", newExpLevel);
            tagCompound.put("XpP", newExpCur);
            
            tag = new CompoundTag("Player", tagCompound);
            
            NBTOutputStream out = new NBTOutputStream(new FileOutputStream(new File(dataDir, name + ".dat")));
            out.writeTag(tag);
            out.close();
            
            return true;
        } catch (IOException ex) {
            logger.severe("Error reading experience for " + name + "[" + ex.getMessage() + "]");
        }
        return false;
    }
}
