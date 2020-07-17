package fr.lifecraft.uhcrun.game;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.listeners.BlockEvent;
import fr.lifecraft.uhcrun.listeners.DeathEvent;
import fr.lifecraft.uhcrun.listeners.RunEvent;
import fr.lifecraft.uhcrun.listeners.StackEvent;
import fr.lifecraft.uhcrun.manager.WorldManager;
import fr.lifecraft.uhcrun.utils.ActionBar;
import fr.lifecraft.uhcrun.utils.Scatter;
import fr.lifecraft.uhcrun.utils.State;
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
    private final WorldManager worldManager;
    private final Scatter scatter;

    public int timer;
    private int task;

    public PreGameManager() {
        this.main = Main.getInstance();
        this.game = main.getGame();
        this.worldManager = main.getWorldManager();
        this.scatter = new Scatter(false, 1);

        this.timer = game.getAutoStartTime();
        main.getGame().setRunnable(true);
        game.setStart(true);

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (!main.getGame().getRunnable()) {
                return;
            }

            timer--;

            //game.setTimer(game.getTimer() - 1);
            if (timer == 0) {
                if (!(Bukkit.getOnlinePlayers().size() >= game.getAutoStart()) && !game.isForcestart()) {
                    Bukkit.broadcastMessage("§dUHCRun §8» §cIl n'y a pas assez de joueurs pour démarrer.");

                    Bukkit.getScheduler().cancelTask(task);
                    //game.setTimer(game.getAutoStartTime());
                    timer = game.getAutoStartTime();
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
                    new ActionBar("§7Téléportation...").sendToAll();
                    players.getInventory().clear();
                });

                new Scatter(true, (int) game.getWorld().getWorldBorder().getSize() - 5).runTaskTimer(main, 0L, 2);

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

                scatter.getStayLocs().clear();
                scatter.getBlocks().forEach(block -> block.setType(Material.AIR));
                scatter.getBlocks().clear();

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
                Bukkit.broadcastMessage(" §8» §7Les alliances entre joueurs sont §cinterdites§7.");
                Bukkit.broadcastMessage(" §8» §7Vous êtes invincible pendant §e30 §7secondes.");
                Bukkit.broadcastMessage("§7§m+---------------------------------------+");

                PluginManager pluginManager = Bukkit.getPluginManager();
                pluginManager.registerEvents(new RunEvent(), main);
                pluginManager.registerEvents(new BlockEvent(main), main);
                pluginManager.registerEvents(new DeathEvent(main), main);
                pluginManager.registerEvents(new StackEvent(3), main);

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
                    Bukkit.broadcastMessage("§dUHCRun §7» §eVous êtes désormais vulnérables aux §9dégâts§e.");
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


