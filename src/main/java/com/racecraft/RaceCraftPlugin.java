package com.racecraft;

import com.racecraft.commands.RaceCommand;
import com.racecraft.gui.RaceGui;
import com.racecraft.managers.RaceManager;
import com.racecraft.races.RaceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RaceCraftPlugin extends JavaPlugin {

    private RaceRegistry raceRegistry;
    private RaceManager raceManager;
    private RaceGui raceGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.raceRegistry = new RaceRegistry();
        this.raceRegistry.registerAllDefaults();

        this.raceManager = new RaceManager(this, raceRegistry);
        this.raceGui = new RaceGui(this, raceRegistry, raceManager);

        Bukkit.getPluginManager().registerEvents(raceManager, this);
        Bukkit.getPluginManager().registerEvents(raceGui, this);

        RaceCommand cmd = new RaceCommand(raceRegistry, raceManager, raceGui);
        for (String name : new String[]{"toc", "tiepnhanhuyetmach", "tubohuyetmach", "givetrung"}) {
            if (getCommand(name) != null) {
                getCommand(name).setExecutor(cmd);
                getCommand(name).setTabCompleter(cmd);
            }
        }

        // Vòng lặp kiểm tra nội tại & môi trường mỗi 1 giây (20 ticks)
        Bukkit.getScheduler().runTaskTimer(this, () -> raceManager.tickPassives(), 20L, 20L);

        getLogger().info("RaceCraft da khoi dong! Da nap " + raceRegistry.getAll().size() + " chung toc.");
    }

    @Override
    public void onDisable() {
        if (raceManager != null) {
            raceManager.saveData();
        }
    }
}
