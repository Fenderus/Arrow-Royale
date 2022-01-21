package mc.fenderas.arrowroyale;

import mc.fenderas.arrowroyale.commands.ArrowRoyaleCommands;
import mc.fenderas.arrowroyale.events.BlockEvents;
import mc.fenderas.arrowroyale.events.InventoryEvents;
import mc.fenderas.arrowroyale.events.ItemEvents;
import mc.fenderas.arrowroyale.events.PlayerEvents;
import mc.fenderas.arrowroyale.files.PlayerScoresFile;
import mc.fenderas.arrowroyale.items.ItemManager;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.manager.LobbyManager;
import mc.fenderas.arrowroyale.placeholder.ArrowRoyaleExpansion;
import mc.fenderas.arrowroyale.simpleconfig.SimpleConfig;
import mc.fenderas.arrowroyale.simpleconfig.SimpleConfigManager;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ArrowRoyale extends JavaPlugin {

    private static ArrowRoyale main;
    private GameManager manager;
    private BlockEvents blockEvents;
    private PlayerEvents playerEvents;
    private ItemEvents itemEvents;
    private InventoryEvents inventoryEvents;

    private Economy economy;

    public static final String version = "1.1.0";

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;
        //startConfig();
        saveDefaultConfig();

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
        inventoryEvents = new InventoryEvents(manager);
        getServer().getPluginManager().registerEvents(blockEvents, this);
        getServer().getPluginManager().registerEvents(playerEvents, this);
        getServer().getPluginManager().registerEvents(itemEvents, this);
        getServer().getPluginManager().registerEvents(inventoryEvents, this);
        ItemManager.init();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ArrowRoyale] Plugin is enabled");

        if (isUsingPlaceholderAPI()){
            new ArrowRoyaleExpansion().register();
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ArrowRoyale] PlaceholderAPI is Successfully Hooked");
        }

        if (isUsingVault()){
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ArrowRoyale] Vault is Successfully Hooked");
        }

        manager.lobbies.forEach(this::setupScoreboards);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        main.saveConfig();
        for (LobbyManager lobbies : manager.lobbies) {
            if(lobbies.state == GameStates.ACTIVE)
            {
                lobbies.getChestSpawners().removeChests();
            }
        }
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ArrowRoyale] Plugin is disabled");
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    public void startConfig(){
        //getConfig().options().copyHeader(true);;
        getConfig().options().copyDefaults(true);

        List<String> exemptedBlocks = new ArrayList<>();
        exemptedBlocks.add(Material.OAK_PLANKS.toString());
        getRoundSection().addDefault("breakable_blocks", exemptedBlocks);

        List<String> unspawnableBlocks = new ArrayList<>();
        unspawnableBlocks.add(Material.LAVA.toString());
        getSpawnSection().addDefault("unspawnable_Blocks", unspawnableBlocks);

        setupLobbyWorldsSection();

        getRoundSection().addDefault("secondsPerRound", 60);

        setupGUIItemsSection();

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
    public static JavaPlugin getJavaPlugin(){
        return main;
    }

    public static List<String> getWorldNames(){return List.copyOf(getLobbyWorldsSection().getKeys(false));}
    public static List<String> getCustomGUIItemNames(){return List.copyOf(getCustomGUIItemsSection().getKeys(false));}
    public static FileConfiguration getMainConfig(){
        return main.getConfig();
    }

    public static List<String> getBreakableBlocks(){return getRoundSection().getStringList("breakable_blocks");}
    public static List<String> getUnspawnableBlocks(){return getSpawnSection().getStringList("unspawnable_Blocks");}

    public static BlockEvents getBlockEvents() {return main.blockEvents;}
    public static PlayerEvents getPlayerEvents() {return main.playerEvents;}
    public static ItemEvents getItemEvents() {return main.itemEvents;}

    public static ConfigurationSection getLobbyItemSection(){return getSection("lobby_hand_item", getGUIItemsSection());}
    public static ConfigurationSection getCoordinatesSection(String worldName) {return getSection("mapCoords", getSpecificSpawnSection(worldName));}
    public static ConfigurationSection getChestSection(String worldName){return getSection("chests", getSpecificWorldSection(worldName));}
    public static ConfigurationSection getSpecificSpawnSection(String worldName){return getSection("spawn", getSpecificWorldSection(worldName));}
    public static ConfigurationSection getWorldBorderSection(String worldName){return getSection("border", getSpecificWorldSection(worldName));}
    public static ConfigurationSection getLobbyWorldsSection(){return getSection("lobby_worlds");}
    public static ConfigurationSection getSpawnSection(){return getSection("spawn");}
    public static ConfigurationSection getRoundSection(){return getSection("round");}
    public static ConfigurationSection getPluginsSection(){return getSection("plugins");}
    public static ConfigurationSection getPlaceholderAPISection(){return getSection("placeholderAPI", getPluginsSection());}
    public static ConfigurationSection getVaultSection(){return getSection("vault", getPluginsSection());}
    public static ConfigurationSection getGUIItemsSection(){return getSection("gui_items");}
    public static ConfigurationSection getCustomGUIItemsSection(){return getSection("custom_items", getGUIItemsSection());}
    public static boolean isUsingPlaceholderAPI(){return getPlaceholderAPISection().getBoolean("usePlaceholderAPI");}
    public static boolean isUsingVault(){return getVaultSection().getBoolean("useVault");}
    public static Economy getEconomy() {return main.economy;}

    public static ConfigurationSection getSpecificWorldSection(String name){
        for (String worldName : getWorldNames()) {
            if(Objects.equals(worldName, name)){
                return getSection(name, getLobbyWorldsSection());
            }
        }
        return null;
    }

    public static ConfigurationSection getSpecificCustomGUIItemSection(String name){
        for (String customGUIItemName : getCustomGUIItemNames()){
            if(Objects.equals(name, customGUIItemName)){
                return getSection(customGUIItemName, getCustomGUIItemsSection());
            }
        }
        return null;
    }

    public static void saveMainConfig(){
        SimpleConfigManager manager = new SimpleConfigManager(main);
        SimpleConfig config = manager.getNewConfig("config.yml");
        config.saveConfig();
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

    private void setupScoreboards(LobbyManager manager){
        if (!manager.getWorld().getPlayers().isEmpty()){
            for (Player player : manager.getWorld().getPlayers()){
                manager.getPlayerScoreboard().addScoreboard(player);
            }
        }
    }

    private void setupLobbyWorldsSection(){
        getSection("world", getLobbyWorldsSection());
        for (String section : getWorldNames()) {
            int xMin = -100, xMax = 100, zMin = -100, zMax = 100;
            ConfigurationSection worldSection = getSection(section, getLobbyWorldsSection());
            ConfigurationSection spawnSection = getSection("spawn", worldSection);
            getSection("mapCoords", spawnSection).addDefault("xMin", xMin);
            getSection("mapCoords", spawnSection).addDefault("xMax", xMax);
            getSection("mapCoords", spawnSection).addDefault("zMin", zMin);
            getSection("mapCoords", spawnSection).addDefault("zMax", zMax);

            getSection("chests", worldSection).addDefault("max_chests", 4);

            getSection("border", worldSection).addDefault("max_border_size", 500);
        }
    }

    private void setupGUIItemsSection(){

        getLobbyItemSection().addDefault("display_name", "Lobbies");
        getLobbyItemSection().addDefault("slot", 8);
        getLobbyItemSection().addDefault("material", Material.BOOKSHELF.toString().toUpperCase(Locale.ROOT));
        List<String> itemLore = new ArrayList<>();
        itemLore.add("Lorem Ipsum");
        getLobbyItemSection().addDefault("lore", itemLore);

        getSection("Lobby_1", getCustomGUIItemsSection());
        for (String section : getCustomGUIItemNames()) {
            ConfigurationSection itemSection = getSection(section, getCustomGUIItemsSection());
            itemSection.addDefault("display_name", "Lobby 1");
            itemSection.addDefault("world", "world");
            itemSection.addDefault("slot", 10);
            itemSection.addDefault("material", Material.BOOKSHELF.toString().toUpperCase(Locale.ROOT));
            List<String> lore = new ArrayList<>();
            lore.add("Lorem Ipsum");
            itemSection.addDefault("lore", lore);
        }
    }
}
