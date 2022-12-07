package life.ferret.ferretPlugin.ItemEco;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;

public class helpers {

    private int valueOfCurrencyItem;
    private Material currencyItem;

    public helpers(Material currencyItem, int valueOfCurrencyItem) {
        this.valueOfCurrencyItem = valueOfCurrencyItem;
        this.currencyItem = currencyItem;
    }

    public int withdrawTotalPossible(int playerBalance) {
        return (int) Math.floor(playerBalance / valueOfCurrencyItem);
    }

    public int getWithdrawItemCount(int withdrawRequestAmount) {
        return withdrawRequestAmount / valueOfCurrencyItem;
    }

    public int returnUsableInteger(String value) {
        if(NumberUtils.isNumber(value)) {
            return NumberUtils.toInt(value);
        } else {
            return -1;
        }
    }

    public int getValueOfCurrencyItem() {
        return valueOfCurrencyItem;
    }

    public Material getCurrencyItem() {
        return currencyItem;
    }
}
