package com.mathewberry.companies.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mc_companies")
public class CompanyTable 
{
	@Id private int id;
	@Column private String company;
	@Column private String owner;
	@Column private float balance;
	
	public int getId()
	{
		return id;
	}
	
	public String getCompany()
	{
		return company;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public float getBalance()
	{
		return balance;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setCompany(String company)
	{
		this.company = company;
	}
	
	public void setOwner(String owner)
	{
		this.owner = owner;
	}
	
	public void setBalance(float balance)
	{
		this.balance = balance;
	}
}