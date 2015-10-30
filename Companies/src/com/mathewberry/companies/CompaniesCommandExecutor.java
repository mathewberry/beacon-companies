package com.mathewberry.companies;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CompaniesCommandExecutor implements CommandExecutor {

	@SuppressWarnings("unused")
	private CompaniesCore plugin;
	
	public CompaniesCommandExecutor(CompaniesCore plugin) 
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if(command.getName().equalsIgnoreCase("companies")) {
			if(args[0].equalsIgnoreCase("create")) {
				return company_create(sender, args);
			}
		}
		return false;
	}

	private boolean company_create(CommandSender sender, String[] args) 
	{
		sender.sendMessage("Create a company!");
		return true;
	}

}
