package com.mathewberry.companies.plugin.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.mathewberry.companies.CompaniesCore;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultHandler {
	
	private Economy economy = null;
	
	public VaultHandler()
	{
		setupEconomy();
	}
	
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = CompaniesCore.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
		if(economyProvider != null) {
			this.economy = ((Economy)economyProvider.getProvider());
		}
		return this.economy != null;
	}
	
	public boolean hasEconomy()
	{
		if(this.economy == null) {
			return false;
		}
		return true;
	}
	
	private boolean canAfford(Player player, double amt) 
	{
		if(this.economy.getBalance(player.getName()) >= amt) {
			return true;
		}
		return false;
	}
	
	public boolean chargeCash(Player player, double amt) 
	{
		if(canAfford(player, amt))
		{
			EconomyResponse response = this.economy.withdrawPlayer(player.getName(), amt);
			return true;
		}
		return false;
	}
	
	public boolean giveCash(Player player, double amt) 
	{
		EconomyResponse response = this.economy.depositPlayer(player.getName(), amt);
		return true;
	}
	
	public String formatCash(double amt) 
	{
		return this.economy.format(amt);
	}
}
