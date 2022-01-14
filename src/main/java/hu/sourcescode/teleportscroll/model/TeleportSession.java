package hu.sourcescode.teleportscroll.model;

import de.slikey.effectlib.effect.HelixEffect;
import hu.sourcescode.teleportscroll.TeleportScroll;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;

@NoArgsConstructor
@Getter @Setter
public class TeleportSession {

    private TeleportScroll main;
    private Player p;
    private Location prevLoc;
    private Scroll scroll;
    private HelixEffect effect;
    private long cooldown;
    private int taskId;
    private boolean isInTeleport;

    public TeleportSession(TeleportScroll main, Player p, Scroll scroll) {
        this.main = main;
        this.p = p;
        this.prevLoc = p.getLocation();
        this.scroll = scroll;
        this.cooldown = System.currentTimeMillis();
        this.isInTeleport = false;
    }

    public void tp() {
        this.effect = new HelixEffect(main.getEffectManager());
        p.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                Objects.requireNonNull(main.getLang().getString("activate_scroll_successfull")),
                scroll, p
        ));
        p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 2f);
        p.teleport(scroll.getDestinationLocation());
        this.effect.targetPlayer = p;
        this.effect.setLocation(scroll.getDestinationLocation());
        this.effect.particle = Particle.PORTAL;
        this.effect.radius = 1;
        this.effect.particleCount = 2;
        this.effect.duration = 999999;
        this.effect.start();
    }

    public void cancel() {
        this.effect.cancel();
    }

    public void tpBack() {
        p.teleport(prevLoc);
        effect.setLocation(prevLoc);
        Bukkit.getScheduler().runTaskLater(main, this::cancel, 20L);
        main.getSessions().remove(p.getUniqueId().toString());
        p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 2f);
    }


}
