package me.esshd.api.main.listener;

import com.google.common.collect.Sets;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.event.PlayerMessageEvent;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.ServerParticipator;
import me.esshd.api.utils.BukkitUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatListener
        implements Listener {
    public ChatListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        Iterator<Player> iterator = event.getRecipients().iterator();
        while (iterator.hasNext()) {
            Player target = (Player) iterator.next();
            BaseUser targetUser = this.plugin.getUserManager().getUser(target.getUniqueId());
            if ((baseUser.isInStaffChat()) && (!targetUser.isStaffChatVisible())) {
                iterator.remove();
            } else if (targetUser.getIgnoring().contains(name)) {
                iterator.remove();
            } else if (!targetUser.isGlobalChatVisible()) {
                iterator.remove();
            }
        }
        if (baseUser.isInStaffChat()) {
            Set<CommandSender> staffChattable = Sets.newHashSet();
            for (Player pl : Bukkit.getOnlinePlayers()) {
                Player permissable = pl;
                if ((permissable.hasPermission("command.staffchat")) && ((permissable instanceof CommandSender))) {
                    staffChattable.add((CommandSender) permissable);
                }
            }
            if ((staffChattable.contains(player)) && (baseUser.isInStaffChat())) {
                String format = ChatColor.AQUA + String.format(Locale.ENGLISH, new StringBuilder("%1$s").append(ChatColor.AQUA).append(": %2$s").toString(), new Object[]{player.getName(), event.getMessage()});
                for (CommandSender target2 : staffChattable) {
                    if ((target2 instanceof Player)) {
                        Player targetPlayer = (Player) target2;
                        BaseUser targetUser2 = this.plugin.getUserManager().getUser(targetPlayer.getUniqueId());
                        if (targetUser2.isStaffChatVisible()) {
                            target2.sendMessage(format);
                        } else if (target2.equals(player)) {
                            target2.sendMessage(ChatColor.RED + "Your message was sent, but you cannot see staff chat messages as your notifications are disabled: Use /togglesc.");
                        }
                    }
                }
                event.setCancelled(true);
                return;
            }
        }
        long remainingChatDisabled = this.plugin.getServerHandler().getRemainingChatDisabledMillis();
        if ((remainingChatDisabled > 0L) && (!player.hasPermission("disablechat.bypass"))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Global chat is currently disabled for another " + ChatColor.RED + DurationFormatUtils.formatDurationWords(remainingChatDisabled, true, true) + ChatColor.RED + '.');
            return;
        }
        long remainingChatSlowed = this.plugin.getServerHandler().getRemainingChatSlowedMillis();
        if ((remainingChatSlowed > 0L) && (!player.hasPermission("slowchat.bypass"))) {
            long speakTimeRemaining = baseUser.getLastSpeakTimeRemaining();
            if (speakTimeRemaining <= 0L) {
                baseUser.updateLastSpeakTime();
                return;
            }
            event.setCancelled(true);
            long delayMillis = this.plugin.getServerHandler().getChatSlowedDelay() * 1000L;
            player.sendMessage(ChatColor.RED + "Global chat is currently in slow mode with a " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(delayMillis, true, true) + ChatColor.RED + " delay for another " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(remainingChatSlowed, true, true) + ChatColor.RED + ". You spoke " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(delayMillis - speakTimeRemaining, true, true) + ChatColor.RED + " ago, so you must wait another " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(speakTimeRemaining, true, true) + ChatColor.RED + '.');
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreMessage(PlayerMessageEvent event) {
        CommandSender sender = event.getSender();
        Player recipient = event.getRecipient();
        UUID recipientUUID = recipient.getUniqueId();
        if (!sender.hasPermission("base.messaging.bypass")) {
            BaseUser recipientUser = this.plugin.getUserManager().getUser(recipientUUID);
            if ((!recipientUser.isMessagesVisible()) || (recipientUser.getIgnoring().contains(sender.getName()))) {
                event.setCancelled(true);
                sender.sendMessage(ChatColor.RED + recipient.getName() + " has private messaging toggled.");
            }
            return;
        }
        ServerParticipator senderParticipator = this.plugin.getUserManager().getParticipator(sender);
        if (!senderParticipator.isMessagesVisible()) {
            event.setCancelled(true);
            sender.sendMessage(ChatColor.RED + "You have private messages toggled.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = event.getRecipient();
        String message = event.getMessage();
        if (BukkitUtils.getIdleTime(recipient) > AUTO_IDLE_TIME) {
            sender.sendMessage(ChatColor.RED + recipient.getName() + " may not respond as their idle time is over " + DurationFormatUtils.formatDurationWords(AUTO_IDLE_TIME, true, true) + '.');
        }
        UUID senderUUID = sender.getUniqueId();
        String senderId = senderUUID.toString();
        String recipientId = recipient.getUniqueId().toString();
        @SuppressWarnings("unchecked")
        Collection<CommandSender> recipients = new HashSet<CommandSender>(Bukkit.getOnlinePlayers());
        recipients.remove(sender);
        recipients.remove(recipient);
        recipients.add(Bukkit.getConsoleSender());
        for (CommandSender target : recipients) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(target);
            Set<String> messageSpying = participator.getMessageSpying();
            if ((messageSpying.contains("all")) || (messageSpying.contains(recipientId)) || (messageSpying.contains(senderId))) {
                target.sendMessage(String.format(Locale.ENGLISH, MESSAGE_SPY_FORMAT, new Object[]{sender.getName(), recipient.getName(), message}));
            }
        }
    }

    private static final String MESSAGE_SPY_FORMAT = ChatColor.GRAY + "[" + ChatColor.AQUA + "SS: " + ChatColor.YELLOW + "%1$s" + ChatColor.GRAY + " -> " + ChatColor.YELLOW + "%2$s" + ChatColor.GRAY + "] %3$s";
    private static final String STAFF_CHAT_NOTIFY = "command.staffchat";
    private static final String SLOWED_CHAT_BYPASS = "slowchat.bypass";
    private static final String TOGGLED_CHAT_BYPASS = "disablechat.bypass";
    private static final long AUTO_IDLE_TIME = TimeUnit.MINUTES.toMillis(5L);
    private final BasePlugin plugin;
}
