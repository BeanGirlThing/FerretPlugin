package life.ferret.ferretPlugin.FerretCoreTools;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class featureStatus {

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

    public featureStatus() {
        this.addFeature(this.ITEM_ECO, false);
        this.addFeature(this.ADMIN_COMMAND_BROADCASTER, false);
        this.addFeature(this.ADMIN_TOOLBOX, false);
        this.addFeature(this.VAULT_API, true);
        this.addFeature(this.PLAYER_TOOLBOX, false);
        this.addFeature(this.NBT_API, true);
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

    private int findFeatureIndexByName(String featureName) {
        for(int i = 0; i < features.size(); i++) {
            if(features.get(i).getFeatureName().equals(featureName)) {
                return i;
            }
        }
        return UNIDENTIFIED_FEATURE;
    }

}

