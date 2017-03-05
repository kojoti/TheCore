/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.api.main.cmds.modules;

import java.util.Set;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommandModule;
import me.esshd.api.main.cmds.modules.teleport.BackCommand;
import me.esshd.api.main.cmds.modules.teleport.TeleportAllCommand;
import me.esshd.api.main.cmds.modules.teleport.TeleportCommand;
import me.esshd.api.main.cmds.modules.teleport.TeleportHereCommand;
import me.esshd.api.main.cmds.modules.teleport.TopCommand;
import me.esshd.api.main.cmds.modules.teleport.WorldCommand;
import me.esshd.api.main.cmds.modules.warp.WarpExecutor;

public class TeleportModule
extends BaseCommandModule {
    public TeleportModule(BasePlugin plugin) {
        this.commands.add(new BackCommand(plugin));
        this.commands.add(new TeleportCommand());
        this.commands.add(new TeleportAllCommand());
        this.commands.add(new TeleportHereCommand());
        this.commands.add(new TopCommand());
        this.commands.add(new WorldCommand());
    }
}

