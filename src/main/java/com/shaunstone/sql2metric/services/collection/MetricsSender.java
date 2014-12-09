package com.shaunstone.sql2metric.services.collection;

import java.util.Collection;

import com.shaunstone.sql2metric.main.Metric;

/**
 * send metrics to the collection engine
 *
 * @author srstone
 *
 */
public interface MetricsSender {

	/**
	 * send the metric to the backed of choice
	 *
	 * @param metric
	 */
	void sendMetric(Metric metric);

}
