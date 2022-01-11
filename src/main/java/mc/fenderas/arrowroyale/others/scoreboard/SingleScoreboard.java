package mc.fenderas.arrowroyale.others.scoreboard;

import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard;
import mc.fenderas.arrowroyale.ArrowRoyale;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SingleScoreboard
{
    public JPerPlayerMethodBasedScoreboard scoreboard;
    public List<Player> players = new ArrayList<>();

    public SingleScoreboard(){
        scoreboard = new JPerPlayerMethodBasedScoreboard();

    }

    public void init(Player player){
        scoreboard.addPlayer(player);
        scoreboard.setLines(player, "");
        List<String> lines = new ArrayList<>();
        scoreboard.setTitle(player, "  §6§lArrow Royale Stats  ");
        lines.add(" ");
        lines.add(PlaceholderAPI.setPlaceholders(player, "§cKills: §f%arrowroyale_kills%"));
        lines.add(" ");
        scoreboard.setLines(player, lines);
    }

    public void addScoreboard(Player player){
        players.add(player);
        init(player);
    }

    public void removeAllPlayers(){
        for (Player player : players){
            removeScoreboard(player);
        }
    }

    public void reAdd(){
        for (Player player : players){
            init(player);
        }
    }

    public void removeScoreboard(Player player){
        scoreboard.removePlayer(player);
    }
}
