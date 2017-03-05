package me.esshd.hcf.eventgame.conquest;

import me.esshd.api.utils.cmds.ArgumentExecutor;
import me.esshd.hcf.HCF;

public class ConquestExecutor extends ArgumentExecutor {

    public ConquestExecutor(HCF plugin) {
        super("conquest");
        addArgument(new ConquestSetpointsArgument(plugin));
    }
}
