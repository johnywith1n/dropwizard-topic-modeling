package org.johnywith1n.healthcheck;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.metrics.core.HealthCheck;

public class FacebookAppHealthCheck extends HealthCheck
{

	private final String appId;
	private final HttpClient client;

	public FacebookAppHealthCheck(String appId, HttpClient client)
	{
		super("facebook login credentials check");
		this.appId = appId;
		this.client = client;
	}

	@Override
	protected Result check() throws Exception
	{
		String url = "https://graph.facebook.com/" + appId;
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
		        .getEntity().getContent()));

		StringBuilder builder = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null)
		{
			builder.append(line);
		}
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, String> json = (Map<String, String>) mapper.readValue(
		        builder.toString(), Map.class);

		if (json.keySet().contains("error"))
			return Result.unhealthy("Facebook doesn't recognize this app id");
		return Result.healthy();
	}
}
