package life.ferret.ferretPlugin.PlayerToolbox.commands;

import life.ferret.ferretPlugin.helpers;
import life.ferret.ferretPlugin.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class netherCoordCalculateCommand implements CommandExecutor {

    private helpers helper;

    private final String name = main.featurecontroller.PLAYER_TOOLBOX;
    private final String disabledMessage = "Feature is currently disabled";

    public netherCoordCalculateCommand(helpers helper) {
        this.helper = helper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (main.featurecontroller.continuePermitted(name,sender,disabledMessage)) {
            Player commandUserPlayerObject = helper.getPlayerObjectFromName(sender.getName());
            Integer inputX = 0;
            Integer inputZ = 0;
            double outputX = 0;
            double outputZ = 0;

            if (args.length == 0) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Console cannot use current position, to calculate provide coordinates");
                    return true;
                } else {
                    inputX = (int) Math.floor(commandUserPlayerObject.getLocation().getX());
                    inputZ = (int) Math.floor(commandUserPlayerObject.getLocation().getZ());
                }
            } else {
                if (args.length != 2) {
                    sender.sendMessage("You must input only 2 numbers, leave entirely blank to calculate current position");
                    return true;
                } else {
                    inputX = helper.returnUsableInteger(args[0]);
                    inputZ = helper.returnUsableInteger(args[1]);

                    if (inputX == null || inputZ == null) {
                        sender.sendMessage("One or more arguments are not integers");
                        return true;
                    }
                }
            }
            if (inputX == 0) {
                outputX = 0;
            } else {
                outputX = inputX / 8;
            }
            if (inputZ == 0) {
                outputZ = 0;
            } else {
                outputZ = inputZ / 8;
            }

            sender.sendMessage("The nether co-ordinate equivalent of " + inputX + " " + inputZ + " is: " + outputX + " " + outputZ);

            return true;
        }
        return false;
    }
}
