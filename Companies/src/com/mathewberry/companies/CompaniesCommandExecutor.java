package com.mathewberry.companies;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.avaje.ebean.Query;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class CompaniesCommandExecutor implements CommandExecutor {

	private CompaniesCore plugin;
	private Economy economy = null;
	
	/**
	 * 
	 * @param plugin
	 */
	public CompaniesCommandExecutor(CompaniesCore plugin) 
	{
		this.plugin = plugin;
		setupEconomy();
	}
	
	/**
	 * 
	 * @return boolean
	 */
	public boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if(economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
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
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @param label
	 * @return boolean
	 */
	private boolean company_help(CommandSender sender, String[] args, String label) 
	{
		Player player = (Player)sender;
		
		sender.sendMessage(ChatColor.GREEN + "--|| Companies " + ChatColor.AQUA + " v" + CompaniesCore.getInstance().getDescription().getVersion() + ChatColor.GREEN + " Created by " + ChatColor.AQUA + "Acuminata" + ChatColor.GREEN + " ||--\n" + ChatColor.GREEN + "Type " + ChatColor.WHITE + "/" + label + " help" + ChatColor.GREEN + "for a list of commands");
		sender.sendMessage(ChatColor.AQUA + "/c help" + ChatColor.WHITE + " - Display list of company commands");
		if(player.hasPermission("companies.create")) {
			sender.sendMessage(ChatColor.AQUA + "/c create <company>" + ChatColor.WHITE + " - Create company");
		}
		if(player.hasPermission("companies.delete")) {
			sender.sendMessage(ChatColor.AQUA + "/c delete <company>" + ChatColor.WHITE + " - Delete company");		
		}
		if(player.hasPermission("companies.rename")) {
			sender.sendMessage(ChatColor.AQUA + "/c rename <company_old> <company_new>" + ChatColor.WHITE + " - Rename company");
		}
		if(player.hasPermission("companies.deposit")) {
			sender.sendMessage(ChatColor.AQUA + "/c deposit <amount> <company>" + ChatColor.WHITE + " - Deposit money");
		}
		if(player.hasPermission("companies.withdraw")) {
			sender.sendMessage(ChatColor.AQUA + "/c withdraw <amount> <company>" + ChatColor.WHITE + " - Withdraw money");
		}
		if(player.hasPermission("companies.balance") || player.hasPermission("companies.balance.others")) {
			sender.sendMessage(ChatColor.AQUA + "/c balance <company>" + ChatColor.WHITE + " - Check company balanace");
		}
		return true;
	}
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @return boolean
	 */
	private boolean company_balance(CommandSender sender, String[] args) 
	{
		Player player = (Player)sender;
		if(!player.hasPermission("companies.balance")) {
			player.sendMessage(ChatColor.RED + "You do not have permission.");
			return true;
		}
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /c balance <company>");
			return true;
		} else {
			Query<CompanyTable> query = plugin.getDatabase().find(CompanyTable.class);
			query.where().eq("company", args[1]);
			query.setMaxRows(1);
			List<CompanyTable> company = query.findList();
			
			if(company == null || company.size() == 0) {
				sender.sendMessage(ChatColor.RED + "Failed to get balance of " + args[1]);
				return true;
			} else {
				if(company.get(0).getOwner().equals(sender.getName())) {
					sender.sendMessage(ChatColor.WHITE + args[1] + " balance is " + ChatColor.GREEN + "�" + company.get(0).getBalance());
					return true;
				} else {
					if(!player.hasPermission("companies.balance.others")) {
						sender.sendMessage(ChatColor.RED + "You can only see the balance of your companies.");
						return true;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @return boolean
	 */
	private boolean company_withdraw(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.withdraw")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
			
			if(args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /c withdraw <amount> <company>");
				return true;
			} else {
				Query<CompanyTable> query = plugin.getDatabase().find(CompanyTable.class);
				query.where().eq("company", args[2]);
				query.setMaxRows(1);
				List<CompanyTable> company = query.findList();
				
				if(company == null || company.size() == 0) {
					sender.sendMessage(ChatColor.RED + "This company does not exist " + args[1]);
					return true;
				} else {
					if(!company.get(0).getOwner().equals(sender.getName())) {
						if(!player.hasPermission("companies.withdraw.others")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to withdraw from other companies");
							return true;
						}
					}
					
					float withdrawAmount = Float.parseFloat(args[1]);
					
					if(company.get(0).getBalance() < withdrawAmount) {
						sender.sendMessage(ChatColor.RED + "You don't have enough money in the company to withdraw this amount.");
						return true;
					} else {
						CompanyTable table = plugin.getDatabase().find(CompanyTable.class, company.get(0).getId());
						table.setBalance(company.get(0).getBalance() - withdrawAmount);
						plugin.getDatabase().save(table);
						
						economy.bankDeposit(sender.getName(), withdrawAmount);
						
						sender.sendMessage(ChatColor.WHITE + args[2] + " balance is now " + ChatColor.GREEN + "�" + company.get(0).getBalance() + args[1]);
						return true;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @return boolean
	 */
	private boolean company_deposit(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.deposit")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @return boolean
	 */
	private boolean company_rename(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.rename")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @return boolean
	 */
	private boolean company_delete(CommandSender sender, String[] args) 
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.delete")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param sender
	 * @param args
	 * @return boolean
	 */
	private boolean company_create(CommandSender sender, String[] args)
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
		} else {
			Player player = (Player)sender;
			if(!player.hasPermission("companies.create")) {
				player.sendMessage(ChatColor.RED + "You do not have permission.");
				return true;
			}
			
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /c create <company>");
				return true;
			}
			
			Query<CompanyTable> query = plugin.getDatabase().find(CompanyTable.class);
			query.where().eq("company", args[1]);
			query.setMaxRows(1);
			List<CompanyTable> company = query.findList();
			
			if(company != null || company.size() != 0) {
				if(company.get(0).getOwner().equals(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You already own this company");
					return true;
				} 
				sender.sendMessage(ChatColor.RED + "This company already exists!");
				return true;
			}
			
			if(economy.getBalance(player) < 50000) {
				sender.sendMessage(ChatColor.RED + "You don't have enough money to start a company!");
				return true;
			} else {
				this.economy.bankWithdraw(sender.getName(), 50000);
			}
			
			CompanyTable table = plugin.getDatabase().createEntityBean(CompanyTable.class);
			
			table.setCompany(args[1]);
			table.setOwner(sender.getName());
			table.setBalance(5000.0F);
			
			plugin.getDatabase().save(table);
			
			sender.sendMessage("Company created!");
		}
		return true;
	}

}
