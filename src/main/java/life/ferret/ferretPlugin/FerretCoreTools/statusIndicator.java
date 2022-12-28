package life.ferret.ferretPlugin.FerretCoreTools;

import org.bukkit.event.Listener;

public class statusIndicator {

    private int status = 2;
    private String featureName;
    private boolean isDependency;
    private commandSkeleton[] commands;
    private boolean containsListeners = false;
    private boolean registrationComplete = false;
    private Listener[] listeners;
    private String customDisabledMessage = null;

    public statusIndicator(String featureName, boolean isDependency) {
        this.featureName = featureName;
        this.isDependency = isDependency;
    }

    public void markRegistrationComplete() {
        registrationComplete = true;
    }

    public boolean featureRegistered() {
        return registrationComplete;
    }

    public String getCustomDisabledMessage() {
        return customDisabledMessage;
    }

    public void setCustomDisabledMessage(String customDisabledMessage) {
        this.customDisabledMessage = customDisabledMessage;
    }

    public commandSkeleton[] getCommands() {
        return commands;
    }

    public void setCommands(commandSkeleton[] commandExecutors) {
        this.commands = commandExecutors;
    }

    public boolean containsListeners() {
        return containsListeners;
    }

    public Listener[] getListeners() {
        return listeners;
    }

    public void setListeners(Listener[] listeners) {
        this.listeners = listeners;
        this.containsListeners = true;
    }

    public commandSkeleton[] getFeatureCommands() {
        return commands;
    }

    public boolean isDependency() {
        return isDependency;
    }

    public String getFeatureName() {
        return featureName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
