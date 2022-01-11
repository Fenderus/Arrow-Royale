package mc.fenderas.arrowroyale.others.scoreboard;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import mc.fenderas.arrowroyale.files.PlayerScoresFile;
import mc.fenderas.arrowroyale.utils.SortUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RoundScoreboard
{
    public JGlobalMethodBasedScoreboard scoreboard;
    public HashMap<String, Integer> playerScores = new HashMap<>();
    public List<Player> players = new ArrayList<>();

    public RoundScoreboard(){
        scoreboard = new JGlobalMethodBasedScoreboard();
    }

    public void init(){
        List<String> lines = new ArrayList<>();
        playerScores = SortUtils.sortByValue(playerScores, false);
        scoreboard.setTitle("  §9§lMost Kills  ");
        lines.add(" ");
        for (String name : playerScores.keySet()){
            lines.add(name + ": " + playerScores.get(name));
        }
        lines.add(" ");
        scoreboard.setLines(lines);
        scoreboard.updateScoreboard();
    }

    public void addPlayer(Player player){
        scoreboard.addPlayer(player);
        playerScores.put(player.getName(), 0);
        players.add(player);
        scoreboard.updateScoreboard();
    }

    public void removePlayer(Player player){
        players.remove(player);
        scoreboard.removePlayer(player);
    }

    public void setScore(Player player, int score){
        int newScore = playerScores.get(player.getName()) + score;
        playerScores.put(player.getName(), newScore);
        PlayerScoresFile.setKillsForPlayer(player.getName(), score);
        init();
    }

    public void start(List<Player> players){
        for (Player player : players){
            addPlayer(player);
        }
        init();
    }

    public void finish(){
        for (Player player : players){
            scoreboard.removePlayer(player);
        }
        players.clear();
        playerScores.clear();
    }

    public int getWinnerScore(){
        if(getWinnerPlayer() != null){
            return playerScores.get(getWinnerPlayer().getName());
        }else{
            return 0;
        }
    }

    public Player getWinnerPlayer(){
        if (!playerScores.isEmpty()){
            if (Bukkit.getPlayer(SortUtils.getKeyByValue(playerScores, Collections.max(playerScores.values()))) != null){
                return Bukkit.getPlayer(SortUtils.getKeyByValue(playerScores, Collections.max(playerScores.values())));
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
