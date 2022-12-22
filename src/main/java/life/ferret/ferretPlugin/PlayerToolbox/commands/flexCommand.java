package life.ferret.ferretPlugin.PlayerToolbox.commands;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import life.ferret.ferretPlugin.helpers;
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

    Plugin rootPlugin;
    helpers helper;

    public flexCommand(Plugin rootPlugin, helpers helper) {
        this.rootPlugin = rootPlugin;
        this.helper = helper;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Console cannot use this command");
        } else {
            Player commandUserPlayerObject = helper.getPlayerObjectFromName(sender.getName());
            ItemStack itemToFlex = commandUserPlayerObject.getInventory().getItemInMainHand();
            ReadWriteNBT itemNBT = NBT.itemStackToNBT(itemToFlex);
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(itemNBT.toString())});
            TextComponent component;
            if(Objects.requireNonNull(itemToFlex.getItemMeta()).hasDisplayName()) {
                component = new TextComponent("You are being flexed on by " + sender.getName() + " with " + ChatColor.LIGHT_PURPLE + " [" + itemToFlex.getItemMeta().getDisplayName()+ "]");
            } else if (itemToFlex.getItemMeta().hasLocalizedName()) {
                component = new TextComponent("You are being flexed on by " + sender.getName() + " with " + itemToFlex.getAmount() + " " + itemToFlex.getItemMeta().getLocalizedName());
            } else {
                component = new TextComponent("You are being flexed on by " + sender.getName() + " with " + itemToFlex.getAmount() + " " + itemToFlex.getType().name());
            }
            component.setHoverEvent(event);
            rootPlugin.getServer().spigot().broadcast(component);
        }
        return true;
    }
}
