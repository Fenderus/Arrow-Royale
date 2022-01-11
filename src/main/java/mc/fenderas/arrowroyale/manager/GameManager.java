package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.others.BossBarManager;
import mc.fenderas.arrowroyale.others.ChatPlayers;
import mc.fenderas.arrowroyale.others.scoreboard.RoundScoreboard;
import mc.fenderas.arrowroyale.others.scoreboard.SingleScoreboard;
import mc.fenderas.arrowroyale.tasks.GameTimer;
import org.bukkit.ChatColor;

public class GameManager
{
    public GameStates state = GameStates.START;

    private final PlayerManager playerManager;
    private final ChestSpawners chestSpawners;

    private GameTimer startTimer;
    private GameTimer activeTimer;
    private GameTimer endTimer;

    private SingleScoreboard playerScoreboard;
    private RoundScoreboard roundScoreboard;
    private BossBarManager bossBar;

    private boolean active = false;

    public GameManager(){
        playerManager = new PlayerManager(this);
        chestSpawners = new ChestSpawners();
        playerScoreboard = new SingleScoreboard();
        roundScoreboard = new RoundScoreboard();
        bossBar = new BossBarManager();
        bossBar.createBar();
    }

    public void setGameState(GameStates newState){
        if (state == GameStates.ACTIVE && newState == GameStates.START) return;
        cancelAllTimers();
        state = newState;
        switch (newState){
            case ACTIVE:
                startGame();
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.GREEN + "Active");
                active = true;
                break;
            case START:
                if(active){
                    endGame();
                }
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.YELLOW + "Starting");
                startTimer = new GameTimer(this, 10, true, GameStates.ACTIVE, "until Game starts");
                startTimer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
                break;
            case END:
                playerManager.rewardWinner();
                announceWinner();
                endGame();
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.LIGHT_PURPLE + "Game Is Finished");
                endTimer = new GameTimer(this, 10, true, GameStates.LOBBY, "to be Back");
                endTimer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
                ArrowRoyale.getBlockEvents().clearBlocks();
                break;
            case LOBBY:
                if(active){
                    endGame();
                }
                break;
        }
    }

    public void startGame(){
        activeTimer = new GameTimer(this, ArrowRoyale.getRoundSection().getInt("secondsPerRound"), false, GameStates.END, "", true);
        activeTimer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
        chestSpawners.SpawnChests(ArrowRoyale.getMinigameWorld());
        playerManager.setPlayers();
        playerManager.giveKits();
        playerManager.randomSpawns();
        playerManager.displayBossBars(bossBar);
        playerScoreboard.removeAllPlayers();
        roundScoreboard.start(playerManager.getCurrentPlayers());
    }

    public void endGame(){
        chestSpawners.removeChests();
        playerManager.removeKits();
        playerManager.clearInventories();
        playerManager.respawnPlayers();
        roundScoreboard.finish();
        playerScoreboard.reAdd();
        bossBar.remove();
        active = false;
        playerManager.resetPlayerList();
    }

    public void announceWinner(){
        if(getRoundScoreboard().getWinnerPlayer() != null){
            ChatPlayers.chatPlayersInArrowRoyale(ChatColor.YELLOW + getRoundScoreboard().getWinnerPlayer().getName() + ChatColor.WHITE + " is the Champion of this round");
        }else{
            ChatPlayers.chatPlayersInArrowRoyale("The Champion already Left the Game!");
        }
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }
    public BossBarManager getBossBar() {
        return bossBar;
    }
    public SingleScoreboard getPlayerScoreboard() {return playerScoreboard;}
    public RoundScoreboard getRoundScoreboard() {return roundScoreboard;}

    public void cancelAllTimers(){
        if (startTimer != null) startTimer.cancel();
        if (activeTimer != null) activeTimer.cancel();
        if (endTimer != null) endTimer.cancel();
    }
}
