package jdbc;

import java.io.IOException;
import java.io.StringReader;
import java.sql.DriverManager;
import java.util.Properties;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


public class DBCPInitListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent arg0)  { 
      String poolConfig=arg0.getServletContext().getInitParameter("poolConfig");
      System.out.println("poolconfig:"+poolConfig);
      Properties prop=new Properties();
      
      try {
		prop.load(new StringReader(poolConfig));
		
      } catch (IOException e) {
		throw new RuntimeException(e);
	}
    loadJDBCDriver(prop);  
    initConnectionPool(prop);
	}

	private void loadJDBCDriver(Properties prop) {
    	String driverClass=prop.getProperty("jdbcdriver");
    	try {
			Class.forName(driverClass);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("fail to load JDBC Driver",e);
		}
    	
		
	}
	
	 private void initConnectionPool(Properties prop) {
	  try {
		String jdbcUrl=prop.getProperty("dbUrl");
		String username=prop.getProperty("dbUser");
		String pw=prop.getProperty("dbPass");
		ConnectionFactory connFactory = 
				 new DriverManagerConnectionFactory(jdbcUrl, username, pw);

		PoolableConnectionFactory poolableConnFactory = 
				new PoolableConnectionFactory(connFactory, null);
		String validationQuery = prop.getProperty("validationQuery");
		if(validationQuery != null &&!validationQuery.isEmpty()) {
			poolableConnFactory.setValidationQuery(validationQuery);
		}
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setTimeBetweenEvictionRunsMillis(1000L * 60L * 5L);
		poolConfig.setTestWhileIdle(true);
		int minIdle=getIntProperty(prop, "minIdle", 5);
		poolConfig.setMinIdle(minIdle);
		int maxTotal=getIntProperty(prop, "maxTotal", 50);
		poolConfig.setMaxTotal(maxTotal);

		GenericObjectPool<PoolableConnection> connectionPool = 
				new GenericObjectPool<>(poolableConnFactory, poolConfig);
		poolableConnFactory.setPool(connectionPool);
		
		Class.forName("org.apache.commons.dbcp2.PoolingDriver");
		PoolingDriver driver = 
				(PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		String poolName=prop.getProperty("poolName");
		driver.registerPool(poolName, connectionPool);
		System.out.println("풀링드라이버");
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
			
}
	 private int getIntProperty(Properties prop, String propName, int defaultValue) {
		 String value = prop.getProperty(propName);
		 if(value == null)return defaultValue;
		 return Integer.parseInt(value);
	 }
	 					
		
	

	public void contextDestroyed(ServletContextEvent arg0)  { 
         
    }
	
}
