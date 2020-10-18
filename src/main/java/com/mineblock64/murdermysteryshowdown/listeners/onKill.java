package com.mineblock64.murdermysteryshowdown.listeners;

import com.mineblock64.murdermysteryshowdown.MurderMysteryShowdown;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class onKill implements Listener {
    @EventHandler
    public void onKill(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            //Probably a bow kill
            Projectile arrow = (Projectile) event.getDamager();
            if(arrow.getShooter() instanceof Player) {
                Player victim = (Player) event.getEntity();
                Player shooter = (Player) arrow.getShooter();
                if (!arePlayersInSameTeam(victim, shooter)) {
                    shooter.sendMessage(ChatColor.YELLOW + "You have killed " + ChatColor.AQUA + victim.getDisplayName());
                    victim.sendMessage(ChatColor.YELLOW + "You have been killed by " + ChatColor.AQUA + shooter.getDisplayName());
                    MurderMysteryShowdown.playersKills.put(shooter, MurderMysteryShowdown.playersKills.get(shooter)+1);
                    MurderMysteryShowdown.playersDeaths.put(victim, MurderMysteryShowdown.playersDeaths.get(victim)+1);
                    MurderMysteryShowdown.checkVictory();
                    MurderMysteryShowdown.respawnProcess(victim);
                    if (MurderMysteryShowdown.outlaws.contains(shooter)) {
                        MurderMysteryShowdown.outlawKills++;
                    } else if (MurderMysteryShowdown.sheriff.contains(shooter)) {
                        MurderMysteryShowdown.sheriffKills++;
                    }
                }/* else {
                    event.setCancelled(true);
                }*/
            }
        } else if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            //Probably a sword kill
            Player victim = (Player) event.getEntity();
            Player killer = (Player) event.getDamager();
            if(!arePlayersInSameTeam(victim, killer) && killer.getItemInHand().getType() == Material.STICK) {
                killer.sendMessage(ChatColor.YELLOW + "You have killed " + ChatColor.AQUA + victim.getDisplayName());
                victim.sendMessage(ChatColor.YELLOW + "You have been killed by " + ChatColor.AQUA + killer.getDisplayName());
                MurderMysteryShowdown.playersKills.put(killer, MurderMysteryShowdown.playersKills.get(killer)+1);
                MurderMysteryShowdown.playersDeaths.put(victim, MurderMysteryShowdown.playersDeaths.get(victim)+1);
                MurderMysteryShowdown.checkVictory();
                MurderMysteryShowdown.respawnProcess(victim);
                if (MurderMysteryShowdown.outlaws.contains(killer)) {
                    MurderMysteryShowdown.outlawKills++;
                } else if (MurderMysteryShowdown.sheriff.contains(killer)) {
                    MurderMysteryShowdown.sheriffKills++;
                }
            }/* else {
                event.setCancelled(true);
            }*/
        }
    }

    private static boolean arePlayersInSameTeam(Player victim, Player killer) {
        ArrayList<Player> outlaws = MurderMysteryShowdown.outlaws;
        ArrayList<Player> sheriff = MurderMysteryShowdown.sheriff;
        boolean areInSameTeam = false;
        if(outlaws.contains(victim) && outlaws.contains(killer)) {
            areInSameTeam = true;
        } else if(sheriff.contains(victim) && sheriff.contains(killer)) {
            areInSameTeam = true;
        }
        return areInSameTeam;
    }
}
