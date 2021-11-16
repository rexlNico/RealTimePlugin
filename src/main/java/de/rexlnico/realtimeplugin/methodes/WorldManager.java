package de.rexlnico.realtimeplugin.methodes;

import de.rexlnico.realtimeplugin.main.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.ArrayList;

public class WorldManager {

    private final ArrayList<String> tabComplete;
    ArrayList<WorldContainer> worlds;

    public WorldManager() throws IOException {
        worlds = new ArrayList<>();
        tabComplete = new ArrayList<>();
        loadAll();
        setTabComplete();
        createTimeTable();
    }

    private void loadAll() throws IOException {
        File worldsDir = new File(Main.getPlugin().getDataFolder(), "worlds");
        File[] fileList = worldsDir.listFiles();
        if (fileList == null) {
            createTemplate(worldsDir);
            createDefault(worldsDir);
            return;
        }
        for (File file : fileList) {
            if (file.getName().contains(".json")) {
                worlds.add(new WorldContainer(file));
            }
        }
    }

    public void loadNew() {
        File directoryPath = new File(Main.getPlugin().getDataFolder(), "worlds");
        File[] fileList = directoryPath.listFiles();
        if (fileList == null) return;
        for (File file : fileList) {
            if (file.getName().contains(".json")) {
                if (containsFile(file.getName())) {
                    worlds.add(new WorldContainer(file));
                }
            }
        }
        setTabComplete();
    }

    private void createTimeTable() throws IOException {
        File file = new File(Main.getPlugin().getDataFolder(), "WeatherTimezones.cfg");
        if (file.exists()) return;
        YamlConfiguration.loadConfiguration(file).save(file);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (String s : ZoneId.getAvailableZoneIds()) {
            writer.write(s + "\n");
        }
        writer.flush();
        writer.close();
    }

    public ArrayList<String> getTabComplete() {
        return tabComplete;
    }

    private void setTabComplete() {
        tabComplete.clear();
        for (WorldContainer world : worlds) {
            tabComplete.add(world.getFile().getName());
        }
    }

    public WorldContainer getWeatherWorld(String name) {
        return worlds.stream()
                .filter(container -> container.getFile().getName().equals(name))
                .findFirst().orElse(null);
    }

    private boolean containsFile(String name) {
        return worlds.stream()
                .anyMatch(container -> container.getFile().getName().equals(name));
    }

    public void updateAll() {
        worlds.forEach(WorldContainer::update);
        setTabComplete();
    }

    public void disable() {
        worlds.forEach(WorldContainer::disable);
    }

    private void createTemplate(File dir) throws IOException {
        InputStream root = Main.getPlugin().getResource("template.json");
        File filePath = new File(dir, "template.json");
        YamlConfiguration.loadConfiguration(filePath).save(filePath);
        Files.copy(root, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void createDefault(File dir) throws IOException {
        InputStream root = Main.getPlugin().getResource("world.json");
        File filePath = new File(dir, "world.json");
        YamlConfiguration.loadConfiguration(filePath).save(filePath);
        Files.copy(root, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
