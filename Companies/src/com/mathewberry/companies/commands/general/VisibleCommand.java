package com.mathewberry.companies.commands.general;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mathewberry.companies.Companies;
import com.mathewberry.companies.helpers.CommandHelper;
import com.mathewberry.companies.helpers.DatabaseHelper;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;

public class VisibleCommand {
	
	private Companies plugin;
	
	public VisibleCommand(Companies plugin)
	{
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
			return true;
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.visible")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
			
			if(args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /c visible (public/private) <company>");
				return true;
			}
			
			String name = CommandHelper.implode(args, " ", 2);
			
			CompanyTable company = DatabaseHelper.findCompany(plugin, name);
			
			if(company == null) {
				sender.sendMessage(ChatColor.RED + "This company does not exist " + name);
				return true;
			} else {
				if(!company.getPlayer().getUniqueId().equals(CommandHelper.getPlayerUUID(sender))) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to edit other companies");
					return true;
				}
				
				boolean visibility = false;
				
				if(args[1].equalsIgnoreCase("public") || args[1].equalsIgnoreCase("private")) {
					if(args[1].equalsIgnoreCase("public")) {
						visibility = true;
					} else if(args[1].equalsIgnoreCase("private")) {
						visibility = false;
					}
					
					company.setVisible(visibility);
					company.setUpdatedAt(DatabaseHelper.currentTime());
					
					plugin.getDatabase().update(company);
					
					sender.sendMessage(ChatColor.GREEN + name + ChatColor.WHITE + " is now " + ChatColor.AQUA + args[1] );
					return true;
				}
				
				sender.sendMessage(ChatColor.RED + "Please use /c visible (public/private) <company>");
				return true;
			}
		}
	}
}