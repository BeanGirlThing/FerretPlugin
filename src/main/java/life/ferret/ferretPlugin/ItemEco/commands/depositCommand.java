package life.ferret.ferretPlugin.ItemEco.commands;

import life.ferret.ferretPlugin.helpers;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class depositCommand implements CommandExecutor {

    private helpers helper;
    private Economy econ;
    private Plugin rootPlugin;
    private List<String> ignoredWorlds;

    public depositCommand(Economy econ, Plugin rootPlugin, helpers helper) {
        this.helper = helper;
        this.econ = econ;
        this.rootPlugin = rootPlugin;
        ignoredWorlds = rootPlugin.getConfig().getStringList("ItemEco.ignored-worlds");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Console cannot use this command");
        } else {
            if (args.length != 1) {
                return false;
            }

            int requestDepositQuantity = Optional.ofNullable(helper.returnUsableInteger(args[0])).orElse(-1);
            if (requestDepositQuantity < 1) {
                sender.sendMessage("You cannot deposit less than one item");
                return true;
            }

            Player commandUserPlayerObject = helper.getPlayerObjectFromName(sender.getName());
            int currencyItemCount = 0;
            rootPlugin.getLogger().info("Player is using deposit in world " + commandUserPlayerObject.getWorld().getName());
            rootPlugin.getLogger().info("Ignored worlds for eco are: " + ignoredWorlds.toString());

            if (ignoredWorlds.contains(commandUserPlayerObject.getWorld().getName())) {
                sender.sendMessage("You cannot use this here");
                return true;
            }

            for (ItemStack itemStack : commandUserPlayerObject.getInventory().getStorageContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == helper.getCurrencyItem()) {
                        currencyItemCount += itemStack.getAmount();
                    }
                }
            }

            if (currencyItemCount < requestDepositQuantity) {
                sender.sendMessage("You do not have sufficient quantities of the item you are trying to deposit");
                return true;
            }

            EconomyResponse response = econ.depositPlayer((OfflinePlayer) sender, requestDepositQuantity * helper.getValueOfCurrencyItem());

            int totalRemoved = 0;

            if (response.transactionSuccess()) {

                ArrayList<ItemStack> updatedInventory = new ArrayList<>();

                for (ItemStack itemStack : commandUserPlayerObject.getInventory().getStorageContents()) {
                    if (itemStack != null) {
                        if (itemStack.getType() == helper.getCurrencyItem() && totalRemoved != requestDepositQuantity) {
                            int amountInStack = itemStack.getAmount();
                            int plannedAndRemovedDelta = requestDepositQuantity - totalRemoved;
                            if (amountInStack < plannedAndRemovedDelta) {
                                itemStack.setAmount(0);
                                totalRemoved += amountInStack;
                            } else {
                                itemStack.setAmount(amountInStack - plannedAndRemovedDelta);
                            }
                        }
                        updatedInventory.add(itemStack);
                    }
                }
                commandUserPlayerObject.getInventory().setStorageContents(updatedInventory.toArray(ItemStack[]::new));
                commandUserPlayerObject.updateInventory();
                sender.sendMessage("Success! I have removed " + requestDepositQuantity + " of the item from your inventory and credited your account with " + requestDepositQuantity * helper.getValueOfCurrencyItem());
                return true;
            } else {
                rootPlugin.getLogger().warning("Deposit transaction failed!!! " + response.errorMessage);
                sender.sendMessage("Sorry! It appears something has gone wrong on my end, your items should still be in your inventory, please go tell an admin");
                return true;
            }
        }
        return true;
    }
}
