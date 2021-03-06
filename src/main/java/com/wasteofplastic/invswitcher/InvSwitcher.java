/*
 * Copyright (c) 2015 - 2017 tastybento
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


import org.bukkit.plugin.java.JavaPlugin;

import com.wasteofplastic.invswitcher.listeners.PlayerTeleportListener;

public class InvSwitcher extends JavaPlugin {
    private Store store;


    @Override
    public void onEnable() {
        final InvSwitcher plugin = this;
        getServer().getScheduler().runTask(this, new Runnable() {

            @Override
            public void run() {
                // Create the store world
                store = new Store(plugin);
                // Register the listeners
                getServer().getPluginManager().registerEvents(new PlayerTeleportListener(plugin), plugin);

            }});
    }


    @Override
    public void onDisable()
    {
        if (store != null) {
            store.saveInventories();
        }
    }


    /**
     * @return the store
     */
    public Store getStore() {
        return store;
    }

}
