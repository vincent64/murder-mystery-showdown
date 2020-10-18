package com.mineblock64.murdermysteryshowdown;

import com.mineblock64.murdermysteryshowdown.Util.CenteredMessage;
import com.mineblock64.murdermysteryshowdown.listeners.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class MurderMysteryShowdown extends JavaPlugin {
    public static HashMap<Player, Integer> playersKills = new HashMap<>();
    public static HashMap<Player, Integer> playersDeaths = new HashMap<>();
    public static int outlawKills = 0;
    public static int sheriffKills = 0;
    public static ArrayList<Player> outlaws = new ArrayList<>();
    public static ArrayList<Player> sheriff = new ArrayList<>();
    public static String WORLD_NAME = "world";
    public static int PLAYERS_NEEDED = 16;
    public static Location outlawMainSpawnpoint;
    public static Location sheriffMainSpawnpoint;
    public static Location outlawSecondarySpawnpoint;
    public static Location sheriffSecondarySpawnpoint;
    public static Location spawn;
    public static boolean hasGameStarted = false;
    public static int timeLeft;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("Murder Mystery Showdown plugin by MineBlock64");
        System.out.println("Murder Mystery Showdown plugin is starting...");
        plugin = this;
        //TODO: Add spawnpoint locations according to Hollywood map
        spawn = new Location(Bukkit.getWorld(WORLD_NAME), -24.5, 75, 8.5, -90, 0);
        outlawMainSpawnpoint = new Location(Bukkit.getWorld(WORLD_NAME), -20.5, 65, -37.5, 0, 0);
        outlawSecondarySpawnpoint = new Location(Bukkit.getWorld(WORLD_NAME), 32.5, 65, -16.5, 0, 0);
        sheriffMainSpawnpoint = new Location(Bukkit.getWorld(WORLD_NAME), 22.5, 65, 76.5, 90, 0);
        sheriffSecondarySpawnpoint = new Location(Bukkit.getWorld(WORLD_NAME), -30.5, 75, 69.5, 180, 0);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new onKill(), this);
        pluginManager.registerEvents(new onJoin(), this);
        pluginManager.registerEvents(new onItemDrop(), this);
        pluginManager.registerEvents(new onMessageSent(), this);
        pluginManager.registerEvents(new onDamage(), this);
        pluginManager.registerEvents(new onFoodDecreasing(), this);
        pluginManager.registerEvents(new onBlockBroken(), this);
        Team hideNametagTest = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("hiddentag");
        if(hideNametagTest != null) {
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("hiddentag").unregister();
        }
        Team hideNametag = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("hiddentag");
        hideNametag.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        System.out.println("Murder Mystery Showdown plugin loaded. 16 players are needed to start a game.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Player[] getOnlinePlayer() {
        return Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
    }

    public static void startGame() {
        hasGameStarted = true;
        timeLeft = 270;
        outlaws.clear();
        sheriff.clear();
        playersKills.clear();
        playersDeaths.clear();
        outlawKills = 0;
        sheriffKills = 0;

        //Choose outlaws and sheriff
        ArrayList<Player> onlinePlayers = new ArrayList<>();
        Collections.addAll(onlinePlayers, getOnlinePlayer());
        Collections.shuffle(onlinePlayers);
        outlaws.addAll(onlinePlayers.subList(0, onlinePlayers.size() / 2));
        sheriff.addAll(onlinePlayers.subList(onlinePlayers.size() / 2, onlinePlayers.size()));

        //Set colored armor to outlaws and sheriff
        ItemStack outlawHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) outlawHelmet.getItemMeta();
        helmetMeta.setColor(Color.RED);
        outlawHelmet.setItemMeta(helmetMeta);

        ItemStack sheriffHelmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta2 = (LeatherArmorMeta) sheriffHelmet.getItemMeta();
        helmetMeta2.setColor(Color.AQUA);
        sheriffHelmet.setItemMeta(helmetMeta2);

        ItemStack outlawBoots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) outlawBoots.getItemMeta();
        bootsMeta.setColor(Color.RED);
        outlawBoots.setItemMeta(bootsMeta);

        ItemStack sheriffBoots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta2 = (LeatherArmorMeta) sheriffBoots.getItemMeta();
        bootsMeta2.setColor(Color.AQUA);
        sheriffBoots.setItemMeta(bootsMeta2);

        for(Player outlaw : outlaws) {
            outlaw.getInventory().setHelmet(outlawHelmet);
            outlaw.getInventory().setBoots(outlawBoots);
        }
        for(Player sheriff : sheriff) {
            sheriff.getInventory().setHelmet(sheriffHelmet);
            sheriff.getInventory().setBoots(sheriffBoots);
        }

        /*//Make invisible nametag above player's head with scoreboards
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams add everyone Everyone");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams join everyone @a");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams option everyone nametagVisibility never");*/

        //Send message to outlwas and sheriff and teleport them to their spawn point in the Hollywood map
        for(Player player : outlaws) {
            CenteredMessage.sendCenteredMessage(player, ChatColor.GREEN + "-----------------------------------------------------");
            CenteredMessage.sendCenteredMessage(player, ChatColor.WHITE + "" + ChatColor.BOLD + "Showdown");
            CenteredMessage.sendCenteredMessage(player, "\u0020");
            CenteredMessage.sendCenteredMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "The first team to get to 30 kills, or the team");
            CenteredMessage.sendCenteredMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "who gets the most kills, win the game!");
            CenteredMessage.sendCenteredMessage(player, "\u0020");
            CenteredMessage.sendCenteredMessage(player, ChatColor.GREEN + "-----------------------------------------------------");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 200));
            player.teleport(outlawMainSpawnpoint);
            playersKills.put(player, 0);
            playersDeaths.put(player, 0);
            generateScoreboard(player, ChatColor.RED + "Outlaw");
            player.setPlayerListName(ChatColor.RED + player.getDisplayName());
        }
        for(Player player : sheriff) {
            CenteredMessage.sendCenteredMessage(player, ChatColor.GREEN + "-----------------------------------------------------");
            CenteredMessage.sendCenteredMessage(player, ChatColor.WHITE + "" + ChatColor.BOLD + "Showdown");
            CenteredMessage.sendCenteredMessage(player, "\u0020");
            CenteredMessage.sendCenteredMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "The first team to get to 30 kills, or the team");
            CenteredMessage.sendCenteredMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "who gets the most kills, win the game!");
            CenteredMessage.sendCenteredMessage(player, "\u0020");
            CenteredMessage.sendCenteredMessage(player, ChatColor.GREEN + "-----------------------------------------------------");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 200));
            player.teleport(sheriffMainSpawnpoint);
            playersKills.put(player, 0);
            playersDeaths.put(player, 0);
            generateScoreboard(player, ChatColor.AQUA + "Sheriff");
            player.setPlayerListName(ChatColor.AQUA + player.getDisplayName());
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : outlaws) {
                player.sendTitle(ChatColor.RED + "OUTLAW", ChatColor.YELLOW + "Kill sheriffs!", 0, 80, 2);
            }
            for(Player player : sheriff) {
                player.sendTitle(ChatColor.AQUA + "SHERIFF", ChatColor.YELLOW + "Kill outlaws!", 0, 80, 2);
            }
            Bukkit.broadcastMessage(ChatColor.YELLOW + "You will receive your weapons in " + ChatColor.RED + "5" + ChatColor.YELLOW + " seconds!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "You will receive your weapons in " + ChatColor.RED + "4" + ChatColor.YELLOW + " seconds!");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "You will receive your weapons in " + ChatColor.RED + "3" + ChatColor.YELLOW + " seconds!");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "You will receive your weapons in " + ChatColor.RED + "2" + ChatColor.YELLOW + " seconds!");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            Bukkit.broadcastMessage(ChatColor.YELLOW + "You will receive your weapons in " + ChatColor.RED + "1" + ChatColor.YELLOW + " seconds!");
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, MurderMysteryShowdown::startCompetitiveGame, 20);
                        }, 20);
                    }, 20);
                }, 20);
            }, 20);
        }, 20), 60);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            timeLeft = timeLeft - 1;
            if(timeLeft != 0) {
                for (Player player : outlaws) {
                    generateScoreboard(player, ChatColor.RED + "Outlaw");
                }
                for (Player player : sheriff) {
                    generateScoreboard(player, ChatColor.AQUA + "Sheriff");
                }
            } else {
                Bukkit.getScheduler().cancelAllTasks();
                for (Player player : outlaws) {
                    generateScoreboard(player, ChatColor.RED + "Outlaw");
                }
                for (Player player : sheriff) {
                    generateScoreboard(player, ChatColor.AQUA + "Sheriff");
                }
                if(outlawKills > sheriffKills) {
                    endGame(ChatColor.RED + "" + ChatColor.BOLD + "Outlaws");
                } else if(sheriffKills > outlawKills) {
                    endGame(ChatColor.AQUA + "" + ChatColor.BOLD + "Sheriffs");
                } else {
                    endGame(ChatColor.RED + "" + ChatColor.BOLD + "Outlaws");
                }
            }
        }, 0, 20);
    }

    private static void startCompetitiveGame() {
        ItemStack infinityBow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = infinityBow.getItemMeta();
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bowMeta.setDisplayName(ChatColor.AQUA + "Bow");
        bowMeta.setUnbreakable(true);
        infinityBow.setItemMeta(bowMeta);
        ItemStack unbreakableSword = new ItemStack(Material.STICK);
        ItemMeta swordMeta = unbreakableSword.getItemMeta();
        swordMeta.setUnbreakable(true);
        swordMeta.setDisplayName(ChatColor.GREEN + "Knife");
        unbreakableSword.setItemMeta(swordMeta);
        for(Player player : outlaws) {
            player.getInventory().setItem(0, infinityBow);
            player.getInventory().setItem(1, unbreakableSword);
            player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        }
        for(Player player : sheriff) {
            player.getInventory().setItem(0, infinityBow);
            player.getInventory().setItem(1, unbreakableSword);
            player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        }
    }

    public static void respawnProcess(Player victim) {
        victim.setGameMode(GameMode.SPECTATOR);
        victim.getInventory().setItem(0, null);
        if(outlaws.contains(victim)) {
            victim.setPlayerListName(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + victim.getDisplayName());
        } else if(sheriff.contains(victim)) {
            victim.setPlayerListName(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + victim.getDisplayName());
        }
        victim.sendTitle(ChatColor.RED + "YOU ARE DEAD", ChatColor.YELLOW + "Respawning in 5 seconds!", 0, 20, 0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            victim.sendTitle(ChatColor.RED + "YOU ARE DEAD", ChatColor.YELLOW + "Respawning in 4 seconds!", 0, 20, 0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                victim.sendTitle(ChatColor.RED + "YOU ARE DEAD", ChatColor.YELLOW + "Respawning in 3 seconds!", 0, 20, 0);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    victim.sendTitle(ChatColor.RED + "YOU ARE DEAD", ChatColor.YELLOW + "Respawning in 2 seconds!", 0, 20, 0);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        victim.sendTitle(ChatColor.RED + "YOU ARE DEAD", ChatColor.YELLOW + "Respawning in 1 seconds!", 0, 20, 0);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            victim.sendTitle(ChatColor.GREEN + "Respawned!", "", 0, 40, 2);
                            if(outlaws.contains(victim)) {
                                int proximityExceededMain = 0;
                                int proximityExceededSec = 0;
                                for(Player player : sheriff) {
                                    if(player.getLocation().distance(outlawMainSpawnpoint) < 8) {
                                        proximityExceededMain++;
                                    }
                                    if(player.getLocation().distance(outlawSecondarySpawnpoint) < 8) {
                                        proximityExceededSec++;
                                    }
                                }
                                if(proximityExceededMain > proximityExceededSec) {
                                    victim.teleport(outlawSecondarySpawnpoint);
                                } else {
                                    victim.teleport(outlawMainSpawnpoint);
                                }
                                victim.setPlayerListName(ChatColor.RED + victim.getDisplayName());
                            } else if(sheriff.contains(victim)) {
                                int proximityExceededMain = 0;
                                int proximityExceededSec = 0;
                                for(Player player : outlaws) {
                                    if(player.getLocation().distance(sheriffMainSpawnpoint) < 8) {
                                        proximityExceededMain++;
                                    }
                                    if(player.getLocation().distance(sheriffSecondarySpawnpoint) < 8) {
                                        proximityExceededSec++;
                                    }
                                }
                                if(proximityExceededMain > proximityExceededSec) {
                                    victim.teleport(sheriffSecondarySpawnpoint);
                                } else {
                                    victim.teleport(sheriffMainSpawnpoint);
                                }
                                victim.setPlayerListName(ChatColor.AQUA + victim.getDisplayName());
                            }
                            ItemStack infinityBow = new ItemStack(Material.BOW);
                            ItemMeta bowMeta = infinityBow.getItemMeta();
                            bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            bowMeta.setDisplayName(ChatColor.AQUA + "Bow");
                            bowMeta.setUnbreakable(true);
                            infinityBow.setItemMeta(bowMeta);
                            victim.setGameMode(GameMode.ADVENTURE);
                            victim.getInventory().setItem(0, infinityBow);
                        }, 20);
                    }, 20);
                }, 20);
            }, 20);
        }, 20);
    }

    public static void checkVictory() {
        if(outlawKills == 30) {
            endGame(ChatColor.RED + "" + ChatColor.BOLD + "Outlaws");
        } else if(sheriffKills == 30) {
            endGame(ChatColor.AQUA + "" + ChatColor.BOLD + "Sheriffs");
        }
    }

    private static void endGame(String winner) {
        hasGameStarted = false;

        for(Player player : outlaws) {
            player.getInventory().clear();
        }
        for(Player player : sheriff) {
            player.getInventory().clear();
        }

        for(Player player : outlaws) {
            displayResults(player, winner);
        }
        for(Player player : sheriff) {
            displayResults(player, winner);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(Player player : outlaws) {
                player.teleport(spawn);
                player.setGameMode(GameMode.ADVENTURE);
            }
            for(Player player : sheriff) {
                player.teleport(spawn);
                player.setGameMode(GameMode.ADVENTURE);
            }

            restartGame();
        }, 100);
    }

    private static void generateScoreboard(Player player, String team) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Scoreboard", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "MURDER MYSTERY");

        Score score1 = objective.getScore(ChatColor.YELLOW + "by MineBlock64");
        Score score2 = objective.getScore("\u0020");
        Score score3 = objective.getScore(ChatColor.WHITE + "Map: " + ChatColor.GREEN + "Hollywood");
        Score score4 = objective.getScore("\u0020\u0020");
        Score score5 = objective.getScore(ChatColor.WHITE + "Outlaw Kills: " + ChatColor.GREEN + outlawKills);
        Score score6 = objective.getScore(ChatColor.WHITE + "Sheriff Kills: " + ChatColor.GREEN + sheriffKills);
        Score score7 = objective.getScore("\u0020\u0020\u0020");
        Score score8 = objective.getScore(ChatColor.WHITE + "Deaths: " + ChatColor.GREEN + playersDeaths.get(player));
        Score score9 = objective.getScore(ChatColor.WHITE + "Kills: " + ChatColor.GREEN + playersKills.get(player));
        Score score10 = objective.getScore("\u0020\u0020\u0020\u0020");
        Score score11 = objective.getScore(ChatColor.WHITE + "Time left: " + ChatColor.GREEN + String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
        Score score12 = objective.getScore("\u0020\u0020\u0020\u0020\u0020");
        Score score13 = objective.getScore(ChatColor.WHITE + "Team: " + team);
        Score score14 = objective.getScore("\u0020\u0020\u0020\u0020\u0020\u0020");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Score score15 = objective.getScore(ChatColor.GRAY + LocalDate.now().format(dateFormatter));
        score1.setScore(1);
        score2.setScore(2);
        score3.setScore(3);
        score4.setScore(4);
        score5.setScore(5);
        score6.setScore(6);
        score7.setScore(7);
        score8.setScore(8);
        score9.setScore(9);
        score10.setScore(10);
        score11.setScore(11);
        score12.setScore(12);
        score13.setScore(13);
        score14.setScore(14);
        score15.setScore(15);

        player.setScoreboard(scoreboard);
    }

    private static void displayResults(Player player, String winner) {
        String dividerLine = ChatColor.GREEN + "-----------------------------------------------------";
        String outlawsKillsLine = ChatColor.RED + "" + ChatColor.BOLD + "Outlaws" + ChatColor.WHITE + " - " + outlawKills + " Kills";
        String sheriffsKillsLine = ChatColor.AQUA + "" + ChatColor.BOLD + "Sheriffs" + ChatColor.WHITE + " - " + sheriffKills + " Kills";
        String playerOfTheGameLine;
        String winnerLine = ChatColor.WHITE + "" + ChatColor.BOLD + "Winner: " + winner;
        double kd;
        if(playersDeaths.get(player) == 0) {
            kd = playersKills.get(player);
        } else {
            kd = playersKills.get(player)/playersDeaths.get(player);
        }
        String myStatsLine = ChatColor.WHITE + "Kills: " + playersKills.get(player) + " - Deaths: " + playersDeaths.get(player) + " - K/D: " + new DecimalFormat("#.##").format(kd);

        //Check for the player with most k/d
        double maxKd = 0;
        for(Map.Entry<Player, Integer> entry : playersKills.entrySet()) {
            double cKd;
            if(playersDeaths.get(entry.getKey()) == 0) {
                cKd = entry.getValue();
            } else {
                cKd = entry.getValue()/playersDeaths.get(entry.getKey());
            }
            if(cKd > maxKd) {
                maxKd = cKd;
            }
        }
        ArrayList<Player> highestKd = new ArrayList<>();
        for(Map.Entry<Player, Integer> entry : playersKills.entrySet()) {
            double cKd;
            if(playersDeaths.get(entry.getKey()) == 0) {
                cKd = entry.getValue();
            } else {
                cKd = entry.getValue()/playersDeaths.get(entry.getKey());
            }
            if(cKd == maxKd) {
                highestKd.add(entry.getKey());
            }
        }
        if(highestKd.size() == 1) {
            playerOfTheGameLine = ChatColor.GOLD + "Player of the Game" + ChatColor.WHITE + " - " + ChatColor.AQUA + highestKd.get(0).getDisplayName() + ChatColor.WHITE + " K/D: " + new DecimalFormat("#.##").format(maxKd);
        } else {
            int random = new Random().nextInt(highestKd.size());
            playerOfTheGameLine = ChatColor.GOLD + "Player of the Game" + ChatColor.WHITE + " - " + ChatColor.AQUA + highestKd.get(random).getDisplayName() + ChatColor.WHITE + " K/D: " + new DecimalFormat("#.##").format(maxKd);
        }

        CenteredMessage.sendCenteredMessage(player, dividerLine);
        CenteredMessage.sendCenteredMessage(player, ChatColor.WHITE + "" + ChatColor.BOLD + "MURDER MYSTERY");
        CenteredMessage.sendCenteredMessage(player, "\u0020");
        CenteredMessage.sendCenteredMessage(player, winnerLine);
        CenteredMessage.sendCenteredMessage(player, "\u0020");
        CenteredMessage.sendCenteredMessage(player, sheriffsKillsLine);
        CenteredMessage.sendCenteredMessage(player, outlawsKillsLine);
        CenteredMessage.sendCenteredMessage(player, "\u0020");
        CenteredMessage.sendCenteredMessage(player, playerOfTheGameLine);
        CenteredMessage.sendCenteredMessage(player, "\u0020");
        CenteredMessage.sendCenteredMessage(player, ChatColor.WHITE + "" + ChatColor.BOLD + "Your Stats");
        CenteredMessage.sendCenteredMessage(player, myStatsLine);
        CenteredMessage.sendCenteredMessage(player, "\u0020");
        CenteredMessage.sendCenteredMessage(player, dividerLine);
    }

    private static void restartGame() {
        if(MurderMysteryShowdown.getOnlinePlayer().length == PLAYERS_NEEDED) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "5" + ChatColor.YELLOW + " seconds!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                if(MurderMysteryShowdown.getOnlinePlayer().length == PLAYERS_NEEDED) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "4" + ChatColor.YELLOW + " seconds!");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                        if(MurderMysteryShowdown.getOnlinePlayer().length == PLAYERS_NEEDED) {
                            Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "3" + ChatColor.YELLOW + " seconds!");
                            Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                                if(MurderMysteryShowdown.getOnlinePlayer().length == PLAYERS_NEEDED) {
                                    Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "2" + ChatColor.YELLOW + " seconds!");
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                                        if(MurderMysteryShowdown.getOnlinePlayer().length == PLAYERS_NEEDED) {
                                            Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start in " + ChatColor.RED + "1" + ChatColor.YELLOW + " seconds!");
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(MurderMysteryShowdown.plugin, () -> {
                                                if(MurderMysteryShowdown.getOnlinePlayer().length == PLAYERS_NEEDED) {
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
    }
}
