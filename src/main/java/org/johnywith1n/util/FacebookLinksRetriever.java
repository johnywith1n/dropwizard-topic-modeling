package org.johnywith1n.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.johnywith1n.models.Links;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FacebookLinksRetriever
{
	private static final Logger LOGGER = LoggerFactory
	        .getLogger(FacebookLinksRetriever.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	public static List<Links> getLinks(HttpClient client, String apiUrl,
	        int numLinksToGet)
	{
		List<Links> links = new ArrayList<Links>();
		int numLinksRetrieved = 0;
		while (numLinksRetrieved < numLinksToGet)
		{
			String linksJson = HttpClientUtil.getResponseAsString(client, apiUrl);
			try
			{
				Links currentLinks = mapper.readValue(linksJson, Links.class);
				links.add(currentLinks);
				numLinksRetrieved += currentLinks.getData().size();
				String pagingUrl = currentLinks.getNextPageUrl();
				LOGGER.info("num links got: " + numLinksRetrieved);
				if (pagingUrl != null)
					apiUrl = pagingUrl;
				else
					break;
			}
			catch (IOException e)
			{
				LOGGER.error("Unable to read json. Ending loop", e);
				break;
			}
		}
		return links;
	}
}
