package life.ferret.ferretPlugin.AdminToolbox.commands;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
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

public class unstashCommand implements CommandExecutor {
    private fileController stashFileController;

    private final String name = main.featurecontroller.ADMIN_TOOLBOX;
    private final String disabledMessage = "Feature is currently disabled";

    public unstashCommand(Plugin rootPlugin) {
        stashFileController = new fileController(rootPlugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (main.featurecontroller.continuePermitted(name, commandSender, disabledMessage)) {
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
                            for (String item : playerStash.inventory) {
                                if (item != null) {
                                    tmpItemStack.add(NBT.itemStackFromNBT(NBT.parseNBT(item)));
                                } else {
                                    tmpItemStack.add(null);
                                }
                            }

                            ArrayList<ItemStack> tmpArmourStack = new ArrayList<>();
                            for (String item : playerStash.armourContents) {
                                if (item != null) {
                                    tmpArmourStack.add(NBT.itemStackFromNBT(NBT.parseNBT(item)));
                                } else {
                                    tmpArmourStack.add(null);
                                }
                            }

                            playerInventory.setStorageContents(tmpItemStack.toArray(ItemStack[]::new));
                            playerInventory.setArmorContents(tmpArmourStack.toArray(ItemStack[]::new));
                            playerInventory.setItemInOffHand(NBT.itemStackFromNBT(NBT.parseNBT(playerStash.offhandItem)));
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
        return false;
    }
}
