package hu.sourcescode.teleportscroll.utils;

import hu.sourcescode.teleportscroll.TeleportScroll;
import hu.sourcescode.teleportscroll.model.Scroll;
import hu.sourcescode.teleportscroll.model.TeleportSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Utils {

    private TeleportScroll main;

    /**
     * Check if Scroll exists with the given name;
     * @param name the required name
     * @return true if exist, false otherwise;
     */
    public boolean isScrollNameExists(String name) {
        return main.getScrolls().stream().anyMatch(scroll -> scroll.getName().equalsIgnoreCase(name));
    }

    /**
     * Gets a scroll by name if exists,
     * returns null if no scroll with the given name;
     * @param name the required name
     * @return the required scroll if exists, or null otherwise;
     */
    public Scroll getScrollByName(String name) {
        if (isScrollNameExists(name)) {
            return main.getScrolls().stream().filter(
                    s -> s.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
        }
        return null;
    }

    /**
     * Check if session exists for the given player's uuid;
     * @param uuid the players uuid;
     * @return true if exists, false otherwise;
     */
    public boolean isSessionExists(String uuid) {
        return main.getSessions().entrySet().stream().anyMatch(key -> key.equals(uuid));
    }

    /**
     * Gets the session for the given player's uuid;
     * @param uuid the players uuid;
     * @return a Session if exists, null otherwise;
     */
    public TeleportSession getSessionByPlayerUUID(String uuid) {
        if (isSessionExists(uuid)) {
            return main.getSessions().get(uuid);
        }
        return null;
    }

    public String translatePlaceholders(String input, Scroll scroll, Player player) {
        return ChatColor.translateAlternateColorCodes('&', input
                .replace("%%scrollname%%", scroll.getName())
                .replace("%%player%%", player.getName()));
    }

    public String translatePlaceholders(String input, Scroll scroll) {
        return ChatColor.translateAlternateColorCodes('&', input
                .replace("%%scrollname%%", scroll.getName()));
    }

    public String translatePlaceholders(String input, Player player) {
        return ChatColor.translateAlternateColorCodes('&', input
                .replace("%%player%%", player.getName()));
    }

    public String translatePlaceholders(String input, String placeholder, String replace) {
        return ChatColor.translateAlternateColorCodes('&', input
                .replace(placeholder, replace));
    }

    public String translatePlaceholders(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }


}
