package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.others.scoreboard.RoundScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager
{
    public List<LobbyManager> lobbies = new ArrayList<>();

    public GameManager(){
        for (String worldNames : ArrowRoyale.getWorldNames()) {
            LobbyManager newManager = new LobbyManager(Bukkit.getWorld(worldNames));
            lobbies.add(newManager);
            Bukkit.getConsoleSender().sendMessage("[ArrowRoyale] World" + Bukkit.getWorld(worldNames).getName() + " loaded as Lobby");
        }
    }

    public void setGameState(World world, GameStates newState){
        if(getLobbyManager(world) != null){
            getLobbyManager(world).setGameState(newState);
        }
    }

    public void startGame(World world){
        if(getLobbyManager(world) != null){
            getLobbyManager(world).startGame();
        }
    }

    public void endGame(World world){
        if(getLobbyManager(world) != null){
            getLobbyManager(world).endGame();
        }
    }

    public void addScore(Player player, int score){
        for (LobbyManager manager : lobbies) {
            RoundScoreboard roundScoreboard = manager.getRoundScoreboard();
            if(roundScoreboard.playerScores.containsKey(player.getName())){
                roundScoreboard.setScore(player, score);
            }
        }
    }

    public LobbyManager getLobbyManager(Player player){
        for (LobbyManager manager : lobbies) {
            if(player.getWorld() == manager.getWorld()){
                return manager;
            }
        }
        return null;
    }

    public LobbyManager getLobbyManager(World world){
        for (LobbyManager manager : lobbies) {
            if(world == manager.getWorld()){
                return manager;
            }
        }
        return null;
    }

    public LobbyManager getLobbyManager(String worldName){
        for (LobbyManager manager : lobbies) {
            if(Bukkit.getWorld(worldName) == manager.getWorld()){
                return manager;
            }
        }
        return null;
    }
}
