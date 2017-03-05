package me.esshd.api.main.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.net.InetAddresses;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.StaffPriority;
import me.esshd.api.main.event.PlayerVanishEvent;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.utils.GenericUtils;
import me.esshd.api.utils.PersistableLocation;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftItem;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.Map.Entry;

public class BaseUser
        extends ServerParticipator {
    private final List<String> addressHistories;
    private final List<NameHistory> nameHistories;
    private final TObjectIntMap<UUID> kitUseMap;
    private final TObjectLongMap<UUID> kitCooldownMap;
    private List<String> notes = new ArrayList<String>();
    private Location backLocation;
    private boolean messagingSounds;
    private boolean hasStarter;
    private boolean staffutil;
    private boolean vanished;
    private boolean glintEnabled;
    private long lastGlintUse;

    public BaseUser(UUID uniqueID) {
        super(uniqueID);
        this.hasStarter = false;
        this.staffutil = false;
        this.addressHistories = new ArrayList<String>();
        this.nameHistories = new ArrayList<NameHistory>();
        this.glintEnabled = true;
        this.kitUseMap = new TObjectIntHashMap<UUID>();
        this.kitCooldownMap = new TObjectLongHashMap<UUID>();
    }

    public BaseUser(Map<String, Object> map) {
        super(map);
        PersistableLocation persistableLocation;
        this.addressHistories = new ArrayList<String>();
        this.nameHistories = new ArrayList<NameHistory>();
        this.glintEnabled = true;
        this.kitUseMap = new TObjectIntHashMap<UUID>();
        this.kitCooldownMap = new TObjectLongHashMap<UUID>();
        this.notes.addAll(GenericUtils.createList(map.get("notes"), String.class));
        this.addressHistories.addAll(GenericUtils.createList(map.get("addressHistories"), String.class));
        Object object = map.get("nameHistories");
        if (object != null) {
            this.nameHistories.addAll(GenericUtils.createList(object, NameHistory.class));
        }
        if ((object = map.get("backLocation")) instanceof PersistableLocation && (persistableLocation = (PersistableLocation) object).getWorld() != null) {
            this.backLocation = ((PersistableLocation) object).getLocation();
        }
        if ((object = map.get("staffmode")) instanceof Boolean) {
            this.staffutil = (Boolean) object;
        }
        if ((object = map.get("starter")) instanceof Boolean) {
            this.hasStarter = (Boolean) object;
        }
        if ((object = map.get("messagingSounds")) instanceof Boolean) {
            this.messagingSounds = (Boolean) object;
        }
        if ((object = map.get("vanished")) instanceof Boolean) {
            this.vanished = (Boolean) object;
        }
        if ((object = map.get("glintEnabled")) instanceof Boolean) {
            this.glintEnabled = (Boolean) object;
        }
        if ((object = map.get("lastGlintUse")) instanceof String) {
            this.lastGlintUse = Long.parseLong((String) object);
        }
        for (Map.Entry<String, Integer> entry : GenericUtils.castMap(map.get("kit-use-map"), String.class, Integer.class).entrySet()) {
            this.kitUseMap.put(UUID.fromString(entry.getKey()), entry.getValue().intValue());
        }
        for (Entry<String, String> entry2 : GenericUtils.castMap(map.get("kit-cooldown-map"), String.class, String.class).entrySet()) {
            this.kitCooldownMap.put(UUID.fromString((String) entry2.getKey()), Long.parseLong((String) entry2.getValue()));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("addressHistories", this.addressHistories);
        map.put("notes", this.notes);
        map.put("staffmode", this.staffutil);
        map.put("starter", this.hasStarter);
        map.put("nameHistories", this.nameHistories);
        if (this.backLocation != null && this.backLocation.getWorld() != null) {
            map.put("backLocation", new PersistableLocation(this.backLocation));
        }
        map.put("messagingSounds", this.messagingSounds);
        map.put("vanished", this.vanished);
        map.put("glintEnabled", this.glintEnabled);
        map.put("lastGlintUse", Long.toString(this.lastGlintUse));
        HashMap<String, Integer> kitUseSaveMap = new HashMap<String, Integer>(this.kitUseMap.size());
        this.kitUseMap.forEachEntry((uuid, value) -> {
                    kitUseSaveMap.put(uuid.toString(), value);
                    return true;
                }
        );
        HashMap<String, String> kitCooldownSaveMap = new HashMap<String, String>(this.kitCooldownMap.size());
        this.kitCooldownMap.forEachEntry((uuid, value) -> {
                    kitCooldownSaveMap.put(uuid.toString(), Long.toString(value));
                    return true;
                }
        );
        map.put("kit-use-map", kitUseSaveMap);
        map.put("kit-cooldown-map", kitCooldownSaveMap);
        return map;
    }

    public long getRemainingKitCooldown(Kit kit) {
        long remaining = this.kitCooldownMap.get(kit.getUniqueID());
        if (remaining == this.kitCooldownMap.getNoEntryValue()) {
            return 0;
        }
        return remaining - System.currentTimeMillis();
    }

    public void updateKitCooldown(Kit kit) {
        this.kitCooldownMap.put(kit.getUniqueID(), System.currentTimeMillis() + kit.getDelayMillis());
    }

    public int getKitUses(Kit kit) {
        int result = this.kitUseMap.get(kit.getUniqueID());
        return result == this.kitUseMap.getNoEntryValue() ? 0 : result;
    }

    public int incrementKitUses(Kit kit) {
        return this.kitUseMap.adjustOrPutValue(kit.getUniqueID(), 1, 1);
    }

    @Override
    public String getName() {
        return this.getLastKnownName();
    }

    public List<NameHistory> getNameHistories() {
        return this.nameHistories;
    }

    public void tryLoggingName(Player player) {
        Preconditions.checkNotNull(player, "Cannot log null player");
        String playerName = player.getName();
        for (NameHistory nameHistory : this.nameHistories) {
            if (!nameHistory.getName().contains(playerName)) continue;
            return;
        }
        this.nameHistories.add(new NameHistory(playerName, System.currentTimeMillis()));
    }

    public List<String> getNotes() {
        return this.notes;
    }

    public void setNote(String note) {
        this.notes.add(note);
    }

    public boolean tryRemoveNote() {
        this.notes.clear();
        return true;
    }

    public List<String> getAddressHistories() {
        return this.addressHistories;
    }

    public boolean isStaffUtil() {
        return this.staffutil;
    }

    public void setStaffUtil(boolean value) {
        this.staffutil = value;
    }

    public void setStarterKit(boolean val) {
        this.hasStarter = val;
    }

    public boolean hasStartKit() {
        return this.hasStarter;
    }

    public void tryLoggingAddress(String address) {
        Preconditions.checkNotNull(address, "Cannot log null address");
        if (!this.addressHistories.contains(address)) {
            Preconditions.checkArgument((boolean) InetAddresses.isInetAddress((String) address), "Not an Inet address");
            this.addressHistories.add(address);
        }
    }

    public Location getBackLocation() {
        return this.backLocation == null ? null : this.backLocation.clone();
    }

    public void setBackLocation(Location backLocation) {
        this.backLocation = backLocation;
    }

    public boolean isMessagingSounds() {
        return this.messagingSounds;
    }

    public void setMessagingSounds(boolean messagingSounds) {
        this.messagingSounds = messagingSounds;
    }

    public boolean isVanished() {
        return this.vanished;
    }

    public void setVanished() {
        this.setVanished(!this.isVanished(), true);
    }

    public void setVanished(boolean vanished) {
        this.setVanished(vanished, true);
    }

    public void setVanished(boolean vanished, boolean update) {
        this.setVanished(Bukkit.getPlayer((UUID) this.getUniqueId()), vanished, update);
    }

    public static Collection<Player> getOnlinePlayers() {
        HashSet<Player> col = new HashSet<Player>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            col.add(p);
        }
        return col;
    }

    public boolean setVanished(Player player, boolean vanished, boolean notifyPlayerList) {
        if (this.vanished != vanished) {
            if (player != null) {
                PlayerVanishEvent event = new PlayerVanishEvent(player, BaseUser.getOnlinePlayers(), vanished);
                Bukkit.getPluginManager().callEvent((Event) event);
                if (event.isCancelled()) {
                    return false;
                }
                if (notifyPlayerList) {
                    this.updateVanishedState(player, event.getViewers(), vanished);
                }
            }
            this.vanished = vanished;
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void updateVanishedState(Player player, boolean vanished) {
        this.updateVanishedState(player, new HashSet<Player>(Bukkit.getOnlinePlayers()), vanished);
    }

    public void updateVanishedState(Player player, Collection<Player> viewers, boolean vanished) {
        player.spigot().setCollidesWithEntities(!vanished);
        StaffPriority playerPriority = StaffPriority.of(player);
        for (Player target : viewers) {
            if (player.equals(target)) continue;
            if (vanished && playerPriority.isMoreThan(StaffPriority.of(target))) {
                target.hidePlayer(player);
                continue;
            }
            target.showPlayer(player);
        }
    }

    public boolean isGlintEnabled() {
        return this.glintEnabled;
    }

    public void setGlintEnabled(boolean glintEnabled) {
        this.setGlintEnabled(glintEnabled, true);
    }

    public void setGlintEnabled(boolean glintEnabled, boolean sendUpdatePackets) {
        Player player = this.toPlayer();
        if (player == null || !player.isOnline()) {
            return;
        }
        this.glintEnabled = glintEnabled;
        if (BasePlugin.getPlugin().getServerHandler().useProtocolLib) {
            int viewDistance = Bukkit.getViewDistance();
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            for (org.bukkit.entity.Entity entity : player.getNearbyEntities((double) viewDistance, (double) viewDistance, (double) viewDistance)) {
                if (entity instanceof Item) {
                    Item item = (Item) entity;
                    if (!(item instanceof CraftItem)) continue;
                    connection.sendPacket((Packet) new PacketPlayOutEntityMetadata(entity.getEntityId(), ((CraftItem) item).getHandle().getDataWatcher(), true));
                    continue;
                }
                if (!(entity instanceof Player) || entity.equals(player)) continue;
                Player target = (Player) entity;
                PlayerInventory inventory = target.getInventory();
                int entityID = entity.getEntityId();
                org.bukkit.inventory.ItemStack[] armour = inventory.getArmorContents();
                int i = 0;
                while (i < armour.length) {
                    org.bukkit.inventory.ItemStack stack = armour[i];
                    if (stack != null && stack.getType() != Material.AIR) {
                        connection.sendPacket((Packet) new PacketPlayOutEntityEquipment(entityID, i + 1, CraftItemStack.asNMSCopy((org.bukkit.inventory.ItemStack) stack)));
                    }
                    ++i;
                }
                org.bukkit.inventory.ItemStack stack2 = inventory.getItemInHand();
                if (stack2 == null || stack2.getType() == Material.AIR) continue;
                connection.sendPacket((Packet) new PacketPlayOutEntityEquipment(entityID, 0, CraftItemStack.asNMSCopy((org.bukkit.inventory.ItemStack) stack2)));
            }
        }
    }

    public long getLastGlintUse() {
        return this.lastGlintUse;
    }

    public void setLastGlintUse(long lastGlintUse) {
        this.lastGlintUse = lastGlintUse;
    }

    public String getLastKnownName() {
        return ((NameHistory) Iterables.getLast(this.nameHistories)).getName();
    }

    public Player toPlayer() {
        return Bukkit.getPlayer((UUID) this.getUniqueId());
    }

}

