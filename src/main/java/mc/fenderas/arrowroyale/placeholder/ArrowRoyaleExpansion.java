package mc.fenderas.arrowroyale.placeholder;

import mc.fenderas.arrowroyale.ArrowRoyale;
import mc.fenderas.arrowroyale.files.PlayerScoresFile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrowRoyaleExpansion extends PlaceholderExpansion {
    @Override
    public @org.jetbrains.annotations.NotNull String getIdentifier() {
        return "arrowroyale";
    }

    @Override
    public @org.jetbrains.annotations.NotNull String getAuthor() {
        return "Fenderas";
    }

    @Override
    public @org.jetbrains.annotations.NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null){
            return "";
        }

        if (params.equalsIgnoreCase("kills")){
            int score = PlayerScoresFile.getKillsFromPlayer(player.getName());
            return Integer.toString(score);
        }else {
            return null;
        }
    }
}
