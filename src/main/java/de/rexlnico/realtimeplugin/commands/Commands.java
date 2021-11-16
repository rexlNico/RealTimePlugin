package de.rexlnico.realtimeplugin.commands;

import de.rexlnico.realtimeplugin.main.Main;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
            switch (args[0].toLowerCase()) {
                case "reload":
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

                case "time":
                    if (args.length > 1) {
                        String file = args[1];
                        WorldContainer world = Main.getWorldManager().getWeatherWorld(file);
                        if (world == null) {
                            sender.sendMessage(Messages.prefix + Messages.noFile.replace("%file%", file));
                            return false;
                        }
                        ZonedDateTime worldDateTime = world.getDateTime();
                        String time = worldDateTime.getHour() + ":" + worldDateTime.getMinute();
                        sender.sendMessage(Messages.prefix + Messages.timeMSG.replace("%file%", file).replace("%time%", time));
                    } else {
                        sender.sendMessage(Messages.prefix + Messages.reloadHelp);
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
            } else if (args[0].equalsIgnoreCase("time")) {
                return Main.getWorldManager().getTabComplete();
            }
        } else if (args.length == 1) {
            return Arrays.asList("reload", "time");
        }

        return new ArrayList<>();
    }
}
