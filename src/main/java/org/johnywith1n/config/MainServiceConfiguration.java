package org.johnywith1n.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class MainServiceConfiguration extends Configuration
{
	@NotEmpty
	@JsonProperty
	private String appId;

	@NotEmpty
	@JsonProperty
	private String appSecret;

	@Valid
	@NotNull
	@JsonProperty
	private HttpClientConfiguration httpClient = new HttpClientConfiguration();

	public String getAppId()
	{
		return appId;
	}

	public String getAppSecret()
	{
		return appSecret;
	}

	public HttpClientConfiguration getHttpClientConfiguration()
	{
		return httpClient;
	}
}
