/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  net.minecraft.util.org.apache.commons.io.FileUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.esshd.hcfextra;

import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.esshd.hcfextra.HCFExtra;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
    private String[] coordsMessage;
    private String[] helpMessage;
    private final JavaPlugin plugin;

    public Configuration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        File folder = this.plugin.getDataFolder();
        if (!folder.exists()) {
            this.quietlyCreateFile(folder);
        }
        try {
            this.coordsMessage = this.convertLines(new File(folder, "coords.txt"));
            this.helpMessage = this.convertLines(new File(folder, "help.txt"));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String[] convertLines(File file) throws IOException {
        if (!file.exists()) {
            this.quietlyCreateFile(file);
        }
        this.plugin.getLogger().log(Level.INFO, "Reading lines of file " + file.getName() + ".");
        String[] lines = (String[])Iterables.toArray((Iterable)FileUtils.readLines((File)file), String.class);
        int count = 0;
        String[] arrstring = lines;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String line = arrstring[n2];
            lines[count++] = ChatColor.translateAlternateColorCodes((char)'&', (String)line);
            ++n2;
        }
        return lines;
    }

    private void quietlyCreateFile(File file) {
        try {
            HCFExtra.getPlugin().getLogger().log(Level.INFO, String.valueOf(file.createNewFile() ? "Failed to create" : "Created") + " file " + file.getName() + ".");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String[] getCoordsMessage() {
        return this.coordsMessage;
    }

    public String[] getHelpMessage() {
        return this.helpMessage;
    }
}

