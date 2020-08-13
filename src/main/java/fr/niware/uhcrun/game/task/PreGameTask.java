package fr.niware.uhcrun.game.task;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.scoreboard.Reflection;
import fr.niware.uhcrun.structure.BlockData;
import fr.niware.uhcrun.structure.Structure;
import fr.niware.uhcrun.utils.Scatter;
import fr.niware.uhcrun.utils.State;
import fr.niware.uhcrun.utils.packet.ActionBar;
import fr.niware.uhcrun.world.WorldManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PreGameTask {

    private final Main main;

    private final Game game;
    private final ActionBar actionBar;

    private final WorldManager worldManager;

    private int task;

    public PreGameTask(boolean forceStart) {
        this.main = Main.getInstance();

        this.game = main.getGame();
        this.actionBar = new ActionBar();

        this.worldManager = main.getWorldManager();

        game.setRunnable(true);
        State.setState(State.STARTING);

        Bukkit.getOnlinePlayers().forEach(all -> Reflection.playSound(all, all.getLocation(), "ORB_PICKUP", 1f, 4f));

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
                    Bukkit.getOnlinePlayers().forEach(players -> players.playSound(players.getLocation(), Sound.CLICK, 4f, 4f));
                }
                return;
            }

            if (countdown == 0) {
                if (game.getAlivePlayers().size() < game.getAutoStartSize() && !forceStart) {
                    game.resetCountdownStart(task);
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

            if (countdown == -5) {
                worldManager.clearAllCustomEntities();
                return;
            }

            if (countdown < -5 && countdown > -9) {
                actionBar.sendToAll("§7» §eDémarrage dans §f" + (countdown + 9) + "s§e.");
                return;
            }

            if (countdown == -9) {
                actionBar.sendToAll("§7» §eQue le meilleur gagne !");

                main.getServer().getScheduler().cancelTask(task);
                State.setState(State.MINING);

                game.setStartMillis(System.currentTimeMillis());
                game.setSizePlayers(game.getAlivePlayers().size());

                worldManager.registerObjectives();

                game.getStayLocs().clear();
                game.getBlocks().forEach(block -> block.setType(Material.AIR));
                game.getBlocks().clear();

                for (UUID uuid : game.getAlivePlayers()) {
                    Player players = Bukkit.getPlayer(uuid);
                    if (players == null) continue;

                    players.playSound(players.getLocation(), Sound.EXPLODE, 5F, 5F);
                    players.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
                    players.setWalkSpeed(0.3f);
                    players.setGameMode(GameMode.SURVIVAL);

                    game.getStartPotionEffects().forEach(players::addPotionEffect);
                }

                Bukkit.broadcastMessage("§7§m+-------------------------------+");
                Bukkit.broadcastMessage("               §6● §eDébut de la partie §6●");
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage("  §7» §aAlliances §cinterdites§7.");
                Bukkit.broadcastMessage("  §7» §aInvincibilité pendant §e30 §asecondes.");
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage("§7§m+-------------------------------+");

                Bukkit.getScheduler().runTaskLater(main, () -> {
                    Structure structure = main.getGame().getStructure().get("spawn");

                    BlockData[][][] blocks = structure.getBlocks();
                    int xStart = (int) game.getSpawn().getX() - structure.getXAnchor();
                    int yStart = (int) game.getSpawn().getY() - structure.getYAnchor();
                    int zStart = (int) game.getSpawn().getZ() - structure.getZAnchor();

                    long start = System.currentTimeMillis();

                    for (int x = 0; x < structure.getXSize(); x++) {
                        for (int y = 0; y < structure.getYSize(); y++) {
                            for (int z = 0; z < structure.getZSize(); z++) {
                                BlockData data = blocks[x][y][z];
                                if (data != null) {
                                    new Location(game.getWorld(), xStart + x, yStart + y, zStart + z).getBlock().setType(Material.AIR);
                                }
                            }
                        }
                    }

                    main.log("Spawn platform remove in " + (System.currentTimeMillis() - start) + " ms");

                }, 20 * 10);

                new GameTask();

                Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
                    game.setInvincibility(false);
                    Bukkit.broadcastMessage("§dUHCRun §7» §aVous êtes désormais vulnérables aux §bdégâts§a.");
                }, 20 * 30);
            }
        }, 0, 20);
    }
}


