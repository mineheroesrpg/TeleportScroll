package hu.sourcescode.teleportscroll.events;

import hu.sourcescode.teleportscroll.TeleportScroll;
import hu.sourcescode.teleportscroll.model.TeleportSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class MoveEvent implements Listener {

    private TeleportScroll main;

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e) {
        Location to = e.getTo();
        Location from = e.getFrom();
        Player p = e.getPlayer();
        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }
        if (!main.getSessions().containsKey(p.getUniqueId().toString())) return;
        TeleportSession session = main.getSessions().get(p.getUniqueId().toString());
        if (session.getScroll().getDestinationLocation().distanceSquared(p.getLocation()) <= 1.2) {
            long diff = (System.currentTimeMillis() - session.getCooldown());
            if (diff > 6000) {
                session.setCooldown(System.currentTimeMillis());
                if (!session.isInTeleport()) {
                    teleportPlayer(p, session);
                }
            }
        } else {
            // If the player moves during the teleport
            if (session.isInTeleport()) {
                Bukkit.getScheduler().cancelTask(session.getTaskId());
                session.setTaskId(-1);
                session.setInTeleport(false);
                session.setCooldown(session.getCooldown() - 6000);
                p.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                        Objects.requireNonNull(main.getLang().getString("activate_scroll_cancelled")),
                        session.getScroll(),
                        p
                ));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, -0.5f);
            }
        }
    }

    public void teleportPlayer(Player p, TeleportSession session) {

        session.setInTeleport(true);
        p.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                Objects.requireNonNull(main.getLang().getString("activate_scroll_in_progress")),
                session.getScroll(),
                p
        ));
        p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 0.5f, 1.4f);
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                session.tpBack();

            }
        }.runTaskLater(main, 3 * 20).getTaskId();
        session.setTaskId(taskId);
    }
}
