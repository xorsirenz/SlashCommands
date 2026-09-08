package dev.xorsirenz.slashcommands;

import org.bukkit.plugin.java.JavaPlugin;

public final class SlashCommands extends JavaPlugin {
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        commandManager = new CommandManager(this);
        commandManager.register();
        getLogger().info("SlashCommands enabled.");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void reloadPlugin() {
        reloadConfig();
        commandManager.reloadConfiguration();
        getLogger().info("SlashCommands configuration reloaded.");
    }
}
