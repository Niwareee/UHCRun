package fr.lifecraft.uhcrun.world;

import fr.lifecraft.uhcrun.Main;
import fr.lifecraft.uhcrun.utils.State;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Chunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldLoader implements Runnable {

    private final Main main;

    public static int loaded;
    public static int area;
    private final World world;
    private final int width;
    private final int depth;
    private int x;
    private int z;
    private long sprint;
    private final int offset;
    private boolean pause;

    private final long start;

    private final ChunkProviderServer provider;

    public final Listener listener = new Listener() {
        @EventHandler(priority = EventPriority.LOW)
        public void onChunkUnload(ChunkUnloadEvent event) {
            event.getChunk().unload(true);
        }
    };

    public WorldLoader(final World world, final int width, final int depth, final int offset) {
        this.pause = false;
        this.world = world;
        this.width = width;
        this.depth = depth;
        this.x = -this.width;
        this.z = -this.depth;
        loaded = 0;
        this.sprint = System.currentTimeMillis();
        area = (this.width >> 4) * 2 * ((this.depth >> 4) * 2);
        this.offset = offset;

        this.provider = ((CraftWorld)world).getHandle().chunkProviderServer;

        start = System.currentTimeMillis();

        this.main = Main.getInstance();

        main.getLogger().info("-----------------------------------------------");
        main.getLogger().info(" Preloading map " + world.getName() + " within radius " + width + " block");
        main.getLogger().info("-----------------------------------------------");
    }

    @Override
    public void run() {
        Thread thread;
        (thread = new Thread()).start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (thread) {
            this.sprint = System.currentTimeMillis();
            this.setPause(false);
            while (this.z < this.depth) {
                this.x = -this.width;
                while (this.x < this.width) {
                    if (System.currentTimeMillis() - this.sprint > 8000L) {
                        this.setPause(true);
                    }
                    if (this.isPause()) {
                        return;
                    }
                    final Chunk chunk = this.provider.getChunkAt(this.offset + this.x >> 4, this.z >> 4);
                    //chunk.load(true);
                    this.provider.loadChunk(chunk.locX, chunk.locZ);
                    ++loaded;
                    if (loaded % 100 == 0) {
                        int percent = (int) ((((float) loaded) / ((float) area)) * 100.0);
                        MinecraftServer.getServer().setMotd("§7Régénération: §e" + percent + "%");
                        System.out.print(loaded + "/" + area + " chunks (" + percent + "%)");
                    }
                    if (loaded % 5000 == 0) {
                        try {
                            this.world.save();
                            Chunk[] chunks;
                            for (int j = (chunks = (Chunk[]) world.getLoadedChunks()).length, i = 0; i < j; ++i) {
                                final Chunk c = chunks[i];
                                provider.loadChunk(c.locX, c.locZ);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    this.x += 16;
                }
                this.z += 16;
            }
        }
        if (loaded >= area) {

            setPause(true);
            long stop = System.currentTimeMillis();
            main.log("§aFinish preload " + world.getName() + " in " + (stop - start) / 1000 + " seconds");
            Bukkit.getScheduler().cancelAllTasks();

            if (world.getEnvironment() == World.Environment.NETHER) {
                main.log("§aSetup finish. Ready to use.");

                State.setState(State.WAITING);
                MinecraftServer.getServer().setMotd("§bEn attente");
                return;
            }

            main.getStructureLoader().load("spawn");
            main.getStructureLoader().paste(main.getGame().getSpawn(), "spawn", true);
            main.getStructureLoader().load("win");

            HandlerList.unregisterAll(listener);

            int preLoadNether = main.getGame().getPreLoadNether();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new WorldLoader(Bukkit.getWorld("world_nether"), preLoadNether, preLoadNether, 0), 20, 100);
        }
    }

    public boolean isPause() {
        return this.pause;
    }

    public void setPause(final boolean pause) {
        this.pause = pause;
    }

    public int getLoad() {
        return loaded;
    }

    public int getArea() {
        return area;
    }
}
