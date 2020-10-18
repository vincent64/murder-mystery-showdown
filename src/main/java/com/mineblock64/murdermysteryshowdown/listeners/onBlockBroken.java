package com.mineblock64.murdermysteryshowdown.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class onBlockBroken implements Listener {
    @EventHandler
    public void onBlockBroken(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemBroken(HangingBreakEvent event) {
        event.setCancelled(true);
    }
}
