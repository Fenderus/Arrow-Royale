package mc.fenderas.arrowroyale.others;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BrokenBlock {
    private Block block;
    private Material prevMaterial;

    public BrokenBlock(Block block, Material mat){
        this.block = block;
        prevMaterial = mat;
    }

    public void restoreBlock(){
        block.setType(prevMaterial);
    }
}
