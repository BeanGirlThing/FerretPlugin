package life.ferret.ferretPlugin;

import life.ferret.ferretPlugin.AdminCommandBroadcaster.commands.checkAllowListCommand;
import life.ferret.ferretPlugin.AdminCommandBroadcaster.listeners.commandBroadcasterEventListener;
import life.ferret.ferretPlugin.AdminToolbox.commands.stashCommand;
import life.ferret.ferretPlugin.AdminToolbox.commands.unstashCommand;
import life.ferret.ferretPlugin.FerretCoreTools.commandSkeleton;
import life.ferret.ferretPlugin.FerretCoreTools.commands.ferretCommand;
import life.ferret.ferretPlugin.FerretCoreTools.featureController;
import life.ferret.ferretPlugin.ItemEco.commands.depositCommand;
import life.ferret.ferretPlugin.ItemEco.commands.withdrawCommand;
import life.ferret.ferretPlugin.PlayerToolbox.commands.flexCommand;
import life.ferret.ferretPlugin.PlayerToolbox.commands.netherCoordCalculateCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {

    private Economy econ = null;
    private boolean economyFeatures = true;

    public static featureController featurecontroller;

    private helpers helper;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        featurecontroller = new featureController(this);

        Material currencyItemType = Material.matchMaterial(getConfig().getString("ItemEco.currency-item"));
        helper = new helpers(currencyItemType, getConfig().getInt("ItemEco.value-of-currency-item"));

        setupVault();
        enableItemEco();

        checkForNBTAPI();
        enablePlayerToolbox();


        enableAdminCommandBroadcaster();
        enableAdminToolbox();

        enableCoreTools(); // Leave on bottom to enable last
    }

    @Override
    public void onDisable() {
        disableAdminCommandBroadcaster();
        disableAdminToolbox();

        if(economyFeatures) {
            disableItemEco();
        }
        disablePlayerToolbox();

        disableCoreTools(); // Leave on bottom to disable last
    }

    private void enablePlayerToolbox() {
        commandSkeleton netherCoordCalculateCommand = new commandSkeleton("nethercoord", new netherCoordCalculateCommand(helper, getConfig().getStringList("PlayerToolbox.NetherCoordCalculator.positional-allowed-worlds")));
        commandSkeleton flexCommand = new commandSkeleton("flex", new flexCommand(this,helper));
        featurecontroller.setCommands(featurecontroller.PLAYER_TOOLBOX, new commandSkeleton[]{netherCoordCalculateCommand,flexCommand});
        if(getConfig().getBoolean("PlayerToolbox.enabled")) {
            if(featurecontroller.getFeatureStatus(featurecontroller.NBT_API) == featurecontroller.FAILED) {
                getLogger().warning("Disabling PlayerToolbox as NBTAPI is disabled. Please install > https://www.spigotmc.org/resources/nbt-api.7939/");
                featurecontroller.disableFeature(featurecontroller.PLAYER_TOOLBOX);
            } else {
                featurecontroller.enableFeature(featurecontroller.PLAYER_TOOLBOX);
            }
        } else {
            featurecontroller.disableFeature(featurecontroller.PLAYER_TOOLBOX);
        }
    }

    private void checkForNBTAPI() {
        if (getServer().getPluginManager().getPlugin("NBTAPI") != null) {
            featurecontroller.setFeatureStatus(featurecontroller.NBT_API, featurecontroller.ENABLED);
        } else {
            featurecontroller.setFeatureStatus(featurecontroller.NBT_API, featurecontroller.DISABLED);
        }
    }

    private void enableCoreTools() {
        CommandExecutor ferretCommand = new ferretCommand(featurecontroller, this);
        this.getCommand("ferret").setExecutor(ferretCommand);
        this.getLogger().info("Core tools enabled");
    }

    private void setupVault() {
        if(!vaultSetupEconomy()) {
            economyFeatures = false;
            getLogger().severe("Unable to hook to economy system, marking vault API as failed");
            featurecontroller.setFeatureStatus(featurecontroller.VAULT_API, featurecontroller.FAILED);
        } else {
            featurecontroller.setFeatureStatus(featurecontroller.VAULT_API, featurecontroller.ENABLED);
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
        commandSkeleton depositCommand = new commandSkeleton("deposit", new depositCommand(econ,this, helper));
        commandSkeleton withdrawCommand = new commandSkeleton("withdraw", new withdrawCommand(econ,this, helper));
        featurecontroller.setCommands(featurecontroller.ITEM_ECO, new commandSkeleton[]{depositCommand,withdrawCommand});
        if(getConfig().getBoolean("ItemEco.enabled")) {
            if(featurecontroller.getFeatureStatus(featurecontroller.VAULT_API) == featurecontroller.FAILED) {
                getLogger().warning("ItemEco has been disabled due to Vault API status FAILED. Please install > https://www.spigotmc.org/resources/vault.34315/");
                featurecontroller.disableFeature(featurecontroller.ITEM_ECO);
            } else if(currencyItemType == null) {
                getLogger().warning("Disabling ItemEco as item specified in config cannot be found");
                featurecontroller.disableFeature(featurecontroller.ITEM_ECO);
            } else {
                featurecontroller.enableFeature(featurecontroller.ITEM_ECO);
                getLogger().info("ItemEco is now enabled");
            }
        } else {
            getLogger().info("ItemEco disabled by config");
            featurecontroller.disableFeature(featurecontroller.ITEM_ECO);
        }

    }
    public void enableAdminCommandBroadcaster() {
        commandSkeleton checkAllowListCommand = new commandSkeleton("allowedcommands", new checkAllowListCommand(getConfig().getStringList("AdminCommandBroadcaster.ignored-commands")));
        Listener commandListener = new commandBroadcasterEventListener(getConfig().getStringList("AdminCommandBroadcaster.ignored-commands"));
        featurecontroller.setCommands(featurecontroller.ADMIN_COMMAND_BROADCASTER, new commandSkeleton[]{checkAllowListCommand});
        featurecontroller.setListeners(featurecontroller.ADMIN_COMMAND_BROADCASTER, new Listener[]{commandListener});
        featurecontroller.setCustomDisabledMessage(featurecontroller.ADMIN_COMMAND_BROADCASTER, ChatColor.RED + "!!! Admin Command Broadcaster is currently disabled, go nag the admins !!!");

        if (getConfig().getBoolean("AdminCommandBroadcaster.enabled")) {
            getLogger().info("AdminCommandBroadcaster is now enabled");
            featurecontroller.enableFeature(featurecontroller.ADMIN_COMMAND_BROADCASTER);
        } else {
            if(!getConfig().getBoolean("AdminCommandBroadcaster.enabled")) {
                getLogger().info("AdminCommandBroadcaster disabled by config");
            }
            featurecontroller.disableFeature(featurecontroller.ADMIN_COMMAND_BROADCASTER);
        }
    }

    public void enableAdminToolbox() {
        commandSkeleton stashCommand = new commandSkeleton("stash", new stashCommand(this));
        commandSkeleton unstashCommand = new commandSkeleton("unstash", new unstashCommand(this));
        featurecontroller.setCommands(featurecontroller.ADMIN_TOOLBOX, new commandSkeleton[]{stashCommand,unstashCommand});

        if (getConfig().getBoolean("AdminToolbox.enabled") || featurecontroller.getFeatureStatus(featurecontroller.ADMIN_TOOLBOX) != featurecontroller.ENABLED) {
            featurecontroller.enableFeature(featurecontroller.ADMIN_TOOLBOX);
            getLogger().info("AdminToolbox is now enabled");
        } else {
            getLogger().info("AdminToolbox disabled by config");
            featurecontroller.disableFeature(featurecontroller.ADMIN_TOOLBOX);
        }
    }

    public void disableCoreTools() {
        getLogger().info("Core tools disabled");
    }

    public void disableAdminCommandBroadcaster() {
        featurecontroller.disableFeature(featurecontroller.ADMIN_COMMAND_BROADCASTER);
        getLogger().info("AdminCommandBroadcaster is now disabled");
    }
    public void disableAdminToolbox() {
        featurecontroller.disableFeature(featurecontroller.ADMIN_TOOLBOX);
        getLogger().info("AdminToolbox is now disabled");
    }

    public void disableItemEco() {
        featurecontroller.disableFeature(featurecontroller.ITEM_ECO);
        getLogger().info("ItemEco is now disabled");
    }

    public void disablePlayerToolbox() {
        featurecontroller.disableFeature(featurecontroller.PLAYER_TOOLBOX);
        getLogger().info("PlayerToolbox is now disabled");
    }

}
