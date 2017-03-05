/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.api.main.cmds;

import me.esshd.api.main.cmds.BaseCommand;
import me.esshd.api.main.cmds.BaseCommandModule;

public interface CommandManager {
    public boolean containsCommand(BaseCommand var1);

    public void registerAll(BaseCommandModule var1);

    public void registerCommand(BaseCommand var1);

    public void registerCommands(BaseCommand[] var1);

    public void unregisterCommand(BaseCommand var1);

    public BaseCommand getCommand(String var1);
}

