package pub.ayada.genutils.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnPool {
	//private ComboPooledDataSource cpds;
	private HikariDataSource hkds;

	public DBConnPool(Properties props) throws PropertyVetoException {		   
/*		cpds = new ComboPooledDataSource();
		cpds.setDriverClass(props.getProperty("JDBC_CLASS")); 
		cpds.setJdbcUrl(props.getProperty("JDBC_URL")); 
		cpds.setUser(props.getProperty("JDBC_UID")); 
		cpds.setPassword(props.getProperty("JDBC_PWD"));

		// the settings below are optional -- c3p0 can work with defaults
		if (props.containsKey("C3P0_MinPoolSize"))
			cpds.setMinPoolSize(Integer.parseInt(props.getProperty("C3P0_MinPoolSize")));
		if (props.containsKey("C3P0_InitialPoolSize"))
			cpds.setInitialPoolSize(Integer.parseInt(props.getProperty("C3P0_InitialPoolSize")));
		if (props.containsKey("C3P0_AcquireIncrement"))
			cpds.setAcquireIncrement(Integer.parseInt(props.getProperty("C3P0_AcquireIncrement")));
		if (props.containsKey("C3P0_MaxPoolSize"))
			cpds.setMaxPoolSize(Integer.parseInt(props.getProperty("C3P0_MaxPoolSize")));
		if (props.containsKey("C3P0_MaxStatements"))
			cpds.setMaxStatements(Integer.parseInt(props.getProperty("C3P0_MaxStatements")));
		if (props.containsKey("C3P0_AutoCommitOnClose"))
			cpds.setAutoCommitOnClose(Boolean.parseBoolean(props.getProperty("C3P0_AutoCommitOnClose")));
		if (props.containsKey("C3P0_CheckoutTimeout"))
			cpds.setCheckoutTimeout(Integer.parseInt(props.getProperty("C3P0_CheckoutTimeout")));
		*/
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(props.getProperty("DRVR"));
		config.setJdbcUrl(props.getProperty("CSTR"));
		config.setUsername(props.getProperty("USER"));
		config.setPassword(props.getProperty("PSWD"));
		config.setAutoCommit(props.getProperty("HK_AUTOCOMMIT", "false").toLowerCase().equals("true") ? true : false);
		config.setMinimumIdle(Integer.parseInt(props.getProperty("HK_MIN_CONN", "2")));
		config.setMaximumPoolSize(Integer.parseInt(props.getProperty("HK_MAX_CONN", "50")));
		config.setConnectionTimeout(Long.parseLong(props.getProperty("HK_CONN_TIMEOUT_SEC", "30000")));
		config.setIdleTimeout(Long.parseLong(props.getProperty("HK_CONN_IDLE_TIME_SEC", "600000")));

		config.addDataSourceProperty("cachePrepStmts", props.getProperty("HK_PREP_STMT_CACHE", "true"));
		config.addDataSourceProperty("prepStmtCacheSize", props.getProperty("HK_PREP_STMT_CACHE_SIZE", "250"));
		config.addDataSourceProperty("prepStmtCacheSqlLimit", props.getProperty("HK_PREP_STMT_CACHE_SQL_LIMIT", "2048"));

		this.hkds = new HikariDataSource(config);

	}

	public Connection getConnFrmPool() throws SQLException {
		return this.hkds.getConnection();
	}
	
	public void returnConn(Connection c) {

		if (c != null)
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

	}
}
