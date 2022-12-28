package life.ferret.ferretPlugin.ItemEco.commands;

import life.ferret.ferretPlugin.helpers;
import life.ferret.ferretPlugin.main;
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

public class withdrawCommand implements CommandExecutor {


    private helpers helper;
    private Economy econ;
    private Plugin rootPlugin;
    private List<String> ignoredWorlds;
    private final String name = main.featurecontroller.ITEM_ECO;
    private final String disabledMessage = "Feature is currently disabled";

    public withdrawCommand(Economy econ, Plugin rootPlugin, helpers helper) {
        this.helper = helper;
        this.econ = econ;
        this.rootPlugin = rootPlugin;
        ignoredWorlds = rootPlugin.getConfig().getStringList("ItemEco.ignored-worlds");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (main.featurecontroller.continuePermitted(name,sender,disabledMessage)) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Console cannot use this command");
            } else {

                if (args.length != 1) {
                    return false;
                }

                int requestWithdrawAmount = Optional.ofNullable(helper.returnUsableInteger(args[0])).orElse(-1);

                Player commandUserPlayerObject = helper.getPlayerObjectFromName(sender.getName());
                int playerBalance = (int) econ.getBalance((OfflinePlayer) sender);

                if (ignoredWorlds.contains(commandUserPlayerObject.getWorld().getName())) {
                    sender.sendMessage("You cannot use this here");
                    return true;
                }

                int maximumPossibleWithdraw = helper.withdrawTotalPossible(playerBalance);

                if (maximumPossibleWithdraw < 1) {
                    sender.sendMessage("You do not have sufficient funds to withdraw anything");
                    return true;
                }

                int playerBalanceAfterTransaction = playerBalance - (requestWithdrawAmount * helper.getValueOfCurrencyItem());

                if (requestWithdrawAmount > maximumPossibleWithdraw || playerBalanceAfterTransaction < 0) {
                    sender.sendMessage("You do not have sufficient funds to withdraw the quantity of which you are requesting");
                    return true;
                }


                ArrayList<ItemStack> itemsToProvide = new ArrayList<>();
                int stackTotal = (int) Math.floor(requestWithdrawAmount / 64);
                if (stackTotal == 0) {
                    ItemStack tmpItemStack = new ItemStack(helper.getCurrencyItem());
                    tmpItemStack.setAmount(requestWithdrawAmount);
                    itemsToProvide.add(tmpItemStack);
                } else {
                    for (int i = 0; i < stackTotal; i++) {
                        ItemStack tmpItemStack = new ItemStack(helper.getCurrencyItem());
                        tmpItemStack.setAmount(64);
                        itemsToProvide.add(tmpItemStack);
                    }
                    ItemStack tmpItemStack = new ItemStack(helper.getCurrencyItem());
                    tmpItemStack.setAmount(requestWithdrawAmount - (64 * stackTotal));
                    itemsToProvide.add(tmpItemStack);

                }
                ItemStack[] itemsToProvideArray = itemsToProvide.toArray(ItemStack[]::new);
                int emptySlotsInInventory = 0;

                for (ItemStack slot : commandUserPlayerObject.getInventory().getStorageContents()) {
                    if (slot == null) {
                        emptySlotsInInventory += 1;
                    }
                }

                if (emptySlotsInInventory < itemsToProvideArray.length) {
                    sender.sendMessage("There are not adequate empty slots in your inventory to perform this operation, you need " + itemsToProvideArray.length);
                    return true;
                }

                EconomyResponse response = econ.withdrawPlayer((OfflinePlayer) sender, requestWithdrawAmount * helper.getValueOfCurrencyItem());

                if (response.transactionSuccess()) {

                    ArrayList<ItemStack> updatedInventory = new ArrayList<>();
                    int stacksDone = 0;

                    for (ItemStack itemStack : commandUserPlayerObject.getInventory().getStorageContents()) {
                        if (itemStack != null) {
                            updatedInventory.add(itemStack);
                        } else {
                            if (stacksDone == itemsToProvideArray.length) {
                                updatedInventory.add(null);
                            } else {
                                updatedInventory.add(itemsToProvideArray[stacksDone]);
                                stacksDone++;
                            }
                        }
                    }

                    commandUserPlayerObject.getInventory().setStorageContents(updatedInventory.toArray(ItemStack[]::new));
                    commandUserPlayerObject.updateInventory();
                    sender.sendMessage("Success! I have added " + requestWithdrawAmount + " items to your inventory and withdrawn " + requestWithdrawAmount * helper.getValueOfCurrencyItem() + " from your account");
                    return true;
                } else {
                    rootPlugin.getLogger().warning("Withdraw transaction failed!!! " + response);
                    sender.sendMessage("Sorry! It appears something has gone wrong on my end, your balance should not have changed, please go tell an admin");
                    return true;
                }
            }
            return true;
        }
        return false;
    }
}
