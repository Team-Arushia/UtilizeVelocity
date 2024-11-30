package com.arushia.utilizevelocity;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Plugin(
        id = "utilizevelocity",
        name = "UtilizeVelocity",
        version = "1.0"
)
public class UtilizeVelocity {
    private static final File PLUGIN_FOLDER = new File("plugins/UtilizeVelocity");
    private static final File CONFIG_FILE = new File("plugins/UtilizeVelocity/config.toml");

    private static UtilizeVelocity instance;

    private final ProxyServer server;
    private final Logger logger;
    private final ConfigData configData;

    @Inject
    public UtilizeVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        UtilizeVelocity.instance = this;

        if(!PLUGIN_FOLDER.exists())
            PLUGIN_FOLDER.mkdirs();
        if(!CONFIG_FILE.exists()) {
            try {
                FileUtils.copyURLToFile(Objects.requireNonNull(getClass().getResource("config.toml")), CONFIG_FILE);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create config file", e);
            }
        }

        configData = new Toml().read(CONFIG_FILE).to(ConfigData.class);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("utilizevelocity")
                        .aliases("uv")
                        .plugin(this)
                        .build();

        commandManager.register(commandMeta, new MainCommand());
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public static UtilizeVelocity getInstance() {
        return UtilizeVelocity.instance;
    }
}
