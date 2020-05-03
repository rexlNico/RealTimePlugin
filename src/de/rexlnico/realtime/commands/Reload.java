package de.rexlnico.realtime.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.rexlnico.realtime.api.Messages;
import de.rexlnico.realtime.main.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.TimeZone;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (p.hasPermission("realtime.reload")) {
                        Main.getInstance().update();
                        Messages.load();
                        p.sendMessage(Messages.prefix + Messages.reloadMSG);
                    } else {
                        p.sendMessage(Messages.prefix + Messages.noPermissions);
                    }
                } else {
                    p.sendMessage(Messages.prefix + Messages.reloadHelp);
                }
            } else {
                p.sendMessage(Messages.prefix + Messages.reloadHelp);
            }
        }
        return false;
    }

}
