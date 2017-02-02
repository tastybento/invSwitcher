/*
 * Copyright (c) 2017 tastybento
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.wasteofplastic.invswitcher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Enables inventory switching between games. Handles food, experience and spawn points.
 * @author tastybento
 *
 */
public class Store extends PluginDependent {
    private YamlConfiguration ymlIndex;
    private File invFile;
    private static final boolean DEBUG = false;

    public Store(InvSwitcher plugin) {
        super(plugin);
        ymlIndex = new YamlConfiguration();
        invFile = new File(plugin.getDataFolder(),"game_inv.yml");
        try {
            if (!invFile.exists()) {
                ymlIndex.save(invFile);
            }
            ymlIndex.load(invFile);
        } catch (Exception e) {
            getLogger().severe("Cannot save or load game_inv.yml!");
        }
    }

    /**
     * Saves the location of all the chests to a file
     */
    public void saveInventories() {
        try {
            ymlIndex.save(invFile);
        } catch (IOException e) {
            getLogger().severe("Problem saving game inventories file!");
        }
    }

    /**
     * Gets items for world. Changes the inventory of player immediately.
     * @param player
     * @param gameName
     * @return last location of the player in the game or null if there is none
     */
    @SuppressWarnings("deprecation")
    public Location getInventory(Player player, World world) {
        // Get inventory
        // Do not differentiate between world environments. Only the location is different
        String worldName = world.getName();
        String overworldName = (world.getName().replace("_the_end", "")).replace("_nether", "");
        List<?> items = ymlIndex.getList(overworldName + "." + player.getUniqueId().toString() + ".inventory");
        if (items != null) player.getInventory().setContents(items.toArray(new ItemStack[items.size()]));
        double health = ymlIndex.getDouble(overworldName + "." + player.getUniqueId().toString() + ".health", 20D);
        
        if (health > player.getMaxHealth()) {
            health = player.getMaxHealth();
        }
        if (health < 0D) {
            health = 0D;
        }
        player.setHealth(health);
        int food = ymlIndex.getInt(overworldName + "." + player.getUniqueId().toString() + ".food", 20); 
        if (food > 20) {
            food = 20;
        }
        if (food < 0) {
            food = 0;
        }
        player.setFoodLevel(food);
        setTotalExperience(player, ymlIndex.getInt(overworldName + "." + player.getUniqueId().toString() + ".exp", 0));
        // Get Spawn Point
        return (Location)(ymlIndex.get(worldName + "." + player.getUniqueId().toString() + ".location"));
    }

    /**
     * Stores the player's inventory
     * @param player
     * @param worldName - the game name for the inventory being store
     * @param from - the last position of the player in this game
     */
    public void storeInventory(Player player, World world, Location from) {
        storeInventory(player, world, from, true);
    }

    /**
     * Puts the player's inventory into the right chest
     * @param player
     * @param world
     * @param from
     * @param storeInv - whether the inventory should be stored or not
     */
    public void storeInventory(Player player, World world, Location from, boolean storeInv) {
        // Do not differentiate between world environments
        String worldName = world.getName();
        String overworldName = (world.getName().replace("_the_end", "")).replace("_nether", ""); 
        if (DEBUG)
            getLogger().info("DEBUG: storeInventory for " + player.getName() + " leaving " + worldName + " from " + from);
        // Copy the player's items to the chest
        List<ItemStack> contents = Arrays.asList(player.getInventory().getContents());  
        ymlIndex.set(overworldName + "." + player.getUniqueId().toString() + ".inventory", contents);
        ymlIndex.set(overworldName + "." + player.getUniqueId().toString() + ".health", player.getHealth());
        ymlIndex.set(overworldName + "." + player.getUniqueId().toString() + ".food", player.getFoodLevel());
        ymlIndex.set(overworldName + "." + player.getUniqueId().toString() + ".exp", getTotalExperience(player));
        ymlIndex.set(worldName + "." + player.getUniqueId().toString() + ".location", player.getLocation());
        saveInventories();
        // Clear the player's inventory
        player.getInventory().clear();
        setTotalExperience(player, 0);
        // Done!
        if (DEBUG)
            getLogger().info("DEBUG: Done!");
    }

    /**
     * Marks all the chests related to a particular world as empty.
     * @param worldName
     */
    public void removeWorld(String worldName) {
        ymlIndex.set(worldName, null);
        saveInventories();
    }

    /**
     * Sets the player's food level in game
     * @param player
     * @param world
     * @param foodLevel
     */
    public void setFood(Player player, World world, int foodLevel) {
        String worldName = world.getName();
        ymlIndex.set(worldName + "." + player.getUniqueId().toString() + ".food", foodLevel);
        saveInventories();
    }
    
    /**
     * Sets player's health in game
     * @param player
     * @param gameName
     * @param maxHealth
     */
    public void setHealth(Player player, World world, double maxHealth) {
        String worldName = world.getName();
        ymlIndex.set(worldName + "." + player.getUniqueId().toString() + ".health", maxHealth);
        saveInventories();
    }

    /**
     * Sets player's exp in game
     * @param player
     * @param gameName
     * @param newExp
     */
    public void setExp(Player player, World world, int newExp) {
        String worldName = world.getName();
        ymlIndex.set(worldName + "." + player.getUniqueId().toString() + ".exp", newExp);
        saveInventories();
    }

    //new Exp Math from 1.8
    private  static  int getExpAtLevel(final int level)
    {
        if (level <= 15)
        {
            return (2*level) + 7;
        }
        if ((level >= 16) && (level <=30))
        {
            return (5 * level) -38;
        }
        return (9*level)-158;
    
    }

    private static int getExpAtLevel(final Player player)
    {
        return getExpAtLevel(player.getLevel());
    }

    //This method is required because the bukkit player.getTotalExperience() method, shows exp that has been 'spent'.
    //Without this people would be able to use exp and then still sell it.
    public static int getTotalExperience(final Player player)
    {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();
    
        while (currentLevel > 0)
        {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0)
        {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    /**
     * Removes the experience from the player
     * @param player
     * @param xpRequired
     * @return
     */
    public static void removeExp(Player player , int xpRequired){
        int xp = getTotalExperience(player);
        if (xp >= xpRequired) {
            setTotalExperience(player, xp - xpRequired);
        }
    }

    // These next methods are taken from Essentials code
    
    //This method is used to update both the recorded total experience and displayed total experience.
    //We reset both types to prevent issues.
    public static void setTotalExperience(final Player player, final int exp)
    {
        if (exp < 0)
        {
            throw new IllegalArgumentException("Experience is negative!");
        }
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
    
        //This following code is technically redundant now, as bukkit now calulcates levels more or less correctly
        //At larger numbers however... player.getExp(3000), only seems to give 2999, putting the below calculations off.
        int amount = exp;
        while (amount > 0)
        {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0)
            {
                // give until next level
                player.giveExp(expToLevel);
            }
            else
            {
                // give the rest
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }
}
