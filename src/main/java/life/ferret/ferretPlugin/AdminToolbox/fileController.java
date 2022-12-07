package life.ferret.ferretPlugin.AdminToolbox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

public class fileController {

    Plugin rootPlugin;

    public fileController(Plugin p) {
        rootPlugin = p;
    }

    public boolean createNewStash(String playerName, stash stashDump) throws IOException {
        File newFile = new File(rootPlugin.getDataFolder().getPath() + "/" + playerName + ".stash");
        if(newFile.createNewFile()) {
            rootPlugin.getLogger().info("Created stash for player "+ playerName);
            FileOutputStream stashFileWriter = new FileOutputStream(rootPlugin.getDataFolder().getPath() + "/" + playerName + ".stash");
            ObjectOutputStream oos = new ObjectOutputStream(stashFileWriter);
            oos.writeObject(stashDump);
            oos.flush();
            stashFileWriter.close();
            return true;
        } else {
            return false;
        }
    }

    public stash getStashData(String playerName) throws IOException, ClassNotFoundException {
        File stashToRead = new File(rootPlugin.getDataFolder().getPath() + "/" + playerName + ".stash");
        rootPlugin.getLogger().info("Stash for "+ playerName +" popped");
        FileInputStream streamIn = new FileInputStream(rootPlugin.getDataFolder().getPath() + "/" + playerName + ".stash");
        ObjectInputStream objectInputStream = new ObjectInputStream(streamIn);
        Object playerStashObject = objectInputStream.readObject();
        streamIn.close();
        if(!stashToRead.delete()) {
            rootPlugin.getLogger().info("Failed to delete stash for "+ playerName);
        }
        return (stash) playerStashObject;
    }
}
