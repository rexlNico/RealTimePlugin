package de.rexlnico.realtimeplugin.commands;

import de.rexlnico.realtimeplugin.main.Main;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("realtime.reload")) {
            sender.sendMessage(Messages.prefix + Messages.noPermissions);
            return true;
        }
        if (args.length > 0) {
            // TODO: replace with switch-case if more sub-commands get added
            if (args[0].equalsIgnoreCase("reload")) {
                if (args.length > 1) {
                    String file = args[1];
                    WorldContainer world = Main.getWorldManager().getWeatherWorld(file);
                    if (world == null) {
                        sender.sendMessage(Messages.prefix + Messages.noFile.replace("%file%", file));
                        return false;
                    }
                    world.update();
                    sender.sendMessage(Messages.prefix + Messages.reloadFileMSG.replace("%file%", file));
                } else {
                    Main.getWorldManager().loadNew();
                    Main.getWorldManager().updateAll();
                    sender.sendMessage(Messages.prefix + Messages.reloadAllMSG);
                }
                return true;
            }
        }

        sender.sendMessage(Messages.prefix + Messages.reloadHelp);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reload")) {
                return Main.getWorldManager().getTabComplete();
            }
        } else if (args.length == 1) {
            return Collections.singletonList("reload");
        }

        return new ArrayList<>();
    }
}
