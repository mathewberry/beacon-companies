package com.mathewberry.companies.commands.general;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

import com.mathewberry.companies.Companies;
import com.mathewberry.companies.commands.CommandCore;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand {
	
	public boolean onCommand(CommandSender sender, String[] args, String label) 
	{	
		sender.sendMessage(ChatColor.GREEN + "--|| Companies " + ChatColor.AQUA + " v" + Companies.getInstance().getDescription().getVersion() + ChatColor.GREEN + " Created by " + ChatColor.AQUA + "Acuminata" + ChatColor.GREEN + " ||--");
		
		Map<Integer, String> map = new TreeMap<Integer, String>();
		
		map.put(1, ChatColor.AQUA + "/c help <page>" + ChatColor.WHITE + " - Display list of company commands");
		map.put(2, ChatColor.AQUA + "/c create <company>" + ChatColor.WHITE + " - Create company");
		map.put(3, ChatColor.AQUA + "/c delete <company>" + ChatColor.WHITE + " - Delete company");
		map.put(4, ChatColor.AQUA + "/c rename <company_old> <company_new>" + ChatColor.WHITE + " - Rename company");
		map.put(5, ChatColor.AQUA + "/c deposit <amount> <company>" + ChatColor.WHITE + " - Deposit money");
		map.put(6, ChatColor.AQUA + "/c withdraw <amount> <company>" + ChatColor.WHITE + " - Withdraw money");
		map.put(7, ChatColor.AQUA + "/c balance <company>" + ChatColor.WHITE + " - Check company balanace");
		
		map.put(8, ChatColor.AQUA + "/c visible <public/private> <company>" + ChatColor.WHITE + " - Company visibility");	
		map.put(9, ChatColor.AQUA + "/c list" + ChatColor.WHITE + " - List all visible companies");
		map.put(10, ChatColor.AQUA + "/c registerMine <region> <resetTime> <schematic> <company>");
		map.put(11, ChatColor.AQUA + "/c deleteMine <region>" + ChatColor.WHITE + " - Delete a region");
		map.put(12, ChatColor.AQUA + "/c listMines <?company>" + ChatColor.WHITE + " - List all mines/companies mines");
		map.put(13, ChatColor.AQUA + "/c employeeStats <player>" + ChatColor.WHITE + " - List an employees stats");
		map.put(14, ChatColor.AQUA + "/c apply <company>" + ChatColor.WHITE + " - Apply for a role");
		
		map.put(15, ChatColor.AQUA + "/c reject <applicant> <company>" + ChatColor.WHITE + " - Reject an applicant");
		map.put(16, ChatColor.AQUA + "/c hire <applicant> <company>" + ChatColor.WHITE + " - Accept an applicant");
		map.put(17, ChatColor.AQUA + "/c applicants <company>" + ChatColor.WHITE + " - List applicants in company");
		map.put(18, ChatColor.AQUA + "/c jobList" + ChatColor.WHITE + " - List all available jobs");
		map.put(19, ChatColor.AQUA + "/c jobJoin <job> <comapany>" + ChatColor.WHITE + " - Join a job");
		map.put(20, ChatColor.AQUA + "/c jobInfo <job>" + ChatColor.WHITE + " - Get info about a job");
		map.put(21, ChatColor.AQUA + "/c listRanks <company>" + ChatColor.WHITE + " - List company ranks");
		
		map.put(22, ChatColor.AQUA + "/c addRank <wage> <rank> <company>" + ChatColor.WHITE + " - Add a rank");
		map.put(23, ChatColor.AQUA + "/c removeRank <rank> <company>" + ChatColor.WHITE + " - Delete a rank");
		map.put(24, ChatColor.AQUA + "/c getWage <rank> <company>" + ChatColor.WHITE + " - Get rank wage");
		map.put(25, ChatColor.AQUA + "/c setWage <rank> <wage> <company>" + ChatColor.WHITE + " - Set wage");
		map.put(26, ChatColor.AQUA + "/c getDRank <company>" + ChatColor.WHITE + " - Get default rank");
		map.put(27, ChatColor.AQUA + "/c setDRank <rank> <company>" + ChatColor.WHITE + " - Set default rank");
		
		if(args.length < 2 || args.length == 0) {
			CommandCore.paginate(sender, map, 1, 7);
			sender.sendMessage(ChatColor.GREEN + "Next page: /c help 2");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("help")) {
			switch(args[1]) {
				case "2": 
					CommandCore.paginate(sender, map, 2, 7); 
					sender.sendMessage(ChatColor.GREEN + "Next page: /c help 3");
				break;
				case "3": 
					CommandCore.paginate(sender, map, 3, 7); 
					sender.sendMessage(ChatColor.GREEN + "Next page: /c help 4");
				break;
				case "4": 
					CommandCore.paginate(sender, map, 4, 7); 
					//sender.sendMessage(ChatColor.GREEN + "Next page: /c help 5");
				break;
				default:
					CommandCore.paginate(sender, map, 1, 7);
					sender.sendMessage(ChatColor.GREEN + "Next page: /c help 2");
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.GREEN + "For a list of commands do: " + ChatColor.AQUA + "/c help");
			return true;
		}
	}

}
