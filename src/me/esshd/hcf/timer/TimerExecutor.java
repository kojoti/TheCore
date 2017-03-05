package me.esshd.hcf.timer;

import me.esshd.api.utils.cmds.ArgumentExecutor;
import me.esshd.hcf.HCF;
import me.esshd.hcf.timer.argument.TimerCheckArgument;
import me.esshd.hcf.timer.argument.TimerSetArgument;

/**
 * Handles the execution and tab completion of the timer command.
 */
public class TimerExecutor extends ArgumentExecutor {

    public TimerExecutor(HCF plugin) {
        super("timer");

        addArgument(new TimerCheckArgument(plugin));
        addArgument(new TimerSetArgument(plugin));
    }
}