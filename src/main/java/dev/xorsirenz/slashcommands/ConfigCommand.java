package dev.xorsirenz.slashcommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class ConfigCommand implements BasicCommand {

    private final SlashCommands plugin;
    private final MiniMessage miniMessage;

    private final String name;
    private final String description;
    private final List<String> aliases;
    private final String permission;
    private final String sender;

    public ConfigCommand(
            SlashCommands plugin,
            MiniMessage miniMessage,
            String name,
            String description,
            List<String> aliases,
            String permission,
            String sender) {
        this.plugin = plugin;
        this.miniMessage = miniMessage;

        this.name = name;
        this.description = description;
        this.aliases = List.copyOf(aliases);
        this.permission = permission;
        this.sender = sender;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public List<String> aliases() {
        return aliases;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender commandSender = source.getSender();
        String currentPermission = plugin.getConfig().getString("commands." + name + ".permission", permission);

        if (currentPermission != null && !currentPermission.isBlank()
                && !commandSender.hasPermission(currentPermission)) {
            sendConfiguredMessage(commandSender, CommandManager.getSetting(
                    plugin,
                    "no-permission-message",
                    "<red>You don't have permission to use this command."));
            return;
        }

        String senderType = plugin.getConfig().getString("commands." + name + ".sender", sender);

        if (senderType != null) {
            switch (senderType.toLowerCase()) {
                case "player" -> {
                    if (!(commandSender instanceof Player)) {
                        sendConfiguredMessage(commandSender, CommandManager.getSetting(
                                plugin,
                                "player-only-message",
                                "<red>This command can only be used by players."));
                        return;
                    }
                }

                case "console" -> {
                    if (commandSender instanceof Player) {
                        sendConfiguredMessage(commandSender, CommandManager.getSetting(
                                plugin,
                                "console-only-message",
                                "<red>This command can only be used from console."));
                        return;
                    }
                }

                case "both", "console_and_player" -> {
                }
            }
        }

        List<String> messages = plugin.getConfig().getStringList("commands." + name + ".messages");

        for (String message : messages) {
            message = replacePlaceholders(commandSender, message);
            Component component = miniMessage.deserialize(message);
            commandSender.sendMessage(component);
        }
    }

    private String replacePlaceholders(CommandSender sender, String message) {
        message = message.replace("%player%", sender.getName());
        message = message.replace("%online%", String.valueOf(plugin.getServer().getOnlinePlayers().size()));
        message = message.replace("%max_players%", String.valueOf(plugin.getServer().getMaxPlayers()));
        return message;
    }

    private void sendConfiguredMessage(CommandSender sender, String message) {
        sender.sendMessage(miniMessage.deserialize(message));
    }

    @Override
    public boolean canUse(CommandSender sender) {
        String currentPermission = plugin.getConfig().getString("commands." + name + ".permission", permission);
        return currentPermission == null || currentPermission.isBlank() || sender.hasPermission(currentPermission);
    }

    @Override
    public String permission() {
        return permission.isBlank() ? null : permission;
    }
}
