/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package me.esshd.api.main.cmds;

import com.google.common.collect.Sets;
import java.util.Set;
import me.esshd.api.main.cmds.BaseCommand;

public abstract class BaseCommandModule {
    protected final Set<BaseCommand> commands = Sets.newHashSet();
    protected boolean enabled = true;

    Set<BaseCommand> getCommands() {
        return this.commands;
    }

    void unregisterCommand(BaseCommand command) {
        this.commands.remove(command);
    }

    void unregisterCommands() {
        this.commands.clear();
    }

    boolean isEnabled() {
        return this.enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

