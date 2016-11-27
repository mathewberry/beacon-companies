package com.mathewberry.companies.commands.money;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.Query;
import com.mathewberry.companies.Companies;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;

public class BalanceCommand {

	private Companies plugin;
	
	public BalanceCommand(Companies plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, String[] args) 
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
				if(company.get(0).getPlayer().getUniqueId().equals(sender.getName())) {
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(2);
					sender.sendMessage(ChatColor.WHITE + args[1] + " balance is " + ChatColor.GREEN + "£" + df.format(company.get(0).getBalance()));
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
}
