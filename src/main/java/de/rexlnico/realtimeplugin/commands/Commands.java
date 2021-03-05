package de.rexlnico.realtimeplugin.commands;

import de.rexlnico.realtimeplugin.main.Main;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WeatherWorld;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("realtime.reload")){
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("reload")){
                        Main.getWorldManager().loadNew();
                        Main.getWorldManager().updateAll();
                        player.sendMessage(Messages.prefix + Messages.reloadAllMSG);
                    }
                }else if(args.length == 2){
                    if(args[0].equalsIgnoreCase("reload")){
                        WeatherWorld weatherWorld = Main.getWorldManager().getWeatherWorld(args[1]);
                        if(weatherWorld == null){
                            player.sendMessage(Messages.prefix + Messages.noFile.replace("%file%", args[1]));
                            return false;
                        }
                        weatherWorld.update();
                        player.sendMessage(Messages.prefix + Messages.reloadFileMSG.replace("%file%", args[1]));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("reload")){
                return Main.getWorldManager().getTabComplete();
            }
        }else if(args.length == 1){
            ArrayList<String> list = new ArrayList<>();
            list.add("reload");
            return list;
        }

        return new ArrayList<>();
    }
}
