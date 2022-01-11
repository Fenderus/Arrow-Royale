package mc.fenderas.arrowroyale.tasks;

import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardChangeTimer extends BukkitRunnable
{
    public ScoreboardChangeTimer(Player player, GameManager manager, GameStates state, int timeLeft){
        this.player = player;
        this.manager = manager;
        this.state = state;
        this.timeLeft = timeLeft;
    }

    int timeLeft = 1;

    GameStates state;
    GameManager manager;
    Player player;

    @Override
    public void run() {
        timeLeft--;
        if(timeLeft <= 0){
            cancel();
            if (state == GameStates.LOBBY){
                manager.getRoundScoreboard().removePlayer(player);
                manager.getPlayerScoreboard().addScoreboard(player);
            }
            return;
        }
    }
}
