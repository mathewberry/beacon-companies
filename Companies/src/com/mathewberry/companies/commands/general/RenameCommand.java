package com.mathewberry.companies.commands.general;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.Query;
import com.avaje.ebean.SqlUpdate;
import com.mathewberry.companies.Companies;
import com.mathewberry.companies.tables.CompanyTable;

import net.md_5.bungee.api.ChatColor;

public class RenameCommand {

	private Companies plugin;
	
	public RenameCommand(Companies plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, String[] args) 
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
					if(!company.get(0).getPlayer().getUniqueId().equals(sender.getName())) {
						if(!player.hasPermission("companies.rename.others")) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to rename other peoples companies");
							return true;
						}
					}
					
					String companyNew = args[2];
					
					if(company.get(0).getName() == companyNew) {
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
}
