package me.esshd.api.main.user;

import com.google.common.base.Preconditions;

import me.esshd.api.utils.Config;
import me.esshd.hcf.HCF;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class UserManager {
    private final ConsoleUser console;
    private final JavaPlugin plugin;
    private Config userConfig;
    private Map<UUID, ServerParticipator> participators;

    public UserManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reloadParticipatorData();
        ServerParticipator participator = this.participators.get(ConsoleUser.CONSOLE_UUID);
        if (participator != null) {
            this.console = (ConsoleUser) participator;
        } else {
            this.console = new ConsoleUser();
            this.participators.put(ConsoleUser.CONSOLE_UUID, this.console);
        }
    }

    public ConsoleUser getConsole() {
        return this.console;
    }

    public Map<UUID, ServerParticipator> getParticipators() {
        return this.participators;
    }

    public ServerParticipator getParticipator(CommandSender sender) {
        Preconditions.checkNotNull((Object) sender, (Object) "CommandSender cannot be null");
        if (sender instanceof ConsoleCommandSender) {
            return this.console;
        }
        if (sender instanceof Player) {
            return this.participators.get(((Player) sender).getUniqueId());
        }
        return null;
    }

    public ServerParticipator getParticipator(UUID uuid) {
        Preconditions.checkNotNull((Object) uuid, (Object) "Unique ID cannot be null");
        return this.participators.get(uuid);
    }

    public BaseUser getUser(UUID uuid) {
        BaseUser baseUser;
        ServerParticipator participator = this.getParticipator(uuid);
        if (participator != null && participator instanceof BaseUser) {
            baseUser = (BaseUser) participator;
        } else {
            baseUser = new BaseUser(uuid);
            this.participators.put(uuid, baseUser);
        }
        return baseUser;
    }

    public void reloadParticipatorData() {
        this.userConfig = new Config(HCF.getPlugin(), "participators");
        Object object = this.userConfig.get("participators");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            Set<String> keys = section.getKeys(false);
            this.participators = new HashMap<UUID, ServerParticipator>(keys.size());
            for (String id : keys) {
                this.participators.put(UUID.fromString(id), (ServerParticipator) this.userConfig.get("participators." + id));
            }
        } else {
            this.participators = new HashMap<UUID, ServerParticipator>();
        }
    }

    public void saveParticipatorData() {
        LinkedHashMap<String, ServerParticipator> saveMap = new LinkedHashMap<String, ServerParticipator>(this.participators.size());
        for (Map.Entry<UUID, ServerParticipator> entry : this.participators.entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("participators", saveMap);
        this.userConfig.save();
    }
}

