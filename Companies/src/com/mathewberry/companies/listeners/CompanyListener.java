package com.mathewberry.companies.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.mathewberry.companies.CompaniesCore;

public class CompanyListener implements Listener 
{
	@SuppressWarnings("unused")
	private final CompaniesCore plugin;
	
	public CompanyListener(final CompaniesCore plugin) 
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(final EntityDeathEvent event) 
	{
		// Do some shit
	}

}