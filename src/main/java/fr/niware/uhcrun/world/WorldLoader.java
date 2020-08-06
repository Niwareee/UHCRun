package fr.niware.uhcrun.world;

import fr.niware.uhcrun.Main;
import fr.niware.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.logging.Logger;

public class WorldLoader {

    private final Main main;
    private final Logger logger;

    private Environment environment;
    private long start;
    private double chunksLoaded = 0.0D;
    private double totalChunksToLoad;

    private ChunkProviderServer chunkProviderServer;

    public WorldLoader(Main main) {
        this.main = main;
        this.logger = Bukkit.getLogger();
    }

    public double getLoadingState() {
        double percentage = 100.0D * this.chunksLoaded / this.totalChunksToLoad;
        return Math.floor(10.0D * percentage) / 10.0D;
    }

    public void generateChunks(World world, int size) {
        this.start = System.currentTimeMillis();
        this.environment = world.getEnvironment();
        this.chunkProviderServer = ((CraftWorld)world).getHandle().chunkProviderServer;

        main.getLogger().info("-----------------------------------------------");
        main.getLogger().info(" Preloading map " + world.getName() + " => radius " + size + " block");
        main.getLogger().info("-----------------------------------------------");

        this.chunksLoaded = 0.0D;
        final int maxChunk = (size - size % 16) / 16;
        this.totalChunksToLoad = (2.0D * (double) maxChunk + 1.0D) * (2.0D * (double) maxChunk + 1.0D);

        System.out.print("Chunk to load: " + (int) totalChunksToLoad);
        Bukkit.getScheduler().runTaskAsynchronously(this.main, new Runnable() {
            public void run() {
                class RunnableWithParameter implements Runnable {
                    private int i;
                    private int j;
                    private int nextRest;

                    RunnableWithParameter(int i, int j, int nextRest) {
                        this.i = i;
                        this.j = j;
                        this.nextRest = nextRest;
                    }

                    public void run() {
                        int loaded;
                        for (loaded = 0; this.i <= maxChunk && this.j <= maxChunk && loaded < 10; ++this.j) {
                            final Chunk chunk = chunkProviderServer.getChunkAt(this.i, this.j);
                            chunkProviderServer.loadChunk(chunk.locX, chunk.locZ);

                            ++loaded;
                        }

                        chunksLoaded = chunksLoaded + (double) loaded;
                        if (this.i <= maxChunk) {
                            if (this.j > maxChunk) {
                                this.j = -maxChunk;
                                ++this.i;
                            }

                            int delayTask = 0;
                            --this.nextRest;
                            if (this.nextRest == 0) {
                                delayTask = 20;
                                this.nextRest = 20;
                                MinecraftServer.getServer().setMotd("§fRégénération en cours: §6" + getLoadingState() + "%");
                                logger.info("Loading chunk... " + getLoadingState() + "%");
                            }

                            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new RunnableWithParameter(this.i, this.j, this.nextRest), delayTask);
                        } else {
                            chunksLoaded = totalChunksToLoad;

                            main.log("§aFinish preload " + world.getName() + " in " + (System.currentTimeMillis() - start) / 1000 + " seconds");
                            main.log(world.getLoadedChunks().length + " chunks have been loaded");
                            Bukkit.getScheduler().cancelAllTasks();

                            if (environment == Environment.NETHER) {

                                main.getStructureLoader().load("spawn");
                                main.getStructureLoader().paste(main.getGame().getSpawn(), "spawn", true);
                                main.getStructureLoader().load("win");

                                State.setState(State.WAITING);
                                MinecraftServer.getServer().setMotd("§bEn attente");

                                main.log("§aWorld ready. Ready to play.");
                                return;
                            }

                            int preLoadNether = main.getGame().getPreLoadNether();
                            generateChunks(Bukkit.getWorld("world_nether"), preLoadNether);
                        }
                    }
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(main, new RunnableWithParameter(-maxChunk, -maxChunk, 20), 0L);
            }
        });
    }
}