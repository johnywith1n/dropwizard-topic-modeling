package org.johnywith1n.util;

import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.topic.LatentDirichletAllocationVectorGibbsSampler.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.johnywith1n.articleExtractor.LinkContentExtractor;
import org.johnywith1n.models.Link;
import org.johnywith1n.models.Links;
import org.johnywith1n.nlp.Filter;
import org.johnywith1n.nlp.TermIndexer;
import org.johnywith1n.nlp.Tokenizer;
import org.johnywith1n.nlp.TopicModeling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.client.HttpClientBuilder;

public class TestUtil
{
	private static final Logger LOGGER = LoggerFactory
	        .getLogger(TestUtil.class);

	public static void main(String[] args)
	{
		HttpClient client = new HttpClientBuilder().build();
		// List<Links> list = getLinks();
		List<Links> list = FacebookLinksRetriever
		        .getLinks(
		                client,
		                "https://graph.facebook.com/me/links?access_token=AAAGx1ABEA6EBAHQZAnfx19TL2ZCQ1DBj5fUTo37nRZCUDzT9rAZAGgQ7gDb8GY8ArPIZAwPfaT9wLdYZBo1O2njVjhAQvMzqLE75ySeDUsZAwZDZD",
		                500);
		long t1 = System.currentTimeMillis();
		LOGGER.info("Getting articles");
		Map<String, String> articleMap = LinkContentExtractor.extractArticles(
		        client, list);
		LOGGER.info("Tokenzing");
		Map<String, Iterable<TermOccurrence>> map = Tokenizer
		        .tokenize(articleMap);
		LOGGER.info("Filtering");
		Filter.filter(map);
		LOGGER.info("Running LDA");
		TopicModeling topicModel = new TopicModeling(
		        TermIndexer.indexTerms(map), 6);
		topicModel.runLDA(map);
		Result result = topicModel.getResult();
		int i = 0;

		double[][] probs = result.getDocumentTopicProbabilities();
		Map<Integer, Set<String>> topicToDocumentMap = new HashMap<Integer, Set<String>>();
		for (i = 0; i < result.getTopicCount(); i++)
		{
			topicToDocumentMap.put(i, new HashSet<String>());
		}
		for (; i < result.getDocumentCount(); i++)
		{
			topicToDocumentMap.get(getMax(probs[i])).add(
			        topicModel.getDocumentUrls().get(i));
		}
		for (i = 0; i < result.getTopicCount(); i++)
		{
			print("Topic #" + i);
			for (String url : topicToDocumentMap.get(i))
			{
				print(url);
			}
		}

		for (i = 0; i < result.getTopicCount(); i++)
		{
			print("Topic #" + i);
			Map<String, Double> terms = topicModel.getTermsForTopic(i, 25);
			for (String key : terms.keySet())
			{
				System.out.println(key + " " + terms.get(key));
			}
			print("");
		}
		long t2 = System.currentTimeMillis();
		print(t2 - t1);

	}

	public static int getMax(double[] probs)
	{
		double max = Double.NEGATIVE_INFINITY;
		int index = 0;
		int result = 0;
		for (double d : probs)
		{
			if (d > max)
			{
				max = d;
				result = index;
			}
			index++;
		}
		return result;
	}

	public static List<Links> getLinks()
	{
		Link link = new Link();
		link.setId("0");
		link.setLink("http://blog.kissmetrics.com/google-products-that-failed/");

		Link link1 = new Link();
		link1.setId("1");
		link1.setLink("http://www.slate.com/articles/health_and_science/pandemics/2012/12/napoleon_march_to_russia_in_1812_typhus_spread_by_lice_was_more_powerful.html");

		Link link2 = new Link();
		link2.setId("2");
		link2.setLink("http://www.nytimes.com/2012/12/12/science/mandatory-prison-sentences-face-growing-skepticism.html?_r=1&pagewanted=all&");

		Link link3 = new Link();
		link3.setId("3");
		link3.setLink("http://cacm.acm.org/blogs/blog-cacm/157645-a-funny-thing-happened-on-the-way-to-academia/fulltext");

		Link link4 = new Link();
		link4.setId("4");
		link4.setLink("http://gawker.com/5966445/senate-hits-new-low-as-mitch-mcconnell-filibusters-himself");

		Link link5 = new Link();
		link5.setId("5");
		link5.setLink("http://www.cbsnews.com/8301-207_162-57557775/nurse-in-duchess-kate-hoax-dead-in-apparent-suicide/");

		Link link6 = new Link();
		link6.setId("6");
		link6.setLink("http://arstechnica.com/tech-policy/2012/12/federal-agency-wants-black-boxes-in-every-car-by-september-2014/");

		Link link7 = new Link();
		link7.setId("7");
		link7.setLink("http://www.pcmag.com/article2/0,2817,2412979,00.asp");

		Link link8 = new Link();
		link8.setId("8");
		link8.setLink("http://www.rawstory.com/rs/2012/12/08/anderson-cooper-destroys-gop-senators-argument-against-u-n-disability-treaty/");

		Link link9 = new Link();
		link9.setId("9");
		link9.setLink("http://www.nydailynews.com/news/politics/romney-visits-manny-pacquiao-stunning-knockout-loss-article-1.1216899");

		List<Link> linkList = new ArrayList<Link>();
		linkList.add(link);
		linkList.add(link1);
		linkList.add(link2);
		linkList.add(link3);
		linkList.add(link4);
		linkList.add(link5);
		linkList.add(link6);
		linkList.add(link7);
		linkList.add(link8);
		linkList.add(link9);

		List<Links> list = new ArrayList<Links>();
		Links links = new Links();
		links.setData(linkList);
		list.add(links);
		return list;
	}

	public static void print(Object o)
	{
		System.out.println(o);
	}
}
