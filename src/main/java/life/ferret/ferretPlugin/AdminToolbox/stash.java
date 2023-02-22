package life.ferret.ferretPlugin.AdminToolbox;

import java.io.Serializable;
import java.util.Map;

public class stash implements Serializable {

    private static final long serialVersionUID = 1L;

    public double health;
    public String[] inventory;
    public Map<String, Object> location;
    public int experience;
    public String offhandItem;
    public String[] armourContents;


    public stash(double health, String[] inventory, Map<String, Object> location, int experience, String offhandItem, String[] armourContents) {
        this.health = health;
        this.inventory = inventory;
        this.location = location;
        this.experience = experience;
        this.offhandItem = offhandItem;
        this.armourContents = armourContents;
    }
}
