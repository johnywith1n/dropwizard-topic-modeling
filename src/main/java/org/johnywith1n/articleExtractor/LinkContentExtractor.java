package org.johnywith1n.articleExtractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.johnywith1n.models.Link;
import org.johnywith1n.models.Links;
import org.johnywith1n.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class LinkContentExtractor
{
	private static final Logger LOGGER = LoggerFactory
	        .getLogger(LinkContentExtractor.class);

	public static Map<String, String> extractArticles(HttpClient client,
	        List<Links> linksList)
	{
		Map<String, String> articleMap = new HashMap<String, String>();

		int i = 0;
		for (Links links : linksList)
		{
			for (Link link : links.getData())
			{
				LOGGER.info("Getting link #" + i++);
				String url = link.getLink();
				articleMap.put(url, extractArticle(client, url));
			}
		}

		return articleMap;
	}

	public static String extractArticle(HttpClient client, String url)
	{
		ArticleExtractor extractor = new ArticleExtractor();
		try
		{
			return extractor.getText(HttpClientUtil.getResponseAsInputSource(
			        client, url));
		}
		catch (BoilerpipeProcessingException e)
		{
			LOGGER.error("", e);
		}
		return "";
	}
}
