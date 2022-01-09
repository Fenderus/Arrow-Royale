package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.others.BossBarManager;
import mc.fenderas.arrowroyale.others.ChatPlayers;
import mc.fenderas.arrowroyale.tasks.GameTimer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;

public class GameManager
{
    public GameStates state = GameStates.START;

    private final PlayerManager playerManager;
    private final ChestSpawners chestSpawners;

    private GameTimer startTimer;
    private GameTimer activeTimer;
    private GameTimer endTimer;

    private ScoreboardInit scoreboardInit;
    private BossBarManager bossBar;

    private boolean active = false;

    public GameManager(){
        playerManager = new PlayerManager(this);
        chestSpawners = new ChestSpawners();
        scoreboardInit = new ScoreboardInit(this);
        bossBar = new BossBarManager();
        bossBar.createBar();
    }

    public void setGameState(GameStates newState){
        if (state == GameStates.ACTIVE && newState == GameStates.START) return;
        cancelAllTimers();
        state = newState;
        switch (newState){
            case ACTIVE:
                activeTimer = new GameTimer(this, 60, false, GameStates.END, "", true);
                activeTimer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
                chestSpawners.SpawnChests(ArrowRoyale.getMinigameWorld());
                playerManager.setPlayers();
                playerManager.giveKits();
                playerManager.randomSpawns();
                playerManager.showScoreBoards(GameStates.ACTIVE);
                playerManager.displayBossBars(bossBar);
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
                endGame();
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.LIGHT_PURPLE + "Game Is Finished");
                announceWinner();
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

    public void endGame(){
        chestSpawners.removeChests();
        playerManager.removeKits();
        playerManager.clearInventories();
        playerManager.showScoreBoards(GameStates.LOBBY);
        playerManager.respawnPlayers();
        bossBar.remove();
        active = false;
        playerManager.resetPlayerList();
    }

    public void announceWinner(){
        if(scoreboardInit.getWinner() != null){
            ChatPlayers.chatPlayersInArrowRoyale(ChatColor.YELLOW + scoreboardInit.getWinner().getName() + ChatColor.WHITE + " is the Champion of this round");
        }else{
            ChatPlayers.chatPlayersInArrowRoyale("The Champion already Left the Game!");
        }
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }
    public ScoreboardInit getScoreboardInit(){
        return scoreboardInit;
    }
    public BossBarManager getBossBar() {
        return bossBar;
    }

    public void cancelAllTimers(){
        if (startTimer != null) startTimer.cancel();
        if (activeTimer != null) activeTimer.cancel();
        if (endTimer != null) endTimer.cancel();
    }
}
