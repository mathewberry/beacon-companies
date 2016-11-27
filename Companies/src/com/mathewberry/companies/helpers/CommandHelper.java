package com.mathewberry.companies.helpers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHelper {

	/**
	 * Get the players UUID
	 * @param sender
	 * @return
	 */
	public static String getPlayerUUID(CommandSender sender)
	{
		Player player = (Player) sender;
		return player.getUniqueId().toString();
	}

	/**
	 * Get the players name
	 * @param uniqueID
	 * @return String
	 */
	public static String getPlayerName(String uniqueID)
	{
		UUID pID = UUID.fromString(uniqueID);
		return Bukkit.getServer().getPlayer(pID).getName();
	}
	
	/**
	 * Turn an array into a string by imploding the items based
	 * the glue provided.
	 * @param items
	 * @param glue
	 * @param ignore
	 * @return
	 */
	public static String implode(String[] items, String glue, int ignore)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < items.length; i++) {
			if(i < ignore) {
				continue;
			}
			
			sb.append(items[i]);
			
			if(i != items.length - 1) {
				sb.append(glue);
			}
		}
		
		return sb.toString();
	}
}