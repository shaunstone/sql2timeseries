package com.shaunstone.sql2metric;

import groovy.lang.Closure;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaunstone.sql2metric.config.ClientConfig;
import com.shaunstone.sql2metric.db.QueryService;
import com.shaunstone.sql2metric.main.Metric;
import com.shaunstone.sql2metric.services.collection.SenderService;

/**
 * coordinates all the activites
 *
 * @author srstone
 *
 */
public class Sql2MetricService {
	SenderService senderService;

	private static final Logger LOGGER = LoggerFactory.getLogger(Sql2MetricService.class);

	public Sql2MetricService() {
		senderService = new SenderService();
	}
//TODO javadoc
	public void execute() {

		ClientConfig clientConfig = ClientConfig.instance();



		long now = System.currentTimeMillis();
		Closure<List<Metric>> rowProcessor = clientConfig.getProperty("rowprocessor", Closure.class);
		Long lastRun = clientConfig.getProperty("lastrun.time", Long.class);

		if (lastRun == null) {
			lastRun = now;
		}

		List<Map<String, Object>> results = QueryService.executeQuery();
		LOGGER.info("query return {} results", results.size());
		for (Map<String, Object> map : results) {
			List<Metric> ms = rowProcessor.call(map);
			for (Metric metric2 : ms) {
				senderService.SendSync(metric2);
			}
		}
		// set the time we started if this completes
		clientConfig.setLastRunTime(now);
		LOGGER.debug("Finished executing");
	}

}
