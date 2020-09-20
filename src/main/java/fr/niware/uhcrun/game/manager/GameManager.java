package fr.niware.uhcrun.game.manager;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.EndTask;
import fr.niware.uhcrun.game.tasks.GameTask;
import fr.niware.uhcrun.game.tasks.ScatterTask;
import fr.niware.uhcrun.utils.packet.ActionBar;
import fr.niware.uhcrun.utils.packet.Title;
import fr.niware.uhcrun.utils.structure.BlockData;
import fr.niware.uhcrun.utils.structure.Structure;
import fr.niware.uhcrun.world.WorldManager;
import io.papermc.lib.PaperLib;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldGenLakes;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final UHCRun main;
    private final Game game;
    private final Title title;
    private final ActionBar actionBar;
    private final WorldManager worldManager;

    public GameManager(UHCRun main) {
        this.main = main;
        this.game = main.getGame();
        this.title = new Title();
        this.actionBar = new ActionBar();
        this.worldManager = main.getWorldManager();
    }

    public void startGame() {
        actionBar.sendToAll("§7» §eQue le meilleur gagne !");

        GameState.setState(GameState.MINING);

        game.setStartMillis(System.currentTimeMillis());
        game.setSizePlayers(game.getAlivePlayers().size());

        worldManager.registerObjectives();

        game.getStayLocs().clear();
        game.getBlocks().forEach(block -> block.setType(Material.AIR));
        new WorldGenLakes(Blocks.WATER);
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

        new GameTask(main).runTaskTimer(main, 0L, 20L);

        Bukkit.getScheduler().runTaskLater(main, () -> {
            Structure structure = game.getStructure().get("spawn");

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

        }, 10 * 20);

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
            if(!GameState.isInGame()) return;

            game.setInvincibility(false);
            Bukkit.broadcastMessage("§dUHCRun §7» §aVous êtes désormais vulnérables aux §bdégâts§a.");
        }, 30 * 20);
    }

    public void startTeleport() {
        GameState.setState(GameState.TELEPORT);
        MinecraftServer.getServer().setMotd("§cEn cours");
        Bukkit.setWhitelist(false);

        game.getWorld().setSpawnLocation(0, game.getSpecSpawn().getBlockY(), 0);
        game.setRunnable(false);
        game.getWorld().getWorldBorder().setSize(game.getSizeMap() * 2);

        Bukkit.getOnlinePlayers().forEach(players -> {
            actionBar.sendToPlayer(players, "§7Téléportation...");

            players.setLevel(0);
            players.playSound(players.getLocation(), Sound.EAT, 3f, 3f);
            players.getInventory().clear();
        });

        new ScatterTask(main, true).runTaskTimer(main, 0L, 10L);
    }

    public void endGame(UUID uuid) {
        Location winLocation = game.getSpawn();

        Player winner = Bukkit.getOfflinePlayer(uuid).getPlayer();
        game.setWinner(winner);
        game.setInvincibility(true);

        GameState.setState(GameState.FINISH);
        MinecraftServer.getServer().setMotd("§6Fin de la partie");

        Bukkit.getScheduler().runTaskLater(main, () -> {
            main.getStructureLoader().paste(winLocation, "win", true);

            for (Player players : Bukkit.getOnlinePlayers()) {
                PaperLib.teleportAsync(players, winLocation);

                if (players.getUniqueId() != uuid) {
                    players.playSound(players.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                    title.sendTitle(players, 10, 40, 10, "§6UHCRun", "§fVictoire de §a" + winner.getName());
                    continue;
                }

                title.sendTitle(players, 10, 40, 10, "§6Vous avez gagné", "§fVictoire de §a" + winner.getName());
                winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 8.0F, 8.0F);
            }

            game.getWorld().getWorldBorder().setSize(400);
            int killsWinner = main.getPlayerManager().getUHCPlayer(winner.getUniqueId()).getKillsGame();

            Bukkit.broadcastMessage("§f§m+------§6§m-----------§f§m------+");
            Bukkit.broadcastMessage("          §a§l● §ePartie terminée §a§l●");
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage(" §7Victoire de §a" + winner.getName() + "§7.");
            Bukkit.broadcastMessage(" §7Avec un total de §f" + killsWinner + " §7kills.");
            Bukkit.broadcastMessage(" ");

            Bukkit.broadcastMessage(" §aTop Kills:");

            Map<String, Integer> top10 = main.getWorldManager().getTop10();

            for (int i = 0; i < 3; i++) {
                if (top10.isEmpty()) {
                    Bukkit.broadcastMessage(" §7- §cAucun");
                    break;
                }
                if (top10.size() <= i) break;
                String playerName = (String) top10.keySet().toArray()[i];
                Bukkit.broadcastMessage(" §a#" + (i + 1) + ". §e" + playerName + " §7» §f" + top10.get(playerName) + " kills");
            }


            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§f§m+------§6§m-----------§f§m------+");

            new EndTask(main, winner).runTaskTimer(main, 5L, 5L);

        }, 10);
    }
}
