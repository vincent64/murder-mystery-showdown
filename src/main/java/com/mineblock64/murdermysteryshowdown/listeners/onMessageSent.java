package com.mineblock64.murdermysteryshowdown.listeners;

import com.mineblock64.murdermysteryshowdown.MurderMysteryShowdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class onMessageSent implements Listener {
    @EventHandler
    public void onMessageSent(PlayerChatEvent event) {
        event.setCancelled(true);
        String rankPrefix = ChatColor.AQUA + "[MVP" + ChatColor.DARK_GREEN + "+" + ChatColor.AQUA + "] " + event.getPlayer().getDisplayName() + ChatColor.WHITE + ": ";
        if(event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            Bukkit.broadcastMessage(rankPrefix + event.getMessage());
        } else {
            for(Player player : MurderMysteryShowdown.sheriff) {
                if(player.getGameMode() == GameMode.SPECTATOR) {
                    player.sendMessage(ChatColor.GRAY + "[DEAD CHAT] " + rankPrefix + event.getMessage());
                }
            }
            for(Player player : MurderMysteryShowdown.outlaws) {
                if(player.getGameMode() == GameMode.SPECTATOR) {
                    player.sendMessage(ChatColor.GRAY + "[DEAD CHAT] " + rankPrefix + event.getMessage());
                }
            }
        }
    }
}
