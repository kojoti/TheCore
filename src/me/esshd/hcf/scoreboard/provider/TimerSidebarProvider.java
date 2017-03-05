package me.esshd.hcf.scoreboard.provider;

import me.esshd.api.main.BasePlugin;
import me.esshd.api.utils.BukkitUtils;
import me.esshd.api.utils.server.TPS;
import me.esshd.hcf.ConfigurationService;
import me.esshd.hcf.DateTimeFormats;
import me.esshd.hcf.DurationFormatter;
import me.esshd.hcf.HCF;
import me.esshd.hcf.eventgame.EventTimer;
import me.esshd.hcf.eventgame.eotw.EotwHandler;
import me.esshd.hcf.eventgame.faction.ConquestFaction;
import me.esshd.hcf.eventgame.faction.EventFaction;
import me.esshd.hcf.eventgame.faction.KothFaction;
import me.esshd.hcf.eventgame.tracker.ConquestTracker;
import me.esshd.hcf.faction.type.PlayerFaction;
import me.esshd.hcf.pvpclass.PvpClass;
import me.esshd.hcf.pvpclass.archer.ArcherClass;
import me.esshd.hcf.pvpclass.bard.BardClass;
import me.esshd.hcf.pvpclass.type.AssassinClass;
import me.esshd.hcf.pvpclass.type.MinerClass;
import me.esshd.hcf.scoreboard.SidebarEntry;
import me.esshd.hcf.scoreboard.SidebarProvider;
import me.esshd.hcf.sotw.SotwTimer;
import me.esshd.hcf.timer.PlayerTimer;
import me.esshd.hcf.timer.Timer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.*;


public class TimerSidebarProvider implements SidebarProvider {
    //	private static final Comparator<Map.Entry<UUID, ArcherMark>> ARCHER_MARK_COMPARATOR;
    public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
    protected static final String STRAIGHT_LINE;
    private static final SidebarEntry EMPTY_ENTRY_FILLER;
    private static final SidebarEntry TYGUH_LINES;
    private static final SidebarEntry TYGUH_LINES1;
    private final HCF plugin;
    int playercount;


    /**
     * @param s
     * @return Takes one whole string and makes it into a readable SidebarEntry
     */
    public SidebarEntry add(String s) {

        if (s.length() < 10) {
            return new SidebarEntry(s);
        }

        if (s.length() > 10 && s.length() < 20) {
            return new SidebarEntry(s.substring(0, 10), s.substring(10, s.length()), "");
        }

        if (s.length() > 20) {
            return new SidebarEntry(s.substring(0, 10), s.substring(10, 20), s.substring(20, s.length()));
        }

        return null;
    }

    // eg of add() method
    // lines.add(add("Hello this is a buffered objective!"));
    // makes it easier to add configurable scoreboard
    // :)

    static {
        CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
            @Override
            protected DecimalFormat initialValue() {
                return new DecimalFormat("00.0");
            }
        };
        STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
        EMPTY_ENTRY_FILLER = new SidebarEntry(" ", " ", " ");
        //ARCHER_MARK_COMPARATOR = ((o1, o2) -> o1.getValue().compareTo((ArcherMark)o2.getValue()));
        TYGUH_LINES = new SidebarEntry("-*-----", "--------", "-----*-");
        TYGUH_LINES1 = new SidebarEntry(ChatColor.STRIKETHROUGH + "-*-----", "--------", "-----*-");
    }

    public TimerSidebarProvider(final HCF plugin) {
        this.playercount = Bukkit.getOnlinePlayers().size();

        this.plugin = plugin;
    }

    @Override
    public String getTitle() {
        return ConfigurationService.SCOREBOARD_TITLE;
    }

    @Override
    public List<SidebarEntry> getLines(final Player player) {
        List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        
        if(player.hasPermission("core.staff")){
        	lines.add(add(ChatColor.GOLD + "Staff Board:"));
        	lines.add(add(ChatColor.GRAY + " * " + ChatColor.YELLOW + "GameMode " + ChatColor.GOLD + "» " + ChatColor.RESET + StringUtils.capitalise(player.getGameMode().name().toLowerCase())));
        	lines.add(add(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Vanished " + ChatColor.GOLD + "» " + ChatColor.RESET + StringUtils.capitalise(("" + BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished()))));
        	lines.add(add(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Online " + ChatColor.GOLD + "» " + ChatColor.RESET + Bukkit.getServer().getOnlinePlayers().size()));
        }
        
        final EotwHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();
        World.Environment[] values;
        for (int length = (values = World.Environment.values()).length, i = 0; i < length; ++i) {
            final World.Environment current = values[i];
            ConfigurationService.BORDER_SIZES.get(current);
            if (eotwRunnable != null) {
                long remaining = eotwRunnable.getMillisUntilStarting();
                if (remaining > 0L) {
                    lines.add(new SidebarEntry(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD, "EOTW" + ChatColor.RED + " starts", " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true)));
                } else if ((remaining = eotwRunnable.getMillisUntilCappable()) > 0L) {
                    lines.add(new SidebarEntry(String.valueOf(ChatColor.RED.toString()) + ChatColor.BOLD, "EOTW" + ChatColor.RED + " cappable", " in " + ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true)));
                }
            }
        }
        final SotwTimer.SotwRunnable sotwRunnable = this.plugin.getSotwTimer().getSotwRunnable();
        if (sotwRunnable != null) {
            lines.add(new SidebarEntry(String.valueOf(ChatColor.GREEN.toString()) + ChatColor.BOLD, "SOTW" + ChatColor.GREEN + " ends in ", String.valueOf(ChatColor.GREEN.toString()) + DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true)));
        }
        final EventTimer eventTimer = this.plugin.getTimerManager().getEventTimer();
        List<SidebarEntry> conquestLines = null;
        final EventFaction eventFaction = eventTimer.getEventFaction();
        if (eventFaction instanceof KothFaction) {
            lines.add(new SidebarEntry(eventTimer.getScoreboardPrefix(), String.valueOf(eventFaction.getScoreboardName()) + ChatColor.GRAY, ": " + ChatColor.RED + DurationFormatter.getRemaining(eventTimer.getRemaining(), true)));
        } else if (eventFaction instanceof ConquestFaction) {
            final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            TimerSidebarProvider.CONQUEST_FORMATTER.get();
            conquestLines = new ArrayList<SidebarEntry>();
            conquestLines.add(new SidebarEntry(ChatColor.GOLD.toString() + ChatColor.BOLD + "Conquest", ChatColor.GOLD.toString() + ChatColor.BOLD + " Event", ChatColor.GRAY + ":"));
            conquestLines.add(new SidebarEntry("  " + ChatColor.RED.toString() + conquestFaction.getRed().getScoreboardRemaining(), ChatColor.RESET + " : ", String.valueOf(ChatColor.YELLOW.toString()) + conquestFaction.getYellow().getScoreboardRemaining()));
            conquestLines.add(new SidebarEntry("  " + ChatColor.GREEN.toString() + conquestFaction.getGreen().getScoreboardRemaining(), ChatColor.RESET + " : " + ChatColor.RESET, String.valueOf(ChatColor.AQUA.toString()) + conquestFaction.getBlue().getScoreboardRemaining()));
            final ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
            int count = 0;
            for (final Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap().entrySet()) {
                String factionName = entry.getKey().getName();
                if (factionName.length() > 11) {
                    factionName = factionName.substring(0, 11);
                }
                //conquestLines.add(new SidebarEntry (ChatColor.GOLD.toString() + ChatColor.BOLD + "Points"));
                conquestLines.add(new SidebarEntry(ChatColor.GOLD, " * " + ChatColor.YELLOW + factionName, ChatColor.GRAY + ": " + ChatColor.GOLD + entry.getValue()));
                if (++count == 3) {
                    break;
                }
            }
        }
        final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (pvpClass != null) {
            if (pvpClass instanceof BardClass || pvpClass instanceof AssassinClass) ;
            {
                lines.add(new SidebarEntry(ChatColor.GREEN.toString(), ChatColor.GREEN + "Class: ", ChatColor.RED + pvpClass.getName()));

            }
            if (pvpClass instanceof BardClass) {
                final BardClass bardClass = (BardClass) pvpClass;
                lines.add(new SidebarEntry(ChatColor.GOLD + " ", ChatColor.YELLOW + "Energy", ChatColor.GOLD + ": " + ChatColor.WHITE + handleBardFormat(bardClass.getEnergyMillis(player), true)));
                final long remaining2 = bardClass.getRemainingBuffDelay(player);
                if (remaining2 > 0L) {
                    lines.add(new SidebarEntry(ChatColor.GOLD + " ", ChatColor.YELLOW + "Buff Delay", ChatColor.GOLD + ": " + ChatColor.WHITE + DurationFormatter.getRemaining(remaining2, true)));
                }
            }
        } else if (pvpClass instanceof ArcherClass) {
            if (ArcherClass.TAGGED.containsValue(player.getUniqueId())) {
                for (final UUID uuid : ArcherClass.TAGGED.keySet()) {
                    if (ArcherClass.TAGGED.get(uuid).equals(player.getUniqueId()) && Bukkit.getPlayer(uuid) != null) {
                        lines.add(new SidebarEntry(String.valueOf(ChatColor.GOLD.toString()) + " " + ChatColor.RED.toString(), "", Bukkit.getPlayer(uuid).getName()));

                    } else if (pvpClass instanceof MinerClass) {
                        lines.add(new SidebarEntry(ChatColor.GOLD + " \u00bb ", ChatColor.AQUA + "Diamonds", ChatColor.GRAY + ": " + ChatColor.RED + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE)));
                        lines.add(new SidebarEntry(ChatColor.GOLD + " \u00bb ", ChatColor.AQUA + "Invisble", ChatColor.GRAY + ": " + ChatColor.RED + player.hasPotionEffect(PotionEffectType.INVISIBILITY) != null ? (ChatColor.GREEN + " Yes") : (ChatColor.RED + " No")));


                    }
                }
            }
        }
        final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
        for (final Timer timer : timers) {
            if (timer instanceof PlayerTimer) {
                final PlayerTimer playerTimer = (PlayerTimer) timer;
                final long remaining3 = playerTimer.getRemaining(player);
                if (remaining3 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if (timerName.length() > 14) {
                    timerName = timerName.substring(0, timerName.length());
                }
                lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), String.valueOf(timerName) + ChatColor.GRAY, ": " + ChatColor.RED + DurationFormatter.getRemaining(remaining3, true)));
            }

        }

        if (conquestLines != null && !conquestLines.isEmpty()) {
            if (!lines.isEmpty()) {
                conquestLines.add(new SidebarEntry("", "", ""));
            }
            conquestLines.addAll(lines);
            lines = conquestLines;
        }
        if (!lines.isEmpty()) {
            lines.add(0, new SidebarEntry(ChatColor.GRAY, TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
            lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
        }
        return lines;
    }

    private static String handleBardFormat(final long millis, final boolean trailingZero) {
        return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
    }
}