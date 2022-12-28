package life.ferret.ferretPlugin.FerretCoreTools;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class featureIsDisabledCommandFallback implements CommandExecutor {

    private String disabledMessage = ChatColor.RED + "Feature is currently disabled";

    public featureIsDisabledCommandFallback(String disabledMessage) { // Leave as null for default message
        if(disabledMessage != null) {
            this.disabledMessage = disabledMessage;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(disabledMessage);
        return true;
    }


}
