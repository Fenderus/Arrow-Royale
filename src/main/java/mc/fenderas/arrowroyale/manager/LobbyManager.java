package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.others.BossBarManager;
import mc.fenderas.arrowroyale.others.ChatPlayers;
import mc.fenderas.arrowroyale.others.scoreboard.RoundScoreboard;
import mc.fenderas.arrowroyale.others.scoreboard.SingleScoreboard;
import mc.fenderas.arrowroyale.tasks.GameTimer;
import org.bukkit.ChatColor;
import org.bukkit.World;

public class LobbyManager {
    public GameStates state = GameStates.START;
    private World world;

    private final PlayerManager playerManager;
    private final ChestSpawners chestSpawners;

    private GameTimer startTimer;
    private GameTimer activeTimer;
    private GameTimer endTimer;

    private SingleScoreboard playerScoreboard;
    private RoundScoreboard roundScoreboard;
    private BossBarManager bossBar;
    private WorldBorderManager worldBorderManager;

    private boolean active = false;
    private int roundTime;

    public LobbyManager(World world){
        this.world = world;
        roundTime = ArrowRoyale.getRoundSection().getInt("secondsPerRound");
        playerManager = new PlayerManager(this);
        chestSpawners = new ChestSpawners();
        playerScoreboard = new SingleScoreboard();
        roundScoreboard = new RoundScoreboard();
        bossBar = new BossBarManager();
        worldBorderManager = new WorldBorderManager(world, roundTime);
        bossBar.createBar();
    }

    public void setGameState(GameStates newState){
        if (state == GameStates.ACTIVE && newState == GameStates.START) return;
        cancelAllTimers();
        state = newState;
        switch (newState){
            case ACTIVE:
                startGame();
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.GREEN + "Active", this);
                active = true;
                break;
            case START:
                if(active){
                    endGame();
                }
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.YELLOW + "Starting", this);
                startTimer = new GameTimer(this, 10, true, GameStates.ACTIVE, "until Game starts");
                startTimer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
                break;
            case END:
                playerManager.rewardWinner();
                announceWinner();
                endGame();
                worldBorderManager.resetBorderSize();
                ChatPlayers.chatPlayersInArrowRoyale(ChatColor.LIGHT_PURPLE + "Game Is Finished", this);
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
        activeTimer = new GameTimer(this, roundTime, false, GameStates.END, "", true);
        activeTimer.runTaskTimer(ArrowRoyale.getPlugin(), 0, 20);
        chestSpawners.SpawnChests(world);
        playerManager.setPlayers(world.getPlayers());
        playerManager.giveKits();
        playerManager.randomSpawns();
        playerManager.displayBossBars(bossBar);
        playerScoreboard.removeAllPlayers();
        roundScoreboard.start(playerManager.getCurrentPlayers());
        worldBorderManager.shrinkWorldBorder();
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
        playerManager.addLobbyItemToPlayers();
    }

    public void announceWinner(){
        if(getRoundScoreboard().getWinnerPlayer() != null){
            ChatPlayers.chatPlayersInArrowRoyale(ChatColor.YELLOW + getRoundScoreboard().getWinnerPlayer().getName() + ChatColor.WHITE + " is the Champion of this round", this);
        }else{
            ChatPlayers.chatPlayersInArrowRoyale("The Champion already Left the Game!", this);
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
    public ChestSpawners getChestSpawners() {return chestSpawners;}
    public World getWorld() {return world;}

    public void cancelAllTimers(){
        if (startTimer != null) startTimer.cancel();
        if (activeTimer != null) activeTimer.cancel();
        if (endTimer != null) endTimer.cancel();
    }
}
