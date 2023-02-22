package life.ferret.ferretPlugin.AdminCommandBroadcaster.listeners;

import life.ferret.ferretPlugin.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class commandBroadcasterEventListener implements Listener {
    private List<String> ignoredCommands;
    private final String name = main.featurecontroller.ADMIN_COMMAND_BROADCASTER;

    public commandBroadcasterEventListener(List<String> ignoredCommands) {
        this.ignoredCommands = ignoredCommands;
    }

    @EventHandler
    public void onPlayerCommandUsage(PlayerCommandPreprocessEvent event) {
        if(main.featurecontroller.continuePermitted(name, null, null)) {
            Player commandUser = event.getPlayer();
            String[] command = event.getMessage().toLowerCase().split(" ");
            if (commandUser.isOp() || commandUser.hasPermission("FerretPlugin.BroadcastCommands")) {
                if (!this.ignoredCommands.contains(command[0])) {
                    Bukkit.broadcastMessage("Op user " + ChatColor.RED + commandUser.getName() + ChatColor.RESET + " has just run the command: " + ChatColor.GREEN + event.getMessage() + ChatColor.RESET);
                }
            }
        }
    }

}
