package com.mathewberry.companies;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mathewberry.companies.listeners.CompanyListener;
import com.mathewberry.companies.tables.CompanyTable;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class Companies extends JavaPlugin {
	
	private CompanyListener companyListener;
	
	@Override 
	public List<Class<?>> getDatabaseClasses()
	{
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(CompanyTable.class);
		return classes;
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
		
		// Databases
		try {
			getDatabase().find(CompanyTable.class).findRowCount();
			getLogger().info("Database information collected");
			getLogger().info("Starting Companies...");
		} catch(PersistenceException error) {
			getLogger().info("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
		
		getLogger().info("Plugin started!");
	}

	@Override
	public void onDisable()
	{
		companyListener = null;
	}
	
	public static Companies getInstance()
	{
		return (Companies)Bukkit.getPluginManager().getPlugin("Companies");
	}
	
	public boolean hasVault()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if ((plugin == null) || (!(plugin instanceof Vault))) {
			return false;
		}
		return true;
	}
	
	public Economy setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if(economyProvider != null) {
			return economyProvider.getProvider();
		}
		return null;
	}
}