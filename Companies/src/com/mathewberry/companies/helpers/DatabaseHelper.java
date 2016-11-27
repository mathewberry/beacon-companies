package com.mathewberry.companies.helpers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.avaje.ebean.Query;
import com.mathewberry.companies.Companies;
import com.mathewberry.companies.tables.CompanyTable;

public class DatabaseHelper {

	/**
	 * Return the current time in a MySQL friendly format.
	 * @return Timestamp
	 */
	public static Timestamp currentTime()
	{
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new Timestamp(now.getTime());
		
		return currentTimestamp;
	}
	
	/**
	 * Find a company based on it's name
	 * @param Companies plugin
	 * @param String name
	 * @return CompanyTable
	 */
	public static CompanyTable findCompany(Companies plugin, String name)
	{
		Query<CompanyTable> query = plugin.getDatabase().find(CompanyTable.class);
		query.where().eq("name", name);
		query.where().isNull("deleted_at");
		
		CompanyTable company = query.findUnique();
		
		return company;
	}
	
}