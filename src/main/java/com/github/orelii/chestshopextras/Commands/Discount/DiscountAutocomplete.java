package com.github.orelii.chestshopextras.Commands.Discount;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DiscountAutocomplete implements TabCompleter {
    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
        List<String> l = new ArrayList<>();
        if(cmd.getName().equalsIgnoreCase("discount") && args.length >= 1){
            if(sender instanceof Player){
                if ((args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))){
                    if (args.length > 2 && !args[0].equalsIgnoreCase("add")) return l;
                    else if (args.length > 3 && args[0].equalsIgnoreCase("add")) return l;
                    if (args.length == 3 && args[0].equalsIgnoreCase("add")){
                        l.add("<percentage>");
                        return l;
                    }
                    for (Player p : Bukkit.getOnlinePlayers()){
                        l.add(p.getName());
                    }
                    return l;
                }
                else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("help")){
                    return l;
                }
                l.add("add");
                l.add("remove");
                l.add("list");
                l.add("help");
                return l;
            }
        }
        return l;
    }
}

