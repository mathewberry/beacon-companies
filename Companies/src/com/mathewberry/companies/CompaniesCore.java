package com.mathewberry.companies;

import org.bukkit.plugin.java.JavaPlugin;

public class CompaniesCore extends JavaPlugin {

	@Override
	public void onEnable()
	{
		getCommand("companies").setExecutor(new CompaniesCommandExecutor(this));
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
}
