package life.ferret.ferretPlugin.FerretCoreTools.commands;

import life.ferret.ferretPlugin.FerretCoreTools.featureIsDisabledCommandFallback;
import life.ferret.ferretPlugin.FerretCoreTools.featureController;
import life.ferret.ferretPlugin.FerretCoreTools.statusIndicator;
import life.ferret.ferretPlugin.main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class ferretCommand implements CommandExecutor {

    private featureController featurecontroller;
    private Plugin rootPlugin;
    private String statusMessage;
    private CommandExecutor featureIsDisabledCommandFallback;

    public ferretCommand(featureController featurecontroller, Plugin rootPlugin) {
        this.featurecontroller = featurecontroller;
        this.rootPlugin = rootPlugin;
    }

    private void buildStatusMessage() {
        featureIsDisabledCommandFallback = new featureIsDisabledCommandFallback(null);

        StringBuilder authorList = new StringBuilder();
        for(String name : rootPlugin.getDescription().getAuthors()) {
            authorList.append(name).append(" ");
        }

        StringBuilder colouredFeatureList = new StringBuilder();
        StringBuilder colouredDependenciesList = new StringBuilder();

        for(statusIndicator feature : featurecontroller.features) {
            if(!feature.isDependency()) {
                colouredFeatureList.append(featurecontroller.returnColourIndicatedFeatureName(feature.getFeatureName())).append(" ");
            } else {
                colouredDependenciesList.append(featurecontroller.returnColourIndicatedFeatureName(feature.getFeatureName())).append(" ");
            }
        }

        statusMessage = """
                ==== Ferret ====
                Version:\040""" + rootPlugin.getDescription().getVersion() + """
                \nAuthors:\040""" + authorList + "\n" + rootPlugin.getDescription().getDescription() + """
                \nRun /ferret help for more information
                == Status ==
                Key:\040""" +
                ChatColor.GREEN + "ENABLED, " +
                ChatColor.YELLOW + "DISABLED, " +
                ChatColor.RED + "FAILED, " +
                ChatColor.DARK_GRAY + "UNIDENTIFIED FEATURE, " +
                ChatColor.LIGHT_PURPLE + "FAILED TO PROCESS STATUS " + ChatColor.RESET + """
                \nFeatures:\040""" + colouredFeatureList + ChatColor.RESET + """
                \nDependencies:\040""" + colouredDependenciesList + ChatColor.RESET + """
                \n============
                Thank you for using my plugin <3
                ================
                """;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            buildStatusMessage();
            sender.sendMessage(statusMessage);
        } else {
            if (args[0].equalsIgnoreCase("disable")) {
                if (args.length == 2) {
                    if (featurecontroller.disableFeature(args[1])) {
                        sender.sendMessage("Feature " + args[1] + " disabled");
                    } else {
                        sender.sendMessage("Feature " + args[1] + " not recognised.");
                        return true;
                    }
                } else {
                    sender.sendMessage("Please supply a feature name and nothing else.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("enable")) {
                if (args.length == 2) {
                    if (featurecontroller.enableFeature(args[1])) {
                        sender.sendMessage("Feature " + args[1] + " enabled");
                    } else {
                        sender.sendMessage("Feature " + args[1] + " not recognised.");
                        return true;
                    }
                } else {
                    sender.sendMessage("Please supply a feature name and nothing else.");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("reload")) {
                main.configManager.reloadConfigFromFile();
                sender.sendMessage(ChatColor.GREEN + "Reloaded the config, Hope you're having a good day :)");
            } else {
                sender.sendMessage("Command not recognised, please check syntax");
                return false;
            }
        }

        return true;
    }
}
