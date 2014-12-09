package com.shaunstone.sql2metric.services.collection.engines;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shaunstone.sql2metric.main.Metric;
import com.shaunstone.sql2metric.services.collection.MetricsSender;

public class CarbonSender implements MetricsSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(CarbonSender.class);

	// Carbon host
	private String hostname = "10.8.51.186";
	private int port = 2003;
	private PrintWriter out;

	public CarbonSender() {
		Socket echoSocket;
		try {
			echoSocket = new Socket(hostname, port);
			this.out = new PrintWriter(echoSocket.getOutputStream(), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * this method should batch request into some size then once the size is
	 * good or the time since last message is > that then we send it.
	 *
	 * TODO i.e. we will send either five metrics, or whatever we have in the
	 * last 5 seconds, whichever comes first
	 */

	@Override
	public void sendMetric(Metric metric) {
		String message = String.format("%s.%s.%s %s %d", metric.getApp(), metric.getResource(), metric.getName(), metric.getValue(),
				metric.getTimestamp());
		LOGGER.trace("writing message to socket: {} ", message);
		out.println(message);

	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		out.close();
	}

}
