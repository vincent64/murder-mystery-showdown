package com.mineblock64.murdermysteryshowdown.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class onItemDrop implements Listener {
    @EventHandler
    public void onItemSlotChanged(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
