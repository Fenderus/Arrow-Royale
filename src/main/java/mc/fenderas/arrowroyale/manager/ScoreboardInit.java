package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.files.PlayerScoresFile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardInit
{
    public Scoreboard scoreboard;
    public Objective objective;
    private GameManager manager;

    private final HashMap<String, Integer> currentPlayerList = new HashMap<>();

    public ScoreboardInit(GameManager manager){
        this.manager = manager;
    }

    public void init(Player player, GameStates states, boolean registerNew){
        if (registerNew){
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            scoreboard = manager.getNewScoreboard();

            resetScores();

            switch (states) {
                case ACTIVE -> {
                    objective = scoreboard.registerNewObjective("kills", "dummy", "  §9§lMost Kills  ");
                    Score s1 = objective.getScore("  ");
                    s1.setScore(999);

                    Score s2 = objective.getScore(" ");
                    s2.setScore(0);
                }
                case LOBBY -> {
                    objective = scoreboard.registerNewObjective("stats", "dummy", "  §6§lArrow Royale Stats  ");
                    if(ArrowRoyale.isUsingPlaceholderAPI()){
                        setScore(PlaceholderAPI.setPlaceholders(player, "§cKills: §f%arrowroyale_kills%"), -8);

                        Score s1 = objective.getScore("  ");
                        s1.setScore(9);

                        Score s2 = objective.getScore(" ");
                        s2.setScore(-9);
                    }else{
                        setScore(PlaceholderAPI.setPlaceholders(player, "You'll need to set true"), -5);
                        setScore(PlaceholderAPI.setPlaceholders(player, "the usePlaceholderAPI"), -6);
                        setScore(PlaceholderAPI.setPlaceholders(player, "in the config.yml"), -7);
                        setScore(PlaceholderAPI.setPlaceholders(player, "to use the full scoreboard"), -8);
                    }
                }
            }

            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    public void addPlayer(Player player, GameStates states, boolean registerNew){
        init(player, states, registerNew);
        player.setScoreboard(scoreboard);
    }

    public void setScore(String entry, int scoreNumber){
        Score score = objective.getScore(entry);
        score.setScore(scoreNumber);
    }

    public void setPlayerCurrentScore(String name, int playerScore){
        PlayerScoresFile.setKillsForPlayer(name, playerScore);
        currentPlayerList.put(name, getScoreOfPlayerInScoreBoard(name) + playerScore);
        showScoreBoard();
    }

    public void showScoreBoard(){
        for (String name : currentPlayerList.keySet()) {
            setScore(name, currentPlayerList.get(name));
        }
        for (Player player : manager.getPlayerManager().getCurrentPlayers()){
            addPlayer(player, GameStates.ACTIVE, false);
        }
    }

    public void resetScores(){
        for (String name : currentPlayerList.keySet()){
            scoreboard.resetScores(name);
        }
        currentPlayerList.clear();
    }

    private int getScoreOfPlayerInScoreBoard(String name){
        if (currentPlayerList.containsKey(name)){
            return currentPlayerList.get(name);
        }else{
            currentPlayerList.put(name, 0);
            return 0;
        }
    }

    public void disableScoreBoard(Player player){
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public Player getWinner(){
        if (!currentPlayerList.isEmpty()){
            if (Bukkit.getPlayer(getKeyByValue(currentPlayerList, Collections.max(currentPlayerList.values()))) != null){
                return Bukkit.getPlayer(getKeyByValue(currentPlayerList, Collections.max(currentPlayerList.values())));
            }else {
                return null;
            }
        }else {
            return null;
        }
    }

    public int getWinnerPoints(){
        if(getWinner() != null){
            return currentPlayerList.get(getWinner().getName());
        }else{
            return 0;
        }
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
