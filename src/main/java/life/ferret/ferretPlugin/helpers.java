package life.ferret.ferretPlugin;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class helpers {

    private int valueOfCurrencyItem;
    private Material currencyItem;

    public helpers(Material currencyItem, int valueOfCurrencyItem) {
        this.valueOfCurrencyItem = valueOfCurrencyItem;
        this.currencyItem = currencyItem;
    }

    public int withdrawTotalPossible(int playerBalance) {
        float totalItems = playerBalance / (float) valueOfCurrencyItem;
        return (int) Math.floor(totalItems);
    }

    public int getWithdrawItemCount(int withdrawRequestAmount) {
        return withdrawRequestAmount / valueOfCurrencyItem;
    }

    public Player getPlayerObjectFromName(String playerName) {
        return Bukkit.getServer().getPlayer(playerName);
    }

    public Integer returnUsableInteger(String value) {
        if(NumberUtils.isNumber(value)) {
            return NumberUtils.toInt(value);
        } else {
            return null;
        }
    }

    public int getValueOfCurrencyItem() {
        return valueOfCurrencyItem;
    }

    public Material getCurrencyItem() {
        return currencyItem;
    }

    /*
    Thank you to SainttX again for this https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964/
     */
}
