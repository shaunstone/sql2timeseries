package com.shaunstone.sql2metric.services.collection.engines;

import com.shaunstone.sql2metric.main.Metric;
import com.shaunstone.sql2metric.services.collection.MetricsSender;

/**
 * post the metric object as json to a rest service. this works for
 * elasticsearch or any other rest endpoint
 *
 * @author srstone
 *
 */
public class RestSender implements MetricsSender {

	@Override
	public void sendMetric(Metric metric) {
		// TODO Auto-generated method stub

	}

	// private WebTarget target;
	//
	// public RestSender() {
	// Client client = ClientBuilder.newClient();
	// client.register(JacksonFeature.class);
	// this.target =
	// client.target("http://localhost:9200").path("metrics").path("api");
	// }
	//
	// @Override
	// public void sendMetric(Metric metric) {
	// target.request().post(Entity.entity(metric,
	// MediaType.APPLICATION_JSON_TYPE), String.class);
	//
	// }

}
