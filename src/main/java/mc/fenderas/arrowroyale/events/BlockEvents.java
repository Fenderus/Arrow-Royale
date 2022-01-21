package mc.fenderas.arrowroyale.events;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.manager.LobbyManager;
import mc.fenderas.arrowroyale.others.BrokenBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockEvents implements Listener
{

    public List<Block> placedBlocks = new ArrayList<>();
    public List<BrokenBlock> brokeBlocks = new ArrayList<>();
    private GameManager manager;

    public BlockEvents(GameManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        if (lobbyManager.state == GameStates.ACTIVE){
            if (player.getWorld() == lobbyManager.getWorld()){
                placedBlocks.add(event.getBlockPlaced());
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = manager.getLobbyManager(player);
        Block block = event.getBlock();
        if (lobbyManager.state == GameStates.ACTIVE){
            if (player.getWorld() == lobbyManager.getWorld()){
                if (!ArrowRoyale.getBreakableBlocks().contains(block.getBlockData().getMaterial().toString())){
                    event.setCancelled(true);
                }else {
                    if(!placedBlocks.contains(block)){
                        if (block.getType() != Material.AIR){
                            brokeBlocks.add(new BrokenBlock(block, block.getType()));
                        }
                    }
                }
            }
        }
    }

    public void clearBlocks(){
        placedBlocks.forEach(this::clearBlock);
        placedBlocks.clear();
        brokeBlocks.forEach(this::restoreBlock);
        brokeBlocks.clear();
    }

    public void clearBlock(Block block){block.setType(Material.AIR);}
    public void restoreBlock(BrokenBlock block){block.restoreBlock();}
}
