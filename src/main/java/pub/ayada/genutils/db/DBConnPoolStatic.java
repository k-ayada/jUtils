package pub.ayada.genutils.db;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnPoolStatic {
	private static DBConnPoolStatic datasource;
	// private ComboPooledDataSource cpds;
	private HikariDataSource hkds;

	private DBConnPoolStatic(Properties pf) throws PropertyVetoException {
		/*
		 * cpds = new ComboPooledDataSource();
		 * cpds.setDriverClass(pf.getProperty("JDBC_CLASS")); //
		 * "oracle.jdbc.driver.OracleDriver");
		 * cpds.setJdbcUrl(pf.getProperty("JDBC_URL")); //
		 * "jdbc:oracle:thin:@gp-devdb:1521/<srvr>");
		 * cpds.setUser(pf.getProperty("JDBC_UID")); // "<user>");
		 * cpds.setPassword(pf.getProperty("JDBC_PWD")); // "<pswd>");
		 * 
		 * // the settings below are optional -- c3p0 can work with defaults
		 * cpds.setMinPoolSize(Integer.parseInt(pf.getProperty(
		 * "C3P0_MinPoolSize")));
		 * cpds.setInitialPoolSize(Integer.parseInt(pf.getProperty(
		 * "C3P0_InitialPoolSize")));
		 * cpds.setAcquireIncrement(Integer.parseInt(pf.getProperty(
		 * "C3P0_AcquireIncrement")));
		 * cpds.setMaxPoolSize(Integer.parseInt(pf.getProperty(
		 * "C3P0_MaxPoolSize")));
		 * cpds.setMaxStatements(Integer.parseInt(pf.getProperty(
		 * "C3P0_MaxStatements")));
		 * cpds.setAutoCommitOnClose(Boolean.parseBoolean(pf.getProperty(
		 * "C3P0_AutoCommitOnClose")));
		 * cpds.setCheckoutTimeout(Integer.parseInt(pf.getProperty(
		 * "C3P0_CheckoutTimeout")));
		 */

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(pf.getProperty("DRVR"));
		config.setJdbcUrl(pf.getProperty("CSTR"));
		config.setUsername(pf.getProperty("USER"));
		config.setPassword(pf.getProperty("PSWD"));
		config.setAutoCommit(pf.getProperty("HK_AUTOCOMMIT", "false").toLowerCase().equals("true") ? true : false);
		config.setMinimumIdle(Integer.parseInt(pf.getProperty("HK_MIN_CONN", "2")));
		config.setMaximumPoolSize(Integer.parseInt(pf.getProperty("HK_MAX_CONN", "50")));
		config.setConnectionTimeout(Long.parseLong(pf.getProperty("HK_CONN_TIMEOUT_SEC", "30000")));
		config.setIdleTimeout(Long.parseLong(pf.getProperty("HK_CONN_IDLE_TIME_SEC", "600000")));

		config.addDataSourceProperty("cachePrepStmts", pf.getProperty("HK_PREP_STMT_CACHE", "true"));
		config.addDataSourceProperty("prepStmtCacheSize", pf.getProperty("HK_PREP_STMT_CACHE_SIZE", "250"));
		config.addDataSourceProperty("prepStmtCacheSqlLimit", pf.getProperty("HK_PREP_STMT_CACHE_SQL_LIMIT", "2048"));

		this.hkds = new HikariDataSource(config);
	}

	public static synchronized void dropConn(Connection c) {
		datasource.hkds.evictConnection(c);
	}

	/**
	 * 
	 * @param pf
	 *             </br> USER=user id
	 *             </br>INST=DB's SID
	 *             </br>DRVR=JDBC Driver
	 *             </br>CSTR=TNS connection string
	 *             </br>HK_AUTOCOMMIT=false 
	 *             </br>HK_MIN_CONN=2
	 *             </br>HK_MAX_CONN=50
	 *             </br>HK_CONN_TIMEOUT_SEC=30000
	 *             </br>HK_CONN_IDLE_TIME_SEC=600000   
	 *             </br>HK_PREP_STMT_CACHE=true
	 *             </br>HK_PREP_STMT_CACHE_SIZE=250 
	 *             </br>HK_PREP_STMT_CACHE_SQL_LIMIT=2048
	 * @return (Connection) Database connection
	 * @throws IOException
	 * @throws SQLException
	 * @throws PropertyVetoException
	 */
	public static synchronized Connection getConnFrmPool(Properties pf)
			throws IOException, SQLException, PropertyVetoException {
		if (datasource == null) {
			datasource = new DBConnPoolStatic(pf);
			return datasource.getConnection();
		} else {
			return datasource.getConnection();
		}
	}

	public synchronized Connection getConnection() throws SQLException {
		return this.hkds.getConnection();
	}

}
