package com.shaunstone.sql2metric.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.lookup.MapLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaunstone.sql2metric.Sql2MetricService;
import com.shaunstone.sql2metric.config.ClientConfig;

public class Launcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	ThreadLocal<String> tl = new ThreadLocal<String>();

	public static void main(final String[] args) {

		// have to do this first
		ClientConfig.instance().init(args);

		LOGGER.info("Hi,  lets go get some data");
		LOGGER.debug("running the immediate job,  this will get us back on schedule");
		new Sql2MetricService().execute();

		/**
		 * TODO make the interval configurable. also allow the user to just run
		 * it once
		 */

		final Runnable runit = new Runnable() {

			@Override
			public void run() {
				try {
					LOGGER.info("kicking off metric job");
					// re-init config incase anything changed
					ClientConfig.instance().init(args);
					new Sql2MetricService().execute();
				} catch (Exception e) {
					LOGGER.error("things went bad during the run: swallowing for now", e);
				}

			}
		};
		ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

		schedule.scheduleAtFixedRate(runit, 0, 5, TimeUnit.MINUTES);
	}

}
