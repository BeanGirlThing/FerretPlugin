package life.ferret.ferretPlugin.FerretCoreTools;

import org.bukkit.command.CommandExecutor;

public class commandSkeleton {
    public String command;
    public CommandExecutor executor;

    public commandSkeleton(String command, CommandExecutor executor) {
        this.command = command;
        this.executor = executor;
    }
}
