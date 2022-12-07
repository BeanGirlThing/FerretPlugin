package life.ferret.ferretPlugin;

import life.ferret.ferretPlugin.AdminCommandBroadcaster.commands.checkAllowListCommand;
import life.ferret.ferretPlugin.AdminCommandBroadcaster.listeners.eventListener;
import life.ferret.ferretPlugin.AdminToolbox.commands.stashCommand;
import life.ferret.ferretPlugin.AdminToolbox.commands.unstashCommand;
import life.ferret.ferretPlugin.FerretCoreTools.commands.ferretCommand;
import life.ferret.ferretPlugin.FerretCoreTools.featureIsDisabledCommandFallback;
import life.ferret.ferretPlugin.FerretCoreTools.featureStatus;
import life.ferret.ferretPlugin.ItemEco.commands.depositCommand;
import life.ferret.ferretPlugin.ItemEco.commands.withdrawCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {

    private Economy econ = null;
    private boolean economyFeatures = true;

    private featureStatus pluginStatus;

    private CommandExecutor featureIsDisabledCommandFallback;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        pluginStatus = new featureStatus();

        featureIsDisabledCommandFallback = new featureIsDisabledCommandFallback();

        setupVault();

        if(economyFeatures){
            enableItemEco();
        }

        enableAdminCommandBroadcaster();
        enableAdminToolbox();
        enableCoreTools();
    }

    @Override
    public void onDisable() {
        disableAdminCommandBroadcaster();
        disableAdminToolbox();

        if(economyFeatures) {
            disableItemEco();
        }

        disableCoreTools();
    }

    private void enableCoreTools() {
        CommandExecutor ferretCommand = new ferretCommand(pluginStatus, this);
        this.getCommand("ferret").setExecutor(ferretCommand);
        this.getLogger().info("Core tools enabled");
    }

    private void setupVault() {
        if(!vaultSetupEconomy()) {
            economyFeatures = false;
            getLogger().severe("Unable to hook to economy system, marking vault API as failed");
            pluginStatus.setFeatureStatus(pluginStatus.VAULT_API, pluginStatus.FAILED);
        } else {
            pluginStatus.setFeatureStatus(pluginStatus.VAULT_API, pluginStatus.ENABLED);
        }
    }

    private boolean vaultSetupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void enableItemEco() {
        Material currencyItemType = Material.matchMaterial(getConfig().getString("ItemEco.currency-item"));
        CommandExecutor depositCommand = new depositCommand(currencyItemType, getConfig().getInt("ItemEco.value-of-currency-item"),econ,this);
        CommandExecutor withdrawCommand = new withdrawCommand(currencyItemType, getConfig().getInt("ItemEco.value-of-currency-item"),econ,this);
        if(getConfig().getBoolean("ItemEco.enabled") || pluginStatus.getFeatureStatus(pluginStatus.VAULT_API) == pluginStatus.ENABLED) {
            if(currencyItemType == null) {
                getLogger().warning("Disabling ItemEco as item specified in config cannot be found");
                pluginStatus.setFeatureStatus(pluginStatus.ITEM_ECO, pluginStatus.DISABLED);
            } else {
                this.getCommand("deposit").setExecutor(depositCommand);
                this.getCommand("withdraw").setExecutor(withdrawCommand);
                getLogger().info("ItemEco is now enabled");
                pluginStatus.setFeatureStatus(pluginStatus.ITEM_ECO, pluginStatus.ENABLED);
            }
        } else {
            if(pluginStatus.getFeatureStatus(pluginStatus.VAULT_API) != pluginStatus.ENABLED) {
                getLogger().warning("ItemEco has been disabled due to Vault API status FAILED");
            } else {
                getLogger().info("ItemEco disabled by config");
            }
            pluginStatus.setFeatureStatus(pluginStatus.ITEM_ECO, pluginStatus.DISABLED);
        }
        if(pluginStatus.getFeatureStatus(pluginStatus.ITEM_ECO) != pluginStatus.ENABLED) {
            this.getCommand("deposit").setExecutor(featureIsDisabledCommandFallback);
            this.getCommand("withdraw").setExecutor(featureIsDisabledCommandFallback);
        }

    }
    public void enableAdminCommandBroadcaster() {
        CommandExecutor checkAllowListCommand = new checkAllowListCommand(getConfig().getStringList("AdminCommandBroadcaster.ignored-commands"), pluginStatus);
        Listener commandListener = new eventListener(getConfig().getStringList("AdminCommandBroadcaster.ignored-commands"));
        this.getCommand("allowedcommands").setExecutor(checkAllowListCommand);
        if (getConfig().getBoolean("AdminCommandBroadcaster.enabled") || pluginStatus.getFeatureStatus(pluginStatus.ADMIN_COMMAND_BROADCASTER) != pluginStatus.ENABLED) {
            getServer().getPluginManager().registerEvents(commandListener, this);
            getLogger().info("AdminCommandBroadcaster is now enabled");
            pluginStatus.setFeatureStatus(pluginStatus.ADMIN_COMMAND_BROADCASTER, pluginStatus.ENABLED);
        } else {
            if(!getConfig().getBoolean("AdminCommandBroadcaster.enabled")) {
                getLogger().info("AdminCommandBroadcaster disabled by config");
            }
            pluginStatus.setFeatureStatus(pluginStatus.ADMIN_COMMAND_BROADCASTER, pluginStatus.DISABLED);
        }


    }

    public void enableAdminToolbox() {
        CommandExecutor stashCommand = new stashCommand(this);
        CommandExecutor unstashCommand = new unstashCommand(this);

        if (getConfig().getBoolean("AdminToolbox.enabled") || pluginStatus.getFeatureStatus(pluginStatus.ADMIN_TOOLBOX) != pluginStatus.ENABLED) {
            this.getCommand("stash").setExecutor(stashCommand);
            this.getCommand("unstash").setExecutor(unstashCommand);
            getLogger().info("AdminToolbox is now enabled");
            pluginStatus.setFeatureStatus(pluginStatus.ADMIN_TOOLBOX, pluginStatus.ENABLED);
        } else {
            if(!getConfig().getBoolean("AdminToolbox.enabled")) {
                getLogger().info("AdminToolbox disabled by config");
            }
            this.getCommand("stash").setExecutor(featureIsDisabledCommandFallback);
            this.getCommand("unstash").setExecutor(featureIsDisabledCommandFallback);
            pluginStatus.setFeatureStatus(pluginStatus.ADMIN_TOOLBOX, pluginStatus.DISABLED);
        }
    }

    public void disableCoreTools() {
        getLogger().info("Core tools disabled");
    }

    public void disableAdminCommandBroadcaster() {
        pluginStatus.setFeatureStatus(pluginStatus.ADMIN_COMMAND_BROADCASTER, pluginStatus.DISABLED);
        getLogger().info("AdminCommandBroadcaster is now disabled");
    }
    public void disableAdminToolbox() {
        pluginStatus.setFeatureStatus(pluginStatus.ADMIN_TOOLBOX, pluginStatus.DISABLED);
        getLogger().info("AdminToolbox is now disabled");
    }

    public void disableItemEco() {
        pluginStatus.setFeatureStatus(pluginStatus.ITEM_ECO, pluginStatus.DISABLED);
        getLogger().info("ItemEco is now disabled");
    }

}
