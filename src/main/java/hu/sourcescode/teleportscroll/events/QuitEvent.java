package hu.sourcescode.teleportscroll.events;

import hu.sourcescode.teleportscroll.TeleportScroll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
@Getter
public class QuitEvent implements Listener {

    private TeleportScroll main;

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {

    }
}
