package com.shaunstone.sql2metric.services.collection.engines;

import com.shaunstone.sql2metric.main.Metric;
import com.shaunstone.sql2metric.services.collection.MetricsSender;

/**
 * sends metrics to an influxdb
 *
 * @author srstone
 *
 */
public class InfluxdbSender implements MetricsSender {

	@Override
	public void sendMetric(Metric metric) {
		// TODO Auto-generated method stub

	}

	// InfluxDB influxdb;
	//
	// public InfluxdbSender() {
	// this.influxdb = InfluxDBFactory.connect("http://localhost:8086", "root",
	// "root");
	// }
	//
	// @Override
	// public void sendMetric(Metric metric) {
	// Serie s = new Serie.Builder(metric.getResource())
	// .columns("app", "resource", "value", "metric", "time", "pop", "host")
	// .values(metric.getApp(), metric.getResource(), metric.getValue(),
	// metric.getName(), metric.getTimestamp(), metric.getPop(),
	// metric.getHost()).build();
	//
	// influxdb.write("api", TimeUnit.SECONDS, s);
	//
	// }

}
