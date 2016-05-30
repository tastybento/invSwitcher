/*
 * Copyright (c) 2015 - 2016
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
import java.util.logging.Logger;

import org.bukkit.Server;

/**
 * Base class for classes that depend on a {@link org.bukkit.plugin.Plugin}.
 *
 * Delegates the often-used methods to the plugin so subclasses don't have to refer to the field when
 * logging, getting the register, etc.
 *
 */
public abstract class PluginDependent {
    private final InvSwitcher beaconzPlugin;

    public final InvSwitcher getPlugin() {
        return beaconzPlugin;
    }

    public PluginDependent(InvSwitcher beaconzPlugin) {
        this.beaconzPlugin = beaconzPlugin;
    }

    public final Logger getLogger() {
        return this.beaconzPlugin.getLogger();
    }

    public final Server getServer() {
        return this.beaconzPlugin.getServer();
    }

    public final File getDataFolder() {
        return this.beaconzPlugin.getDataFolder();
    }

    public final Store getBeaconzStore() {
        return this.beaconzPlugin.getStore();
    }
}
