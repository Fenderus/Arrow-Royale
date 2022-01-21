package mc.fenderas.arrowroyale.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorCodeUtils
{
    public static List<String> translateColorsInStringList(List<String> strings){
        List<String> newList = new ArrayList<>();
        if (!strings.isEmpty()){
            for (String names : strings) {
                newList.add(ChatColor.translateAlternateColorCodes('&', names));
            }
        }
        return newList;
    }

    public static String translateColorsInString(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
