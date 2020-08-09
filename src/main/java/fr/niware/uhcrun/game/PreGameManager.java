package fr.niware.uhcrun.game;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.listeners.*;
import fr.niware.uhcrun.scoreboard.Reflection;
import fr.niware.uhcrun.utils.packet.ActionBar;
import fr.niware.uhcrun.utils.Scatter;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.utils.packet.Title;
import fr.niware.uhcrun.world.WorldManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PreGameManager {

    private final Main main;

    private final Game game;
    private final ActionBar actionBar;
    private final Title title;

    private final WorldManager worldManager;

    private int task;

    public PreGameManager(boolean forceStart) {
        this.main = Main.getInstance();

        this.game = main.getGame();
        this.actionBar = new ActionBar();
        this.title = new Title();

        this.worldManager = main.getWorldManager();

        game.setRunnable(true);
        State.setState(State.STARTING);

        Bukkit.getOnlinePlayers().forEach(all -> {
            Reflection.playSound(all, all.getLocation(), "ORB_PICKUP", 1f, 4f);
            title.sendTitle(all, 10, 60, 10, "§6Démarrage", "§aPréparez-vous !");
        });

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§dUHCRun §7» §aDémarrage dans §6" + game.getCountdownStart() + " §asecondes.");
        Bukkit.broadcastMessage(" ");

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {

            if (!game.isRunnable()) {
                return;
            }

            game.removeCountdownStart();
            int countdown = game.getCountdownStart();

            if (countdown > 0) {
                Bukkit.getOnlinePlayers().forEach(players -> players.setLevel(countdown));
                if (countdown < 4) {
                    actionBar.sendToAll("§7» §eDémarrage dans §f" + countdown + "s§e.");
                    Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 4f, 4f));
                }
                return;
            }

            if (countdown == 0) {
                if (game.getAlivePlayers().size() < game.getAutoStartSize() && !forceStart) {
                    Bukkit.broadcastMessage("§dUHCRun §7» §cIl n'y a pas assez de joueurs pour démarrer.");

                    Bukkit.getScheduler().cancelTask(task);
                    game.resetCountdownStart();
                    State.setState(State.WAITING);
                    return;
                }

                State.setState(State.TELEPORT);
                MinecraftServer.getServer().setMotd("§cEn cours");
                Bukkit.setWhitelist(false);

                game.getWorld().setSpawnLocation(0, game.getSpecSpawn().getBlockY(), 0);
                game.setRunnable(false);
                game.getWorld().getWorldBorder().setSize(game.getSizeMap() * 2);

                Bukkit.getOnlinePlayers().forEach(players -> {
                    players.setLevel(0);
                    Reflection.playSound(players, players.getLocation(), "EAT", 3f, 3f);
                    actionBar.sendToPlayer(players, "§7Téléportation...");
                    players.getInventory().clear();
                });

                new Scatter(true).runTaskTimer(main, 0L, 10L);
                return;
            }

            if (countdown == -7) {
                worldManager.clearAllCustomEntities();
                return;
            }

            if (countdown < -8 && countdown > -12) {
                actionBar.sendToAll("§7» §eDémarrage dans §f" + (countdown + 12) + "s§e.");
                return;
            }

            if (countdown == -12) {
                actionBar.sendToAll("§7» §eQue le meilleur gagne !");

                Bukkit.getScheduler().cancelTask(task);
                State.setState(State.MINING);

                game.setStartMillis(System.currentTimeMillis());
                game.setSizePlayers(game.getAlivePlayers().size());

                worldManager.registerObjectives();

                game.getStayLocs().clear();
                game.getBlocks().forEach(block -> block.setType(Material.AIR));
                game.getBlocks().clear();

                Bukkit.getOnlinePlayers().forEach(players -> {

                    players.playSound(players.getLocation(), Sound.EXPLODE, 5F, 5F);
                    players.getActivePotionEffects().clear();
                    players.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 24));
                    players.setWalkSpeed(0.3f);
                    players.setGameMode(GameMode.SURVIVAL);

                    for (PotionEffect potionEffect : game.getStartPotionEffects()) {
                        players.addPotionEffect(potionEffect);
                    }
                });

                Bukkit.broadcastMessage("§7§m+---------------------------------------+");
                Bukkit.broadcastMessage("  §7» §aLes alliances entre joueurs sont §cinterdites§7.");
                Bukkit.broadcastMessage("  §7» §aVous êtes invincible pendant §e30 §asecondes.");
                Bukkit.broadcastMessage("§7§m+---------------------------------------+");

                PluginManager pluginManager = Bukkit.getPluginManager();
                pluginManager.registerEvents(new AutoLapisListener(), main);
                pluginManager.registerEvents(new BlockListener(main), main);
                pluginManager.registerEvents(new DeathListener(main), main);
                pluginManager.registerEvents(new CraftListener(), main);


                Bukkit.getScheduler().runTaskLater(main, () -> {
                    List<Block> blocks = new ArrayList<>();

                    for (int x = -30; x < 30; x++) {
                        for (int y = 205; y > 195; y--) {
                            for (int z = -30; z < 30; z++) {
                                Block block = new Location(game.getWorld(), x, y, z).getBlock();
                                if (block.getType() != Material.AIR) {
                                    if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                                        block.setType(Material.AIR);
                                    } else {
                                        blocks.add(block);
                                    }
                                }
                            }
                        }
                    }
                    removeBlocks(blocks);

                }, 20 * 10);

                new GameManager();

                Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                    game.setInvincibility(false);
                    Bukkit.broadcastMessage("§dUHCRun §7» §aVous êtes désormais vulnérables aux §bdégâts§a.");
                }, 20 * 30);
            }
        }, 0, 20);
    }

    private void removeBlocks(List<Block> blocks) {
        List<Block> toRemove = new ArrayList<>(blocks);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < 200; i++) {
                    if (toRemove.isEmpty()) {
                        cancel();
                    } else {
                        Block block = toRemove.get(0);
                        toRemove.remove(block);
                        block.setType(Material.AIR);
                    }
                }
            }
        }.runTaskTimer(main, 20, 2);
    }
}


