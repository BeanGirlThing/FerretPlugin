package life.ferret.ferretPlugin.FerretCoreTools.commands;

import life.ferret.ferretPlugin.FerretCoreTools.featureStatus;
import life.ferret.ferretPlugin.FerretCoreTools.statusIndicator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ferretCommand implements CommandExecutor {

    private featureStatus status;
    private Plugin rootPlugin;

    private String statusMessage;

    public ferretCommand(featureStatus status, Plugin rootPlugin) {
        this.status = status;
        this.rootPlugin = rootPlugin;

        StringBuilder authorList = new StringBuilder();
        for(String name : rootPlugin.getDescription().getAuthors()) {
            authorList.append(name).append(" ");
        }

        StringBuilder colouredFeatureList = new StringBuilder();
        StringBuilder colouredDependenciesList = new StringBuilder();

        for(statusIndicator feature : status.features) {
            if(!feature.isDependency()) {
                colouredFeatureList.append(status.returnColourIndicatedFeatureName(feature.getFeatureName())).append(" ");
            } else {
                colouredDependenciesList.append(status.returnColourIndicatedFeatureName(feature.getFeatureName())).append(" ");
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
            sender.sendMessage(statusMessage);
        }

        return true;
    }
}
