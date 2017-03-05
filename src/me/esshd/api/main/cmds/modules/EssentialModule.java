/*
 * Decompiled with CFR 0_119.
 */
package me.esshd.api.main.cmds.modules;

import java.util.Set;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.cmds.BaseCommandModule;
import me.esshd.api.main.cmds.modules.essential.AmivisCommand;
import me.esshd.api.main.cmds.modules.essential.ClearLagg;
import me.esshd.api.main.cmds.modules.essential.EnchantCommand;
import me.esshd.api.main.cmds.modules.essential.FeedCommand;
import me.esshd.api.main.cmds.modules.essential.FlyCommand;
import me.esshd.api.main.cmds.modules.essential.FreezeCommand;
import me.esshd.api.main.cmds.modules.essential.GamemodeCommand;
import me.esshd.api.main.cmds.modules.essential.HatCommand;
import me.esshd.api.main.cmds.modules.essential.HealCommand;
import me.esshd.api.main.cmds.modules.essential.KillCommand;
import me.esshd.api.main.cmds.modules.essential.LagCommand;
import me.esshd.api.main.cmds.modules.essential.ListCommand;
import me.esshd.api.main.cmds.modules.essential.NoteCommand;
import me.esshd.api.main.cmds.modules.essential.PingCommand;
import me.esshd.api.main.cmds.modules.essential.PlayTimeCommand;
import me.esshd.api.main.cmds.modules.essential.RenameCommand;
import me.esshd.api.main.cmds.modules.essential.RepairCommand;
import me.esshd.api.main.cmds.modules.essential.ReportCommand;
import me.esshd.api.main.cmds.modules.essential.RequestCommand;
import me.esshd.api.main.cmds.modules.essential.RulesCommand;
import me.esshd.api.main.cmds.modules.essential.SetMaxPlayersCommand;
import me.esshd.api.main.cmds.modules.essential.SpeedCommand;
import me.esshd.api.main.cmds.modules.essential.StopLagCommand;
import me.esshd.api.main.cmds.modules.essential.SudoCommand;
import me.esshd.api.main.cmds.modules.essential.ToggleDonorOnly;
import me.esshd.api.main.cmds.modules.essential.VanishCommand;
import me.esshd.api.main.cmds.modules.essential.WhoisCommand;

public class EssentialModule
extends BaseCommandModule {
    public EssentialModule(BasePlugin plugin) {
        this.commands.add(new ToggleDonorOnly(plugin));
        this.commands.add(new ClearLagg());
        this.commands.add(new RequestCommand());
        this.commands.add(new AmivisCommand(plugin));
        this.commands.add(new ListCommand());
        this.commands.add(new EnchantCommand());
        this.commands.add(new NoteCommand());
        this.commands.add(new FeedCommand());
        this.commands.add(new FlyCommand());
        this.commands.add(new FreezeCommand(plugin));
        this.commands.add(new GamemodeCommand());
        this.commands.add(new HatCommand());
        this.commands.add(new HealCommand());
        this.commands.add(new KillCommand());
        this.commands.add(new LagCommand());
        this.commands.add(new PingCommand());
        this.commands.add(new PlayTimeCommand(plugin));
        this.commands.add(new RenameCommand());
        this.commands.add(new ReportCommand());
        this.commands.add(new RepairCommand());
        this.commands.add(new RulesCommand(plugin));
        this.commands.add(new SetMaxPlayersCommand());
        this.commands.add(new SpeedCommand());
        this.commands.add(new StopLagCommand(plugin));
        this.commands.add(new SudoCommand());
        this.commands.add(new VanishCommand(plugin));
        this.commands.add(new WhoisCommand(plugin));
    }
}

