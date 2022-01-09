package mc.fenderas.arrowroyale.others;

import mc.fenderas.arrowroyale.ArrowRoyale;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChatPlayers
{
    public static void chatPlayersInArrowRoyale(String message){
        chatPlayersThroughWorld(ArrowRoyale.getMinigameWorld(), message);
    }

    public static void chatPlayersThroughWorld(World world, String message)
    {
        for (Player player : world.getPlayers()){
            player.sendMessage(message);
        }
    }

}
