package com.mathewberry.companies.commands.money;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.Query;
import com.avaje.ebean.SqlUpdate;
import com.mathewberry.companies.Companies;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class DepositCommand {

	private Companies plugin;
	private Economy economy = null;
	
	public DepositCommand(Companies plugin)
	{
		this.plugin = plugin;
		this.economy = plugin.setupEconomy();
	}
	
	public boolean onCommand(CommandSender sender, String[] args) 
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
					if(!company.get(0).getPlayer().getUniqueId().equals(sender.getName())) {
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
						sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.WHITE + " balance is now " + ChatColor.AQUA + "£" + df.format(companyBalance));
						sender.sendMessage(ChatColor.WHITE + "Your personal balance is now " + ChatColor.AQUA + "£" + df.format(personalBalance));
						return true;
					}
				}
			}
		}
		return true;
	}
}
