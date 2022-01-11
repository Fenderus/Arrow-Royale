package mc.fenderas.arrowroyale.commands;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.items.EditAxeModes;
import mc.fenderas.arrowroyale.items.ItemManager;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArrowRoyaleCommands implements CommandExecutor, TabCompleter {

    public static final String START = "arrowRoyale";
    private GameManager manager;

    public ArrowRoyaleCommands(ArrowRoyale plugin, GameManager manager)
    {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (!sender.hasPermission("arrowroyale.admin")) {
            sender.sendMessage(ChatColor.RED + "You dont have the Permissions to do this!");
            return true;
        }

        Player player = (Player) sender;
        if(command.getName().equalsIgnoreCase(START)){
            if(args.length > 0){
                try {
                    if(args[0].equalsIgnoreCase("startgame")){
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Arrow Royale" + ChatColor.WHITE + " has Started");
                        manager.setGameState(GameStates.START);
                    }
                    else if(args[0].equalsIgnoreCase("mode")){
                        sender.sendMessage(ChatColor.YELLOW + "Arrow Royale mode " + ChatColor.GREEN + args[1] + ChatColor.WHITE + " has been set");
                        manager.setGameState(GameStates.valueOf(args[1].toUpperCase(Locale.ROOT)));
                    }
                    else if (args[0].equalsIgnoreCase("editaxe")){
                        if (args[1].equalsIgnoreCase("give")){
                            player.getInventory().addItem(ItemManager.editAxe);
                            player.sendMessage("The Edit axe has been sent, use it wisely");
                        }else if(args[1].equalsIgnoreCase("mode")){
                            ArrowRoyale.getItemEvents().setEditAxeMode(EditAxeModes.valueOf(args[2].toUpperCase(Locale.ROOT)));
                            sender.sendMessage(ChatColor.YELLOW + "Edit Axe mode " + ChatColor.GREEN + args[2] + ChatColor.WHITE + " for Arrow Royale has been set");
                        }
                    }
                    else if(args[0].equalsIgnoreCase("debug")){
                        if (player.hasPermission("arrowroyale.admin.debug")) {
                            if (args[1].equalsIgnoreCase("givemekills")) {
                                player.sendMessage("Points Added");
                                manager.getRoundScoreboard().setScore(player, Integer.parseInt(args[2]));
                            }
                            if (args[1].equalsIgnoreCase("changemyscoreboardstate")) {
                                player.sendMessage("Scoreboard Changed");
                                GameStates states = GameStates.valueOf(args[1].toUpperCase(Locale.ROOT));
                                switch (states){
                                    case LOBBY, END, START -> {
                                        manager.getRoundScoreboard().removePlayer(player);
                                        manager.getPlayerScoreboard().addScoreboard(player);
                                    }
                                    case ACTIVE -> {
                                        manager.getRoundScoreboard().addPlayer(player);
                                        manager.getPlayerScoreboard().removeScoreboard(player);
                                    }
                                }
                            }
                        }
                        else{
                            sender.sendMessage(ChatColor.RED + "You dont have the Permissions to do this!");
                            return true;
                        }
                    }
                    else if(args[0].equalsIgnoreCase("player")){
                        if (args[1].equalsIgnoreCase("getkills")) {
                            if (ArrowRoyale.isUsingPlaceholderAPI()){
                                player.sendMessage(PlaceholderAPI.setPlaceholders(player, "Your Kill count is %arrowroyale_kills%"));
                            }else{
                                player.sendMessage("You haven't set true to usePlaceholderAPI in the config.yml");
                            }
                        }
                    }
                }catch (IllegalArgumentException e){
                    sender.sendMessage("That was not a valid argument");
                }
            }
            else {
                sender.sendMessage("&c&l! &f/" + START + " <do> <state>");
            }
        }



        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!sender.hasPermission("arrowroyale.admin")){return null;}

        if(command.getName().equalsIgnoreCase(START)){
            if (args.length == 1)
            {
                List<String> doStuff = new ArrayList<>();
                doStuff.add("mode");
                doStuff.add("startgame");
                doStuff.add("editaxe");
                if (sender.hasPermission("arrowroyale.admin.debug")){
                    doStuff.add("debug");
                }
                doStuff.add("player");
                return doStuff;
            }
            else if(args.length == 2){
                if (args[0].equalsIgnoreCase("mode")){
                    List<String> modes = new ArrayList<>();
                    modes.add(GameStates.LOBBY.toString().toLowerCase(Locale.ROOT));
                    modes.add(GameStates.START.toString().toLowerCase(Locale.ROOT));
                    modes.add(GameStates.ACTIVE.toString().toLowerCase(Locale.ROOT));
                    modes.add(GameStates.END.toString().toLowerCase(Locale.ROOT));
                    return modes;
                }
                else if (args[0].equalsIgnoreCase("editaxe")){
                    List<String> choice = new ArrayList<>();
                    choice.add("give");
                    choice.add("mode");
                    return choice;
                }
                else if (args[0].equalsIgnoreCase("debug")){
                    if (sender.hasPermission("arrowroyale.admin.debug")){
                        if (manager.state == GameStates.ACTIVE){
                            List<String> choice = new ArrayList<>();
                            choice.add("givemekills");
                            choice.add("changemyscoreboardstate");
                            return choice;
                        }else{
                            sender.sendMessage("This command is only used when the game is Active!");
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("player")){
                    List<String> choice = new ArrayList<>();
                    choice.add("getkills");
                    return choice;
                }
            }
            else if(args.length == 3){
                if(args[1].equalsIgnoreCase("mode")){
                    List<String> modes = new ArrayList<>();
                    modes.add(EditAxeModes.RANDOMSPAWN.toString().toLowerCase(Locale.ROOT));
                    modes.add(EditAxeModes.SPAWNPOINT.toString().toLowerCase(Locale.ROOT));
                    return modes;
                }else if(args[1].equalsIgnoreCase("changemyscoreboardstate")){
                    List<String> modes = new ArrayList<>();
                    modes.add(GameStates.LOBBY.toString().toLowerCase(Locale.ROOT));
                    modes.add(GameStates.START.toString().toLowerCase(Locale.ROOT));
                    modes.add(GameStates.ACTIVE.toString().toLowerCase(Locale.ROOT));
                    modes.add(GameStates.END.toString().toLowerCase(Locale.ROOT));
                    return modes;
                }
            }
        }
        return null;
    }
}
