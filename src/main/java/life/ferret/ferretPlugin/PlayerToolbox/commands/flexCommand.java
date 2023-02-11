package life.ferret.ferretPlugin.PlayerToolbox.commands;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import life.ferret.ferretPlugin.helpers;
import life.ferret.ferretPlugin.main;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class flexCommand implements CommandExecutor {

    private Plugin rootPlugin;
    private helpers helper;
    private final String name = main.featurecontroller.PLAYER_TOOLBOX;
    private final String disabledMessage = "Feature is currently disabled";

    public flexCommand(Plugin rootPlugin, helpers helper) {
        this.rootPlugin = rootPlugin;
        this.helper = helper;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (main.featurecontroller.continuePermitted(name,sender,disabledMessage)) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Console cannot use this command");
            } else {
                Player commandUserPlayerObject = helper.getPlayerObjectFromName(sender.getName());
                ItemStack itemToFlex = commandUserPlayerObject.getInventory().getItemInMainHand();
                ReadWriteNBT itemNBT = NBT.itemStackToNBT(itemToFlex);
                HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(itemNBT.toString())});
                TextComponent component;
                if (Objects.requireNonNull(itemToFlex.getItemMeta()).hasDisplayName()) {
                    component = new TextComponent("You are being flexed on by " + sender.getName() + " with " + ChatColor.LIGHT_PURPLE + "[" + itemToFlex.getItemMeta().getDisplayName() + "]");
                } else {
                    component = new TextComponent("You are being flexed on by " + sender.getName() + " with " + itemToFlex.getAmount() + " " + formatItemName(itemToFlex));
                }
                component.setHoverEvent(event);
                rootPlugin.getServer().spigot().broadcast(component);
            }
            return true;
        }
        return false;
    }

    public String formatItemName(ItemStack item) {
        StringBuilder output = new StringBuilder();
        String[] itemname = item.getType().name().toLowerCase().split("_");
        for(String word : itemname) {
            String firstLetter = word.substring(0,1).toUpperCase();
            String restOfWord = word.substring(1);
            output.append(firstLetter + restOfWord + " ");
        }
        return output.substring(0,output.length()-1); // No Magic Numbers: -1 here removes the extra space at the end of the string created by concatenating words.
    }
}
