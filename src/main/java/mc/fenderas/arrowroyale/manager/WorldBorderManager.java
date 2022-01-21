package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.utils.LocationUtil;
import mc.fenderas.arrowroyale.utils.RandomUtil;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldBorderManager {

    private WorldBorder border;
    private World world;
    private int maxTime = 60;
    private int maxSize = 0;

    public WorldBorderManager(World world, int maxTime){
        this.world = world;
        border = world.getWorldBorder();
        border.setCenter(world.getSpawnLocation());
        this.maxTime = maxTime;
        this.maxSize = ArrowRoyale.getWorldBorderSection(world.getName()).getInt("max_border_size");
        border.setSize(maxSize);
    }

    public void shrinkWorldBorder(){
        border.setCenter(LocationUtil.generateRandomLocation(world, true));
        int rand = RandomUtil.randomIntProper(maxTime,maxTime * 2);
        border.setSize(0, rand);
        //shrinkBorder.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
    }

    public void resetBorderSize(){
        border.setSize(maxSize);
        border.setCenter(world.getSpawnLocation());
    }

    public WorldBorder getBorder() {
        return border;
    }
}
