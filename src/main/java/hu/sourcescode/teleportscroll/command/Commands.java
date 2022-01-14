package hu.sourcescode.teleportscroll.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import hu.sourcescode.teleportscroll.TeleportScroll;
import hu.sourcescode.teleportscroll.model.Scroll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@CommandAlias("teleportscroll|tpscroll")
public class Commands extends BaseCommand {

    private final TeleportScroll main;

    @Subcommand("create")
    @CommandPermission("teleportscrolls.create")
    public void createScroll(Player sender, String name) {
        if (!main.getUtils().isScrollNameExists(name)) {
            Scroll scroll = new Scroll(name, sender.getLocation());

            File dataFile = new File(main.getDataFolder() + "/" + "scrolls" + "/" + name + ".yml");
            if (!dataFile.exists()) {
                try {
                    dataFile.createNewFile();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
            config.set("name", name);
            config.set("world", Objects.requireNonNull(scroll.getDestinationLocation().getWorld()).getName());
            config.set("x", scroll.getDestinationLocation().getX());
            config.set("y", scroll.getDestinationLocation().getY());
            config.set("z", scroll.getDestinationLocation().getZ());

            try {
                config.save(dataFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            main.getScrolls().add(scroll);
            sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(Objects.requireNonNull(main.getLang().getString("create_scroll_successfull")), scroll));
        } else {

            sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(Objects.requireNonNull(main.getLang().getString("create_scroll_already_exists")), "%%scrollname%%", name));
        }
    }

    @Subcommand("list")
    @CommandPermission("teleportscroll.list")
    public void listScrolls(CommandSender sender) {
        StringBuilder list = new StringBuilder("");
        if (main.getScrolls().size() > 0) {
            main.getScrolls().forEach(scroll -> {
                list.append(scroll.getName()).append(", ");
            });
            sender.sendMessage(ChatColor.AQUA + "=============== " + main.getPluginPrefix() + " ===============");
            sender.sendMessage(ChatColor.GREEN + String.valueOf(list));
        }
    }

    @Subcommand("delete")
    @CommandPermission("teleportscroll.delete")
    public void deleteScroll(CommandSender sender, String name) {
        if (main.getUtils().isScrollNameExists(name)) {
            Scroll scroll = main.getUtils().getScrollByName(name);
            main.getScrolls().remove(scroll);
            File file = new File(main.getDataFolder() + "/" + "scrolls" + "/" + name + ".yml");
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                    Objects.requireNonNull(main.getLang().getString("delete_scroll_successfull")), scroll));
        } else {
            sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                    Objects.requireNonNull(main.getLang().getString("delete_scroll_not_exists")), "%%scrollname%%", name
            ));
        }
    }

    @Subcommand("get")
    @CommandPermission("teleportscroll.get")
    public void getScroll(Player sender, String scrollName) {
        if (main.getUtils().isScrollNameExists(scrollName)) {
            if (sender.getInventory().firstEmpty() == -1) {
                sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                        Objects.requireNonNull(main.getLang().getString("get_scroll_inventory_full")),
                        sender
                ).replace("%%scrollname%%", scrollName));
            } else {
                sender.getInventory().addItem(itemBuilder(scrollName));
                sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                        Objects.requireNonNull(main.getLang().getString("get_scroll_successfull")),
                        sender
                ).replace("%%scrollname%%", scrollName));
            }
        } else {
            sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                    Objects.requireNonNull(main.getLang().getString("get_scroll_not_found")),
                    sender
            ).replace("%%scrollname%%", scrollName));
        }
    }

    @Subcommand("reload")
    @CommandPermission("teleportscroll.reload")
    public void reload(CommandSender sender) {
        main.reload();
        sender.sendMessage(main.getPluginPrefix() + main.getUtils().translatePlaceholders(
                main.getLang().getString("plugin_reload")
        ));
    }

    public ItemStack itemBuilder(String scrollName) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        NamespacedKey key = new NamespacedKey(main, "teleportscroll");
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("item.name")
                .replace("%%scroll%%", scrollName)));
        List<String> lore = new ArrayList<>();
        for (String string : main.getConfig().getStringList("item.lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', string.replace("%%scroll%%", scrollName)));
        }
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, scrollName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }


}
