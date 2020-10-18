package com.mineblock64.murdermysteryshowdown.listeners;

import com.mineblock64.murdermysteryshowdown.MurderMysteryShowdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!MurderMysteryShowdown.hasGameStarted && MurderMysteryShowdown.getOnlinePlayer().length < MurderMysteryShowdown.PLAYERS_NEEDED+1) {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("hiddentag").addEntry(event.getPlayer().getName());
            event.getPlayer().getInventory().clear();
            event.setJoinMessage(ChatColor.AQUA + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " joined (" + ChatColor.AQUA + MurderMysteryShowdown.getOnlinePlayer().length + ChatColor.YELLOW + "/" + ChatColor.AQUA + "16" + ChatColor.YELLOW + ")!");
            event.getPlayer().teleport(MurderMysteryShowdown.spawn);
            if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                startTickingGame();
            }
        } else {
            event.getPlayer().kickPlayer("Game already started. Join later!");
        }
    }

    private static void startTickingGame() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
            if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "5" + ChatColor.YELLOW + " seconds!");
                Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                    if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "4" + ChatColor.YELLOW + " seconds!");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                            if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "3" + ChatColor.YELLOW + " seconds!");
                                Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                                    if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                                        Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "2" + ChatColor.YELLOW + " seconds!");
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                                            if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                                                Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "1" + ChatColor.YELLOW + " seconds!");
                                                Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                                                    if(MurderMysteryShowdown.getOnlinePlayer().length == MurderMysteryShowdown.PLAYERS_NEEDED) {
                                                        MurderMysteryShowdown.startGame();
                                                    } else {
                                                        Bukkit.broadcastMessage(ChatColor.RED + "Game cancelled. Not enough players.");
                                                    }
                                                }, 20);
                                            } else {
                                                Bukkit.broadcastMessage(ChatColor.RED + "Game cancelled. Not enough players.");
                                            }
                                        }, 20);
                                    } else {
                                        Bukkit.broadcastMessage(ChatColor.RED + "Game cancelled. Not enough players.");
                                    }
                                }, 20);
                            } else {
                                Bukkit.broadcastMessage(ChatColor.RED + "Game cancelled. Not enough players.");
                            }
                        }, 20);
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + "Game cancelled. Not enough players.");
                    }
                }, 20);
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "Game cancelled. Not enough players.");
            }
        }, 20);
    }
}
