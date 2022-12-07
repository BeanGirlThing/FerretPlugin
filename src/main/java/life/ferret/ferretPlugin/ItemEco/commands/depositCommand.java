package life.ferret.ferretPlugin.ItemEco.commands;

import life.ferret.ferretPlugin.ItemEco.helpers;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class depositCommand implements CommandExecutor {

    private helpers helper;
    private Economy econ;
    private Plugin rootPlugin;

    public depositCommand(Material currencyItem, int currencyItemValue, Economy econ, Plugin rootPlugin) {
        helper = new helpers(currencyItem, currencyItemValue);
        this.econ = econ;
        this.rootPlugin = rootPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            return false;
        }

        int requestDepositQuantity = helper.returnUsableInteger(args[0]);
        if(requestDepositQuantity < 1) {
            sender.sendMessage("You cannot deposit less than one item");
            return true;
        }

        Player commandUserPlayerObject = Bukkit.getServer().getPlayer(sender.getName());
        int currencyItemCount = 0;

        for(ItemStack itemStack : commandUserPlayerObject.getInventory().getStorageContents()) {
            if(itemStack.getType() == helper.getCurrencyItem()) {
                currencyItemCount += itemStack.getAmount();
            }
        }

        if(currencyItemCount <= requestDepositQuantity) {
            sender.sendMessage("You do not have sufficient quantities of the item you are trying to deposit");
            return true;
        }

        EconomyResponse response = econ.bankDeposit(sender.getName(),requestDepositQuantity * helper.getValueOfCurrencyItem());

        int totalRemoved = 0;

        if(response.transactionSuccess()) {

            ArrayList<ItemStack> updatedInventory = new ArrayList<>();

            for (ItemStack itemStack : commandUserPlayerObject.getInventory().getStorageContents()) {
                if(itemStack.getType() == helper.getCurrencyItem() && totalRemoved != requestDepositQuantity) {
                    int amountInStack = itemStack.getAmount();
                    int plannedAndRemovedDelta = requestDepositQuantity - totalRemoved;
                    if(amountInStack < plannedAndRemovedDelta) {
                        itemStack.setAmount(0);
                        totalRemoved += amountInStack;
                    } else {
                        itemStack.setAmount(amountInStack - plannedAndRemovedDelta);
                    }
                }
                updatedInventory.add(itemStack);
            }
            commandUserPlayerObject.getInventory().setStorageContents(updatedInventory.toArray(ItemStack[]::new));
            commandUserPlayerObject.updateInventory();
            sender.sendMessage("Success! I have removed "+ requestDepositQuantity +" of the item from your inventory and credited your account with " + requestDepositQuantity * helper.getValueOfCurrencyItem());
            return true;
        } else {
            rootPlugin.getLogger().warning("Deposit transaction failed!!! " + response);
            sender.sendMessage("Sorry! It appears something has gone wrong on my end, your items should still be in your inventory, please go tell an admin");
            return true;
        }
    }
}
