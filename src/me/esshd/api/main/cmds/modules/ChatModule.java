/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.api.main.cmds.modules;

import java.util.Set;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommandModule;
import me.esshd.api.main.cmds.modules.chat.AnnouncementCommand;
import me.esshd.api.main.cmds.modules.chat.BroadcastCommand;
import me.esshd.api.main.cmds.modules.chat.ClearChatCommand;
import me.esshd.api.main.cmds.modules.chat.DisableChatCommand;
import me.esshd.api.main.cmds.modules.chat.IgnoreCommand;
import me.esshd.api.main.cmds.modules.chat.MessageCommand;
import me.esshd.api.main.cmds.modules.chat.MessageSpyCommand;
import me.esshd.api.main.cmds.modules.chat.ReplyCommand;
import me.esshd.api.main.cmds.modules.chat.SlowChatCommand;
import me.esshd.api.main.cmds.modules.chat.StaffChatCommand;
import me.esshd.api.main.cmds.modules.chat.ToggleMessagesCommand;

public class ChatModule
extends BaseCommandModule {
    public ChatModule(BasePlugin plugin) {
        this.commands.add(new AnnouncementCommand(plugin));
        this.commands.add(new BroadcastCommand(plugin));
        this.commands.add(new ClearChatCommand());
        this.commands.add(new DisableChatCommand(plugin));
        this.commands.add(new SlowChatCommand(plugin));
        this.commands.add(new StaffChatCommand(plugin));
        this.commands.add(new IgnoreCommand(plugin));
        this.commands.add(new MessageCommand(plugin));
        this.commands.add(new MessageSpyCommand(plugin));
        this.commands.add(new ReplyCommand(plugin));
        this.commands.add(new ToggleMessagesCommand(plugin));
    }
}

