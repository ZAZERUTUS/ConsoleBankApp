package org.example.config;

import lombok.SneakyThrows;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigHandler {

    public static final Path configPath = Paths.get("./config.yml");

    private static ConfigHandler configHandler;

    Config config;

    public static ConfigHandler getInstanceConfig() {
        return getInstanceConfig(configPath);
    }

    @SneakyThrows
    public static ConfigHandler getInstanceConfig(Path configPath) {
        if(configHandler == null) {
            configHandler = new ConfigHandler(configPath);
        }
        return configHandler;
    }

    private ConfigHandler(Path configPath) throws FileNotFoundException {
        this.config = loadConfig(configPath);
    }

    public Config loadConfig(Path configPath) throws FileNotFoundException {
        Constructor constructor = new Constructor(Config.class);
        Yaml yaml = new Yaml(constructor);
        return (Config) yaml.load(new FileInputStream(configPath.toFile()));
    }

    public Config getConfig() {
        return this.config;
    }
}
