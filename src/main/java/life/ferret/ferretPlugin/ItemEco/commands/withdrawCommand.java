package life.ferret.ferretPlugin.ItemEco.commands;

import life.ferret.ferretPlugin.ItemEco.helpers;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class withdrawCommand implements CommandExecutor {


    private helpers helper;
    private Economy econ;
    private Plugin rootPlugin;

    public withdrawCommand(Material currencyItem, int currencyItemValue, Economy econ, Plugin rootPlugin) {
        helper = new helpers(currencyItem, currencyItemValue);
        this.econ = econ;
        this.rootPlugin = rootPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            return false;
        }

        int requestWithdrawAmount = helper.returnUsableInteger(args[0]);
        if(requestWithdrawAmount % helper.getValueOfCurrencyItem() != 0) {
            sender.sendMessage("You cannot withdraw a fraction of an item, the item is worth "+ helper.getValueOfCurrencyItem());
            return true;
        }

        if(requestWithdrawAmount < helper.getValueOfCurrencyItem()) {
            sender.sendMessage("You cannot withdraw less than the value of the item");
            return true;
        }

        Player commandUserPlayerObject = Bukkit.getServer().getPlayer(sender.getName());
        int playerBalance = (int) econ.getBalance((OfflinePlayer) sender);

        int maximumPossibleWithdraw = helper.withdrawTotalPossible(playerBalance);

        if(maximumPossibleWithdraw < 1) {
            sender.sendMessage("You do not have sufficient funds to withdraw anything");
            return true;
        }

        if(requestWithdrawAmount > maximumPossibleWithdraw) {
            sender.sendMessage("You do not have sufficient funds to withdraw the quantity of which you are requesting");
        }

        int actualItemCountFromWithdraw = helper.getWithdrawItemCount(requestWithdrawAmount);
        ArrayList<ItemStack> itemsToProvide = new ArrayList<>();
        int stackTotal = (int) Math.floor(actualItemCountFromWithdraw / 64);
        if(stackTotal == 0) {
            ItemStack tmpItemStack = new ItemStack(helper.getCurrencyItem());
            tmpItemStack.setAmount(actualItemCountFromWithdraw);
            itemsToProvide.add(tmpItemStack);
        } else {
            for(int i = 0; i <= stackTotal; i++) {
                ItemStack tmpItemStack = new ItemStack(helper.getCurrencyItem());
                tmpItemStack.setAmount(64);
                itemsToProvide.add(tmpItemStack);
            }
            ItemStack tmpItemStack = new ItemStack(helper.getCurrencyItem());
            tmpItemStack.setAmount(actualItemCountFromWithdraw - (64 * stackTotal));
            itemsToProvide.add(tmpItemStack);

        }
        ItemStack[] itemsToProvideArray = itemsToProvide.toArray(ItemStack[]::new);
        int emptySlotsInInventory = 0;

        for(ItemStack slot : commandUserPlayerObject.getInventory().getStorageContents()) {
            if(slot == null) {
                emptySlotsInInventory += 1;
            }
        }

        if(emptySlotsInInventory < itemsToProvideArray.length) {
            sender.sendMessage("There are not adequate empty slots in your inventory to perform this operation, you need "+ itemsToProvideArray.length);
            return true;
        }

        EconomyResponse response = econ.withdrawPlayer((OfflinePlayer) sender, requestWithdrawAmount);

        if(response.transactionSuccess()) {

            ArrayList<ItemStack> updatedInventory = new ArrayList<>();
            int stacksDone = 0;

            for (ItemStack itemStack : commandUserPlayerObject.getInventory().getStorageContents()) {
                if(itemStack != null) {
                    updatedInventory.add(itemStack);
                } else {
                    if(stacksDone == itemsToProvideArray.length) {
                        updatedInventory.add(null);
                    } else {
                        updatedInventory.add(itemsToProvideArray[stacksDone]);
                        stacksDone++;
                    }
                }
            }

            commandUserPlayerObject.getInventory().setStorageContents(updatedInventory.toArray(ItemStack[]::new));
            commandUserPlayerObject.updateInventory();
            sender.sendMessage("Success! I have added "+ actualItemCountFromWithdraw + " items to your inventory and withdrawn "+ requestWithdrawAmount +" from your account");
            return true;
        } else {
            rootPlugin.getLogger().warning("Withdraw transaction failed!!! " + response);
            sender.sendMessage("Sorry! It appears something has gone wrong on my end, your balance should not have changed, please go tell an admin");
            return true;
        }
    }
}
