package life.ferret.ferretPlugin.PlayerToolbox.commands;

import life.ferret.ferretPlugin.helpers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class netherCoordCalculateCommand implements CommandExecutor {

    private helpers helper;

    public netherCoordCalculateCommand(Plugin rootPlugin, helpers helper) {
        this.helper = helper;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player commandUserPlayerObject = helper.getPlayerObjectFromName(sender.getName());
        Integer inputX = 0;
        Integer inputY = 0;
        double outputX = 0;
        double outputY = 0;

        if(args.length == 0) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Console cannot use current position, to calculate provide coordinates");
            } else {
                inputX = (int) Math.floor(commandUserPlayerObject.getLocation().getX());
                inputY = (int) Math.floor(commandUserPlayerObject.getLocation().getY());
            }
        } else {
            if(args.length != 2) {
                sender.sendMessage("You must input only 2 numbers, leave entirely blank to calculate current position");
                return true;
            } else {
                inputX = helper.returnUsableInteger(args[0]);
                inputY = helper.returnUsableInteger(args[1]);

                if(inputX == null || inputY == null) {
                    sender.sendMessage("One or more arguments are not integers");
                    return true;
                } else {

                }
            }
        }
        if(inputX == 0) {
            outputX = 0;
        } else {
            outputX = inputX / 8;
        }
        if(inputY == 0) {
            outputY = 0;
        } else {
            outputY = inputY / 8;
        }

        sender.sendMessage("The nether co-ordinate equivalent of "+ inputX + " " + inputY + " is: " + outputX + " " + outputY);

        return true;
    }
}
