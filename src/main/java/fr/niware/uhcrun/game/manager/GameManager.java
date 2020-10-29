package fr.niware.uhcrun.game.manager;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.EndTask;
import fr.niware.uhcrun.game.tasks.GameTask;
import fr.niware.uhcrun.game.tasks.ScatterTask;
import fr.niware.uhcrun.player.UHCPlayer;
import fr.niware.uhcrun.player.manager.PlayerManager;
import fr.niware.uhcrun.utils.structure.BlockData;
import fr.niware.uhcrun.utils.structure.Structure;
import fr.niware.uhcrun.world.WorldManager;
import io.papermc.lib.PaperLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private static GameManager instance;

    private final UHCRun main;
    private final Game game;
    private final WorldManager worldManager;
    private final PlayerManager playerManager;

    public GameManager(UHCRun main) {
        instance = this;
        this.main = main;
        this.game = main.getGame();
        this.worldManager = main.getWorldManager();
        this.playerManager = main.getPlayerManager();
    }

    public static GameManager get(){
        return instance;
    }

    public void startGame() {
        GameState.setState(GameState.MINING);
        worldManager.registerObjectives();

        game.setStartMillis(System.currentTimeMillis());
        game.setSizePlayers(game.getAlivePlayers().size());
        game.getStayLocs().clear();
        game.getBlocks().forEach(block -> block.setType(Material.AIR));
        game.getBlocks().clear();

        for (UHCPlayer uhcPlayer : playerManager.getPlayers()) {
            uhcPlayer.sendActionBar("§7» §eQue le meilleur gagne !");
            if (uhcPlayer.getPlayer() == null) continue;
            Player player = uhcPlayer.getPlayer();

            player.playSound(player.getLocation(), Sound.EXPLODE, 5F, 5F);
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
            player.setWalkSpeed(0.3f);
            player.setGameMode(GameMode.SURVIVAL);

            game.getStartPotionEffects().forEach(player::addPotionEffect);
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
            if (!GameState.isInGame()) return;
            game.setInvincibility(false);
            Bukkit.broadcastMessage("§dUHCRun §7» §aVous êtes désormais vulnérables aux §bdégâts§a.");
        }, 30 * 20);
    }

    public void startTeleport() {
        GameState.setState(GameState.TELEPORT);
        Bukkit.setWhitelist(false);

        game.getWorld().setSpawnLocation(0, game.getSpecSpawn().getBlockY(), 0);
        game.setRunnable(false);
        game.getWorld().getWorldBorder().setSize(game.getSizeMap() * 2);

        for (UHCPlayer uhcPlayer : playerManager.getPlayers()) {
            uhcPlayer.sendActionBar("§7Téléportation...");

            Player player = uhcPlayer.getPlayer();
            player.playSound(player.getLocation(), Sound.EAT, 3f, 3f);
            player.getInventory().clear();
            player.setLevel(0);
        }

        new ScatterTask(main, true).runTaskTimer(main, 0L, 10L);
    }

    public void endGame(UUID uuid) {
        Location winLocation = game.getSpawn();

        Player winner = Bukkit.getOfflinePlayer(uuid).getPlayer();
        game.setWinner(winner);
        game.setInvincibility(true);

        GameState.setState(GameState.FINISH);

        Bukkit.getScheduler().runTaskLater(main, () -> {
            main.getStructureLoader().paste(winLocation, "win", true);

            for (UHCPlayer uhcPlayer : playerManager.getPlayers()) {
                Player player = uhcPlayer.getPlayer();
                PaperLib.teleportAsync(player, winLocation);

                if (uhcPlayer.getUUID() != uuid) {
                    player.playSound(player.getLocation(), Sound.WITHER_DEATH, 5.0F, 5.0F);
                    uhcPlayer.sendTitle(10, 40, 10, "§6UHCRun", "§fVictoire de §a" + winner.getName());
                    continue;
                }

                uhcPlayer.sendTitle(10, 40, 10, "§6Vous avez gagné", "§fVictoire de §a" + winner.getName());
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

    public void launchPvP() {
        GameState.setState(GameState.PVP);

        game.setInvincibility(true);
        game.getWorld().setPVP(true);

        new ScatterTask(main,false).runTaskTimer(main, 0L, 5L);

        Bukkit.broadcastMessage("§7§m+---------------------+");
        Bukkit.broadcastMessage("              §6● §eCombats §6●");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("  §f» §aPvP activé.");
        Bukkit.broadcastMessage("  §f» §aFinal heal acivé.");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("              §6● §eBordure §6●");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" §f» §aRéduction en cours.");
        Bukkit.broadcastMessage(" §f» §aTaille finale: §b" + game.getFinalBorderSize() + " §a/ §b-" + game.getFinalBorderSize());
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§7§m+---------------------+");

        for (UHCPlayer uhcPlayer : playerManager.getPlayers()) {
            if (uhcPlayer.getPlayer() == null) continue;
            Player player = uhcPlayer.getPlayer();

            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.setHealth(20D);
            player.setFoodLevel(20);
        }

        // REDUCE BORDER
        WorldBorder worldBorder = game.getWorld().getWorldBorder();
        worldBorder.setSize(game.getSizeTpBorder() * 2);
        worldBorder.setSize(game.getFinalBorderSize() * 2, game.getBorderMoveTime() * 60);

        // REMOVE OFFLINE PLAYERS FROM GAME
        List<UUID> uuids = new ArrayList<>(game.getAlivePlayers());

        uuids.stream().filter(uuid -> Bukkit.getPlayer(uuid) == null).forEach(uuid -> game.getAlivePlayers().remove(uuid));
        Bukkit.broadcastMessage("§dUHCRun §7» §cLes joueurs déconnectés ont été éliminés.");
        playerManager.checkIsEnd();
    }
}
