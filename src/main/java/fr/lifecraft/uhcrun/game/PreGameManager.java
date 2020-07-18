package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.listeners.BlockListener;
import fr.lifecraft.uhcrun.listeners.DeathListener;
import fr.lifecraft.uhcrun.listeners.RunListener;
import fr.lifecraft.uhcrun.listeners.StackListener;
import fr.lifecraft.uhcrun.world.WorldManager;
import fr.lifecraft.uhcrun.utils.ActionBar;
import fr.lifecraft.uhcrun.utils.Scatter;
import fr.lifecraft.uhcrun.utils.State;
import fr.lifecraft.uhcrun.utils.Title;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PreGameManager {

    private final Main main;

    private final Game game;
    private final Title title;

    private final WorldManager worldManager;

    private int task;


    public PreGameManager() {
        this.main = Main.getInstance();

        this.game = main.getGame();
        this.title = new Title();

        this.worldManager = main.getWorldManager();

        main.getGame().setRunnable(true);
        game.setStart(true);

        Bukkit.getOnlinePlayers().forEach(all -> {
            all.playSound(all.getLocation(), Sound.ORB_PICKUP, 1, 4);
            title.sendTitle(all, 10, 60, 10, "§6Démarrage", "§aPréparez-vous !");
        });

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§dUHCRun §7» §aLa partie va démarrer dans §6" + game.getTimer() + " §asecondes.");
        Bukkit.broadcastMessage(" ");

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (!game.getRunnable()) {
                return;
            }

            int timer = game.getTimer();
            game.setTimer(game.getTimer() - 1);

            if (timer == 0) {
                if (!(Bukkit.getOnlinePlayers().size() >= game.getAutoStart()) && !game.isForcestart()) {
                    Bukkit.broadcastMessage("§dUHCRun §7» §cIl n'y a pas assez de joueurs pour démarrer.");

                    Bukkit.getScheduler().cancelTask(task);
                    game.setTimer(game.getAutoStartTime());
                    game.setStart(false);
                    return;
                }

                State.setState(State.TELEPORT);
                MinecraftServer.getServer().setMotd("§cEn cours");
                Bukkit.setWhitelist(false);
                game.setRunnable(false);
                game.setTimer(0);

                World world = game.getWorld();
                world.getWorldBorder().setSize(game.getSize() * 2);

                Bukkit.getOnlinePlayers().forEach(players -> {
                    players.setLevel(0);
                    players.playSound(players.getLocation(), Sound.EAT, 3F, 3F);
                    new ActionBar("§7Téléportation...").sendToPlayer(players);
                    players.getInventory().clear();
                });

                new Scatter(true).runTaskTimer(main, 0L, 10L);

            }

            if (timer == -7) {
                worldManager.clearAllCustomEntities();
            }

            if (timer < -8 && timer > -12) {
                new ActionBar("§7≫ §eDémarrage dans §f" + (timer + 12) + "s§e.").sendToAll();
            }

            if (timer == -12) {
                new ActionBar("§7≫ §eQue le meilleur gagne !").sendToAll();

                Bukkit.getScheduler().cancelTask(task);
                State.setState(State.MINING);

                worldManager.registerObjectives();

                System.out.print("TEST");
                game.getStayLocs().clear();
                game.getBlocks().forEach(block -> block.setType(Material.AIR));
                game.getBlocks().clear();

                for (UUID uuid : game.getAlivePlayers()) {

                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;

                    player.playSound(player.getLocation(), Sound.EXPLODE, 5F, 5F);
                    player.getActivePotionEffects().clear();
                    player.teleport(player.getLocation().clone().add(0, 2, 0));

                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 3, 1, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, false, false));

                    player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 24));

                    player.setWalkSpeed(0.3f);

                    player.setGameMode(GameMode.SURVIVAL);
                }

                Bukkit.broadcastMessage("§7§m+---------------------------------------+");
                Bukkit.broadcastMessage(" §7» §aLes alliances entre joueurs sont §cinterdites§7.");
                Bukkit.broadcastMessage(" §7» §aVous êtes invincible pendant §e30 §asecondes.");
                Bukkit.broadcastMessage("§7§m+---------------------------------------+");

                PluginManager pluginManager = Bukkit.getPluginManager();
                pluginManager.registerEvents(new RunListener(), main);
                pluginManager.registerEvents(new BlockListener(main), main);
                pluginManager.registerEvents(new DeathListener(main), main);
                pluginManager.registerEvents(new StackListener(3), main);

                Bukkit.getScheduler().runTaskLater(main, () -> {

                    List<Block> blocks = new ArrayList<>();

                    for (int x = -30; x < 30; x++) {
                        for (int y = 205; y > 195; y--) {
                            for (int z = -30; z < 30; z++) {
                                Block block = new Location(WorldManager.WORLD, x, y, z).getBlock();
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
                    Bukkit.broadcastMessage("§dUHCRun §7» §aVous êtes désormais vulnérables aux §9dégâts§e.");
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


