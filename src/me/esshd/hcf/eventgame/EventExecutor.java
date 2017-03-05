package me.esshd.hcf.eventgame;

import me.esshd.api.utils.cmds.ArgumentExecutor;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.argument.EventCancelArgument;
import me.esshd.hcf.eventgame.argument.EventCreateArgument;
import me.esshd.hcf.eventgame.argument.EventDeleteArgument;
import me.esshd.hcf.eventgame.argument.EventRenameArgument;
import me.esshd.hcf.eventgame.argument.EventSetAreaArgument;
import me.esshd.hcf.eventgame.argument.EventSetCapzoneArgument;
import me.esshd.hcf.eventgame.argument.EventStartArgument;
import me.esshd.hcf.eventgame.argument.EventUptimeArgument;

/**
 * Handles the execution and tab completion of the event command.
 */
public class EventExecutor extends ArgumentExecutor {

    public EventExecutor(HCF plugin) {
        super("event");

        addArgument(new EventCancelArgument(plugin));
        addArgument(new EventCreateArgument(plugin));
        addArgument(new EventDeleteArgument(plugin));
        addArgument(new EventRenameArgument(plugin));
        addArgument(new EventSetAreaArgument(plugin));
        addArgument(new EventSetCapzoneArgument(plugin));
        addArgument(new EventStartArgument(plugin));
        addArgument(new EventUptimeArgument(plugin));
    }
}