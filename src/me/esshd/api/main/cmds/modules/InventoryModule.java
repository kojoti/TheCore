/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.api.main.cmds.modules;

import java.util.Set;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommandModule;
import me.esshd.api.main.cmds.modules.inventory.ClearInvCommand;
import me.esshd.api.main.cmds.modules.inventory.CopyInvCommand;
import me.esshd.api.main.cmds.modules.inventory.GiveCommand;
import me.esshd.api.main.cmds.modules.inventory.IdCommand;
import me.esshd.api.main.cmds.modules.inventory.InvSeeCommand;
import me.esshd.api.main.cmds.modules.inventory.MoreCommand;

public class InventoryModule
extends BaseCommandModule {
    public InventoryModule(BasePlugin plugin) {
        this.commands.add(new ClearInvCommand());
        this.commands.add(new GiveCommand());
        this.commands.add(new IdCommand());
        this.commands.add(new InvSeeCommand(plugin));
        this.commands.add(new MoreCommand());
        this.commands.add(new CopyInvCommand());
    }
}

