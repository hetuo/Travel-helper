package cs601.Server;

import java.sql.ResultSet;

public class SqlResult {
	
	private ResultSet rs = null;
	private int test = 0;
	
	public void setResultSet(ResultSet rs)
	{
		this.rs =rs;
		this.test = 1;
	}
	
	public ResultSet getResultSet()
	{
		return rs;
	}
	
	public int getTest()
	{
		return test;
	}

}
