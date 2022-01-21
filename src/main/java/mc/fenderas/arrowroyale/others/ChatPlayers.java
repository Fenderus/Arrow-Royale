package mc.fenderas.arrowroyale.others;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.manager.LobbyManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChatPlayers
{
    public static void chatPlayersInArrowRoyale(String message, LobbyManager manager){
        chatPlayersThroughWorld(manager.getWorld(), message);
    }

    public static void chatPlayersThroughWorld(World world, String message)
    {
        for (Player player : world.getPlayers()){
            player.sendMessage(message);
        }
    }

}
