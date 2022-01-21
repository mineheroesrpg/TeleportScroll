package hu.sourcescode.teleportscroll;


import co.aikar.commands.PaperCommandManager;
import de.slikey.effectlib.EffectManager;
import hu.sourcescode.teleportscroll.command.Commands;
import hu.sourcescode.teleportscroll.events.InteractEvent;
import hu.sourcescode.teleportscroll.events.MoveEvent;
import hu.sourcescode.teleportscroll.events.QuitEvent;
import hu.sourcescode.teleportscroll.model.Scroll;
import hu.sourcescode.teleportscroll.model.TeleportSession;
import hu.sourcescode.teleportscroll.utils.ConfigManager;
import hu.sourcescode.teleportscroll.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Getter
public class TeleportScroll extends JavaPlugin {

    private static TeleportScroll instance;
    private static Logger logger;
    private ConfigManager configManager;
    private FileConfiguration config;
    private FileConfiguration lang;
    private EffectManager effectManager;
    private PaperCommandManager commandManager;
    private String pluginPrefix = ChatColor.AQUA + "[TeleportScrolls] ";
    private Set<Scroll> scrolls;
    private Map<String, TeleportSession> sessions;
    private Utils utils;


    @Override
    public void onEnable() {
        instance = this;
        logger = Bukkit.getLogger();
        this.configManager = new ConfigManager();
        this.effectManager = new EffectManager(this);
        this.commandManager = new PaperCommandManager (this);
        this.scrolls = new HashSet<>();
        this.sessions = new HashMap<>();
        this.utils = new Utils(this);
        registerCommands();
        registerListeners();
        setupConfig();
        setupLang();
        loadScrolls();
    }

    private void loadScrolls() {
        File dataFolder = new File(this.getDataFolder() + File.separator + "scrolls");
        if (!dataFolder.isDirectory()) {
            dataFolder.mkdirs();
            logger.info(getPluginPrefix() + ChatColor.BLUE + " scrolls folder created.");
        }
        if (dataFolder.list().length > 0 && dataFolder.list() != null) {
            File[] files = dataFolder.listFiles();
            for (File f : files) {
                String name = f.getName().replace(".yml", "");
                FileConfiguration conf = configManager.getConfig(name, "scrolls", this);
                Scroll scroll = new Scroll();
                scroll.setName(name);
                Location destinationLocation = new Location(
                        Bukkit.getWorld(Objects.requireNonNull(conf.getString("world")))
                        , conf.getDouble("x")
                        , conf.getDouble("y")
                        , conf.getDouble("z"));
                scroll.setDestinationLocation(destinationLocation);
                scrolls.add(scroll);
                logger.info(getPluginPrefix() + ChatColor.BLUE + " " + name + " scroll loaded.");
            }
        } else {
            logger.info(getPluginPrefix() + ChatColor.RED + " No teleport scrolls found!");
        }
    }

    private void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new MoveEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InteractEvent(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new QuitEvent(this), this);

    }

    private void registerCommands() {
        commandManager.registerCommand(new Commands(this));
        commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            getLogger().warning("An error occurred while executing the command: " + command.getName());
            return false;
        });
    }

    private void setupConfig() {
        File file = new File(getDataFolder() + "/" + "config.yml");
        if (!file.exists()) {
            this.saveResource("config.yml", false);
            config = YamlConfiguration.loadConfiguration(file);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void setupLang() {
        File file = new File(getDataFolder() + "/" + "language.yml");
        if (!file.exists()) {
            this.saveResource("language.yml", false);
            lang = YamlConfiguration.loadConfiguration(file);
            this.pluginPrefix = ChatColor.translateAlternateColorCodes(
                    '&',
                    Objects.requireNonNull(getLang().getString("prefix")));
        }
        lang = YamlConfiguration.loadConfiguration(file);
        this.pluginPrefix = ChatColor.translateAlternateColorCodes(
                '&',
                Objects.requireNonNull(getLang().getString("prefix")));
        if (lang.getString("player_not_found") == null) {
            lang.set("player_not_found", "%%player%% not online.");
        }
        if (lang.getString("get_scroll_inventory_full_other") == null) {
            lang.set("get_scroll_inventory_full_other", "&cThere is not enough space in %%player%%'s inventory.");
        }
        if (lang.getString("get_scroll_successfull_other") == null) {
            lang.set("get_scroll_successfull_other", "&a%%scrollname%% added to %%player%%'s inventory.");
        }
        try {
            lang.save(file);
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    public void reload() {
        this.sessions.values().forEach(TeleportSession::cancel);
        this.scrolls.clear();
        this.sessions.clear();
        setupConfig();
        setupLang();
        loadScrolls();
    }

    @Override
    public void onDisable() {
        this.sessions.values().forEach(TeleportSession::cancel);
        this.scrolls.clear();
        this.sessions.clear();
    }
}
