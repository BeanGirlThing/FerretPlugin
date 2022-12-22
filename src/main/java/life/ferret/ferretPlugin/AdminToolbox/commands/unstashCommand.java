package life.ferret.ferretPlugin.AdminToolbox.commands;

import life.ferret.ferretPlugin.AdminToolbox.fileController;
import life.ferret.ferretPlugin.AdminToolbox.stash;
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

public class unstashCommand implements CommandExecutor {
    fileController stashFileController;

    public unstashCommand(Plugin rootPlugin) {
        stashFileController = new fileController(rootPlugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("Console cannot use this command");
        } else {
            if (commandSender.hasPermission("FerretPlugin.AdminToolbox.unstash")) {
                Player commandUserPlayerObject = Bukkit.getServer().getPlayer(commandSender.getName());
                if (commandUserPlayerObject != null) {
                    try {
                        stash playerStash = stashFileController.getStashData(commandSender.getName());
                        PlayerInventory playerInventory = commandUserPlayerObject.getInventory();
                        playerInventory.clear();
                        commandUserPlayerObject.updateInventory();

                        ArrayList<ItemStack> tmpItemStack = new ArrayList<>();
                        for (Map<String, Object> item : playerStash.inventory) {
                            if (item != null) {
                                tmpItemStack.add(ItemStack.deserialize(item));
                            } else {
                                tmpItemStack.add(null);
                            }
                        }

                        ArrayList<ItemStack> tmpArmourStack = new ArrayList<>();
                        for (Map<String, Object> item : playerStash.armourContents) {
                            if (item != null) {
                                tmpArmourStack.add(ItemStack.deserialize(item));
                            } else {
                                tmpArmourStack.add(null);
                            }
                        }

                        playerInventory.setStorageContents(tmpItemStack.toArray(ItemStack[]::new));
                        playerInventory.setArmorContents(tmpArmourStack.toArray(ItemStack[]::new));
                        playerInventory.setItemInOffHand(ItemStack.deserialize(playerStash.offhandItem));
                        commandUserPlayerObject.setHealth(playerStash.health);
                        commandUserPlayerObject.setTotalExperience(playerStash.experience);
                        commandUserPlayerObject.teleport(Location.deserialize(playerStash.location));
                        commandUserPlayerObject.updateInventory();
                        commandUserPlayerObject.setGameMode(GameMode.SURVIVAL);
                        commandUserPlayerObject.sendMessage(ChatColor.GREEN + "Stash rollback complete");
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return true;
        }
        return true;
    }
}
