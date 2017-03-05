package me.esshd.api.main.kit;

import com.google.common.base.Preconditions;
import me.esshd.api.main.kit.event.KitApplyEvent;
import me.esshd.api.utils.GenericUtils;
import me.esshd.api.utils.InventoryUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class Kit
        implements ConfigurationSerializable {
    private static final ItemStack DEFAULT_IMAGE = new ItemStack(Material.EMERALD, 1);
    protected final UUID uniqueID;
    protected String name;
    protected String description;
    protected ItemStack[] items;
    protected ItemStack[] armour;
    protected Collection<PotionEffect> effects;
    protected ItemStack image;
    protected boolean enabled;
    protected long delayMillis;
    protected String delayWords;
    protected long minPlaytimeMillis;
    protected String minPlaytimeWords;
    protected int maximumUses;

    public Kit(String name, String description, PlayerInventory inventory, Collection<PotionEffect> effects) {
        this(name, description, (Inventory) inventory, effects, 0);
    }

    public Kit(String name, String description, Inventory inventory, Collection<PotionEffect> effects, long milliseconds) {
        this.enabled = true;
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.setItems(inventory.getContents());
        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory) inventory;
            this.setArmour(playerInventory.getArmorContents());
            this.setImage(playerInventory.getItemInHand());
        }
        this.effects = effects;
        this.delayMillis = milliseconds;
        this.maximumUses = Integer.MAX_VALUE;
    }

    public Kit(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        setName((String) map.get("name"));
        setDescription((String) map.get("description"));
        setEnabled(((Boolean) map.get("enabled")).booleanValue());
        setEffects(GenericUtils.createList(map.get("effects"), PotionEffect.class));
        List<ItemStack> items = GenericUtils.createList(map.get("items"), ItemStack.class);
        setItems((ItemStack[]) items.toArray(new ItemStack[items.size()]));
        List<ItemStack> armour = GenericUtils.createList(map.get("armour"), ItemStack.class);
        setArmour((ItemStack[]) armour.toArray(new ItemStack[armour.size()]));
        setImage((ItemStack) map.get("image"));
        setDelayMillis(Long.parseLong((String) map.get("delay")));
        setMaximumUses(((Integer) map.get("maxUses")).intValue());
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("enabled", this.enabled);
        map.put("effects", this.effects);
        map.put("items", this.items);
        map.put("armour", this.armour);
        map.put("image", (Object) this.image);
        map.put("delay", Long.toString(this.delayMillis));
        map.put("maxUses", this.maximumUses);
        return map;
    }

    public Inventory getPreview(Player player) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder) player, (int) InventoryUtils.getSafestInventorySize(this.items.length), (String) ((Object) ChatColor.GREEN + this.name + " Preview"));
        ItemStack[] arritemStack = this.items;
        int n = arritemStack.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = arritemStack[n2];
            inventory.addItem(new ItemStack[]{itemStack});
            ++n2;
        }
        return inventory;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemStack[] getItems() {
        return Arrays.copyOf(this.items, this.items.length);
    }

    public void setItems(ItemStack[] items) {
        int length = items.length;
        this.items = new ItemStack[length];
        int i = 0;
        while (i < length) {
            ItemStack next = items[i];
            this.items[i] = next == null ? null : next.clone();
            ++i;
        }
    }

    public ItemStack[] getArmour() {
        return Arrays.copyOf(this.armour, this.armour.length);
    }

    public void setArmour(ItemStack[] armour) {
        int length = armour.length;
        this.armour = new ItemStack[length];
        int i = 0;
        while (i < length) {
            ItemStack next = armour[i];
            this.armour[i] = next == null ? null : next.clone();
            ++i;
        }
    }

    public ItemStack getImage() {
        if (this.image == null || this.image.getType() == Material.AIR) {
            this.image = DEFAULT_IMAGE;
        }
        return this.image;
    }

    public void setImage(ItemStack image) {
        this.image = image == null || image.getType() == Material.AIR ? null : image.clone();
    }

    public Collection<PotionEffect> getEffects() {
        return this.effects;
    }

    public void setEffects(Collection<PotionEffect> effects) {
        this.effects = effects;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getDelayMillis() {
        return this.delayMillis;
    }

    public void setDelayMillis(long delayMillis) {
        if (this.delayMillis != delayMillis) {
            Preconditions.checkArgument((boolean) (this.minPlaytimeMillis >= 0), (Object) "Minimum delay millis cannot be negative");
            this.delayMillis = delayMillis;
            this.delayWords = DurationFormatUtils.formatDurationWords((long) delayMillis, (boolean) true, (boolean) true);
        }
    }

    public String getDelayWords() {
        return DurationFormatUtils.formatDurationWords((long) this.delayMillis, (boolean) true, (boolean) true);
    }

    public long getMinPlaytimeMillis() {
        return this.minPlaytimeMillis;
    }

    public void setMinPlaytimeMillis(long minPlaytimeMillis) {
        if (this.minPlaytimeMillis != minPlaytimeMillis) {
            Preconditions.checkArgument((boolean) (minPlaytimeMillis >= 0), (Object) "Minimum playtime millis cannot be negative");
            this.minPlaytimeMillis = minPlaytimeMillis;
            this.minPlaytimeWords = DurationFormatUtils.formatDurationWords((long) minPlaytimeMillis, (boolean) true, (boolean) true);
        }
    }

    public String getMinPlaytimeWords() {
        return this.minPlaytimeWords;
    }

    public int getMaximumUses() {
        return this.maximumUses;
    }

    public void setMaximumUses(int maximumUses) {
        Preconditions.checkArgument((boolean) (maximumUses >= 0), (Object) "Maximum uses cannot be negative");
        this.maximumUses = maximumUses;
    }

    public String getPermissionNode() {
        return "base.kit." + this.name;
    }

    public Permission getBukkitPermission() {
        String node = this.getPermissionNode();
        return node == null ? null : new Permission(node);
    }

    public boolean applyTo(Player player, boolean force, boolean inform) {
        KitApplyEvent event = new KitApplyEvent(this, player, force);
        Bukkit.getPluginManager().callEvent((Event) event);
        if (event.isCancelled()) {
            return false;
        }
        player.addPotionEffects(this.effects);
        ItemStack cursor = player.getItemOnCursor();
        Location location = player.getLocation();
        World world = player.getWorld();
        if (cursor != null && cursor.getType() != Material.AIR) {
            player.setItemOnCursor(new ItemStack(Material.AIR, 1));
            world.dropItemNaturally(location, cursor);
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack[] arritemStack = this.items;
        int n = arritemStack.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack item = arritemStack[n2];
            if (item != null && item.getType() != Material.AIR) {
                item = item.clone();
                for (Map.Entry excess : inventory.addItem(new ItemStack[]{item.clone()}).entrySet()) {
                    world.dropItemNaturally(location, (ItemStack) excess.getValue());
                }
            }
            ++n2;
        }
        if (this.armour != null) {
            int i = Math.min(3, this.armour.length);
            while (i >= 0) {
                ItemStack stack = this.armour[i];
                if (stack != null && stack.getType() != Material.AIR) {
                    int armourSlot = i + 36;
                    ItemStack previous = inventory.getItem(armourSlot);
                    stack = stack.clone();
                    if (previous != null && previous.getType() != Material.AIR) {
                        boolean KitMap = false;
                        world.dropItemNaturally(location, stack);
                    } else {
                        inventory.setItem(armourSlot, stack);
                    }
                }
                --i;
            }
        }
        if (inform) {
            player.sendMessage((Object) ChatColor.YELLOW + "Kit " + (Object) ChatColor.AQUA + this.name + (Object) ChatColor.YELLOW + " has been applied.");
        }
        return true;
    }
}

