package com.mathewberry.companies.commands.general;

import java.sql.Timestamp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mathewberry.companies.Companies;
import com.mathewberry.companies.helpers.CommandHelper;
import com.mathewberry.companies.helpers.DatabaseHelper;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class CreateCommand {
	
	private Companies plugin;
	private Economy economy = null;
	
	public CreateCommand(Companies plugin)
	{
		this.plugin = plugin;
		this.economy = plugin.setupEconomy();
	}

	public boolean onCommand(CommandSender sender, String[] args) 
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
		
			String name = CommandHelper.implode(args, " ", 1);
			
			CompanyTable company = DatabaseHelper.findCompany(plugin, name);
			
			if(company != null) {
				if(company.getPlayer().getUniqueId().equals(CommandHelper.getPlayerUUID(sender))) {
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
			
			Timestamp currentTime = DatabaseHelper.currentTime();
			
			table.setName(name);
			table.setBalance(5000.0F);
			table.setPlayer(Companies.player);
			table.setCreatedAt(currentTime);
			table.setUpdatedAt(currentTime);
			
			plugin.getDatabase().save(table);
			
			sender.sendMessage("Company created!");
			return true;
		}
	}
}