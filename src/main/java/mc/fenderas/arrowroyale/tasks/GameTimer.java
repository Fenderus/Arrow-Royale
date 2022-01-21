package mc.fenderas.arrowroyale.tasks;

import mc.fenderas.arrowroyale.manager.LobbyManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.others.BossBarManager;
import mc.fenderas.arrowroyale.others.ChatPlayers;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable {

    public LobbyManager manager;

    public GameTimer(LobbyManager manager, int maxTime, boolean tellTime, GameStates state, String tell){
        this.manager = manager;
        this.timeLeft = maxTime;
        this.maxTime = maxTime;
        this.tellTime = tellTime;
        this.state = state;
        this.tell = tell;
    }

    public GameTimer(LobbyManager manager, int maxTime, boolean tellTime, GameStates state, String tell, boolean bar){
        this.manager = manager;
        this.timeLeft = maxTime;
        this.maxTime = maxTime;
        this.tellTime = tellTime;
        this.state = state;
        this.tell = tell;
        this.bar = bar;
    }

    public boolean tellTime;
    public String tell;
    public GameStates state;
    public int timeLeft = 0;
    public int maxTime = 0;
    public boolean bar = false;

    public double progress = 1.0;

    @Override
    public void run() {
        if(tellTime){
            ChatPlayers.chatPlayersInArrowRoyale(timeLeft + " " + tell, manager);
        }

        timeLeft--;

        if(bar){
            BossBarManager bossBar = manager.getBossBar();
            bossBar.bar.setProgress(progress);
            double divided = 1.0 / maxTime;
            progress -= divided;
        }

        if(timeLeft <= 0){
            cancel();
            manager.setGameState(state);
            progress = 1.0;
            return;
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        bar = false;
    }
}

