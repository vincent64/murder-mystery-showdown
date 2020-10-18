package com.mineblock64.murdermysteryshowdown.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class onFoodDecreasing implements Listener {
    @EventHandler
    public void onFoodDecreasing(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
