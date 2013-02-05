package org.johnywith1n.services;

import org.apache.http.client.HttpClient;
import org.eclipse.jetty.server.session.SessionHandler;
import org.johnywith1n.config.MainServiceConfiguration;
import org.johnywith1n.healthcheck.FacebookAppHealthCheck;
import org.johnywith1n.resources.FacebookLinksResource;
import org.johnywith1n.resources.MainResource;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class MainService extends Service<MainServiceConfiguration>
{
	public static void main(String[] args) throws Exception
	{
		new MainService().run(args);
	}

	@Override
	public void initialize(Bootstrap<MainServiceConfiguration> bootstrap)
	{
		bootstrap.setName("facebook-links");
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets", "/"));
		bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars",
		        "/webjars"));
	}

	@Override
	public void run(MainServiceConfiguration configuration,
	        Environment environment) throws Exception
	{
		final String appId = configuration.getAppId();
		final String appSecret = configuration.getAppSecret();
		final HttpClient httpClient = new HttpClientBuilder().using(
		        configuration.getHttpClientConfiguration()).build();
		final HttpClient healthCheckHttpClient = new HttpClientBuilder().using(
		        configuration.getHttpClientConfiguration()).build();

		environment.setSessionHandler(new SessionHandler());
		environment.addResource(new MainResource(appId, appSecret, httpClient));
		environment.addResource(new FacebookLinksResource(httpClient));
		environment.addHealthCheck(new FacebookAppHealthCheck(appId,
		        healthCheckHttpClient));
	}

}
