package com.mathewberry.companies.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.mathewberry.companies.Companies;

public class CompanyListener implements Listener 
{
	@SuppressWarnings("unused")
	private final Companies plugin;
	
	public CompanyListener(final Companies plugin) 
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(final EntityDeathEvent event) 
	{
		// Do some shit
	}

}
