package life.ferret.ferretPlugin.FerretCoreTools;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

public class featureController {

    public Plugin rootPlugin;

    public final int ENABLED = 1;
    public final int DISABLED = 2;
    public final int FAILED = 3;
    public final int UNIDENTIFIED_FEATURE = -1;

    public final String PLAYER_TOOLBOX = "PlayerToolbox";
    public final String ADMIN_COMMAND_BROADCASTER = "AdminCommandBroadcaster";
    public final String ADMIN_TOOLBOX = "AdminToolbox";
    public final String ITEM_ECO = "ItemEco";
    public final String VAULT_API = "VaultAPI";
    public final String NBT_API = "NBTAPI";
    public ArrayList<statusIndicator> features = new ArrayList<>();


    public featureController(Plugin rootPlugin) {
        this.rootPlugin = rootPlugin;
        this.addFeature(this.ITEM_ECO, false);
        this.addFeature(this.ADMIN_COMMAND_BROADCASTER, false);
        this.addFeature(this.ADMIN_TOOLBOX, false);
        this.addFeature(this.VAULT_API, true);
        this.addFeature(this.PLAYER_TOOLBOX, false);
        this.addFeature(this.NBT_API, true);
    }

    public boolean continuePermitted(String featureName, CommandSender sender, String disabledMessage) {
        if(this.getFeatureStatus(featureName) == 1) {
            return true;
        } else {
            if(sender != null && disabledMessage != null) {
                sender.sendMessage(disabledMessage);
            }
            return false;
        }
    }

    public void addFeature(String featureName, boolean isDependency) {
        features.add(new statusIndicator(featureName, isDependency));
    }

    public boolean setFeatureStatus(String featureName, int status) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            features.get(index).setStatus(status);
            return true;
        }
        return false;
    }

    public int getFeatureStatus(String featureName) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            return features.get(index).getStatus();
        } else {
            return UNIDENTIFIED_FEATURE;
        }
    }

    public boolean setCommands(String featureName, commandSkeleton[] commands) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            features.get(index).setCommands(commands);
            return true;
        } else {
            return false;
        }
    }

    public commandSkeleton[] getCommands(String featureName) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            return features.get(index).getCommands();
        } else {
            return null;
        }
    }

    public boolean setListeners(String featureName, Listener[] listeners) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            features.get(index).setListeners(listeners);
            return true;
        } else {
            return false;
        }
    }

    public Listener[] getListeners(String featureName) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            return features.get(index).getListeners();
        } else {
            return null;
        }
    }

    public boolean setCustomDisabledMessage(String featureName, String message) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            features.get(index).setCustomDisabledMessage(message);
            return true;
        } else {
            return false;
        }
    }

    public String getCustomDisabledMessage(String featureName) {
        int index = findFeatureIndexByName(featureName);
        if(index != UNIDENTIFIED_FEATURE) {
            return features.get(index).getCustomDisabledMessage();
        } else {
            return null;
        }
    }

    public String returnColourIndicatedFeatureName(String featureName) {
        int index = findFeatureIndexByName(featureName);
        switch(features.get(index).getStatus()){
            case(1):
                return ChatColor.GREEN + featureName;
            case(2):
                return ChatColor.YELLOW + featureName;
            case(3):
                return ChatColor.RED + featureName;
            case(-1):
                return ChatColor.DARK_GRAY + featureName;
        }
        return ChatColor.LIGHT_PURPLE + featureName;
    }

    public boolean disableFeature(String featureName) {
        if(this.setFeatureStatus(featureName, DISABLED)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean enableFeature(String featureName) {
        int featureIndex = findFeatureIndexByName(featureName);
        if(featureIndex == UNIDENTIFIED_FEATURE) {
            return false;
        }
        statusIndicator focusedFeature = features.get(findFeatureIndexByName(featureName));
        if (focusedFeature != null) {
            if(!focusedFeature.featureRegistered()) {
                if (focusedFeature.getCommands().length != 0) {
                    for (commandSkeleton commandToEnable : focusedFeature.getCommands()) {
                        rootPlugin.getServer().getPluginCommand(commandToEnable.command).setExecutor(commandToEnable.executor);
                    }
                }
                if (focusedFeature.containsListeners()) {
                    if (!focusedFeature.featureRegistered()) {
                        for (Listener listener : focusedFeature.getListeners()) {
                            rootPlugin.getServer().getPluginManager().registerEvents(listener, rootPlugin);
                        }
                    }
                }
                focusedFeature.markRegistrationComplete();
            } else {
                rootPlugin.getLogger().info("Skipped registration for " + featureName + " as it is already registered");
            }
            this.setFeatureStatus(featureName, ENABLED);
            return true;
        } else {
            return false;
        }
    }

    private int findFeatureIndexByName(String featureName) {
        for(int i = 0; i < features.size(); i++) {
            if(features.get(i).getFeatureName().equals(featureName)) {
                return i;
            }
        }
        return UNIDENTIFIED_FEATURE;
    }

}

