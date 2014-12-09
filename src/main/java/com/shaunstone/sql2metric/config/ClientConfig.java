package com.shaunstone.sql2metric.config;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaunstone.sql2metric.main.Metric;

/**
 * handles reading in the configuration from the user based on the file
 *
 * @author srstone
 *
 */
public class ClientConfig {

	private final static Set<String> _props = new HashSet<String>(Arrays.asList("query", "jdbcDriver", "pathForJar", "jdbcUrl", "dbUser",
			"dbPassword", "rowprocessor"));
	private Map<String, Object> config;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfig.class);
	private static boolean inited = false;

	private static ClientConfig __instance;

	private ClientConfig() {

	}

	private CommandLine cliArgs;

	private static Long lastRunTime;

	public static ClientConfig instance() {
		if (__instance == null) {
			__instance = new ClientConfig();
		}
		return __instance;
	}

	/**
	 * FIXME should i make sure that the property is in the list above? probably
	 *
	 * @param property
	 * @return
	 */
	public String getString(String property) {
		isPropDefined(property);
		String resp = null;
		Object o = config.get(property);
		if (o == null) {
			return null;
		}
		if (o instanceof GString) {
			return o.toString();
		}
		return (String) o;
	}

	public <T> T getProperty(String property, Class<T> type) {
		isPropDefined(property);
		return (T) config.get(property);
	}

	private void isPropDefined(String prop) {
		if (!config.containsKey(prop)) {
			throw new RuntimeException("this is not a valid prop: " + prop);
		}
	}

	public void init(String[] args) {
		if (inited == true) {
			throw new IllegalStateException("you can only init the config once");
		}
		LOGGER.trace("initilizing the config");
		// inited = true;
		config = new HashMap<String, Object>();
		// TODO cleanup this stuff
		// get the tmp directory and load the props file
		Path dataFile = null;
		if (config.containsKey("dataFile")) {
			dataFile = Paths.get((String) config.get("dataFile"));
		} else {
			// for now its specific to linux
			dataFile = Paths.get(System.getProperty("user.home"), ".sql2metrics", "config.properties");

			LOGGER.debug("config.dir was not configured will be looking in tmp dir for file which is set to : {}",
					dataFile.toAbsolutePath());
		}

		if (!dataFile.toFile().exists()) {
			try {
				dataFile.getParent().toFile().mkdirs();
				dataFile.toFile().createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("cannot create new file at: " + dataFile, e);
			}
		}

		config.put("dataFile", dataFile);
		//so that we can use this in the logging

		Properties props = new Properties();
		try {
			props.load(new FileReader(dataFile.toFile()));
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

		String lastruntimeStr = props.getProperty("lastrun.time");
		lastRunTime = System.currentTimeMillis();
		if (lastruntimeStr == null) {
			setLastRunTime(lastRunTime);
		} else {
			lastRunTime = Long.parseLong(lastruntimeStr);
			config.put("lastrun.time", lastRunTime);
			lastRunTime = Long.parseLong(lastruntimeStr);
		}

		// add the metric class to the script they sent us
		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addImport("Metric", Metric.class.getName());

		compilerConfiguration.addCompilationCustomizers(importCustomizer);

		/**
		 * @todo FIXME to find the file we should default to looking on the
		 *       classpath, otherwise we expect a -Dsql2metric.groovy
		 */
		try {
			parseCommandLineArgs(args);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);

		}

		File scriptFile = new File((String) config.get("configLocation"));
		GroovyShell gs = new GroovyShell(compilerConfiguration);
		Closure groovyObject;
		try {
			Script s = gs.parse(scriptFile);
			HandleMetaClass hmc = new HandleMetaClass(s.getMetaClass(), s);
			hmc.setProperty("lastRun", lastRunTime);

			groovyObject = (Closure) s.run();

		} catch (Exception e) {
			throw new RuntimeException("unable to load configuration", e);
		}

		for (String prop : _props) {
			config.put(prop, groovyObject.getProperty(prop));
		}
	}

	/**
	 * parses the cliArgs
	 *
	 * @param args
	 * @throws ParseException
	 */
	private void parseCommandLineArgs(String[] args) throws ParseException {

		Options options = new Options();
		options.addOption("c", "config", true, "path to config file");
		options.addOption("d", "data", true, "path to file where the application can store its data");
		// options.
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
		String configLocation = cmd.getOptionValue('c');
		String dataFile = cmd.getOptionValue('d');
		// if(configLocation == null || "".equals(configLocation)){
		// throw new IllegalStateException();
		// }
		config.put("configLocation", configLocation);
		if (dataFile != null)
			config.put("dataFile", dataFile);

	}

	/**
	 * last run time in milliseconds since epoc (Date.getTime())
	 *
	 * @param lastRun
	 */
	public void setLastRunTime(long lastRun) {
		Properties props = new Properties();
		props.setProperty("lastrun.time", String.valueOf(lastRun));
		config.put("lastrun.time", lastRun);
		lastRunTime = lastRun;
		try {
			props.store(new FileOutputStream(this.getProperty("dataFile", Path.class).toFile()), "Cool");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
