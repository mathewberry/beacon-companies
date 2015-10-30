package com.mathewberry.companies;

import java.sql.Connection;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CompaniesCommandExecutor implements CommandExecutor {

	private CompaniesCore plugin;
	private Connection connection;
	
	public CompaniesCommandExecutor(CompaniesCore plugin) 
	{
		this.plugin = plugin;
		this.connection = plugin.connectToDatabase();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		if(command.getName().equalsIgnoreCase("companies")) {
			if(args.length == 0) {
				return company_help(sender, args, label);
			}
			if(args[0].equalsIgnoreCase("help")) {
				return company_help(sender, args, label);
			}
			if(args[0].equalsIgnoreCase("create")) {
				return company_create(sender, args);
			}
			if(args[0].equalsIgnoreCase("delete")) {
				return company_delete(sender, args);
			}
			if(args[0].equalsIgnoreCase("rename")) {
				return company_rename(sender, args);
			}
			if(args[0].equalsIgnoreCase("deposit")) {
				return company_deposit(sender, args);
			}
			if(args[0].equalsIgnoreCase("withdraw")) {
				return company_withdraw(sender, args);
			}
			if(args[0].equalsIgnoreCase("balance")) {
				return company_balance(sender, args);
			}
		}
		return false;
	}
	
	private boolean company_help(CommandSender sender, String[] args, String label) 
	{
		sender.sendMessage(ChatColor.GREEN + "--|| Companies " + ChatColor.AQUA + " v" + CompaniesCore.getInstance().getDescription().getVersion() + ChatColor.GREEN + " Created by " + ChatColor.AQUA + "Acuminata & Acroneous" + ChatColor.GREEN + " ||--\n" + ChatColor.GREEN + "Type " + ChatColor.WHITE + "/" + label + " help" + ChatColor.GREEN + "for a list of commands");
		return true;
	}
	
	private boolean company_balance(CommandSender sender, String[] args) 
	{
		Player player = (Player)sender;
		if(!player.hasPermission("companies.player.balance")) {
			player.sendMessage(ChatColor.RED + "You do not have permission.");
			return true;
		}
		return true;
	}

	private boolean company_withdraw(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.player.withdraw")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}

	private boolean company_deposit(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.player.deposit")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}

	private boolean company_rename(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.player.rename")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}

	private boolean company_delete(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.player.create")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}

	private boolean company_create(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.player.create")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}

}
