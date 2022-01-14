package hu.sourcescode.teleportscroll.events;

import hu.sourcescode.teleportscroll.TeleportScroll;
import hu.sourcescode.teleportscroll.model.Scroll;
import hu.sourcescode.teleportscroll.model.TeleportSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class InteractEvent implements Listener {

    private TeleportScroll main;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != (EquipmentSlot.HAND)) return;
        if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.PAPER) return;
            ItemStack itemInHand = e.getPlayer().getInventory().getItemInMainHand();
            if (!itemInHand.hasItemMeta()) return;
            NamespacedKey key = new NamespacedKey(main, "teleportscroll");
            ItemMeta meta = itemInHand.getItemMeta();
            PersistentDataContainer container = Objects.requireNonNull(meta).getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                String scrollName = container.get(key, PersistentDataType.STRING);
                Scroll scroll = main.getUtils().getScrollByName(scrollName);
                if (scroll != null) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (!main.getSessions().containsKey(e.getPlayer().getUniqueId().toString())) {
                        TeleportSession session = new TeleportSession(main, e.getPlayer(), scroll);
                        main.getSessions().put(e.getPlayer().getUniqueId().toString(), session);
                        Bukkit.getScheduler().runTaskLater(main, session::tp, 4L);
                    } else {
                        TeleportSession session = main.getSessions().get(e.getPlayer().getUniqueId().toString());
                        if ((System.currentTimeMillis() - session.getCooldown()) > 6000) {
                            session.getEffect().cancel();
                            session = new TeleportSession(main, e.getPlayer(), scroll);
                            main.getSessions().put(e.getPlayer().getUniqueId().toString(), session);
                            //To avoid the event being activated twice, i have to delay teleport.
                            Bukkit.getScheduler().runTaskLater(main, session::tp, 4L);
                        }
                    }
                } else {
                    e.getPlayer().sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                            Objects.requireNonNull(main.getLang().getString("activate_scroll_invalid")),
                            e.getPlayer()
                    ));
                }
            }
        }
    }

}
