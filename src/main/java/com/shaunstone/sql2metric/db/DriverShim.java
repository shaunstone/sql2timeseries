package com.shaunstone.sql2metric.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverShim implements Driver {

	private Driver driver;

	public DriverShim(Driver d) {
		this.driver = d;
	}

	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}

	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}

	public int getMajorVersion() {
		return driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return driver.getMinorVersion();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// return driver.getParentLogger();
		return null;
	}

	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1) throws SQLException {
		return driver.getPropertyInfo(arg0, arg1);
	}

	public boolean jdbcCompliant() {
		return driver.jdbcCompliant();
	}
}
