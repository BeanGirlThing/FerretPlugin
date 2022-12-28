package life.ferret.ferretPlugin.AdminCommandBroadcaster.commands;

import life.ferret.ferretPlugin.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class checkAllowListCommand implements CommandExecutor {

    private List<String> allowList;

    private final String name = main.featurecontroller.ADMIN_COMMAND_BROADCASTER;

    private final String disabledMessage = "!!! This feature is currently disabled, please pester the admins !!!";

    public checkAllowListCommand(List<String> allowList) {
        this.allowList = allowList;

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (main.featurecontroller.continuePermitted(this.name,commandSender,this.disabledMessage)) {
            commandSender.sendMessage("Current ignored commands: " + this.allowList.toString());
            return true;
        }
        return false;
    }

}
