package mc.fenderas.arrowroyale.manager;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.events.PlayerEvents;
import mc.fenderas.arrowroyale.files.PlayerScoresFile;
import mc.fenderas.arrowroyale.inventories.LobbySelection;
import mc.fenderas.arrowroyale.others.BossBarManager;
import mc.fenderas.arrowroyale.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager
{

    private List<Player> currentPlayers = new ArrayList<>();
    private LobbyManager manager;

    public PlayerManager(LobbyManager manager){
        this.manager = manager;
    }

    public void setPlayers(List<Player> players){
        currentPlayers = players;
    }

    public void giveKits(){currentPlayers.forEach(this::giveKit);}

    public void removeKits(){
        currentPlayers.forEach(this::removeKit);
    }

    public void randomSpawns(){currentPlayers.forEach(this::randomSpawn);}

    public void clearInventories(){currentPlayers.forEach(this::clearInventory);}

    public void displayBossBars(BossBarManager bossBarManager){
        for (Player player : currentPlayers){
            displayBossBar(player, bossBarManager);
        }
    }

    public void disableScoreBoards(){currentPlayers.forEach(this::disableScoreBoard);}

    public void respawnPlayers(){currentPlayers.forEach(this::respawnPlayer);}

    public void addLobbyItemToPlayers(){manager.getWorld().getPlayers().forEach(this::addLobbyItemToPlayer);}

    public void giveKit(Player player){
        clearInventory(player);
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        player.getInventory().addItem(bow);
        player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
        player.getInventory().addItem(new ItemStack(Material.OAK_PLANKS, 64));
    }

    public void removeKit(Player player){
        player.getInventory().remove(Material.BOW);
        player.getInventory().remove(Material.ARROW);
        player.getInventory().remove(Material.OAK_PLANKS);
    }

    public void randomSpawn(Player player){
        Location location = LocationUtil.generateRandomLocation(player.getWorld(), true);
        double lastY = location.getY();
        location.setY(lastY + 1);
        //player.sendMessage("You have been teleported to " + location.getX() + ", " + location.getY() + ", "+ location.getZ());
        player.teleport(location);
    }

    public void clearInventory(Player player){
        player.getInventory().clear();
    }

    public void disableScoreBoard(Player player){
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void displayBossBar(Player player, BossBarManager manager){
        manager.addPlayer(player);
    }

    public void respawnPlayer(Player player){
        if (manager.getWorld().getBlockAt(manager.getWorld().getSpawnLocation()).getType() == Material.AIR){
            player.teleport(manager.getWorld().getSpawnLocation());
        }else{
            Location location = manager.getWorld().getSpawnLocation();
            double prevY = location.getY();
            location.setY(prevY + 1);
            player.teleport(location);
        }
    }

    public void addLobbyItemToPlayer(Player player){
        player.getInventory().setItem(ArrowRoyale.getLobbyItemSection().getInt("slot"), PlayerEvents.selectionOpener);
    }

    //Methods w.o. Arguments that don't get access to all Players

    public void rewardWinner(){
        if (ArrowRoyale.isUsingVault()){
            Player winner = manager.getRoundScoreboard().getWinnerPlayer();
            if (winner != null){
                ArrowRoyale.getEconomy().depositPlayer(winner, manager.getRoundScoreboard().getWinnerScore());
            }
        }
    }

    public void removeCurrentPlayer(Player player){
        currentPlayers.remove(player);
    }

    public void resetPlayerList(){
        currentPlayers.clear();
    }

    //Get Methods

    public List<Player> getCurrentPlayers() {
        return currentPlayers;
    }
}
