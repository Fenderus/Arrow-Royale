package mc.fenderas.arrowroyale.others;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager
{
    public BossBar bar;

    public BossBarManager(){

    }

    public void addPlayer(Player player){
        bar.addPlayer(player);
    }

    public void createBar(){
        bar = Bukkit.createBossBar("§9§lTime", BarColor.BLUE, BarStyle.SOLID);
        bar.setVisible(true);
    }

    public void remove(){
        bar.removeAll();
    }
}
