package mc.fenderas.arrowroyale.utils;

import mc.fenderas.arrowroyale.ArrowRoyale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class LocationUtil
{

    public static List<Material> badBlocks = new ArrayList<>();

    static {
        for (String mat : ArrowRoyale.getUnspawnableBlocks()){
            badBlocks.add(Material.getMaterial(mat.toUpperCase(Locale.ROOT)));
        }

    }

    public static Location generateRandomLocation(World world, boolean checkArea){
        int x = RandomUtil.randomIntProper(ArrowRoyale.getCoordinatesSection(world.getName()).getInt("xMin"), ArrowRoyale.getCoordinatesSection(world.getName()).getInt("xMax"));
        int y = 150;
        int z = RandomUtil.randomIntProper(ArrowRoyale.getCoordinatesSection(world.getName()).getInt("zMin"), ArrowRoyale.getCoordinatesSection(world.getName()).getInt("zMax"));

        Location randomLocation = new Location(world, x, y ,z);
        y = randomLocation.getWorld().getHighestBlockYAt(randomLocation);
        randomLocation.setY(y);

        if (checkArea){
            while (!isLocationSafe(randomLocation)){
                randomLocation = generateRandomLocation(world, checkArea);
            }
            return randomLocation;
        }else{
            return randomLocation;
        }
    }

    public static boolean isLocationSafe(Location location){
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x,y + 1,z);
        Block below = location.getWorld().getBlockAt(x,y,z);
        Block above = location.getWorld().getBlockAt(x,y + 2,z);

        return !(badBlocks.contains(below.getType()) || (block.getType().isSolid()) || (above.getType().isSolid()));
    }

    public static void spawnPlayerToNewWorld(Player player, World world){
        Location spawn = world.getSpawnLocation();
        Location anotherWorld = new Location(world, spawn.getX(), spawn.getY(), spawn.getBlockZ());
        player.teleport(anotherWorld);
    }
}
