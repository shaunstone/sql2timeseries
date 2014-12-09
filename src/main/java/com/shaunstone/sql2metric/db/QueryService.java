package com.shaunstone.sql2metric.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaunstone.sql2metric.config.ClientConfig;

/**
 * service which executes the query
 *
 * @author srstone
 *
 */
public class QueryService {

	private final static ClientConfig clientConfig = ClientConfig.instance();
	private final static Logger LOGGER = LoggerFactory.getLogger(QueryService.class);

	private QueryService() {

	}

	public static List<Map<String, Object>> executeQuery() {

		QueryRunner run = new QueryRunner();
		Connection conn = null;
		try {
			conn = getConnection();

			List<Map<String, Object>> result;
			try {
				String query = clientConfig.getString("query");
				LOGGER.debug("executing query: {} ", query);
				return run.query(conn, query, new MapListHandler());
			} catch (SQLException e) {
				throw new RuntimeException("could not execute query due to : ", e);
			}

		} finally {
			try {
				DbUtils.close(conn);
			} catch (SQLException exception) {
				throw new RuntimeException("Had an issue closing connection: ", exception);
			}
		}
	}

	private static Connection getConnection() {
		URL u;
		try {
			u = new URL(clientConfig.getString("pathForJar"));
		} catch (MalformedURLException e1) {
			throw new RuntimeException("could not find the driver jar due to: ", e1);
		}

		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver d;
		try {
			d = (Driver) Class.forName(clientConfig.getString("jdbcDriver"), true, ucl).newInstance();
		} catch (Exception e1) {
			throw new RuntimeException("could not load driver due to: ", e1);
		}
		try {
			DriverManager.registerDriver(new DriverShim(d));

			return DriverManager.getConnection(clientConfig.getString("jdbcUrl"), clientConfig.getString("dbUser"),
					clientConfig.getString("dbPassword"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("could not make connection due to : ", e);
		}
	}

}
