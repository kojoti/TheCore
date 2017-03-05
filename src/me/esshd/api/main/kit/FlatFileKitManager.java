/*
 * Decompiled with CFR 0_119.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.ChatPaginator
 */
package me.esshd.api.main.kit;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.esshd.api.main.BasePlugin;
import me.esshd.api.main.kit.Kit;
import me.esshd.api.main.kit.KitManager;
import me.esshd.api.main.kit.event.KitRenameEvent;
import me.esshd.api.main.user.BaseUser;
import me.esshd.api.main.user.UserManager;
import me.esshd.api.utils.Config;
import me.esshd.api.utils.GenericUtils;
import me.esshd.hcf.HCF;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.ChatPaginator;

public class FlatFileKitManager
implements KitManager,
Listener {
    private static final int INV_WIDTH = 9;
    private final Map<String, Kit> kitNameMap = new CaseInsensitiveMap<String, Kit>();
    private final Map<UUID, Kit> kitUUIDMap = new HashMap<UUID, Kit>();
    private final HCF plugin;
    private Config config;
    private List<Kit> kits = new ArrayList<Kit>();

    public FlatFileKitManager(HCF plugin) {
        this.plugin = plugin;
        this.reloadKitData();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    @EventHandler
    public void onKitRename(KitRenameEvent event) {
        this.kitNameMap.remove(event.getOldName());
        this.kitNameMap.put(event.getNewName(), event.getKit());
    }

    @Override
    public List<Kit> getKits() {
        return this.kits;
    }

    @Override
    public Kit getKit(UUID uuid) {
        return this.kitUUIDMap.get(uuid);
    }

    @Override
    public Kit getKit(String id) {
        return this.kitNameMap.get(id);
    }

    @Override
    public boolean containsKit(Kit kit) {
        return this.kits.contains(kit);
    }

    @Override
    public void createKit(Kit kit) {
        if (this.kits.add(kit)) {
            this.kitNameMap.put(kit.getName(), kit);
            this.kitUUIDMap.put(kit.getUniqueID(), kit);
        }
    }

    @Override
    public void removeKit(Kit kit) {
        if (this.kits.remove(kit)) {
            this.kitNameMap.remove(kit.getName());
            this.kitUUIDMap.remove(kit.getUniqueID());
        }
    }

    @Override
    public Inventory getGui(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory((InventoryHolder)player, (int)((this.kits.size() + 9 - 1) / 9 * 9), (String)((Object)ChatColor.BLUE + "Kit Selector"));
        for (Kit kit : this.kits) {
            ArrayList lore;
            ItemStack stack = kit.getImage();
            String description = kit.getDescription();
            String kitPermission = kit.getPermissionNode();
            if (kitPermission == null || player.hasPermission(kitPermission)) {
                int maxUses;
                lore = new ArrayList();
                if (kit.isEnabled()) {
                    if (kit.getDelayMillis() > 0) {
                        lore.add((Object)ChatColor.YELLOW + kit.getDelayWords() + " cooldown");
                    }
                } else {
                    lore.add((String)((Object)ChatColor.RED + "Disabled"));
                }
                if ((maxUses = kit.getMaximumUses()) != Integer.MAX_VALUE) {
                    lore.add((Object)ChatColor.YELLOW + "Used " + BasePlugin.getPlugin().getUserManager().getUser(uuid).getKitUses(kit) + '/' + maxUses + " times.");
                }
                if (description != null) {
                    lore.add(" ");
                    String[] arrstring = ChatPaginator.wordWrap((String)description, (int)24);
                    int n = arrstring.length;
                    int n2 = 0;
                    while (n2 < n) {
                        String part = arrstring[n2];
                        lore.add((Object)ChatColor.WHITE + part);
                        ++n2;
                    }
                }
            } else {
                lore = Lists.newArrayList((Object[])new String[]{(Object)ChatColor.RED + "You do not own this kit."});
            }
            ItemStack cloned = stack.clone();
            ItemMeta meta = cloned.getItemMeta();
            meta.setDisplayName((Object)ChatColor.GREEN + kit.getName());
            meta.setLore((List)lore);
            cloned.setItemMeta(meta);
            inventory.addItem(new ItemStack[]{cloned});
        }
        return inventory;
    }

    @Override
    public void reloadKitData() {
        this.config = new Config(HCF.getPlugin(), "kits");
        Object object = this.config.get("kits");
        if (object instanceof List) {
            this.kits = GenericUtils.createList(object, Kit.class);
            for (Kit kit : this.kits) {
                this.kitNameMap.put(kit.getName(), kit);
                this.kitUUIDMap.put(kit.getUniqueID(), kit);
            }
        }
    }

    @Override
    public void saveKitData() {
        this.config.set("kits", this.kits);
        this.config.save();
    }
}

