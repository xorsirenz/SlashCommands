package dev.xorsirenz.slashcommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandManager {
    private final SlashCommands plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Map<String, ConfigCommand> commands = new ConcurrentHashMap<>();

    public CommandManager(SlashCommands plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS, event -> {
                    reloadConfiguration();
                    var registrar = event.registrar();
                    for (ConfigCommand command : commands.values()) {
                        registrar.register(command.name(), command.description(), command.aliases(), command);
                    }

                    registrar.register("slashcommands", "Manage SlashCommands", List.of("scmd"), new SlashCommandsCommand(plugin));
                });
    }

    public void reloadConfiguration() {
        commands.clear();
        if (!plugin.getConfig().isConfigurationSection("commands")) {
            plugin.getLogger().warning("No commands section found in config.yml.");
            return;
        }
        var section = plugin.getConfig().getConfigurationSection("commands");

        if (section == null) {
            return;
        }

        for (String name : section.getKeys(false)) {
            String path = "commands." + name;
            boolean enabled = plugin.getConfig().getBoolean(path + ".enabled", true);

            if (!enabled) {
                continue;
            }

            String commandName = name.toLowerCase(Locale.ROOT);

            if (!isValidCommandName(commandName)) {
                plugin.getLogger().warning("Invalid command name: " + name);
                continue;
            }

            String description = plugin.getConfig().getString(path + ".description", "Custom command");
            List<String> aliases = plugin.getConfig().getStringList(path + ".aliases");
            String permission = plugin.getConfig().getString(path + ".permission", "");
            String sender = plugin.getConfig().getString(path + ".sender", "both");

            ConfigCommand command = new ConfigCommand(
                    plugin,
                    miniMessage,
                    commandName,
                    description,
                    aliases,
                    permission,
                    sender);

            commands.put(commandName,command);
        }
        plugin.getLogger().info("Loaded " + commands.size() + " custom command(s).");
    }

    public List<String> getCommandNames() {
        List<String> result = new ArrayList<>(commands.keySet());
        Collections.sort(result);
        return result;
    }

    public int getCommandCount() {
        return commands.size();
    }

    public boolean isConfigured(String name) {
        return commands.containsKey(name.toLowerCase(Locale.ROOT));
    }

    private boolean isValidCommandName(String name) {
        return name.matches("[a-z0-9_\\-.]+");
    }

    public static String getSetting(SlashCommands plugin, String path, String fallback) {
        return plugin.getConfig().getString("settings." + path, fallback);
    }
}
