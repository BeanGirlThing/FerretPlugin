package life.ferret.ferretPlugin;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class configManager {

    private Plugin plugin;

    public configManager(Plugin rootPlugin) {
        this.plugin = rootPlugin;
    }

    public boolean reloadConfigFromFile() {
        try {
            getConfig().load(this.plugin.getConfig().getCurrentPath());
            saveConfig();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }

    public void saveConfig() {
        this.plugin.saveConfig();
    }
}
