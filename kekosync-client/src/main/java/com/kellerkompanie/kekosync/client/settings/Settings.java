package com.kellerkompanie.kekosync.client.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.core.gsonConverter.PathConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class Settings {

    private static final File settingsPath = new File(System.getenv("APPDATA") + File.separator + "KekoSync");
    private static final File settingsFile = new File(settingsPath, File.separator + "settings.json");
    private static Settings instance;
    @Getter
    private String executableLocation;
    private HashSet<Path> searchDirectories;
    private ArrayList<ArmAParameter> launchParams;
    @Getter
    private double windowWidth = 800;
    @Getter
    private double windowHeight = 600;
    @Getter
    private double windowX = 0;
    @Getter
    private double windowY = 0;

    public static Settings getInstance() {
        if (instance == null) {
            if (!settingsPath.exists()) {
                if (!settingsPath.mkdirs()) {
                    throw new IllegalStateException("unable to create settings folder: " + settingsPath);
                }
            }

            if (settingsFile.exists()) {
                instance = loadSettings();
            } else {
                instance = new Settings();
                instance.createDefaultSettings();
                instance.saveSettings();
            }
        }
        return instance;
    }

    private static Settings loadSettings() {
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).create();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(settingsFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return gson.fromJson(reader, Settings.class);
    }

    public void setArmAExecutable(String executableLocation) {
        this.executableLocation = executableLocation;
        saveSettings();
    }

    public Set<Path> getSearchDirectories() {
        return Collections.unmodifiableSet(searchDirectories);
    }

    public void addSearchDirectory(Path searchDir) {
        searchDirectories.add(searchDir);
        saveSettings();
    }

    public void removeSearchDirectory(Path path) {
        searchDirectories.remove(path);
        saveSettings();
    }

    public List<ArmAParameter> getLaunchParams() {
        return launchParams;
    }

    private void createDefaultSettings() {
        searchDirectories = new HashSet<>();
        executableLocation = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Arma 3\\arma3_x64.exe";
        launchParams = ArmAParameter.getDefaultParameters();
    }

    private void saveSettings() {
        log.debug("saving settings");

        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                log.error("ran into trouble while creating settings file", e);
                System.exit(1);
            }
        }

        try (Writer writer = new FileWriter(settingsFile)) {
            Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).setPrettyPrinting().create();
            gson.toJson(this, writer);
        } catch (IOException e) {
            log.error("ran into trouble while writing settings to file", e);
            System.exit(1);
        }
    }

    public void updateWindowSize(double width, double height) {
        this.windowWidth = width;
        this.windowHeight = height;
        saveSettings();
    }

    public void updateWindowPosition(double x, double y) {
        this.windowX = x;
        this.windowY = y;
        saveSettings();
    }

    @Getter
    boolean windowMaximized = false;

    public void updateWindowMaximized(boolean maximized) {
        this.windowMaximized = maximized;
        saveSettings();
    }
}
