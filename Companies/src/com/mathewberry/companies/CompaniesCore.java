package com.mathewberry.companies;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mathewberry.companies.plugin.hooks.VaultHandler;

import net.milkbowl.vault.Vault;

public class CompaniesCore extends JavaPlugin {
	
	private Connection connection;
	private VaultHandler vaultHandler;
	
	@Override
	public void onEnable()
	{
		Map<String, Object> oldConfig = getConfig().getValues(true);
	    
	    saveDefaultConfig();
	    
	    FileConfiguration config = getConfig();
	    for (Map.Entry<String, Object> entry : config.getDefaults().getValues(true).entrySet()) {
	      if (oldConfig.containsKey(entry.getKey())) {
	        config.set((String)entry.getKey(), oldConfig.get(entry.getKey()));
	      } else {
	        config.set((String)entry.getKey(), entry.getValue());
	      }
	    }
	    saveConfig();
	    
		getCommand("companies").setExecutor(new CompaniesCommandExecutor(this));
		
		if((!hasVault())) {
			getLogger().warning("Vault not found, plugin disabled!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		getLogger().info("Loading database driver...");
		
		if((!checkDatabaseDriver())) {
			getLogger().warning("Cannot find database driver!");
			Bukkit.getPluginManager().disablePlugin(this);
		} else {
			getLogger().info("Database driver loaded!");
		}
		
		getLogger().info("Connecting to database...");
		
		this.connection = connectToDatabase();
		
		if(this.connection != null) {
			getLogger().info("Database connection successful!");
		} else {
			getLogger().warning("Failed to connect to database!");
		}
		
		try {
			setupDatabase();
		} catch (SQLException error) {
			error.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable()
	{
		try {
			this.connection.close();
		} catch (SQLException error) {
			getLogger().warning("Database failed to shutdown properly!");
		}
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
	
	public VaultHandler getVaultHandler()
	{
		return this.vaultHandler;
	}
	
	public void setupDatabase() throws SQLException
	{
		Connection connection = connectToDatabase();
		connection.setAutoCommit(false);
		
		Statement stmt = null;
		
		String createCompaniesTable = "CREATE TABLE IF NOT EXISTS mc_companies" + 
									  "(id INTEGER not NULL AUTO_INCREMENT, " +
									  "company_name VARCHAR(255) NOT NULL, " +
									  "company_creator_uuid VARCHAR(60) NOT NULL, " +
									  "company_balance FLOAT NOT NULL, " +
									  "company_created DATETIME NOT NULL, " +
									  "PRIMARY KEY(id))";
		
		try {
			stmt = connection.createStatement();
			stmt.execute(createCompaniesTable);
			
		} catch (SQLException error) {
			getLogger().warning("Failed to create companies table!");
		} finally {
			if(stmt != null) {
				stmt.close();
				getLogger().info("Company Table Added!");
			}
		}
	}
	
	public Connection connectToDatabase()
	{
		Configuration config = getConfig();
		
		String url = config.getString("database.url");
		String username = config.getString("database.username");
		String password = config.getString("database.password");
		
		try(Connection connection = DriverManager.getConnection("jdbc:" + url, username, password)) {
			return connection;
		} catch(SQLException error) {
			Bukkit.getPluginManager().disablePlugin(this);
			return null;
		}
	}
	
	public boolean checkDatabaseDriver()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException error) {
			return false;
		}
	}
}
