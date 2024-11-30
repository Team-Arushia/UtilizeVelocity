package com.arushia.utilizevelocity;

import com.google.common.io.Files;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MainCommand implements SimpleCommand {
    private final ConfigData configData = UtilizeVelocity.getInstance().getConfigData();

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if(args.length == 0){
            source.sendPlainMessage("Wrong usage!");
            return;
        }

        switch (args[0]){
            case "applyplugins":
                if(args.length < 2){
                    source.sendPlainMessage("Wrong usage!");
                    return;
                }
                for(int i = 1; i < args.length; i++){
                    String pluginName = args[i];
                    File pluginFile = null;
                    for(File otherPluginFile : Objects.requireNonNull(new File(configData.serversFolder + "/" + configData.mainServerFolder + "/plugins/").listFiles())){
                        if(!otherPluginFile.getName().endsWith(".jar")) continue;
                        if(otherPluginFile.getName().toLowerCase().contains(pluginName.toLowerCase())){
                            pluginFile = otherPluginFile;
                            break;
                        }
                    }
                    File pluginFolder = new File(configData.serversFolder + "/" + configData.mainServerFolder + "/plugins/" + pluginName);

                    for(File otherServerFolder : Objects.requireNonNull(new File(configData.serversFolder).listFiles())){
                        if(otherServerFolder.getName().equals(configData.mainServerFolder) || !otherServerFolder.isDirectory()) continue;
                        UtilizeVelocity.getInstance().getLogger().info("Copied to {}", otherServerFolder.getName());
                        try {
                            new File(otherServerFolder.getPath() + "/plugins/" + pluginName).deleteOnExit();
                            Files.copy(Objects.requireNonNull(pluginFile), new File(otherServerFolder.getPath() + "/plugins/" + pluginName + ".jar"));
                            FileUtils.copyDirectory(pluginFolder, new File(otherServerFolder.getPath() + "/plugins/" + pluginName));
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to copy plugin", e);
                        }
                    }
                    UtilizeVelocity.getInstance().getLogger().info("done!");
                }
                break;
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("utilizevelocity.command");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return List.of();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(List.of("applyplugins"));
    }
}
