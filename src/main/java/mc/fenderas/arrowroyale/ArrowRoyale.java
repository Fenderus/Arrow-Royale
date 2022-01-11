package mc.fenderas.arrowroyale;

import mc.fenderas.arrowroyale.commands.ArrowRoyaleCommands;
import mc.fenderas.arrowroyale.events.BlockEvents;
import mc.fenderas.arrowroyale.events.ItemEvents;
import mc.fenderas.arrowroyale.events.PlayerEvents;
import mc.fenderas.arrowroyale.files.PlayerScoresFile;
import mc.fenderas.arrowroyale.items.ItemManager;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.placeholder.ArrowRoyaleExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ArrowRoyale extends JavaPlugin {

    private static ArrowRoyale main;
    private GameManager manager;
    private BlockEvents blockEvents;
    private PlayerEvents playerEvents;
    private ItemEvents itemEvents;

    private Economy economy;

    public static final String version = "1.1.0";

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;
        startConfig();

        if(isUsingPlaceholderAPI()){
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null){
                getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
                getLogger().warning("If you don't want to use PlaceholderAPI, then set false usePlaceholderAPI in the config.yml");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        if (isUsingVault()){
            if (!setupEconomy()){
                getLogger().warning("Could not find Vault! This plugin including an economy plugin like EssentialsX is required.");
                getLogger().warning("If you don't want to use Vault, then set false useVault in the config.yml");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        startPlayerScoresFile();

        manager = new GameManager();
        ArrowRoyaleCommands commands = new ArrowRoyaleCommands(this, manager);
        getCommand(ArrowRoyaleCommands.START).setExecutor(commands);

        blockEvents = new BlockEvents(manager);
        playerEvents = new PlayerEvents(manager);
        itemEvents = new ItemEvents();
        getServer().getPluginManager().registerEvents(blockEvents, this);
        getServer().getPluginManager().registerEvents(playerEvents, this);
        getServer().getPluginManager().registerEvents(itemEvents, this);
        ItemManager.init();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ArrowRoyale] Plugin is enabled");

        if (isUsingPlaceholderAPI()){
            new ArrowRoyaleExpansion().register();
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ArrowRoyale] PlaceholderAPI is Successfully Hooked");
        }

        if (isUsingVault()){
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ArrowRoyale] Vault is Successfully Hooked");
        }

        if (!getMinigameWorld().getPlayers().isEmpty()){
            for (Player player : getMinigameWorld().getPlayers()){
                manager.getPlayerScoreboard().addScoreboard(player);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ArrowRoyale]: Plugin is disabled");
    }

    public void startConfig(){
        saveDefaultConfig();
        //getConfig().options().copyHeader(true);;
        getConfig().options().copyDefaults(true);
        getSpawnSection().addDefault("minigame_world", "world");

        List<String> exemptedBlocks = new ArrayList<>();
        exemptedBlocks.add(Material.OAK_PLANKS.toString());
        getSpawnSection().addDefault("exempted_blocks", exemptedBlocks);

        List<String> unspawnableBlocks = new ArrayList<>();
        unspawnableBlocks.add(Material.LAVA.toString());
        getSpawnSection().addDefault("unspawnable_Blocks", unspawnableBlocks);

        getSection("chest", getSpawnSection()).addDefault("chests_spawned", 2);

        int xMin = -100, xMax = 100, zMin = -100, zMax = 100;
        getCoordiatesSection().addDefault("xMin", xMin);
        getCoordiatesSection().addDefault("xMax", xMax);
        getCoordiatesSection().addDefault("zMin", zMin);
        getCoordiatesSection().addDefault("zMax", zMax);

        getRoundSection().addDefault("secondsPerRound", 60);

        getPlaceholderAPISection().addDefault("usePlaceholderAPI", true);
        getVaultSection().addDefault("useVault", true);

        saveMainConfig();
    }

    public void startPlayerScoresFile(){
        PlayerScoresFile.setup();
        PlayerScoresFile.get().options().copyDefaults(true);
        PlayerScoresFile.get().addDefault("Test", 1);
        PlayerScoresFile.getSection("Players");
        PlayerScoresFile.save();
    }

    public static ConfigurationSection getSection(String name){
        if(main.getConfig().isConfigurationSection(name)){
            return main.getConfig().getConfigurationSection(name);
        }else{
            return main.getConfig().createSection(name);
        }
    }

    public static ConfigurationSection getSection(String name, ConfigurationSection section){
        if(section.isConfigurationSection(name)){
            return section.getConfigurationSection(name);
        }else{
            return section.createSection(name);
        }
    }

    public static Plugin getPlugin(){
        return main;
    }

    public static World getMinigameWorld(){
        return Bukkit.getWorld(getMinigameWorldName());
    }
    public static String getMinigameWorldName(){
        return getSpawnSection().getString("minigame_world");
    }
    public static FileConfiguration getMainConfig(){
        return main.getConfig();
    }

    public static List<String> getExemptedBlocks(){return getSpawnSection().getStringList("exempted_blocks");}

    public static BlockEvents getBlockEvents() {return main.blockEvents;}
    public static PlayerEvents getPlayerEvents() {return main.playerEvents;}
    public static ItemEvents getItemEvents() {return main.itemEvents;}

    public static ConfigurationSection getCoordiatesSection(){return getSection("mapCoords", getSpawnSection());}
    public static ConfigurationSection getSpawnSection(){return getSection("spawn");}
    public static ConfigurationSection getRoundSection(){return getSection("round");}
    public static ConfigurationSection getPluginsSection(){return getSection("plugins");}
    public static ConfigurationSection getPlaceholderAPISection(){return getSection("placeholderAPI", getPluginsSection());}
    public static ConfigurationSection getVaultSection(){return getSection("vault", getPluginsSection());}
    public static boolean isUsingPlaceholderAPI(){return getPlaceholderAPISection().getBoolean("usePlaceholderAPI");}
    public static boolean isUsingVault(){return getVaultSection().getBoolean("useVault");}
    public static Economy getEconomy() {return main.economy;}

    public static void saveMainConfig(){
        main.saveConfig();
        main.saveDefaultConfig();
    }

    public static void savePlayerScoresConfig(){
        PlayerScoresFile.save();
        PlayerScoresFile.reload();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("Yes 2");
            return false;
        }

        economy = rsp.getProvider();
        return (economy != null);
    }
}
