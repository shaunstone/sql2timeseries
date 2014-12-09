package com.shaunstone.sql2metric.services.collection;

import com.shaunstone.sql2metric.main.Metric;
import com.shaunstone.sql2metric.services.collection.engines.CarbonSender;

public class SenderService {

	MetricsSender metricsSender;

	public SenderService() {
		// we will look into a way to configure backends. it may be global or
		// based on something else
		metricsSender = new CarbonSender();
	}

	/**
	 * synchrounsly sends messages to the configured sender, the actual sender
	 * engine may choose to asyncronously respond
	 *
	 * @param metric
	 */
	public void SendSync(Metric metric) {
		metricsSender.sendMetric(metric);
	}

}
