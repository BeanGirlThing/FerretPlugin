package life.ferret.ferretPlugin.AdminToolbox;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.Serializable;
import java.util.Map;

public class stash implements Serializable {

    private static final long serialVersionUID = 1L;

    public double health;
    public Map<String, Object>[] inventory;
    public Map<String, Object> location;
    public int experience;
    public Map<String, Object> offhandItem;
    public Map<String, Object>[] armourContents;


    public stash(double health, Map<String, Object>[] inventory, Map<String, Object> location, int experience, Map<String, Object> offhandItem, Map<String, Object>[] armourContents) {
        this.health = health;
        this.inventory = inventory;
        this.location = location;
        this.experience = experience;
        this.offhandItem = offhandItem;
        this.armourContents = armourContents;
    }
}
