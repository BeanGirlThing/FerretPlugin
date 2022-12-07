package life.ferret.ferretPlugin.AdminCommandBroadcaster.commands;

import life.ferret.ferretPlugin.FerretCoreTools.featureStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class checkAllowListCommand implements CommandExecutor {

    private List<String> allowList;
    private featureStatus pluginStatus;

    public checkAllowListCommand(List<String> allowList, featureStatus pluginStatus) {
        this.allowList = allowList;
        this.pluginStatus = pluginStatus;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.hasPermission("FerretPlugin.AdminCommandBroadcaster.allowedcommands")) {
            if (pluginStatus.getFeatureStatus(pluginStatus.ADMIN_COMMAND_BROADCASTER) != pluginStatus.ENABLED) {
                commandSender.sendMessage(ChatColor.RED + "!!! Admin Command Broadcaster is currently disabled, go nag the admins !!!");
            }
            commandSender.sendMessage("Current ignored commands: " + this.allowList.toString());
            return true;
        } else {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command");
            return false;
        }
    }

}
