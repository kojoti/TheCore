package me.esshd.hcf.listener;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.HCF;
import me.esshd.hcf.faction.event.FactionChatEvent;
import me.esshd.hcf.faction.struct.ChatChannel;
import me.esshd.hcf.faction.type.Faction;
import me.esshd.hcf.faction.type.PlayerFaction;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.*;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
	 String prefix;
    private static final String EOTW_CAPPER_PREFIX = ChatColor.YELLOW + " TESTEOTWPREFIX ";
    private static final ImmutableSet<UUID> EOTW_CAPPERS;

    static {
        ImmutableSet.Builder<UUID> builder = ImmutableSet.<UUID>builder();

        // TODO: Configurable.
        /*
		 * builder.add(UUID.fromString("52c143e2-85ca-4edf-9d83-615cb8f5c41a")).
		 * add(UUID.fromString("45060d62-0967-4ccc-83e2-16883499183d")).
		 * add(UUID.fromString("186342e7-9d3f-495d-8641-f3a402cd1dd8")).
		 * add(UUID.fromString("48d75a3c-3a63-4f3a-9468-df6f90ead502")).
		 * add(UUID.fromString("646e401b-0e78-4b4e-ae89-a5d2bdd38201")).
		 * add(UUID.fromString("7408a8d4-dfeb-43a8-9955-6001ecdf06df")).
		 * add(UUID.fromString("7a645b12-02ad-4cf2-81b5-952566dc3203")).
		 * add(UUID.fromString("3162e8bf-9ed3-41c2-b437-a773715c2195")).
		 * add(UUID.fromString("e7ad72a6-afc5-49b5-aa17-035b0334d7f8")).
		 * add(UUID.fromString("2b940f6c-16c2-4c94-8346-19d63be309e3")).
		 * add(UUID.fromString("10ae2682-2321-4b53-ab2c-be25ff2d2c00")).
		 * add(UUID.fromString("a3e54366-9676-4f84-ad62-30e79ce84893")).
		 * add(UUID.fromString("77755c55-f3bc-4894-9eaa-d140040b65f9")).
		 * add(UUID.fromString("59554514-74f5-4eb4-ac55-d35ad340241c")).
		 * add(UUID.fromString("86462511-2f0b-4806-ab4d-dc0e466e58f1")).
		 * add(UUID.fromString("eae221cf-84b0-4302-ae34-e302685efcf4")).
		 * add(UUID.fromString("a6b4a47e-8895-4f48-8cf6-e7e59fce2c48")).
		 * add(UUID.fromString("9f039a5d-6a52-475d-bf2e-1b6d0657d090")).
		 * add(UUID.fromString("5906eb81-010f-4ca5-b1d7-65998d4c402a")).
		 * add(UUID.fromString("631437f0-3b09-40da-9a8f-9d3c6377246d")).
		 * add(UUID.fromString("f39b2276-290a-453e-967f-71ea61f30df4")).
		 * add(UUID.fromString("bf596f94-c146-4cdf-bcbf-bb2fe0432caa")).
		 * add(UUID.fromString("f0b19235-3166-4e6c-a4f0-67edb9fce9ba")).
		 * add(UUID.fromString("25af4a41-1c2e-471b-ba65-59d5d5e6a761"));
		 */

        EOTW_CAPPERS = builder.build();
    }

    private static final String DOUBLE_POST_BYPASS_PERMISSION = "hcf.doublepost.bypass";
    private static final Pattern PATTERN = Pattern.compile("\\W");

    private final Map<UUID, String> messageHistory;
    private final HCF plugin;

    public ChatListener(HCF plugin) {
        this.plugin = plugin;

        // Use a temporary 2 minute cache map to prevent large maps causing
        // higher memory usage and long lookups.
        this.messageHistory = new MapMaker().makeMap();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        // Prevent double posting.
        String lastMessage = messageHistory.get(player.getUniqueId());
        String cleanedMessage = PATTERN.matcher(message).replaceAll("");
        if (lastMessage != null
                && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1)
                && !player.hasPermission(DOUBLE_POST_BYPASS_PERMISSION)) {

            player.sendMessage(ChatColor.RED + "Hey, don't double post.");
            event.setCancelled(true);
            return;
        } else {
            messageHistory.put(player.getUniqueId(), cleanedMessage);
        }

        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC
                : playerFaction.getMember(player).getChatChannel();

        // Handle faction or alliance chat modes.
        Set<Player> recipients = event.getRecipients();
        if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if (isGlobalChannel(message)) { // allow players to use '!' to
                // bypass friendly chat.
                message = message.substring(1, message.length()).trim();
                event.setMessage(message);
            } else {
                Collection<Player> online = playerFaction.getOnlinePlayers();
                if (chatChannel == ChatChannel.ALLIANCE) {
                    Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }

                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));

                Bukkit.getPluginManager().callEvent(
                        new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
                return;
            }
        }

        PermissionUser user = PermissionsEx.getUser(player);
        @SuppressWarnings("deprecation")
        PermissionGroup g = user.getGroups()[0];
        String prefix1 = g.getPrefix();
        String name = player.getName();

        prefix = ChatColor.translateAlternateColorCodes('&', prefix1);
        String displayName = ChatColor.YELLOW + "";

        // Handle the custom messaging here.
        event.setCancelled(true);

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(this.getFormattedMessage(player, playerFaction, displayName, message, console));
        for (Player recipient : event.getRecipients()) {
            recipient.sendMessage(this.getFormattedMessage(player, playerFaction, displayName, message, recipient));
        }
    }
    
    private String getFormattedMessage(Player player, PlayerFaction playerFaction, String playerDisplayName,
                                       String message, CommandSender viewer) {
        String tag = playerFaction == null ? ChatColor.RED + Faction.FACTIONLESS_PREFIX
                : playerFaction.getDisplayName(viewer);
        return ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + ChatColor.RESET
                + prefix + player.getName() + ChatColor.GRAY + " � " + ChatColor.RESET + message;
    }

    /**
     * Checks if a message should be posted in {@link ChatChannel#PUBLIC}.
     *
     * @param input the message to check
     * @return true if the message should be posted in
     * {@link ChatChannel#PUBLIC}
     */
    private boolean isGlobalChannel(String input) {
        int length = input.length();
        if (length <= 1 || !input.startsWith("!")) {
            return false;
        }

        for (int i = 1; i < length; i++) {
            char character = input.charAt(i);
            if (character == ' ')
                continue;
            if (character == '/') {
                return false;
            } else {
                break;
            }
        }

        return true;
    }
}
