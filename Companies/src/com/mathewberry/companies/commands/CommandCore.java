package com.mathewberry.companies.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mathewberry.companies.Companies;
import com.mathewberry.companies.commands.general.CreateCommand;
import com.mathewberry.companies.commands.general.DeleteCommand;
import com.mathewberry.companies.commands.general.HelpCommand;
import com.mathewberry.companies.commands.general.ListCommand;
import com.mathewberry.companies.commands.general.RenameCommand;
import com.mathewberry.companies.commands.general.VisibleCommand;

public class CommandCore implements CommandExecutor {

	private Companies plugin;

	public CommandCore(Companies plugin) 
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if(command.getName().equalsIgnoreCase("companies")) {
			if(args.length == 0) {
				return new HelpCommand().onCommand(sender, args, label);
			}
			
			switch(args[0]) {
				// General Commands
				case "help": return new HelpCommand().onCommand(sender, args, label);
				case "list": return new ListCommand(plugin).onCommand(sender, args);
				case "create": return new CreateCommand(plugin).onCommand(sender, args);
				case "delete": return new DeleteCommand(plugin).onCommand(sender, args);
				case "rename": return new RenameCommand(plugin).onCommand(sender, args);
				case "visible": return new VisibleCommand(plugin).onCommand(sender, args);
			}
		}
		return false;
	}
	
	public static  void paginate(CommandSender sender, Map<Integer, String> map, int page, int pageLength) 
	{
		sender.sendMessage(ChatColor.GREEN + "List: Page (" + String.valueOf(page) + " of " + (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1 + ")"));
	    int i = 0, k = 0;
	    page--;
	    for (final Entry<Integer, String> e : map.entrySet()) {
	        k++;
	        if ((((page * pageLength) + i + 1) == k) && (k != ((page * pageLength) + pageLength + 1))) {
	            i++;
	            sender.sendMessage(ChatColor.GREEN + " - " + e.getValue());
	        }
	    }
	}
}
