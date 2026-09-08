package dev.xorsirenz.slashcommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class SlashCommandsCommand implements BasicCommand {
    private final SlashCommands plugin;

    public SlashCommandsCommand(SlashCommands plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (!sender.hasPermission("slashcommands.admin")) {
            sender.sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
            return;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadPlugin();
                sender.sendMessage(Component.text( "SlashCommands configuration reloaded.", NamedTextColor.GREEN));
            }

            case "list" -> {
                sender.sendMessage(Component.text( "Configured commands:", NamedTextColor.AQUA));
                for (String command : plugin.getCommandManager().getCommandNames()) {
                    sender.sendMessage(Component.text( " /" + command, NamedTextColor.GRAY));
                }
            }
            default -> sendHelp(sender);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("SlashCommands", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/slashcommands reload", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/slashcommands list", NamedTextColor.WHITE));
    }

    @Override
    public List<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        return List.of("reload", "list");
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.hasPermission("slashcommands.admin");
    }

    @Override
    public String permission() {
        return "slashcommands.admin";
    }
}
