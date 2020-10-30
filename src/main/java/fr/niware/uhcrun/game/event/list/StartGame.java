package fr.niware.uhcrun.game.event.list;

import fr.niware.uhcrun.UHCRun;
import fr.niware.uhcrun.game.Game;
import fr.niware.uhcrun.game.event.UHCEvent;
import fr.niware.uhcrun.game.state.GameState;
import fr.niware.uhcrun.game.tasks.GameTask;
import fr.niware.uhcrun.player.UHCPlayer;
import fr.niware.uhcrun.utils.structure.BlockData;
import fr.niware.uhcrun.utils.structure.Structure;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StartGame extends UHCEvent {

    private final UHCRun main;
    private final Game game;

    public StartGame(UHCRun main){
        this.main = main;
        this.game = main.getGame();
    }

    @Override
    public void activate() {
        GameState.setState(GameState.MINING);
        main.getWorldManager().registerObjectives();

        game.setStartMillis(System.currentTimeMillis());
        game.setSizePlayers(game.getAlivePlayers().size());
        game.getStayLocs().clear();
        game.getBlocks().forEach(block -> block.setType(Material.AIR));
        game.getBlocks().clear();

        for (UHCPlayer uhcPlayer : main.getPlayerManager().getPlayers()) {
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
}
