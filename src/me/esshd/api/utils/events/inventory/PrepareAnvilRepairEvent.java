package me.esshd.api.utils.events.inventory;

import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PrepareAnvilRepairEvent
        extends InventoryEvent
        implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final HumanEntity repairer;
    private final Block anvil;
    private ItemStack result;

    public PrepareAnvilRepairEvent(HumanEntity repairer, InventoryView view, Block anvil, ItemStack result) {
        super(view);
        this.anvil = anvil;
        this.result = result;
        this.repairer = repairer;
    }

    public HumanEntity getRepairer() {
        return this.repairer;
    }

    public Block getAnvil() {
        return this.anvil;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}