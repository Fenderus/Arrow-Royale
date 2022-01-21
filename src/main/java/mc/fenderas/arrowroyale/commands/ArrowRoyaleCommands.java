package mc.fenderas.arrowroyale.commands;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.items.EditAxeModes;
import mc.fenderas.arrowroyale.items.ItemManager;
import mc.fenderas.arrowroyale.manager.GameManager;
import mc.fenderas.arrowroyale.manager.GameStates;
import mc.fenderas.arrowroyale.manager.LobbyManager;
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
                    if (args[0].equalsIgnoreCase("lobby")){
                        LobbyManager lobbyManager = manager.getLobbyManager(args[1]);
                        if (lobbyManager != null){
                            if(args[2].equalsIgnoreCase("startgame")){
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "Arrow Royale" + ChatColor.WHITE + " has Started");
                                lobbyManager.setGameState(GameStates.START);
                            }
                            else if(args[2].equalsIgnoreCase("mode")){
                                sender.sendMessage(ChatColor.YELLOW + "Arrow Royale mode " + ChatColor.GREEN + args[2] + ChatColor.WHITE + " has been set");
                                lobbyManager.setGameState(GameStates.valueOf(args[3].toUpperCase(Locale.ROOT)));
                            }
                            else if(args[2].equalsIgnoreCase("debug")){
                                if (player.hasPermission("arrowroyale.admin.debug")) {
                                    if (args[3].equalsIgnoreCase("givemekills")) {
                                        player.sendMessage("Points Added");
                                        lobbyManager.getRoundScoreboard().setScore(player, Integer.parseInt(args[4]));
                                        //manager.addScore(player, Integer.parseInt(args[4]));
                                    }
                                }
                                else{
                                    sender.sendMessage(ChatColor.RED + "You dont have the Permissions to do this!");
                                    return true;
                                }
                            }
                        }else {
                            sender.sendMessage("This lobby is not a Lobby because the 4th argument is empty or incorrect");
                        }
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
                doStuff.add("editaxe");
                doStuff.add("lobby");
                doStuff.add("player");
                return doStuff;
            }
            else if(args.length == 2){
                if (args[0].equalsIgnoreCase("lobby")){
                    return ArrowRoyale.getWorldNames();
                }
                else if (args[0].equalsIgnoreCase("editaxe")){
                    List<String> choice = new ArrayList<>();
                    choice.add("give");
                    choice.add("mode");
                    return choice;
                }

                else if (args[0].equalsIgnoreCase("player")){
                    List<String> choice = new ArrayList<>();
                    choice.add("getkills");
                    return choice;
                }
            }
            else if(args.length == 3){

                if (args[0].equalsIgnoreCase("lobby")){
                    List<String> doStuff = new ArrayList<>();
                    doStuff.add("mode");
                    doStuff.add("startgame");
                    if (sender.hasPermission("arrowroyale.admin.debug")){
                        doStuff.add("debug");
                    }
                    return doStuff;
                }

                //

                else if(args[1].equalsIgnoreCase("mode")){
                    List<String> modes = new ArrayList<>();
                    modes.add(EditAxeModes.RANDOMSPAWN.toString().toLowerCase(Locale.ROOT));
                    modes.add(EditAxeModes.SPAWNPOINT.toString().toLowerCase(Locale.ROOT));
                    return modes;
                }

                //

            }
            else if(args.length == 4){
                if (args[0].equalsIgnoreCase("lobby")){
                    if (args[2].equalsIgnoreCase("mode")){
                        List<String> modes = new ArrayList<>();
                        modes.add(GameStates.LOBBY.toString().toLowerCase(Locale.ROOT));
                        modes.add(GameStates.START.toString().toLowerCase(Locale.ROOT));
                        modes.add(GameStates.ACTIVE.toString().toLowerCase(Locale.ROOT));
                        modes.add(GameStates.END.toString().toLowerCase(Locale.ROOT));
                        return modes;
                    }
                    else if (args[2].equalsIgnoreCase("debug")){
                        if (sender.hasPermission("arrowroyale.admin.debug")){
                            List<String> choice = new ArrayList<>();
                            choice.add("givemekills");
                            return choice;
                        }
                    }
                }
            }
        }
        return null;
    }
}
