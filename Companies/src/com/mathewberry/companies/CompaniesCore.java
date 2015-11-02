package com.mathewberry.companies;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mathewberry.companies.listeners.CompanyListener;
import com.mathewberry.companies.tables.CompanyTable;

import net.milkbowl.vault.Vault;

public class CompaniesCore extends JavaPlugin {
	
	private CompanyListener companyListener;
	
	@Override 
	public List<Class<?>> getDatabaseClasses()
	{
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(CompanyTable.class);
		return classes;
	}
	
	@Override 
	public void installDDL()
	{
		super.installDDL();
	}
	
	@Override
	public void onEnable()
	{
		// Configuration
	    FileConfiguration config = getConfig();
	    config.options().copyDefaults(true);
	    saveConfig();
		
		// Dependencies
		if((!hasVault())) {
			getLogger().warning("Vault not found, plugin disabled!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		// Create listeners
		companyListener = new CompanyListener(this);
		
		// Register listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(companyListener, this);
		
		// Load commands
		getCommand("companies").setExecutor(new CompaniesCommandExecutor(this));
		
		getLogger().info("Plugin started!");
	}

	@Override
	public void onDisable()
	{
		companyListener = null;
	}
	
	public static CompaniesCore getInstance()
	{
		return (CompaniesCore)Bukkit.getPluginManager().getPlugin("Companies");
	}
	
	public boolean hasVault()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if ((plugin == null) || (!(plugin instanceof Vault))) {
			return false;
		}
		return true;
	}
}