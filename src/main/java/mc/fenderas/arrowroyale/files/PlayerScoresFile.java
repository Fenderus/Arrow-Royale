package mc.fenderas.arrowroyale.files;

import mc.fenderas.arrowroyale.ArrowRoyale;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PlayerScoresFile
{
    private static Plugin plugin;
    public static File file;
    private static FileConfiguration customFile;

    public static void setup()
    {
        plugin = ArrowRoyale.getPlugin();

        file = new File(plugin.getDataFolder(), "playerScores.yml");

        if(!file.exists())
        {
            try {
                file.createNewFile();
            }catch (IOException e){

            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get()
    {
        return customFile;
    }

    public static void save()
    {
        try
        {
            customFile.save(file);
        }catch (IOException e)
        {
            System.out.print("Couldn't save file!");
        }
    }

    public static void load()
    {
        try
        {
            try {
                try {
                    customFile.load(file);
                }catch (InvalidConfigurationException i){
                    System.out.print("Couldn't load file!");
                }
            }catch (FileNotFoundException f)
            {
                System.out.print("Couldn't load file!");
            }
        }catch (IOException e)
        {
            System.out.print("Couldn't load file!");
        }
    }

    public static void reload()
    {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveDefaultConfig() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), "playerScores.yml");
        }
        if (!file.exists()) {
            plugin.saveResource("playerScores.yml", false);
        }
        reload();
    }

    public static boolean exists()
    {
        if(file.exists()){
            return true;
        }else
        {
            return false;
        }
    }

    public static ConfigurationSection getSection(String name){
        if(customFile.isConfigurationSection(name)){
            return customFile.getConfigurationSection(name);
        }else{
            return customFile.createSection(name);
        }
    }

    public static ConfigurationSection getPlayerSection(String name){
        if(getPlayersSection().isConfigurationSection(name)){
            return getPlayersSection().getConfigurationSection(name);
        }else{
            return getPlayersSection().createSection(name);
        }
    }

    public static void setKillsForPlayer(String name, int newScore){
        if (getPlayerSection(name).isInt("kills")){
            int score = getPlayerSection(name).getInt("kills");
            getPlayerSection(name).set("kills", score + newScore);
        }else{
            getPlayerSection(name).addDefault("kills", newScore);
        }

        save();
        saveDefaultConfig();
    }

    public static int getKillsFromPlayer(String name){
        if (getPlayerSection(name).isInt("kills")){
            return getPlayerSection(name).getInt("kills");
        }else{
            getPlayerSection(name).addDefault("kills", 0);
            return 0;
        }
    }

    public static ConfigurationSection getPlayersSection(){
        return getSection("Players");
    }
}
