package com.mathewberry.companies;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.Query;
import com.avaje.ebean.SqlUpdate;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class CompaniesCommandExecutor implements CommandExecutor {

	private Companies plugin;
	private Economy economy = null;
	
	/**
	 * 
	 * @param plugin
	 */
	public CompaniesCommandExecutor(Companies plugin) 
	{
		this.plugin = plugin;
		this.economy = plugin.setupEconomy();
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
		
		sender.sendMessage(ChatColor.GREEN + "--|| Companies " + ChatColor.AQUA + " v" + Companies.getInstance().getDescription().getVersion() + ChatColor.GREEN + " Created by " + ChatColor.AQUA + "Acuminata" + ChatColor.GREEN + " ||--\n" + ChatColor.GREEN + "Type " + ChatColor.WHITE + "/" + label + " help" + ChatColor.GREEN + "for a list of commands");
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
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(2);
					sender.sendMessage(ChatColor.WHITE + args[1] + " balance is " + ChatColor.GREEN + "�" + df.format(company.get(0).getBalance()));
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
					float companyBalance = company.get(0).getBalance() - withdrawAmount;
					
					if(company.get(0).getBalance() < withdrawAmount) {
						sender.sendMessage(ChatColor.RED + "You don't have enough money in the company to withdraw this amount.");
						return true;
					} else {
						String updateSql = "UPDATE mc_companies SET balance='" + companyBalance + "' WHERE id=:id";
						SqlUpdate update = plugin.getDatabase().createSqlUpdate(updateSql);
						update.setParameter("id", company.get(0).getId());
						update.execute();
						
						economy.bankDeposit(sender.getName(), withdrawAmount);
						float personalBalance = (float)economy.getBalance(player);

						DecimalFormat df = new DecimalFormat();
						df.setMaximumFractionDigits(2);
						
						sender.sendMessage(ChatColor.GREEN + "--|| Company: " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " ||--" );
						sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.WHITE + " balance is now " + ChatColor.AQUA + "�" + df.format(companyBalance));
						sender.sendMessage(ChatColor.WHITE + "Your personal balance is now " + ChatColor.AQUA + "�" + df.format(personalBalance));
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
			
			if(args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /c deposit <amount> <company>");
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
						if(!player.hasPermission("companies.deposit.others")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to deposit from other companies");
							return true;
						}
					}
					
					float depositAmount = Float.parseFloat(args[1]);
					float companyBalance = company.get(0).getBalance() + depositAmount;
					
					if(company.get(0).getBalance() < depositAmount) {
						sender.sendMessage(ChatColor.RED + "You don't have enough money to deposit this amount.");
						return true;
					} else {
						String updateSql = "UPDATE mc_companies SET balance='" + companyBalance + "' WHERE id=:id";
						SqlUpdate update = plugin.getDatabase().createSqlUpdate(updateSql);
						update.setParameter("id", company.get(0).getId());
						update.execute();
						
						economy.bankWithdraw(sender.getName(), depositAmount);
						float personalBalance = (float)economy.getBalance(player);
						
						DecimalFormat df = new DecimalFormat();
						df.setMaximumFractionDigits(2);
						
						sender.sendMessage(ChatColor.GREEN + "--|| Company: " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " ||--" );
						sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.WHITE + " balance is now " + ChatColor.AQUA + "�" + df.format(companyBalance));
						sender.sendMessage(ChatColor.WHITE + "Your personal balance is now " + ChatColor.AQUA + "�" + df.format(personalBalance));
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
			
			if(args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /c deposit <company_old> <company_new>");
				return true;
			} else {
				Query<CompanyTable> query = plugin.getDatabase().find(CompanyTable.class);
				query.where().eq("company", args[1]);
				query.setMaxRows(1);
				List<CompanyTable> company = query.findList();
				
				if(company == null || company.size() == 0) {
					sender.sendMessage(ChatColor.RED + "This company does not exist " + args[1]);
					return true;
				} else {
					if(!company.get(0).getOwner().equals(sender.getName())) {
						if(!player.hasPermission("companies.rename.others")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to rename other peoples companies");
							return true;
						}
					}
					
					String companyNew = args[2];
					
					if(company.get(0).getCompany() == companyNew) {
						sender.sendMessage(ChatColor.RED + "You can't call the company the name it already has!");
						return true;
					} else {
						Query<CompanyTable> query2 = plugin.getDatabase().find(CompanyTable.class);
						query2.where().eq("company", args[2]);
						query2.setMaxRows(1);
						List<CompanyTable> companyExists = query2.findList();
						
						if(companyExists == null || companyExists.size() == 0) {
							String updateSql = "UPDATE mc_companies SET company='" + companyNew + "' WHERE id=:id";
							SqlUpdate update = plugin.getDatabase().createSqlUpdate(updateSql);
							update.setParameter("id", company.get(0).getId());
							update.execute();
							
							sender.sendMessage(ChatColor.GREEN + "--|| Company: " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " ||--" );
							sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.WHITE + " is now called " + ChatColor.AQUA + args[2]);
							return true;
						}
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
	private boolean company_delete(CommandSender sender, String[] args) 
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
				Query<CompanyTable> query = plugin.getDatabase().find(CompanyTable.class);
				query.where().eq("company", args[2]);
				query.setMaxRows(1);
				List<CompanyTable> company = query.findList();
				
				if(company == null || company.size() == 0) {
					sender.sendMessage(ChatColor.RED + "This company does not exist " + args[1]);
					return true;
				} else {
					if(!company.get(0).getOwner().equals(sender.getName())) {
						if(!player.hasPermission("companies.delete.others")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to delete other peoples companies");
							return true;
						}
					}
					
					String updateSql = "DELETE FROM mc_companies WHERE id=:id";
					SqlUpdate update = plugin.getDatabase().createSqlUpdate(updateSql);
					update.setParameter("id", company.get(0).getId());
					update.execute();
					
					sender.sendMessage(ChatColor.DARK_RED + args[1] + " deleted!");
					return true;
				}
			}
		}
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
			return true;
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
			
			if(company == null || company.size() == 0) {
				// Pointless space for the time being
			} else {
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
			return true;
		}
	}
}
