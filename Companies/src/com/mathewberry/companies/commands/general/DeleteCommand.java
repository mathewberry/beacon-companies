package com.mathewberry.companies.commands.general;

import java.sql.Timestamp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mathewberry.companies.Companies;
import com.mathewberry.companies.helpers.CommandHelper;
import com.mathewberry.companies.helpers.DatabaseHelper;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;

public class DeleteCommand {
	
	private Companies plugin;
	
	public DeleteCommand(Companies plugin)
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
			if(!player.hasPermission("companies.delete")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
			
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /c delete <company>");
				return true;
			} else {
				String name = CommandHelper.implode(args, " ", 1);
				
				CompanyTable company = DatabaseHelper.findCompany(plugin, name);
				
				if(company == null) {
					sender.sendMessage(ChatColor.RED + "This company does not exist " + name);
					return true;
				} else {
					if(!company.getPlayer().getUniqueId().equals(CommandHelper.getPlayerUUID(sender))) {
						if(!player.hasPermission("companies.delete.others")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to delete other peoples companies");
							return true;
						}
					}
					
					Timestamp currentTime = DatabaseHelper.currentTime();
					
					company.setUpdatedAt(currentTime);
					company.setDeletedAt(currentTime);
					
					plugin.getDatabase().update(company);
					
					sender.sendMessage(ChatColor.DARK_RED + name + " deleted!");
					return true;
				}
			}
		}
	}
}
