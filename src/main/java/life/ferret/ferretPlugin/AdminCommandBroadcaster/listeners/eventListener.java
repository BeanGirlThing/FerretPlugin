package life.ferret.ferretPlugin.AdminCommandBroadcaster.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class eventListener implements Listener {
    private List<String> ignoredCommands;

    public eventListener(List<String> ignoredCommands) {
        this.ignoredCommands = ignoredCommands;
    }

    @EventHandler
    public void onPlayerCommandUsage(PlayerCommandPreprocessEvent event) {
        Player commandUser = event.getPlayer();
        String[] command = event.getMessage().toLowerCase().split(" ");
        if(commandUser.isOp() && !this.ignoredCommands.contains(command[0])) {
            Bukkit.broadcastMessage("Op user " + ChatColor.RED + commandUser.getName() + ChatColor.RESET + " has just run the command: " + ChatColor.GREEN + event.getMessage() + ChatColor.RESET);
        }
    }

}
