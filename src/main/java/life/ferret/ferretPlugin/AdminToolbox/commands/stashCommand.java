package life.ferret.ferretPlugin.AdminToolbox.commands;

import life.ferret.ferretPlugin.AdminToolbox.fileController;
import life.ferret.ferretPlugin.AdminToolbox.stash;
import life.ferret.ferretPlugin.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class stashCommand implements CommandExecutor {

    private fileController stashFileController;

    private final String disabledMessage = "Feature is currently disabled";

    private final String name = main.featurecontroller.ADMIN_TOOLBOX;

    public stashCommand(Plugin rootPlugin) {
        stashFileController = new fileController(rootPlugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (main.featurecontroller.continuePermitted(name, commandSender, disabledMessage)) {
            if (commandSender instanceof ConsoleCommandSender) {
                commandSender.sendMessage("Console cannot use this command");
            } else {
                Player commandUserPlayerObject = Bukkit.getServer().getPlayer(commandSender.getName());
                if (commandUserPlayerObject != null) {
                    PlayerInventory inventory = commandUserPlayerObject.getInventory();
                    double health = commandUserPlayerObject.getHealth();
                    Location location = commandUserPlayerObject.getLocation();
                    int experience = commandUserPlayerObject.getTotalExperience();

                    ArrayList<Map<String, Object>> tmpInventoryArray = new ArrayList<>();

                    for (ItemStack item : commandUserPlayerObject.getInventory().getStorageContents()) {
                        if (item != null) {
                            tmpInventoryArray.add(item.serialize());
                        } else {
                            tmpInventoryArray.add(null);
                        }
                    }

                    ArrayList<Map<String, Object>> tmpArmourArray = new ArrayList<>();

                    for (ItemStack item : commandUserPlayerObject.getInventory().getArmorContents()) {
                        if (item != null) {
                            tmpArmourArray.add(item.serialize());
                        } else {
                            tmpArmourArray.add(null);
                        }
                    }

                    @SuppressWarnings("unchecked")
                    stash playerStash = new stash(health, tmpInventoryArray.toArray(Map[]::new), location.serialize(), experience, inventory.getItemInOffHand().serialize(), tmpArmourArray.toArray(Map[]::new));
                    try {
                        stashFileController.createNewStash(commandSender.getName(), playerStash);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    inventory.clear();
                    commandUserPlayerObject.setGameMode(GameMode.CREATIVE);
                    commandUserPlayerObject.updateInventory();
                    commandUserPlayerObject.sendMessage(ChatColor.GREEN + "Stash store complete");
                    return true;
                }
                return true;
            }
            return true;
        }
        return false;
    }
}

